package fr.xyness.AMS;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import fr.xyness.AMS.Types.ActionBarMessage;
import fr.xyness.AMS.Types.BossBarMessage;
import fr.xyness.AMS.Types.ChatMessage;
import fr.xyness.AMS.Types.TitleMessage;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Utility class for managing automatic messages such as BossBar, Title, ActionBar, and Chat messages. This class provides methods to add messages, set disabled worlds, start schedulers, and display messages.
 */
public class AutoMessageUtils {

	
    // ***************
    // *  Variables  *
    // ***************

	
    /** Represents the AutoMessageSystem instance. */
    private AutoMessageSystem instance;

    /** Stores BossBarMessage objects mapped by their names. */
    private Map<String, BossBarMessage> bossBars = new HashMap<>();

    /** Stores TitleMessage objects mapped by their names. */
    private Map<String, TitleMessage> titles = new HashMap<>();

    /** Stores ActionBarMessage objects mapped by their names. */
    private Map<String, ActionBarMessage> actionBars = new HashMap<>();

    /** Stores ChatMessage objects mapped by their names. */
    private Map<String, ChatMessage> chats = new HashMap<>();

    /** Stores the names of worlds where messages are disabled. */
    private Set<String> disabledWorlds = new HashSet<>();

    /** Maps BossBarMessage to their corresponding BukkitTask. */
    private Map<BossBarMessage, BukkitTask> bossBarsBukkitTasks = new HashMap<>();

    /** Maps BossBarMessage to their corresponding ScheduledTask. */
    private Map<BossBarMessage, ScheduledTask> bossBarsScheduledTasks = new HashMap<>();

    /** Maps ActionBarMessage to their corresponding BukkitTask. */
    private Map<ActionBarMessage, BukkitTask> actionBarsBukkitTasks = new HashMap<>();

    /** Maps ActionBarMessage to their corresponding ScheduledTask. */
    private Map<ActionBarMessage, ScheduledTask> actionBarsScheduledTasks = new HashMap<>();
    
    /** Maps TitleMessage to their corresponding BukkitTask. */
    private Map<TitleMessage, BukkitTask> titleBukkitTasks = new HashMap<>();

    /** Maps TitlerMessage to their corresponding ScheduledTask. */
    private Map<TitleMessage, ScheduledTask> titleScheduledTasks = new HashMap<>();
    
    /** Maps BossBarMessage to their players with bossbar. */
    private Map<BossBarMessage,Map<Player,BossBar>> playersBossBars = new HashMap<>();

    
    // ******************
    // *  Constructors  *
    // ******************

    
    /**
     * Constructor for AutoMessageUtils.
     *
     * @param instance The instance of the AutoMessageSystem plugin.
     */
    public AutoMessageUtils(AutoMessageSystem instance) {
        this.instance = instance;
    }

    
    // ********************
    // *  Others methods  *
    // ********************
    
    
    /**
     * Load the bossbars for players
     */
    public void loadBossBars() {
    	bossBars.values().forEach(b -> {
    		playersBossBars.put(b, new HashMap<>());
    	});
    }
    
    /**
     * Clear all the variables
     */
    public void clearData() {
    	bossBars.clear();
    	titles.clear();
    	actionBars.clear();
    	chats.clear();
    	disabledWorlds.clear();
    	playersBossBars.keySet().forEach(v -> playersBossBars.get(v).values().forEach(b -> b.setVisible(false)));
    	playersBossBars.clear();
    	if(instance.isFolia()) {
        	bossBarsScheduledTasks.values().forEach(t -> t.cancel());
        	bossBarsScheduledTasks.clear();
        	actionBarsScheduledTasks.values().forEach(t -> t.cancel());
        	actionBarsScheduledTasks.clear();
        	titleScheduledTasks.values().forEach(t -> t.cancel());
        	titleScheduledTasks.clear();
    	} else {
        	bossBarsBukkitTasks.values().forEach(t -> t.cancel());
        	bossBarsBukkitTasks.clear();
        	actionBarsBukkitTasks.values().forEach(t -> t.cancel());
        	actionBarsBukkitTasks.clear();
        	titleBukkitTasks.values().forEach(t -> t.cancel());
        	titleBukkitTasks.clear();
    	}
    }

