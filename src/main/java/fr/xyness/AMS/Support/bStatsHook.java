package fr.xyness.AMS.Support;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class handles the integration of bStats metrics for the plugin.
 */
public class bStatsHook {
	
	
	// ***************
	// *  Variables  *
	// ***************
	
	
	/** The Metrics instance for bStats integration. */
	public static Metrics metrics;
	
	
	// ********************
	// *  Others Methods  *
	// ********************
	
	
	/**
	 * Enables bStats metrics for the plugin.
	 *
	 * @param plugin the JavaPlugin instance.
	 */
	public static void enableMetrics(JavaPlugin plugin) {
	    int pluginId = 22487;
	    metrics = new Metrics(plugin, pluginId);
	}
}
