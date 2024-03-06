package org.example.lab7.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.random.RandomGenerator;

public class Message extends Entity<Integer> {

    private int idConversation;
    private int id;
    private String dela;
    private String catre;
    private String message;
    private LocalDateTime date;

    private Message reply;

    public Message(int id, int idConversation, String dela, String catre, String message, LocalDateTime date) {
        super.setId(id);
        this.idConversation = idConversation;
        this.dela = dela;
        this.catre = catre;
        this.message = message;
        this.date = date;
    }

    public Message(int idConversation, String dela, String catre, String message,LocalDateTime date) {
        this.idConversation = idConversation;
        this.dela = dela;
        this.catre = catre;
        this.message = message;
        this.date = date;
    }

    public int getIdConversation() {
        return idConversation;
    }

    public void setIdConversation(int idConversation) {
        this.idConversation = idConversation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFrom() {
        return dela;
    }

    public void setFrom(String from) {
        this.dela = from;
    }

    public String getTo() {
        return catre;
    }

    public void setTo(String to) {
        this.catre = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", from=" + dela +
                ", to=" + catre +
                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}
