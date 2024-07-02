package fr.xyness.AMS.Commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.xyness.AMS.AutoMessageSystem;

/**
 * Handles the /ams command for admin
 */
public class MainCommand implements CommandExecutor, TabCompleter {

	
    // ***************
    // *  Variables  *
    // ***************

	
    /** Represents the AutoMessageSystem instance. */
    private AutoMessageSystem instance;
    
	
    // ******************
    // *  Constructors  *
    // ******************
	
	
    /**
     * Constructs the main command
     *
     * @param name The AutoMessageSystem instance
     */
    public MainCommand(AutoMessageSystem instance) {
    	this.instance = instance;
    }

	
    // ******************
    // *  Tab Complete  *
    // ******************

	
    /**
     * Provides tab completion suggestions for the /sclaim command.
     *
     * @param sender Source of the command
     * @param cmd Command which was executed
     * @param alias Alias of the command which was used
     * @param args Passed command arguments
     * @return A list of tab completion suggestions
     */
	@Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		
        CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
            List<String> completions = new ArrayList<>();
            
            if(args.length == 1) {
            	completions.addAll(Set.of("reload","add-disabled-world","remove-disabled-world","list-disabled-world"));
            	return completions;
            }
            
            if(args.length == 2 && args[0].equalsIgnoreCase("remove-disabled-world")) {
            	completions.addAll(instance.getUtils().getDisabledWorlds());
            	return completions;
            }
            
            if(args.length == 2 && args[0].equalsIgnoreCase("add-disabled-world")) {
            	completions.addAll(Bukkit.getWorlds().stream()
					    .map(w -> w.getName())
					    .collect(Collectors.toSet()));
            	return completions;
            }
            
            return completions;
        });

        try {
            return future.get(); // Return the result from the CompletableFuture
        } catch (ExecutionException | InterruptedException e) {
            return new ArrayList<>();
        }
	}
	

    // ******************
    // *  Main command  *
    // ******************

	
    /**
     * Executes the given command, returning its success.
     *
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	
    	int arg_length = args.length;
    	
    	if(arg_length > 0) {
    		String arg = args[0];
    		switch(arg) {
	    		case "reload":
	    			if(instance.loadConfig(true)) {
	    				sender.sendMessage("§aReload complete.");
	    			}
	    			break;
	    		case "add-disabled-world":
	    			if(arg_length == 2) {
	    				World world = Bukkit.getWorld(args[1]);
	    				if(world == null) {
	    					sender.sendMessage("§cWorld '"+args[1]+"' not found. Available worlds :\n"
	    							+ "§o"+Bukkit.getWorlds().stream()
	    						    .map(w -> w.getName())
	    						    .collect(Collectors.joining(", ")));
	    					return false;
	    				}
	    				String world_name = world.getName();
	                    File configFile = new File(instance.getDataFolder(), "config.yml");
	                    FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
	                    List<String> worlds = config.getStringList("disabled-worlds");
	                    if(worlds.contains(args[1])) {
	                    	sender.sendMessage("§cWorld '"+world_name+"' already in disabled worlds.");
	                    	return false;
	                    }
	                    worlds.add(world_name);
	                    config.set("disabled-worlds", worlds);
	                    instance.getUtils().setDisabledWorlds(new HashSet<>(worlds));
	                    try {
	                    	config.save(configFile);
	                    	sender.sendMessage("§fWorld '§e"+world_name+"§f' added to disabled world.");
	    				} catch (IOException e) {
	    					e.printStackTrace();
	    				}
	                    return true;
	    			}
	    			sender.sendMessage("§cSyntax : /ams add-disabled-world <world>");
	    			break;
	    		case "remove-disabled-world":
	    			if(arg_length == 2) {
	    				World world = Bukkit.getWorld(args[1]);
	    				if(world == null) {
	    					sender.sendMessage("§cWorld '"+args[1]+"' not found. Available worlds :\n"
	    							+ "§o"+Bukkit.getWorlds().stream()
	    						    .map(w -> w.getName())
	    						    .collect(Collectors.joining(", ")));
	    					return false;
	    				}
	    				String world_name = world.getName();
	                    File configFile = new File(instance.getDataFolder(), "config.yml");
	                    FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
	                    List<String> worlds = config.getStringList("disabled-worlds");
	                    if(!worlds.contains(args[1])) {
	                    	sender.sendMessage("§cWorld '"+world_name+"' is not a disabled world.");
	                    	return false;
	                    }
	                    worlds.remove(world_name);
	                    config.set("disabled-worlds", worlds);
	                    instance.getUtils().setDisabledWorlds(new HashSet<>(worlds));
	                    try {
	                    	config.save(configFile);
	                    	sender.sendMessage("§fWorld '§e"+world_name+"§f' removed from disabled world.");
	    				} catch (IOException e) {
	    					e.printStackTrace();
	    				}
	                    return true;
	    			}
	    			sender.sendMessage("§cSyntax : /ams remove-disabled-world <world>");
	    			break;
	    		case "list-disabled-world":
	    			sender.sendMessage("§fDisabled worlds :§e\n"
	    					+ instance.getUtils().getDisabledWorlds().stream().collect(Collectors.joining("§f,§e ")));
	    			break;
	    		default:
	    			sender.sendMessage("§cWrong argument. Available arguments :\n"
	    					+ "§oreload, add-disabled-world, remove-disabled-world, list-disabled-world§r§c.");
	    			break;
    		}
    		return true;
    	}
    	
		sender.sendMessage("§cSyntax /ams <argument> [<sub-arg>]\n"
				+ "Available arguments :\n"
				+ "§oreload, add-disabled-world, remove-disabled-world, list-disabled-world§r§c.");
    	return true;
    }
}
