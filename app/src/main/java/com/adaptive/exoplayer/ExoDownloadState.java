package com.adaptive.exoplayer;

/**
 * Created by Mayur Solanki (mayursolanki120@gmail.com) on 30/05/20, 12:25 PM.
 */
public enum ExoDownloadState {

    DOWNLOAD_START("Start Download"),
    DOWNLOAD_PAUSE("Pause Download"),
    DOWNLOAD_RESUME("Resume Download"),
    DOWNLOAD_COMPLETED("Downloaded"),
    DOWNLOAD_RETRY("Retry Download"),
    DOWNLOAD_QUEUE("Download In Queue");



    private String value;

    public String getValue() {
        return value;
    }
    private ExoDownloadState(String value) {
        this.value = value;
    }

    }
