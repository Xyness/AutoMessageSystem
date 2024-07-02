package fr.xyness.AMS.Types;

import java.util.List;

/**
 * Represents an ActionBarMessage which encapsulates the data and behavior of a Minecraft ActionBar message.
 */
public class ActionBarMessage {
    
	
    // ***************
    // *  Variables  *
    // ***************
    
	
    /** The name of the ActionBar message. */
    private String name;

    /** The message to be displayed in the ActionBar. */
    private List<String> message;

    /** The display time of the ActionBar message in seconds. */
    private List<Integer> display_time;

    /** The frequency at which the ActionBar message appears. */
    private int frequency;
    
    /** The index for messages. */
    private int index;

    
    // ******************
    // *  Constructors  *
    // ******************
    
    
    /**
     * Constructs a new ActionBarMessage.
     *
     * @param name The name of the ActionBar message.
     * @param message The message to be displayed in the ActionBar.
     * @param display_time The display time of the ActionBar message in seconds.
     * @param frequency The frequency at which the ActionBar message appears.
     */
    public ActionBarMessage(String name, List<String> message, List<Integer> display_time, int frequency) {
        this.name = name;
        this.message = message;
        this.display_time = display_time;
        this.frequency = frequency;
        this.index = 0;
    }
    
    
    // ********************
    // *  Other methods  *
    // ********************
    
    
    /**
     * Gets the name of the ActionBar message.
     *
     * @return The name of the ActionBar message.
     */
    public String getName() { return name; }

    /**
     * Gets the message to be displayed in the ActionBar.
     *
     * @return The message to be displayed in the ActionBar.
     */
    public List<String> getMessage() { return message; }

    /**
     * Gets the display time of the ActionBar message in seconds.
     *
     * @return The display time of the ActionBar message in seconds.
     */
    public List<Integer> getDisplayTime() { return display_time; }

    /**
     * Gets the frequency at which the ActionBar message appears.
     *
     * @return The frequency at which the ActionBar message appears.
     */
    public int getFrequency() { return frequency; }
    
    /**
     * Gets the index to get the message.
     * 
     * @return The index to get the message.
     */
    public int getIndex() { return index; }
    
    /**
     * Sets the index.
     *
     * @param index The index to set.
     */
    public void setIndex(int index) { this.index = index; }
}
