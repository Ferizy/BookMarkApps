package com.example.bookapps.model.firebase;


public class BookVolume {
    private String volumeID;
    private String readStatus;

    public BookVolume(String volumeID, String readStatus) {
        this.volumeID = volumeID;
        this.readStatus = readStatus;
    }

    public String getVolumeID() {
        return volumeID;
    }

    public void setVolumeID(String volumeID) {
        this.volumeID = volumeID;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

}
