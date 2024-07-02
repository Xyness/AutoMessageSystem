package fr.xyness.AMS.Types;

import java.util.List;

/**
 * Represents a TitleMessage which encapsulates the data and behavior of a Minecraft title message.
 */
public class TitleMessage {
    
	
    // ***************
    // *  Variables  *
    // ***************
    
	
    /** The name of the title message. */
    private String name;

    /** The main title to be displayed. */
    private List<String> title;

    /** The subtitle to be displayed. */
    private List<String> subtitle;

    /** The duration the title will be displayed. */
    private List<Integer> display_time;

    /** The time it takes for the title to fade in. */
    private List<Integer> fade_in;

    /** The time it takes for the title to fade out. */
    private List<Integer> fade_out;

    /** The frequency at which the title message appears. */
    private int frequency;
    
    /** The index for messages. */
    private int index;
    
    
    // ******************
    // *  Constructors  *
    // ******************
    
    
    /**
     * Constructs a new TitleMessage.
     *
     * @param name The name of the title message.
     * @param title The main title to be displayed.
     * @param subtitle The subtitle to be displayed.
     * @param display_time The duration the title will be displayed.
     * @param fade_in The time it takes for the title to fade in.
     * @param fade_out The time it takes for the title to fade out.
     * @param frequency The frequency at which the title message appears.
     */
    public TitleMessage(String name, List<String> title, List<String> subtitle, List<Integer> display_time, List<Integer> fade_in, List<Integer> fade_out, int frequency) {
        this.name = name;
        this.title = title;
        this.subtitle = subtitle;
        this.display_time = display_time;
        this.fade_in = fade_in;
        this.fade_out = fade_out;
        this.frequency = frequency;
        this.index = 0;
    }
    
    
    // ********************
    // *  Others methods  *
    // ********************
    
    
    /**
     * Gets the name of the title message.
     *
     * @return The name of the title message.
     */
    public String getName() { return name; }

    /**
     * Gets the main title to be displayed.
     *
     * @return The main title to be displayed.
     */
    public List<String> getTitle() { return title; }

    /**
     * Gets the subtitle to be displayed.
     *
     * @return The subtitle to be displayed.
     */
    public List<String> getSubtitle() { return subtitle; }

    /**
     * Gets the duration the title will be displayed.
     *
     * @return The duration the title will be displayed.
     */
    public List<Integer> getDisplayTime() { return display_time; }

    /**
     * Gets the time it takes for the title to fade in.
     *
     * @return The time it takes for the title to fade in.
     */
    public List<Integer> getFadeIn() { return fade_in; }

    /**
     * Gets the time it takes for the title to fade out.
     *
     * @return The time it takes for the title to fade out.
     */
    public List<Integer> getFadeOut() { return fade_out; }

    /**
     * Gets the frequency at which the title message appears.
     *
     * @return The frequency at which the title message appears.
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
