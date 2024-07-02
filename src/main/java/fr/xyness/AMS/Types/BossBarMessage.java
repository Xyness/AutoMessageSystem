package fr.xyness.AMS.Types;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

/**
 * Represents a BossBarMessage which encapsulates the data and behavior of a Minecraft BossBar message.
 */
public class BossBarMessage {
    
	
    // ***************
    // *  Variables  *
    // ***************
    
	
    /** The name of the BossBar message. */
    private String name;
    
    /** The title of the BossBar message. */
    private List<String> title;
    
    /** The color of the BossBar. */
    private List<BarColor> color;
    
    /** The style of the BossBar. */
    private List<BarStyle> style;
    
    /** Whether the BossBar is progressive. */
    private List<Boolean> progressive;
    
    /** Whether the BossBar is progressively reversing. */
    private List<Boolean> progressive_reverse;
    
    /** The display time of the BossBar in seconds. */
    private List<Integer> display_time;
    
    /** The frequency at which the BossBar appears. */
    private int frequency;
    
    /** The index for messages. */
    private int index;
    
    
    // ******************
    // *  Constructors  *
    // ******************
    
    
    /**
     * Constructs a new BossBarMessage.
     *
     * @param name The name of the BossBar message.
     * @param title The title of the BossBar message.
     * @param color The color of the BossBar.
     * @param style The style of the BossBar.
     * @param progressive Whether the BossBar is progressive.
     * @param progressive_reverse Whether the BossBar is progressively reversing.
     * @param display_time The display time of the BossBar in seconds.
     * @param frequency The frequency at which the BossBar appears.
     */
    public BossBarMessage(String name, List<String> title, List<BarColor> color, List<BarStyle> style, List<Boolean> progressive, List<Boolean> progressive_reverse, List<Integer> display_time, int frequency) {
        this.name = name;
        this.title = title;
        this.color = color;
        this.style = style;
        this.progressive = progressive;
        this.progressive_reverse = progressive_reverse;
        this.display_time = display_time;
        this.frequency = frequency;
        this.index = 0;
    }
    
    
    // ********************
    // *  Others methods  *
    // ********************
    
    
    /**
     * Gets the name of the BossBar message.
     *
     * @return The name of the BossBar message.
     */
    public String getName() { return name; }

    /**
     * Gets the title of the BossBar message.
     *
     * @return The title of the BossBar message.
     */
    public List<String> getTitle() { return title; }

    /**
     * Gets the color of the BossBar.
     *
     * @return The color of the BossBar.
     */
    public List<BarColor> getColor() { return color; }

    /**
     * Gets the style of the BossBar.
     *
     * @return The style of the BossBar.
     */
    public List<BarStyle> getStyle() { return style; }

    /**
     * Gets whether the BossBar is progressive.
     *
     * @return A list of Booleans indicating if the BossBar is progressive.
     */
    public List<Boolean> getProgressive() { return progressive; }

    /**
     * Gets whether the BossBar is progressively reversing.
     *
     * @return A list of Booleans indicating if the BossBar is progressively reversing.
     */
    public List<Boolean> getProgressiveReverse() { return progressive_reverse; }

    /**
     * Gets the display time of the BossBar in seconds.
     *
     * @return A list of integers representing the display time of the BossBar in seconds.
     */
    public List<Integer> getDisplayTime() { return display_time; }

    /**
     * Gets the frequency at which the BossBar appears.
     *
     * @return The frequency at which the BossBar appears.
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
