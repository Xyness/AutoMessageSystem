package fr.xyness.AMS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class for managing player options and database interactions in the AutoMessageSystem.
 */
public class PlayersUtils {
	
	
    // ***************
    // *  Variables  *
    // ***************
	

	/** Map for UUID to their settings */
    private Map<UUID, Map<String, Boolean>> playersOptions = new HashMap<>();
    
    /** Instance of AutoMessageSystem */
    private AutoMessageSystem instance;
    
    
    // ******************
    // *  Constructors  *
    // ******************
    

    /**
     * Constructs a new PlayersUtils instance.
     *
     * @param instance the AutoMessageSystem instance
     */
    public PlayersUtils(AutoMessageSystem instance) {
        this.instance = instance;
    }
    
    
    // *******************
    // *  Other methods  *
    // *******************
    

    /**
     * Adds options for a specific player.
     *
     * @param targetUUID the UUID of the target player
     * @param options    the options to add for the player
     */
    public void addPlayerOptions(UUID targetUUID, Map<String, Boolean> options) {
        playersOptions.put(targetUUID, options);
    }

    /**
     * Retrieves the value of a specific option for a player.
     *
     * @param targetUUID the UUID of the target player
     * @param option     the name of the option to retrieve
     * @return the value of the option for the player
     */
    public boolean getPlayerOption(UUID targetUUID, String option) {
        return playersOptions.get(targetUUID).get(option);
    }

    /**
     * Sets the value of a specific option for a player.
     *
     * @param targetUUID the UUID of the target player
     * @param option     the name of the option to set
     * @param value      the value to set for the option
     */
    public void setPlayerOption(UUID targetUUID, String option, boolean value) {
        playersOptions.get(targetUUID).put(option, value);
    }

    /**
     * Checks if options exist for a specific player.
     *
     * @param targetUUID the UUID of the target player
     * @return true if options exist for the player, false otherwise
     */
    public boolean isPlayerOptions(UUID targetUUID) {
        return playersOptions.containsKey(targetUUID);
    }

    /**
     * Inserts a player into the database with default options.
     *
     * @param targetUUID the UUID of the target player
     * @param targetName the name of the target player
     * @return true if the player was successfully inserted, false otherwise
     */
    public boolean insertPlayer(UUID targetUUID, String targetName) {
        Map<String, Boolean> options = new HashMap<>();
        options.put("bossbar", true);
        options.put("title", true);
        options.put("actionbar", true);
        options.put("chat", true);
        playersOptions.put(targetUUID, options);
        try (Connection connection = instance.getDataSource().getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO ams_players (uuid, playername, bossbar, title, actionbar, chat) VALUES (?, ?, 1, 1, 1, 1)")) {
            stmt.setString(1, targetUUID.toString());
            stmt.setString(2, targetName);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sets the value of a specific option for a player in the database.
     *
     * @param targetUUID the UUID of the target player
     * @param option     the name of the option to set
     * @param value      the value to set for the option
     */
    public void setPlayerOptionInDatabase(UUID targetUUID, String option, boolean value) {
        instance.executeAsync(() -> {
            try (Connection connection = instance.getDataSource().getConnection();
                 PreparedStatement stmt = connection.prepareStatement(
                         "UPDATE ams_players SET " + option + " = ? WHERE uuid = ?")) {
                stmt.setBoolean(1, value);
                stmt.setString(2, targetUUID.toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
