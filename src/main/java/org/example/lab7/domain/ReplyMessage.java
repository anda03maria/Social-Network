package org.example.lab7.domain;

import java.time.LocalDateTime;

public class ReplyMessage extends Message{

    Message reply;
    public ReplyMessage(int id, int idConversation, String dela, String catre, String message, LocalDateTime date, Message reply) {
        super(id, idConversation, dela, catre, message, date);
        this.reply = reply;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }
}
