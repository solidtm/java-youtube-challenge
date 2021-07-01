package com.google;

import java.util.ArrayList;

import java.util.List;

/** A class used to represent a Playlist */
public class VideoPlaylist {
    private final String name;
    List<Video> videoList;

    public VideoPlaylist(String playListName){
        this.name = playListName;
        this.videoList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Video> getVideoList() {
        return videoList;
    }
}
