package treeChat.message;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private MessageType type;
    private String text;
    private String name;
    private UUID uuid;

    public Message(MessageType type, String name, String text, UUID uuid) {
        this.name = name;
        this.text = text;
        this.type = type;
        this.uuid = uuid;
    }

    public MessageType getType() {
        return type;
    }
    public String getName() {
        return name;
    }
    public String getText() {
        return text;
    }
    public UUID getUuid() {
        return uuid;
    }

}
