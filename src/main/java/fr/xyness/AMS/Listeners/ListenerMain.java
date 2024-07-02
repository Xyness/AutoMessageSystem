package fr.xyness.AMS.Listeners;

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
    
    
    // ********************
    // *  Others methods  *
    // ********************

    
    /**
     * Handles the PlayerJoinEvent.
     * Executes an asynchronous task to load the player's BossBars when they join the server.
     *
     * @param event The PlayerJoinEvent triggered when a player joins the server.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        instance.executeAsync(() -> {
            instance.getUtils().loadPlayerBossBars(event.getPlayer());
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
        instance.executeAsync(() -> {
            instance.getUtils().unloadPlayerBossBars(event.getPlayer());
        });
    }
}
