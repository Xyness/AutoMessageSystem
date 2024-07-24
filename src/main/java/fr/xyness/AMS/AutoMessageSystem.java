package fr.xyness.AMS;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import fr.xyness.AMS.Commands.MainCommand;
import fr.xyness.AMS.Commands.ToggleCommand;
import fr.xyness.AMS.Listeners.ListenerMain;
import fr.xyness.AMS.Support.bStatsHook;
import fr.xyness.AMS.Types.ActionBarMessage;
import fr.xyness.AMS.Types.BossBarMessage;
import fr.xyness.AMS.Types.ChatMessage;
import fr.xyness.AMS.Types.TitleMessage;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.md_5.bungee.api.ChatColor;

/**
 * Main class to enable AutoMessageSystem
 * This class provides some useful methods
 */
public class AutoMessageSystem extends JavaPlugin {
	
	
    // ***************
    // *  Variables  *
    // ***************

	
    /** The plugin instance */
    private JavaPlugin plugin;
    
    /** The AutoMessageUtils instance */
    private AutoMessageUtils utils;
    
    /** The PlayersUtils instance */
    private PlayersUtils playersUtils;
    
    /** Instance of ClaimbStats for bStats integration */
    private bStatsHook bStatsInstance;
    
    /** The version of the plugin */
    final private String Version = "1.0.4";
	
    /** Whether the server is using Folia */
    private boolean isFolia = false;
    
    /** Whether the server is using PlaceholderAPI */
    private boolean isPAPI = false;
    
    /** Whether an update is available for the plugin */
    private boolean isUpdateAvailable;
    
    /** The update message */
    private String updateMessage;
    
    /** Set of all BukkitTasks of the plugin */
    private Set<BukkitTask> bukkitTasks = new HashSet<>();
    
    /** Set of all ScheduledTasks of the plugin */
    private Set<ScheduledTask> scheduledTasks = new HashSet<>();
    
