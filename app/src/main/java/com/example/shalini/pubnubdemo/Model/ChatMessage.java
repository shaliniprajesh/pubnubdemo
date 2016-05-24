package com.example.shalini.pubnubdemo.Model;

/**
 * Created by shalini on 18/5/16.
 */
public class ChatMessage {
    private String message;
    private boolean isPublisher;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isPublisher() {
        return isPublisher;
    }

    public void setPublisher(boolean publisher) {
        isPublisher = publisher;
    }

    @Override
    public boolean equals(Object o) {
        ChatMessage chatMessage = (ChatMessage)o;
        if((this.message).equals(chatMessage.getMessage()))
            return true;
        return false;
    }
}