    /**
     * Sets the worlds where messages are disabled.
     *
     * @param worlds The set of world names where messages are disabled.
     */
    public void setDisabledWorlds(Set<String> worlds) {
        this.disabledWorlds = worlds;
    }
    
    /**
     * Gets the worlds where messages are disabled.
     *
     * @return The disabled worlds set.
     */
    public Set<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    /**
     * Adds a BossBarMessage to the collection of boss bars.
     *
     * @param bossbar The BossBarMessage to be added.
     */
    public void addBossBarMessage(BossBarMessage bossbar) {
        bossBars.put(bossbar.getName(), bossbar);
    }

    /**
     * Adds a TitleMessage to the collection of titles.
     *
     * @param title The TitleMessage to be added.
     */
    public void addTitleMessage(TitleMessage title) {
        titles.put(title.getName(), title);
    }

    /**
     * Adds an ActionBarMessage to the collection of action bars.
     *
     * @param actionbar The ActionBarMessage to be added.
     */
    public void addActionBarMessage(ActionBarMessage actionbar) {
        actionBars.put(actionbar.getName(), actionbar);
    }

    /**
     * Adds a ChatMessage to the collection of chat messages.
     *
     * @param chat The ChatMessage to be added.
     */
    public void addChatMessage(ChatMessage chat) {
        chats.put(chat.getName(), chat);
    }
    
    /**
     * Loads all bossbars for a player
     * 
     * @param player The target player
     */
    public void loadPlayerBossBars(Player player) {
    	bossBars.values().forEach(b -> {
    		int index = b.getIndex();
    		String title = instance.isPAPI() ? PlaceholderAPI.setPlaceholders(player, b.getTitle().get(index)) : b.getTitle().get(index);
    		BossBar playerBossBar = Bukkit.createBossBar(title, b.getColor().get(index), b.getStyle().get(index));
    		playerBossBar.setVisible(false);
    		playerBossBar.addPlayer(player);
    		playersBossBars.get(b).put(player, playerBossBar);
    	});
    }
    
    /**
     * Unloads all bossbars for a player
     * 
     * @param player The target player
     */
    public void unloadPlayerBossBars(Player player) {
    	bossBars.values().forEach(b -> {
    		playersBossBars.get(b).remove(player);
    	});
    }
    
    /**
     * Unloads and disables all bossbars for a player
     * 
     * @param player The target player
     */
    public void unloadAndDisablePlayerBossBars(Player player) {
    	bossBars.values().forEach(b -> {
    		playersBossBars.get(b).get(player).setVisible(false);
    		playersBossBars.get(b).remove(player);
    	});
    }

    /**
     * Starts the scheduler for all types of messages (BossBar, Title, ActionBar, Chat).
     */
    public void startScheduler() {
        // Start scheduler for BossBars
    	if(!bossBars.isEmpty()) {
	        bossBars.values().forEach(b -> {
	            instance.executeTimedAsync(() -> displayBossBar(b), b.getFrequency(), b.getFrequency() * 20, false);
	        });
    	}

        // Start scheduler for Titles
    	if(!titles.isEmpty()) {
	        titles.values().forEach(t -> {
	            instance.executeTimedAsync(() -> displayTitle(t), t.getFrequency(), t.getFrequency() * 20, false);
	        });
    	}

        // Start scheduler for ActionBars
    	if(!actionBars.isEmpty()) {
            actionBars.values().forEach(a -> {
                instance.executeTimedAsync(() -> displayActionBar(a), a.getFrequency(), a.getFrequency() * 20, false);
            });
    	}

        // Start scheduler for Chats
        if(!chats.isEmpty()) {
        	chats.values().forEach(c -> {
                instance.executeTimedAsync(() -> displayChat(c), c.getFrequency(), c.getFrequency() * 20, false);
            });
        }
    }