    /** Data source for database connections */
    private HikariDataSource dataSource;
	
    
    // ******************
    // *  Main Methods  *
    // ******************
    
    
    /**
     * Called when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        getLogger().info("============================================================");
        plugin = this;
        if (loadConfig(false)) {
            getLogger().info(" ");
            getLogger().info("AutoMessageSystem is enabled !");
            getLogger().info("Discord for support : https://discord.gg/xyness");
            getLogger().info("Documentation : https://xyness.gitbook.io/automessagesystem");
            getLogger().info("Developped by Xyness");
        } else {
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
        getLogger().info("============================================================");
    }
    
    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        getLogger().info("============================================================");
        getLogger().info("AutoMessageSystem is disabled !");
        getLogger().info("Discord for support : https://discord.gg/xyness");
        getLogger().info("Documentation : https://xyness.gitbook.io/automessagesystem");
        getLogger().info("Developped by Xyness");
        getLogger().info("============================================================");
    }
    
    
    // ********************
    // *  Others Methods  *
    // ********************
    
    
    /**
     * Loads or reloads the plugin configuration.
     * 
     * @param reload Whether to reload the configuration
     * @return True if the configuration was loaded successfully, false otherwise
     */
    public boolean loadConfig(boolean reload) {
        if (reload) getLogger().info("============================================================");
        saveDefaultConfig();
        reloadConfig();
        
        // Check for updates
        isUpdateAvailable = checkForUpdates();
        
        // Update the config if needed
        updateConfigWithDefaults();
        
        // Check if the server is running Folia
        checkFolia();
        
        // Unregister all handlers, schedulers and data
        HandlerList.unregisterAll(plugin);
        if(reload) {
        	bukkitTasks.forEach(task -> task.cancel());
            scheduledTasks.forEach(task -> task.cancel());
            utils.clearData();
        } else {
        	bStatsInstance = new bStatsHook();
        	bStatsInstance.enableMetrics(plugin);
        }
        
        // Check PlaceholderAPI
        isPAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null ? true : false;
        
        // Register new instance
        if (!reload) utils = new AutoMessageUtils(this);
        
        // Register new instance
        if (!reload) playersUtils = new PlayersUtils(this);
        
        // Check database for options
        HikariConfig configH = new HikariConfig();
        configH.setJdbcUrl("jdbc:sqlite:plugins/AutoMessageSystem/players.db");
        configH.addDataSourceProperty("cachePrepStmts", "true");
        configH.addDataSourceProperty("prepStmtCacheSize", "250");
        configH.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        configH.setPoolName("SQLitePool");
        configH.setMaximumPoolSize(10);
        configH.setMinimumIdle(2);
        configH.setIdleTimeout(60000);
        configH.setMaxLifetime(600000);
        dataSource = new HikariDataSource(configH);
        try (Connection connection = dataSource.getConnection()) {
        	
        	// Create tables if necessary
            try (Statement stmt = connection.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS ams_players " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "uuid VARCHAR(36) NOT NULL UNIQUE, " +
                        "playername VARCHAR(36) NOT NULL, " +
                        "bossbar TINYINT(1) DEFAULT 1, " +
                        "title TINYINT(1) DEFAULT 1, " +
                        "actionbar TINYINT(1) DEFAULT 1, " +
                        "chat TINYINT(1) DEFAULT 1)";
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                getLogger().severe("Error creating tables, disabling plugin.");
                return false;
            }
            
            // Load players data
            int i = 0;
            int max_i = 0;
            String getQuery = "SELECT * FROM ams_players";
            try (PreparedStatement stat = connection.prepareStatement(getQuery);
                    ResultSet resultSet = stat.executeQuery()) {
            	while(resultSet.next()) {
            		max_i++;
                	UUID uuid = null;
                	String uuidString = resultSet.getString("uuid");
                	try {
                		uuid = UUID.fromString(uuidString);
                	} catch (IllegalArgumentException e) {
                		getLogger().severe("Error when loading UUID for the player '" + resultSet.getString("playername") + "'.");
                	}
                	if(uuid != null) {
                		Map<String,Boolean> options = new HashMap<>();
                		Boolean bossbar = resultSet.getBoolean("bossbar");
                		Boolean title = resultSet.getBoolean("title");
                		Boolean actionbar = resultSet.getBoolean("actionbar");
                		Boolean chat = resultSet.getBoolean("chat");
                		options.put("bossbar", bossbar);
                		options.put("title", title);
                		options.put("actionbar", actionbar);
                		options.put("chat", chat);
                		playersUtils.addPlayerOptions(uuid, options);
                		i++;
                	}
            	}
            	getLogger().info(String.valueOf(i)+"/"+String.valueOf(max_i)+" loaded players.");
            } catch (SQLException e) {
            	getLogger().severe("Error loading players, disabling plugin.");
                return false;
            }
            
        } catch (SQLException e) {
        	getLogger().severe("Error connecting to database, disabling plugin.");
            return false;
        }
        
        // Loading disabled worlds
        Set<String> worlds = new HashSet<>(plugin.getConfig().getStringList("disabled-worlds"));
        utils.setDisabledWorlds(worlds);

        // Loading BossBar messages
        ConfigurationSection bossbars = plugin.getConfig().getConfigurationSection("auto-messages.bossbars");
        String path = "auto-messages.bossbars.";
        int i = 0;
        int max_i = bossbars.getKeys(false).size();
        for (String key : bossbars.getKeys(false)) {
            List<String> titles = getConfig().getStringList(path + key + ".titles");
            List<String> colors = getConfig().getStringList(path + key + ".colors");
            List<String> styles = getConfig().getStringList(path + key + ".styles");
            List<String> progressives = getConfig().getStringList(path + key + ".progressives");
            List<String> progressives_reverse = getConfig().getStringList(path + key + ".progressives-reverse");
            List<String> displays_time = getConfig().getStringList(path + key + ".displays-time");
            String frequency = getConfig().getString(path + key + ".frequency");
            String enabled = getConfig().getString(path + key + ".enabled");

            if (titles.isEmpty() || colors.isEmpty() || styles.isEmpty() || progressives.isEmpty() || progressives_reverse.isEmpty() || frequency == null || enabled == null || displays_time.isEmpty()) {
                getLogger().severe("The BossBar '" + key + "' is not configured correctly.");
                continue;
            }
            
            // Enabled check
            if(enabled.equalsIgnoreCase("true") || enabled.equalsIgnoreCase("false")) {
            	if(!Boolean.parseBoolean(enabled)) {
            		continue;
            	}
            } else {
                getLogger().severe("The BossBar '" + key + "' is not configured correctly.");
                continue;
            }
            
            // Conversion
            List<BarColor> colors_final = convertToBarColors(colors,key);
            List<BarStyle> styles_final = convertToBarStyles(styles,key);
            List<Boolean> progressives_final = convertToBooleans(progressives,key,"progressives");
            List<Boolean> progressives_reverse_final = convertToBooleans(progressives_reverse,key,"progressives-reverse");
            List<Integer> displays_time_final = convertToIntegers(displays_time,key,"displays-time","BossBar");
            if(colors_final == null || styles_final == null || progressives_final == null || progressives_reverse_final == null || displays_time_final == null) {
                getLogger().severe("The BossBar '" + key + "' is not configured correctly.");
                continue;
            }
            
            // Frequency check
            int frequencySeconds = utils.convertTimeToSeconds(frequency);
            if (frequencySeconds < 0) {
                getLogger().severe("The BossBar '" + key + "' is not configured correctly. Bad setting for 'frequency'.");
                continue;
            }

            i++;
            utils.addBossBarMessage(new BossBarMessage(key, titles, colors_final, styles_final, progressives_final, progressives_reverse_final, displays_time_final, frequencySeconds));
        }
        getLogger().info(String.valueOf(i)+"/"+String.valueOf(max_i)+" loaded BossBars.");

        // Loading Title messages
        ConfigurationSection titles = plugin.getConfig().getConfigurationSection("auto-messages.titles");
        i = 0;
        max_i = titles.getKeys(false).size();
        path = "auto-messages.titles.";
        for(String key : titles.getKeys(false)) {
        	List<String> titless = plugin.getConfig().getStringList(path+key+".titles");
        	List<String> subtitles = plugin.getConfig().getStringList(path+key+".subtitles");
        	List<String> displays_time = plugin.getConfig().getStringList(path+key+".displays-time");
        	List<String> fades_in = plugin.getConfig().getStringList(path+key+".fades-in");
        	List<String> fades_out = plugin.getConfig().getStringList(path+key+".fades-out");
        	String frequency = plugin.getConfig().getString(path+key+".frequency");
        	String enabled = getConfig().getString(path + key + ".enabled");
        	if(titless.isEmpty() || subtitles.isEmpty() || frequency == null || displays_time.isEmpty() || fades_in.isEmpty() || fades_out.isEmpty() || enabled == null) {
        		getLogger().severe("The Title '"+key+"' is not configured correctly.");
        		continue;
        	}
        	
            // Enabled check
            if(enabled.equalsIgnoreCase("true") || enabled.equalsIgnoreCase("false")) {
            	if(!Boolean.parseBoolean(enabled)) {
            		continue;
            	}
            } else {
                getLogger().severe("The Title '" + key + "' is not configured correctly.");
                continue;
            }
        	
        	// Conversion
        	List<Integer> displays_time_final = convertToIntegers(displays_time,key,"displays-time","Title");
        	List<Integer> fades_in_final = convertToIntegers(fades_in,key,"fades-in","Title");
        	List<Integer> fades_out_final = convertToIntegers(fades_out,key,"fades-out","Title");
        	if(displays_time_final == null || fades_in_final == null || fades_out_final == null) {
                getLogger().severe("The Title '" + key + "' is not configured correctly.");
                continue;
        	}
        	
        	// Frequency check
        	int frequencySeconds = utils.convertTimeToSeconds(frequency);
        	if(frequencySeconds < 0) {
        		getLogger().severe("The Title '"+key+"' is not configured correctly. Bad setting for 'frequency'.");
        		continue;
        	}
        	
        	i++;
        	utils.addTitleMessage(new TitleMessage(key,titless,subtitles,displays_time_final,fades_in_final,fades_out_final,frequencySeconds));
        }
        getLogger().info(String.valueOf(i)+"/"+String.valueOf(max_i)+" loaded Titles.");
        
        // Loading ActionBar messages
        ConfigurationSection actionbars = plugin.getConfig().getConfigurationSection("auto-messages.actionbars");
        i = 0;
        max_i = actionbars.getKeys(false).size();
        path = "auto-messages.actionbars.";
        for(String key : actionbars.getKeys(false)) {
        	List<String> messages = plugin.getConfig().getStringList(path+key+".messages");
        	List<String> displays_time = plugin.getConfig().getStringList(path+key+".displays-time");
        	String frequency = plugin.getConfig().getString(path+key+".frequency");
        	String enabled = getConfig().getString(path + key + ".enabled");
        	if(messages.isEmpty() || frequency == null || displays_time.isEmpty() || enabled == null) {
        		getLogger().severe("The ActionBar '"+key+"' is not configured correctly.");
        		continue;
        	}
        	
            // Enabled check
            if(enabled.equalsIgnoreCase("true") || enabled.equalsIgnoreCase("false")) {
            	if(!Boolean.parseBoolean(enabled)) {
            		continue;
            	}
            } else {
                getLogger().severe("The ActionBar '" + key + "' is not configured correctly.");
                continue;
            }
        	
        	// Conversion
        	List<Integer> displays_time_final = convertToIntegers(displays_time,key,"displays-time","ActionBar");
        	if(displays_time_final == null) {
                getLogger().severe("The ActionBar '" + key + "' is not configured correctly.");
                continue;
        	}
        	
        	// Frequency check
        	int frequencySeconds = utils.convertTimeToSeconds(frequency);
        	if(frequencySeconds < 0) {
        		getLogger().severe("The ActionBar '"+key+"' is not configured correctly. Bad setting for 'frequency'.");
        		continue;
        	}
        	
        	i++;
        	utils.addActionBarMessage(new ActionBarMessage(key,messages,displays_time_final,frequencySeconds));
        }
        getLogger().info(String.valueOf(i)+"/"+String.valueOf(max_i)+" loaded ActionBars.");
        
        // Loading Chat messages
        ConfigurationSection chats = plugin.getConfig().getConfigurationSection("auto-messages.chats");
        i = 0;
        max_i = chats.getKeys(false).size();
        path = "auto-messages.chats.";
        for(String key : chats.getKeys(false)) {
        	List<String> message = plugin.getConfig().getStringList(path+key+".messages");
        	String frequency = plugin.getConfig().getString(path+key+".frequency");
        	String enabled = getConfig().getString(path + key + ".enabled");
        	if(message == null || frequency == null || enabled == null) {
        		getLogger().severe("The Chat '"+key+"' is not configured correctly.");
        		continue;
        	}
        	
            // Enabled check
            if(enabled.equalsIgnoreCase("true") || enabled.equalsIgnoreCase("false")) {
            	if(!Boolean.parseBoolean(enabled)) {
            		continue;
            	}
            } else {
                getLogger().severe("The Chat '" + key + "' is not configured correctly.");
                continue;
            }
        	
        	// Frequency check
        	int frequencySeconds = utils.convertTimeToSeconds(frequency);
        	if(frequencySeconds < 0) {
        		getLogger().severe("The Chat '"+key+"' is not configured correctly. Bad setting for 'frequency'.");
        		continue;
        	}
        	
        	i++;
        	utils.addChatMessage(new ChatMessage(key,message,frequencySeconds));
        }
        getLogger().info(String.valueOf(i)+"/"+String.valueOf(max_i)+" loaded Chats.");
        
        // Load bossbars
        utils.loadBossBars();
        
        // Load players bossbars if reload
        if (reload) Bukkit.getOnlinePlayers().forEach(p -> utils.loadPlayerBossBars(p));
        
        // Setup schedulers
        utils.startScheduler();
        
        // Register commands
        getCommand("ams").setExecutor(new MainCommand(this));
        getCommand("toggle").setExecutor(new ToggleCommand(this));
        
        // Register listener
        getServer().getPluginManager().registerEvents(new ListenerMain(this), plugin);
        
        if (reload) {
        	getLogger().info(" ");
        	getLogger().info("Reload complete.");
        	getLogger().info("============================================================");
        }
        return true;
    }
    
