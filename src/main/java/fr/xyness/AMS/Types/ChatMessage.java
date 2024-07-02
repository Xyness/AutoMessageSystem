package fr.xyness.AMS.Types;

import java.util.List;

/**
 * Represents a ChatMessage which encapsulates the data and behavior of a Minecraft chat message.
 */
public class ChatMessage {
    
	
    // ***************
    // *  Variables  *
    // ***************
    
	
    /** The name of the chat message. */
    private String name;

    /** The message to be displayed in the chat. */
    private List<String> message;

    /** The frequency at which the chat message appears. */
    private int frequency;
    
    /** The index for messages. */
    private int index;
    
    
    // ******************
    // *  Constructors  *
    // ******************
    
    
    /**
     * Constructs a new ChatMessage.
     *
     * @param name The name of the chat message.
     * @param message The message to be displayed in the chat.
     * @param frequency The frequency at which the chat message appears.
     */
    public ChatMessage(String name, List<String> message, int frequency) {
        this.name = name;
        this.message = message;
        this.frequency = frequency;
        this.index = 0;
    }

    
    // ********************
    // *  Others methods  *
    // ********************
    
    
    /**
     * Gets the name of the chat message.
     *
     * @return The name of the chat message.
     */
    public String getName() { return name; }

    /**
     * Gets the message to be displayed in the chat.
     *
     * @return The message to be displayed in the chat.
     */
    public List<String> getMessage() { return message; }

    /**
     * Gets the frequency at which the chat message appears.
     *
     * @return The frequency at which the chat message appears.
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
