package com.adaptive.exoplayer;

/**
 * Created by Mayur Solanki (mayursolanki120@gmail.com) on 22/06/20, 12:05 AM.
 */
public class VideoModel {

    private String videoId;
    private String videoName;
    private String videoUrl;
    private long videoDuration;


    public VideoModel(String videoId, String videoName, String videoUrl, long videoDuration) {
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoUrl = videoUrl;
        this.videoDuration = videoDuration;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public long getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(long videoDuration) {
        this.videoDuration = videoDuration;
    }
}