    /**
     * Displays a BossBar message.
     *
     * @param bossbar The BossBarMessage to be displayed.
     */
    public void displayBossBar(BossBarMessage bossbar) {
    	
        // Setup bossbar
        int index = bossbar.getIndex();
        if(index>=bossbar.getTitle().size()) return;
        String title = bossbar.getTitle().get(index);
        BarColor barColor = bossbar.getColor().get(index);
        BarStyle barStyle = bossbar.getStyle().get(index);
        boolean progressive = bossbar.getProgressive().get(index);
        boolean progressive_reverse = bossbar.getProgressiveReverse().get(index);

        // Setup for players
        Map<Player, BossBar> bossBars = playersBossBars.get(bossbar);
        bossBars.forEach((p, bossBar) -> {
            if (!disabledWorlds.contains(p.getWorld().getName())) {
                bossBar.setColor(barColor);
                bossBar.setStyle(barStyle);
                bossBar.setTitle(instance.isPAPI() ? PlaceholderAPI.setPlaceholders(p, title) : title);
                bossBar.setProgress(progressive_reverse ? 0 : 1);
                bossBar.setVisible(true);
            }
        });

        // Displaying bossbar
        int display_time = bossbar.getDisplayTime().get(index) * 10;
        final int[] counter = {display_time};

        Runnable updateTask = () -> {
            if (counter[0] <= 0) {
                bossBars.values().forEach(b -> b.setVisible(false));
                bossBarsScheduledTasks.remove(bossbar);
                bossBarsBukkitTasks.remove(bossbar);
            } else {
                counter[0]--;
                if (progressive) {
                    double progress = progressive_reverse ? (display_time - counter[0]) / (double) display_time : counter[0] / (double) display_time;
                    bossBars.forEach((p, b) -> {
                    	instance.executeEntitySync(p, () -> {
                        	b.setProgress(progress);
                        	b.setTitle(instance.isPAPI() ? PlaceholderAPI.setPlaceholders(p, title) : title);
                    	});
                    });
                } else {
                	bossBars.forEach((p, b) -> {
                		instance.executeEntitySync(p, () -> b.setTitle(instance.isPAPI() ? PlaceholderAPI.setPlaceholders(p, title) : title));
                    });
                }
            }
        };

        // Start progressive
        if (instance.isFolia()) {
            ScheduledTask foliaTask = bossBarsScheduledTasks.get(bossbar);
            if (foliaTask != null && !foliaTask.isCancelled()) {
                foliaTask.cancel();
            }
            foliaTask = Bukkit.getAsyncScheduler().runAtFixedRate(instance.getPlugin(), task -> {
                updateTask.run();
                if (counter[0] <= 0) {
                	task.cancel();
                	bossBars.values().forEach(b -> b.setVisible(false));
                }
            }, 0, 100, TimeUnit.MILLISECONDS);
            bossBarsScheduledTasks.put(bossbar, foliaTask);
        } else {
            BukkitTask bukkitTask = bossBarsBukkitTasks.get(bossbar);
            if (bukkitTask != null && !bukkitTask.isCancelled()) {
                bukkitTask.cancel();
            }
            bukkitTask = new BukkitRunnable() {
                public void run() {
                    updateTask.run();
                    if (counter[0] <= 0) {
                    	this.cancel();
                    	bossBars.values().forEach(b -> b.setVisible(false));
                    }
                }
            }.runTaskTimerAsynchronously(instance.getPlugin(), 0, 2);
            bossBarsBukkitTasks.put(bossbar, bukkitTask);
        }
        
        // Update the index
        index++;
        if(index == bossbar.getTitle().size()) {
        	index = 0;
        }
        bossbar.setIndex(index);
        
    }

