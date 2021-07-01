package com.google;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * A class used to represent a Video Library.
 */
class VideoLibrary {

  private final HashMap<String, Video> videos;

  VideoLibrary() {
    this.videos = new HashMap<>();
    try {
      File file = new File(this.getClass().getResource("/videos.txt").getFile());

      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] split = line.split("\\|"); // ["Funny Dogs", "funny_dogs_video_id", "#dog , #animal"]
        String title = split[0].strip();  //Funny Dogs
        String id = split[1].strip();   //funny_dogs_video_id
        List<String> tags;
        if (split.length > 2) {
          tags = Arrays.stream(split[2].split(",")).map(String::strip).collect(     // ["dog", "animal"]
              Collectors.toList());
        } else {
          tags = new ArrayList<>();
        }
        this.videos.put(id, new Video(title, id, tags));    // [funny_dogs_video_id, Video["Funny Dogs", "funny_dogs_video_id", tags["#dog", "#animal"]]]
      }
    } catch (FileNotFoundException e) {
      System.out.println("Couldn't find videos.txt");
      e.printStackTrace();
    }
  }

  List<Video> getVideos() {
    return new ArrayList<>(this.videos.values());
  }

  /**
   * Get a video by id. Returns null if the video is not found.
   */
  Video getVideo(String videoId) {
    return this.videos.get(videoId);
  }
}
