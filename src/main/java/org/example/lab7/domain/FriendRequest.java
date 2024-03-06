package org.example.lab7.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FriendRequest extends Entity<Long>{

    private String dela, catre;
    private FriendRequestStatus status;
    /**
     * Constructor, keeps the IDs sorted alphabetically
     * @param dela, String, the first ID
     * @param catre, String, the second ID
     * @param status, FriendRequestStatus
     */
    public FriendRequest(String dela, String catre, FriendRequestStatus status) {
        this.dela = dela;
        this.catre = catre;
        this.status = status;
    }

    public FriendRequest(Long id, String dela, String catre, FriendRequestStatus status) {
        this.setId(id);
        this.dela = dela;
        this.catre = catre;
        this.status = status;
    }



    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }

    public String getDela() {
        return dela;
    }

    public void setDela(String dela) {
        this.dela = dela;
    }

    public String getCatre() {
        return catre;
    }

    public void setCatre(String catre) {
        this.catre = catre;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "dela='" + dela + '\'' +
                ", catre='" + catre + '\'' +
                ", status=" + status +
                '}';
    }
}
