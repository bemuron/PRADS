package bruca.prads.models;

import java.util.Date;

/**
 * Created by Emo on 7/7/2017.
 */

public class FocusChatMessage {
    private String messageText;
    private String messageUser;
    private Long messageTime;

    public FocusChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;

        messageTime = new Date().getTime();
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public Long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Long messageTime) {
        this.messageTime = messageTime;
    }

    public FocusChatMessage(){

    }


}
