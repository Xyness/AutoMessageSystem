package fr.xyness.AMS;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.PlaceholderAPI;

/**
 * LanguageSystem class handles the language settings and messages for the plugin.
 */
public class LanguageSystem {

	
    // ***************
    // *  Variables  *
    // ***************

	
    /** A map to store language keys and their corresponding messages. */
    private Map<String, String> lang = new HashMap<>();
    
    /** Instance of AutoMessageSystem. */
    private AutoMessageSystem instance;
    
    
    // ******************
    // *  Constructors  *
    // ******************
    
    
    /**
     * Constructor for LanguageSystem.
     *
     * @param instance The instance of the AutoMessageSystem plugin.
     */
    public LanguageSystem(AutoMessageSystem instance) {
    	this.instance = instance;
    }

    
    // ********************
    // *  Others Methods  *
    // ********************

    
    /**
     * Sets the language map with the provided messages map.
     *
     * @param messagesMap A map containing message keys and their corresponding messages.
     * @return true if the language map is successfully set.
     */
    public boolean setLanguage(Map<String, String> messagesMap) {
        lang = messagesMap;
        return true;
    }

    /**
     * Gets a message corresponding to the provided key.
     *
     * @param key The key of the message to retrieve.
     * @return The message corresponding to the key, or an empty string if the key is not found.
     */
    public String getMessage(String key) {
    	return lang.containsKey(key) ? lang.get(key) : "";
    }

}