    /**
     * Converts a list of color strings to a list of BarColor enums.
     * 
     * @param colors The list of color strings to convert.
     * @param key The key for the BossBar configuration used for logging purposes.
     * @return A list of BarColor enums, or null if any color string is invalid.
     */
    private List<BarColor> convertToBarColors(List<String> colors, String key) {
        List<BarColor> list = new ArrayList<>();
        for (String color : colors) {
            try {
                list.add(BarColor.valueOf(color.toUpperCase()));
            } catch (IllegalArgumentException e) {
                getLogger().info("The BossBar '" + key + "' is not configured correctly. Bad setting for 'color'.");
                return null;
            }
        }
        return list;
    }

    /**
     * Converts a list of style strings to a list of BarStyle enums.
     * 
     * @param styles The list of style strings to convert.
     * @param key The key for the BossBar configuration used for logging purposes.
     * @return A list of BarStyle enums, or null if any style string is invalid.
     */
    private List<BarStyle> convertToBarStyles(List<String> styles, String key) {
        List<BarStyle> list = new ArrayList<>();
        for (String style : styles) {
            try {
                list.add(BarStyle.valueOf(style.toUpperCase()));
            } catch (IllegalArgumentException e) {
                getLogger().info("The BossBar '" + key + "' is not configured correctly. Bad setting for 'style'.");
                return null;
            }
        }
        return list;
    }

