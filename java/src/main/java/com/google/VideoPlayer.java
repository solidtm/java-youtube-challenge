package com.google;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VideoPlayer {

  private final VideoLibrary videoLibrary;

  List<Video> videos;
  private final HashMap<String, VideoPlaylist> playlistNames = new HashMap<>();
  private Video currentVideo = new Video("", "", new ArrayList<>());
  private Video pausedVideo = new Video("", "", new ArrayList<>());
  private final Video nullVideo = new Video("", "", new ArrayList<>());
  private VideoPlaylist videoPlaylist = new VideoPlaylist("");
  private final Pattern searchPattern;
  private final Pattern tagPattern;
  private List<Video> unFlaggedVids;

  public VideoPlayer() {
    this.videoLibrary = new VideoLibrary();
    videos = videoLibrary.getVideos();
    searchPattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
    tagPattern = Pattern.compile("(#[a-z]+)", Pattern.CASE_INSENSITIVE);
    unFlaggedVids = videoLibrary.getVideos();
  }

  public void numberOfVideos() {
    System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
  }

  public void showAllVideos() {
    List<Video> videos = videoLibrary.getVideos();
    videos.sort(Comparator.comparing(Video::getTitle));
    System.out.println("Here's a list of all available videos:");
    for (Video video : videos) {
      System.out.println(video.getTitle() + " (" + video.getVideoId() + ") ["  +
              String.join(" ", video.getTags()) + "]");
    }
  }

  public void playVideo(String videoId) {
      if (!(videos.contains(videoLibrary.getVideo(videoId)))) {
        System.out.println("Cannot play video: Video does not exist");
      }
      else if(videos.contains(videoLibrary.getVideo(videoId)) && currentVideo.getTitle().isEmpty()){
        System.out.println("Playing video: " + videoLibrary.getVideo(videoId).getTitle());
        currentVideo = videoLibrary.getVideo(videoId);
        pausedVideo = nullVideo;
      }
      else if(isCurrentlyPlaying(videoId)){
        System.out.println("Stopping video: " + currentVideo.getTitle());
        System.out.println("Playing video: " + videoLibrary.getVideo(videoId).getTitle());
        pausedVideo = nullVideo;
      }
      else if (!isCurrentlyPlaying(videoId) && !(currentVideo.getTitle().isEmpty())){
        System.out.println("Stopping video: " + currentVideo.getTitle());
        System.out.println("Playing video: " + videoLibrary.getVideo(videoId).getTitle());
        pausedVideo = nullVideo;
      }else if (isPaused(pausedVideo)){
        pausedVideo = new Video("", "", new ArrayList<>());
        System.out.println("Stopping video: " + pausedVideo.getTitle());
        System.out.println("Playing video: " + videoLibrary.getVideo(videoId).getTitle());
        currentVideo = nullVideo;
      }
  }

  private boolean isCurrentlyPlaying(String videoId) {
    return currentVideo.equals(videoLibrary.getVideo(videoId));
  }

  public void stopVideo() {
    if (currentVideo.getTitle().isEmpty()){
      System.out.println("Cannot stop video: No video is currently playing");
    }else {
      System.out.println("Stopping video: " + currentVideo.getTitle());
      currentVideo = nullVideo;
    }
  }

  public void playRandomVideo() {
    Random rand = new Random();
    Video randomVideo;
    if (videos.isEmpty()){
      System.out.println("No videos available");
    }else if (currentVideo.getTitle().isEmpty()){
      randomVideo = videos.get(rand.nextInt(videoLibrary.getVideos().size()));
      System.out.println("Playing video: " + randomVideo.getTitle());
      currentVideo = randomVideo;
      pausedVideo = nullVideo;
    }else if (!(currentVideo.getTitle().isEmpty())){
      randomVideo = videos.get(rand.nextInt(videoLibrary.getVideos().size()));
      System.out.println("Stopping video: " + currentVideo.getTitle());
      System.out.println("Playing video: " + randomVideo.getTitle());
      currentVideo = randomVideo;
      pausedVideo = nullVideo;
    }
  }

  public void pauseVideo() {
    if (currentVideo.getTitle().isEmpty()){
      System.out.println("Cannot pause video: No video is currently playing");
    }else if (!(currentVideo.getTitle().isEmpty()) && pausedVideo.getTitle().isEmpty()){
      pausedVideo = currentVideo;
      System.out.println("Pausing video: " + pausedVideo.getTitle());
    } else if (isPaused(pausedVideo)){
      System.out.println("Video already paused: " + currentVideo.getTitle());
    }
  }

  private boolean isPaused(Video pausedVideo) {
    return currentVideo.equals(pausedVideo);
  }


  public void continueVideo() {
    if (isPaused(pausedVideo)){
      System.out.println("Continuing video: " + pausedVideo.getTitle());
      pausedVideo = nullVideo;
    } else if (!isPaused(pausedVideo) && !currentVideo.getTitle().isEmpty()){
      System.out.println("Cannot continue video: Video is not paused");
    } else if(currentVideo.getTitle().isEmpty()){
      System.out.println("Cannot continue video: No video is currently playing");
    }
  }

  public void showPlaying() {
    if (isPaused(pausedVideo)){
      System.out.println("Currently playing: " + pausedVideo.getTitle() + " (" + pausedVideo.getVideoId() + ") [" +
              String.join(" ", pausedVideo.getTags()) + "] - PAUSED");
    }else if (!isPaused(pausedVideo) && !currentVideo.getTitle().isEmpty()){
      System.out.println("Currently playing: " + currentVideo.getTitle() + " (" + currentVideo.getVideoId() + ") [" +
              String.join(" ", currentVideo.getTags()) + "]");
    }else if ((!isPaused(pausedVideo) && currentVideo.getTitle().isEmpty())){
      System.out.println("No video is currently playing");
    }
  }

  public void createPlaylist(String playlistName) {
    if(playlistNames.containsKey(playlistName.trim().toUpperCase())){
      System.out.println("Cannot create playlist: A playlist with the same name already exists");
    }else {
      videoPlaylist = new VideoPlaylist(playlistName);
      playlistNames.put(playlistName.toUpperCase(), videoPlaylist);
      System.out.println("Successfully created new playlist: " + playlistName);
    }
  }


  public void addVideoToPlaylist(String playlistName, String videoId) {
    if (!playlistNames.containsKey(playlistName.toUpperCase())){
      System.out.println("Cannot add video to " + playlistName + ": Playlist does not exist");
    }else if (!videos.contains(videoLibrary.getVideo(videoId))){
      System.out.println("Cannot add video to " + playlistName + ": Video does not exist");
    }else if (playlistNames.get(playlistName.toUpperCase()).getVideoList().contains(videoLibrary.getVideo(videoId))){
      System.out.println("Cannot add video to " + playlistName + ": Video already added");
    }else{
      videoPlaylist.getVideoList().add(videoLibrary.getVideo(videoId));
      playlistNames.put(playlistName.toUpperCase(), videoPlaylist);
      System.out.println("Added video to " + playlistName + ": "
      + videoLibrary.getVideo(videoId).getTitle());
    }
  }

  public void showAllPlaylists() {
    if (playlistNames.isEmpty()){
      System.out.println("No playlists exist yet");
    }else {
      System.out.println("Showing all playlists:");
      List<String> namePlaylist = new ArrayList<>(playlistNames.keySet());
      namePlaylist.sort(Comparator.naturalOrder());
      for (String val : namePlaylist){
        System.out.println(val.toLowerCase());
      }
    }
  }

  public void showPlaylist(String playlistName) {
    if (!playlistNames.containsKey(playlistName.toUpperCase())){
      System.out.println("Cannot show playlist " + playlistName + ": Playlist does not exist");
    }else if (playlistNames.containsKey(playlistName.toUpperCase())
            && playlistNames.get(playlistName.toUpperCase()).getVideoList().isEmpty()){
      System.out.println("Showing playlist: " + playlistName);
      System.out.println("No videos here yet");
    }else {
          List<Video> videosList = playlistNames.get(playlistName.toUpperCase()).getVideoList();
          System.out.println("Showing playlist: " + playlistName);
          for (Video video : videosList){
            System.out.println(video.getTitle() + " (" + video.getVideoId() + ") ["  +
                    String.join(" ", video.getTags()) + "]");
          }
    }
  }

  public void removeFromPlaylist(String playlistName, String videoId) {
    String playListNameCaps = playlistName.toUpperCase();
    if (!playlistNames.containsKey(playListNameCaps)){
      System.out.println("Cannot remove video from " + playlistName + ": Playlist does not exist");
    }else if (videoLibrary.getVideo(videoId) == null){
      System.out.println("Cannot remove video from " + playlistName + ": Video does not exist");
    }else if (playlistNames.containsKey(playListNameCaps) &&
            !playlistNames.get(playListNameCaps).getVideoList().contains(videoLibrary.getVideo(videoId))){
      System.out.println("Cannot remove video from " + playlistName + ": Video is not in playlist");
    }else{
      playlistNames.get(playListNameCaps).getVideoList().remove(videoLibrary.getVideo(videoId));
      System.out.println("Removed video from " + playlistName + ": " + videoLibrary.getVideo(videoId).getTitle());
    }
  }

  public void clearPlaylist(String playlistName) {
    if (!playlistNames.containsKey(playlistName.toUpperCase())){
      System.out.println("Cannot clear playlist " + playlistName + ": Playlist does not exist");
    }else {
      playlistNames.get(playlistName.toUpperCase()).getVideoList().clear();
      System.out.println("Successfully removed all videos from " + playlistName);
    }
  }

  public void deletePlaylist(String playlistName) {
    if (!playlistNames.containsKey(playlistName.toUpperCase())){
      System.out.println("Cannot delete playlist " + playlistName + ": Playlist does not exist");
    }else{
      playlistNames.remove(playlistName.toUpperCase());
      System.out.println("Deleted playlist: " + playlistName);
    }
  }

  public void searchVideos(String searchTerm) {
    Matcher m = searchPattern.matcher(searchTerm);
    if (m.find())
      System.out.println("No search results for " + searchTerm);
    else {
      List<Video> searchResult = unFlaggedVids.stream()
              .filter(a ->
                      Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE)
                              .matcher(a.getTitle()).find())
              .collect(Collectors.toList());
      if (searchResult.isEmpty())
        System.out.println("No search results for " + searchTerm);
      else {
        System.out.println("Here are the results for " + searchTerm + ":");
        displaySearchResult(searchResult);
      }
    }
  }

  private void displaySearchResult(List<Video> videos) {
    Scanner readIn = new Scanner(System.in);
    videos.sort(Comparator.comparing(Video::getTitle));

    for (int i = 0; i < videos.size(); i++) {
      System.out.println("\t" + (i + 1) + ") " + videos.get(i).getTitle() + " (" + videos.get(i).getVideoId() + ") ["  +
              String.join(" ", videos.get(i).getTags()) + "]");
    }
    System.out.println("Would you like to play any of the above? If yes, specify the number of the video.\n" +
            "If your answer is not a valid number, we will assume it's a no.");
    try {
      int response = readIn.nextInt();
      if (response > 0 && response <= videos.size())
        playVideo(videos.get(response - 1).getVideoId());
    } catch (InputMismatchException ignored) {
    }
  }

  public void searchVideosWithTag(String videoTag) {
    Matcher m = tagPattern.matcher(videoTag);
    if(unFlaggedVids.isEmpty()) {
      System.out.println("No videos available");
    }
    else if (m.matches()) {
      List<Video> searchResult = unFlaggedVids.stream()
              .filter(a -> {
                List<String> tags = a.getTags();
                for (String tag : tags)
                  if (Pattern.compile(Pattern.quote(videoTag), Pattern.CASE_INSENSITIVE)
                          .matcher(tag).find())
                    return true;
                return false;
              })
              .collect(Collectors.toList());
      if (searchResult.isEmpty())
        System.out.println("No search results for " + videoTag);
      else {
        System.out.println("Here are the results for " + videoTag + ":");
        displaySearchResult(searchResult);
      }
    } else
      System.out.println("No search results for " + videoTag);
  }

  public void flagVideo(String videoId) {
    System.out.println("flagVideo needs implementation");
  }

  public void flagVideo(String videoId, String reason) {
    System.out.println("flagVideo needs implementation");
  }

  public void allowVideo(String videoId) {
    System.out.println("allowVideo needs implementation");
  }
}