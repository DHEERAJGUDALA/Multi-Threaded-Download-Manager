package com.downloadmanager;

public enum DownloadStatus {
    WAITING("Waiting"),
    DOWNLOADING("Downloading"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled");

    private final String displayName;
    DownloadStatus(String displayName){
        this.displayName=displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}