    /**
     * Converts a list of boolean strings to a list of Boolean objects.
     * 
     * @param values The list of boolean strings to convert.
     * @param key The key for the BossBar configuration used for logging purposes.
     * @param settingName The name of the setting used for logging purposes.
     * @return A list of Boolean objects, or null if any boolean string is invalid.
     */
    private List<Boolean> convertToBooleans(List<String> values, String key, String settingName) {
        List<Boolean> list = new ArrayList<>();
        for (String value : values) {
            if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                list.add(Boolean.parseBoolean(value));
            } else {
                getLogger().info("The BossBar '" + key + "' is not configured correctly. Bad setting for '" + settingName + "'.");
                return null;
            }
        }
        return list;
    }

    /**
     * Converts a list of integer strings to a list of Integer objects.
     * 
     * @param values The list of integer strings to convert.
     * @param key The key for the BossBar configuration used for logging purposes.
     * @param settingName The name of the setting used for logging purposes.
     * @return A list of Integer objects, or null if any integer string is invalid.
     */
    private List<Integer> convertToIntegers(List<String> values, String key, String settingName, String type) {
        List<Integer> list = new ArrayList<>();
        for (String value : values) {
            try {
                Integer i = Integer.parseInt(value);
                list.add(i);
            } catch (NumberFormatException e) {
                getLogger().info("The " + type + " '" + key + "' is not configured correctly. Bad setting for '" + settingName + "'.");
                return null;
            }
        }
        return list;
    }

    
    /**
     * Checks if the server is using Folia.
     * 
     * @return True if the server is using Folia, false otherwise
     */
    public boolean isFolia() { return isFolia; }
    
    /**
     * Checks if the server is using PlaceholderAPI.
     * 
     * @return True if the server is using PlaceholderAPI, false otherwise
     */
    public boolean isPAPI() { return isPAPI; }
    
    /**
     * Returns the plugin instance.
     * 
     * @return The plugin instance
     */
    public JavaPlugin getPlugin() { return plugin; }
    
    /**
     * Returns the Utils instance
     * 
     * @return The Utils instance
     */
    public AutoMessageUtils getUtils() { return utils; }
    
    /**
     * Returns the PlayersUtils instance
     * 
     * @return The PlayersUtils instance
     */
    public PlayersUtils getPlayersUtils() { return playersUtils; }
    
    /**
     * Returns the data source for database connections.
     * 
     * @return The data source
     */
    public HikariDataSource getDataSource() { return dataSource; }
    
    /**
     * Returns the update message.
     * 
     * @return The update message
     */
    public String getUpdateMessage() { return updateMessage; }
    
    /**
     * Checks if an update is available for the plugin.
     * 
     * @return True if an update is available, false otherwise
     */
    public boolean isUpdateAvailable() { return isUpdateAvailable; }
    
    /**
     * Executes a timed task asynchronously.
     * 
     * @param gTask The task to execute
     */
    public void executeTimedAsync(Runnable gTask, int seconds, int ticks, boolean startZero) {
        if (isFolia) {
            ScheduledTask foliaTask = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task -> gTask.run(), startZero ? 0 : seconds, seconds, TimeUnit.SECONDS);
            scheduledTasks.add(foliaTask);
        } else {
        	BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, gTask, startZero ? 0 : ticks, ticks);
        	bukkitTasks.add(bukkitTask);
        }
    }
    
    /**
     * Executes a task synchronously (for entities).
     * 
     * @param gTask The task to execute
     */
    public void executeEntitySync(Player player, Runnable gTask) {
        if (isFolia) {
        	player.getScheduler().execute(plugin, gTask, null, 0);
        } else {
            Bukkit.getScheduler().runTask(plugin, gTask);
        }
    }
    
    /**
     * Executes a task asynchronously.
     * 
     * @param gTask The task to execute
     */
    public void executeAsync(Runnable gTask) {
        if (isFolia) {
            Bukkit.getAsyncScheduler().runNow(plugin, task -> gTask.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, gTask);
        }
    }
    
    /**
     * Checks if the server is using Folia.
     */
    public void checkFolia() {
        if (Bukkit.getVersion().contains("folia")) {
            isFolia = true;
            return;
        }
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServerInitEvent");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }
    }
    
    /**
     * Updates the configuration file with default values.
     * 
     * @param plugin The plugin instance
     */
    public void updateConfigWithDefaults() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        try (InputStream defConfigStream = plugin.getResource("config.yml")) {
            if (defConfigStream == null) return;
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));

            boolean changed = false;
            for (String key : defConfig.getKeys(true)) {
                if (!config.contains(key)) {
                    config.set(key, defConfig.get(key));
                    changed = true;
                }
            }

            if (changed) {
                config.save(configFile);
                plugin.reloadConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Checks for updates for the plugin.
     * 
     * @param plugin The plugin instance
     * @return True if an update is available, false otherwise
     */
    public boolean checkForUpdates() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/Xyness/AutoMessageSystem/main/version.yml");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String response = reader.readLine();
                if (!Version.equalsIgnoreCase(response)) {
                    getLogger().info("A new update is available : " + response + " (You have "+Version+")");
                    updateMessage = "§b[AutoMessageSystem] §dA new update is available : §b" + response + " §c(You have "+Version+")";
                    return true;
                } else {
                    getLogger().info("You are using the latest version : " + response);
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }
}