    /**
     * Displays a Title message.
     *
     * @param title The TitleMessage to be displayed.
     */
    public void displayTitle(TitleMessage title) {
    	
    	// Setup title
        int index = title.getIndex();
        if(index>=title.getTitle().size()) return;
        String mainTitle = title.getTitle().get(index);
        String subTitle = title.getSubtitle().get(index);
        int display_time = title.getDisplayTime().get(index);
        int fade_in = title.getFadeIn().get(index);
        int fade_out = title.getFadeOut().get(index);
        final int[] counter = {display_time*10};
        final boolean[] first = {true};
        
        // Set a task
        Runnable aTask = () -> {
        	if(first[0]) {
                if(instance.isPAPI()) {
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        if (!disabledWorlds.contains(p.getWorld().getName()) && instance.getPlayersUtils().getPlayerOption(p.getUniqueId(), "title")) {
                        	instance.executeEntitySync(p, () -> p.sendTitle(PlaceholderAPI.setPlaceholders(p, mainTitle), PlaceholderAPI.setPlaceholders(p, subTitle), fade_in, 5, fade_out));
                        }
                    });	
                } else {
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        if (!disabledWorlds.contains(p.getWorld().getName()) && instance.getPlayersUtils().getPlayerOption(p.getUniqueId(), "title")) {
                        	instance.executeEntitySync(p, () -> p.sendTitle(mainTitle, subTitle, fade_in, 5, fade_out));
                        }
                    });
                }
                first[0] = false;
        	} else {
                if(instance.isPAPI()) {
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        if (!disabledWorlds.contains(p.getWorld().getName()) && instance.getPlayersUtils().getPlayerOption(p.getUniqueId(), "title")) {
                        	instance.executeEntitySync(p, () -> p.sendTitle(PlaceholderAPI.setPlaceholders(p, mainTitle), PlaceholderAPI.setPlaceholders(p, subTitle), 0, 5, fade_out));
                        }
                    });	
                } else {
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        if (!disabledWorlds.contains(p.getWorld().getName()) && instance.getPlayersUtils().getPlayerOption(p.getUniqueId(), "title")) {
                        	instance.executeEntitySync(p, () -> p.sendTitle(mainTitle, subTitle, 0, 5, fade_out));
                        }
                    });
                }
        	}
        };

        // Displaying action bar
        if (instance.isFolia()) {
            ScheduledTask foliaTask = titleScheduledTasks.get(title);
            if (foliaTask != null && !foliaTask.isCancelled()) {
                foliaTask.cancel();
            }

            // Schedule a new Folia task
            foliaTask = Bukkit.getAsyncScheduler().runAtFixedRate(instance.getPlugin(), task -> {
                if (counter[0] <= 0) {
                    titleScheduledTasks.remove(title);
                    task.cancel();
                } else {
                    counter[0]--;
                    aTask.run();
                }
            }, 0, 100, TimeUnit.MILLISECONDS);
            titleScheduledTasks.put(title, foliaTask);
        } else {
            BukkitTask task = titleBukkitTasks.get(title);
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }

            // Schedule a new Bukkit task
            task = new BukkitRunnable() {
                public void run() {
                    if (counter[0] <= 0) {
                        titleBukkitTasks.remove(title);
                        this.cancel();
                    } else {
                        counter[0]--;
                        aTask.run();
                    }
                }
            }.runTaskTimerAsynchronously(instance.getPlugin(), 0, 2);
            titleBukkitTasks.put(title, task);
        }
        
        
        // Update the index
        index++;
        if(index == title.getTitle().size()) {
        	index = 0;
        }
        title.setIndex(index);
        
    }

    /**
     * Displays an ActionBar message.
     *
     * @param actionbar The ActionBarMessage to be displayed.
     */
    public void displayActionBar(ActionBarMessage actionbar) {
    	
    	// Setup action bar
        int index = actionbar.getIndex();
        if(index>=actionbar.getMessage().size()) return;
        int display_time = actionbar.getDisplayTime().get(index);
        final int[] counter = {display_time*10};
        String message = actionbar.getMessage().get(index);

        // Set a task
        Runnable aTask = () -> {
            if(instance.isPAPI()) {
            	Bukkit.getOnlinePlayers().forEach(p -> {
                    if (!disabledWorlds.contains(p.getWorld().getName()) && instance.getPlayersUtils().getPlayerOption(p.getUniqueId(), "actionbar")) {
                        instance.executeEntitySync(p, () -> p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(PlaceholderAPI.setPlaceholders(p, message))));
                    }
                });
            } else {
            	Bukkit.getOnlinePlayers().forEach(p -> {
                    if (!disabledWorlds.contains(p.getWorld().getName()) && instance.getPlayersUtils().getPlayerOption(p.getUniqueId(), "actionbar")) {
                        instance.executeEntitySync(p, () -> p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message)));
                    }
                });
            }
        };

        // Displaying action bar
        if (instance.isFolia()) {
            ScheduledTask foliaTask = actionBarsScheduledTasks.get(actionbar);
            if (foliaTask != null && !foliaTask.isCancelled()) {
                foliaTask.cancel();
            }

            // Schedule a new Folia task
            foliaTask = Bukkit.getAsyncScheduler().runAtFixedRate(instance.getPlugin(), task -> {
                if (counter[0] <= 0) {
                    actionBarsScheduledTasks.remove(actionbar);
                    task.cancel();
                } else {
                    counter[0]--;
                    aTask.run();
                }
            }, 0, 100, TimeUnit.MILLISECONDS);
            actionBarsScheduledTasks.put(actionbar, foliaTask);
        } else {
            BukkitTask task = actionBarsBukkitTasks.get(actionbar);
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }

            // Schedule a new Bukkit task
            task = new BukkitRunnable() {
                public void run() {
                    if (counter[0] <= 0) {
                        actionBarsBukkitTasks.remove(actionbar);
                        this.cancel();
                    } else {
                        counter[0]--;
                        aTask.run();
                    }
                }
            }.runTaskTimerAsynchronously(instance.getPlugin(), 0, 2);
            actionBarsBukkitTasks.put(actionbar, task);
        }
        
        // Update the index
        index++;
        if(index == actionbar.getMessage().size()) {
        	index = 0;
        }
        actionbar.setIndex(index);
        
    }

    /**
     * Displays a Chat message.
     *
     * @param chat The ChatMessage to be displayed.
     */
    public void displayChat(ChatMessage chat) {
    	
    	// Setup chat
        int index = chat.getIndex();
        if(index>=chat.getMessage().size()) return;
        String message = chat.getMessage().get(index);
        
        // Display chat message
        if(instance.isPAPI()) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (!disabledWorlds.contains(p.getWorld().getName()) && instance.getPlayersUtils().getPlayerOption(p.getUniqueId(), "chat")) {
                    p.sendMessage(PlaceholderAPI.setPlaceholders(p, message));
                }
            });
        } else {
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (!disabledWorlds.contains(p.getWorld().getName()) && instance.getPlayersUtils().getPlayerOption(p.getUniqueId(), "chat")) {
                    p.sendMessage(message);
                }
            });
        }
        
        // Update the index
        index++;
        if(index == chat.getMessage().size()) {
        	index = 0;
        }
        chat.setIndex(index);
        
    }

    /**
     * Converts a time string from the configuration to seconds.
     *
     * @param time The time string to convert.
     * @return The equivalent time in seconds.
     */
    public int convertTimeToSeconds(String time) {
        int totalSeconds = -1;
        Pattern pattern = Pattern.compile("(\\d+)([smhdwMy])");
        Matcher matcher = pattern.matcher(time);
        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            char unit = matcher.group(2).charAt(0);
            switch (unit) {
                case 's':
                    totalSeconds += value + 1;
                    break;
                case 'm':
                    totalSeconds += value * 60 + 1;
                    break;
                case 'h':
                    totalSeconds += value * 60 * 60 + 1;
                    break;
                case 'd':
                    totalSeconds += value * 60 * 60 * 24 + 1;
                    break;
                case 'w':
                    totalSeconds += value * 60 * 60 * 24 * 7 + 1;
                    break;
                case 'M':
                    totalSeconds += value * 60 * 60 * 24 * 30 + 1;
                    break;
                case 'y':
                    totalSeconds += value * 60 * 60 * 24 * 365 + 1;
                    break;
                default:
                    break;
            }
        }
        return totalSeconds;
    }
}
