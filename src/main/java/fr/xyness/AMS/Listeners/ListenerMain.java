package fr.xyness.AMS.Listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.xyness.AMS.AutoMessageSystem;

/**
 * ListenerMain class that handles player join and quit events for the AutoMessageSystem.
 * Implements the Bukkit Listener interface to respond to these events.
 */
public class ListenerMain implements Listener {
	
	
    // ***************
    // *  Variables  *
    // ***************
    
	
    /** Represents the AutoMessageSystem instance. */
    private AutoMessageSystem instance;
    
    
    // ******************
    // *  Constructors  *
    // ******************
    
    
    /**
     * Constructs a new ListenerMain with the specified AutoMessageSystem instance.
     *
     * @param instance The instance of the AutoMessageSystem.
     */
    public ListenerMain(AutoMessageSystem instance) {
        this.instance = instance;
    }
    
    
    // *******************
    // *  Other methods  *
    // *******************

    
    /**
     * Handles the PlayerJoinEvent.
     * Executes an asynchronous task to load the player's BossBars when they join the server.
     *
     * @param event The PlayerJoinEvent triggered when a player joins the server.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        instance.executeAsync(() -> {
        	Player player = event.getPlayer();
        	UUID playerId = player.getUniqueId();
        	if(!instance.getPlayersUtils().isPlayerOptions(playerId)) {
        		if(instance.getPlayersUtils().insertPlayer(playerId, player.getName())) {
        			instance.getUtils().loadPlayerBossBars(player);
        		}
        	} else if (instance.getPlayersUtils().getPlayerOption(playerId, "bossbar")){
        		instance.getUtils().loadPlayerBossBars(player);
        	}
        });
    }

    /**
     * Handles the PlayerQuitEvent.
     * Executes an asynchronous task to unload the player's BossBars when they leave the server.
     *
     * @param event The PlayerQuitEvent triggered when a player leaves the server.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        instance.executeAsync(() -> instance.getUtils().unloadPlayerBossBars(event.getPlayer()));
    }
}
