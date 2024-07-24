package fr.xyness.AMS.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import fr.xyness.AMS.AutoMessageSystem;

/**
 * Handles the /toggle command for admin
 */
public class ToggleCommand implements CommandExecutor, TabCompleter {

	
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
    public ToggleCommand(AutoMessageSystem instance) {
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
            	completions.addAll(Set.of("bossbar","title","actionbar","chat"));
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
    	
    	if(!(sender instanceof Player)) {
    		sender.sendMessage("§cThis command can be used only by players");
    		return false;
    	}
    	
    	Player player = (Player) sender;
    	UUID playerId = player.getUniqueId();
    	
    	int arg_length = args.length;
    	
    	if(arg_length == 1) {
    		String arg = args[0];
    		switch(arg) {
	    		case "bossbar":
	    			if(instance.getPlayersUtils().getPlayerOption(playerId, "bossbar")) {
	    				instance.getPlayersUtils().setPlayerOption(playerId, "bossbar", false);
	    				instance.getPlayersUtils().setPlayerOptionInDatabase(playerId, "bossbar", false);
	    				instance.getUtils().unloadAndDisablePlayerBossBars(player);
	    				player.sendMessage("§cBossBars disabled.");
	    			} else {
	    				instance.getPlayersUtils().setPlayerOption(playerId, "bossbar", true);
	    				instance.getPlayersUtils().setPlayerOptionInDatabase(playerId, "bossbar", true);
	    				instance.getUtils().loadPlayerBossBars(player);
	    				player.sendMessage("§aBossBars enabled.");
	    			}
	    			break;
	    		case "title":
	    			if(instance.getPlayersUtils().getPlayerOption(playerId, "title")) {
	    				instance.getPlayersUtils().setPlayerOption(playerId, "title", false);
	    				instance.getPlayersUtils().setPlayerOptionInDatabase(playerId, "title", false);
	    				player.sendMessage("§cTitles disabled.");
	    			} else {
	    				instance.getPlayersUtils().setPlayerOption(playerId, "title", true);
	    				instance.getPlayersUtils().setPlayerOptionInDatabase(playerId, "title", true);
	    				player.sendMessage("§aTitles enabled.");
	    			}
	    			break;
	    		case "actionbar":
	    			if(instance.getPlayersUtils().getPlayerOption(playerId, "actionbar")) {
	    				instance.getPlayersUtils().setPlayerOption(playerId, "actionbar", false);
	    				instance.getPlayersUtils().setPlayerOptionInDatabase(playerId, "actionbar", false);
	    				player.sendMessage("§cActionBars disabled.");
	    			} else {
	    				instance.getPlayersUtils().setPlayerOption(playerId, "actionbar", true);
	    				instance.getPlayersUtils().setPlayerOptionInDatabase(playerId, "actionbar", true);
	    				player.sendMessage("§aActionBars enabled.");
	    			}
	    			break;
	    		case "chat":
	    			if(instance.getPlayersUtils().getPlayerOption(playerId, "chat")) {
	    				instance.getPlayersUtils().setPlayerOption(playerId, "chat", false);
	    				instance.getPlayersUtils().setPlayerOptionInDatabase(playerId, "chat", false);
	    				player.sendMessage("§cChats disabled.");
	    			} else {
	    				instance.getPlayersUtils().setPlayerOption(playerId, "chat", true);
	    				instance.getPlayersUtils().setPlayerOptionInDatabase(playerId, "chat", true);
	    				player.sendMessage("§aChats enabled.");
	    			}
	    			break;
	    		default:
	    			sender.sendMessage("§cWrong argument. Available arguments :\n"
	    					+ "§obossbar, title, actionbar and chat§r§c.");
	    			break;
    		}
    		return true;
    	}
    	
		sender.sendMessage("§cSyntax /disable <argument>\n"
				+ "Available arguments :\n"
				+ "§obossbar, title, actionbar and chat§r§c.");
    	return true;
    }
}
