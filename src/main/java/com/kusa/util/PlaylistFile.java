package com.kusa.util;

import com.kusa.playlist.CircularQueuePlaylist;
import com.kusa.playlist.Playlist;
import com.kusa.service.LocalService;
import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

/**
 *
 * Playlist file containing helpful utilites to bridge
 * a playlist file to com.kusa.playlist.Playlist object.
 *
 */
public class PlaylistFile {

  private File playlistFile;
  private String path;
  private Map<String, List<String>> sections;

  public PlaylistFile(String p) throws IOException {
    this.path = p;
    this.sections = new HashMap<>();
    this.playlistFile = new File(p);
    this.generate();
  }

  //generate should be called anytime the FILE is updated.
  private void generate() throws IOException {
    String sectionName = ".root";
    Pattern sectionPattern = Pattern.compile("\\[(.*?)]");

    for (String line : FileUtils.readLines(playlistFile, "UTF-8")) {
      Matcher matcher = sectionPattern.matcher(line);
      //------- \[(.*?)\]
      if (matcher.find()) {
        sectionName = matcher.group().replace("[", "").replace("]", "");
      } else if (!line.isEmpty()) {
        List<String> sectionContents = sections.containsKey(sectionName)
          ? sections.get(sectionName)
          : new ArrayList<>();

        if (line.contains("$")) {
          String nestedSectionName = line.replace("$", "").trim();
          if (sections.get(nestedSectionName) != null) {
            for (String content : sections.get(
              nestedSectionName
            )) sectionContents.add(content);
          }
        } else sectionContents.add(line);
        sections.put(sectionName, sectionContents);
      }
    }
  }

  //returns the latest playlist obj of this playlist file.
  //USES circular queue playlist!
  public Playlist latest() {
    List<String> latestPlaylistContents = new ArrayList<>();
    switch (LocalDateTime.now().getDayOfWeek()) {
      case MONDAY:
        if (sections.get("monday") != null) for (String content : sections.get(
          "monday"
        )) latestPlaylistContents.addAll(resolvePlaylistContent(content));
        break;
      case TUESDAY:
        if (sections.get("tuesday") != null) for (String content : sections.get(
          "tuesday"
        )) latestPlaylistContents.addAll(resolvePlaylistContent(content));
        break;
      case WEDNESDAY:
        if (
          sections.get("wednesday") != null
        ) for (String content : sections.get(
          "wednesday"
        )) latestPlaylistContents.addAll(resolvePlaylistContent(content));
        break;
      case THURSDAY:
        if (
          sections.get("thursday") != null
        ) for (String content : sections.get(
          "thursday"
        )) latestPlaylistContents.addAll(resolvePlaylistContent(content));
        break;
      case FRIDAY:
        if (sections.get("friday") != null) for (String content : sections.get(
          "friday"
        )) latestPlaylistContents.addAll(resolvePlaylistContent(content));
        break;
      case SATURDAY:
        if (
          sections.get("saturday") != null
        ) for (String content : sections.get(
          "saturday"
        )) latestPlaylistContents.addAll(resolvePlaylistContent(content));
        break;
      case SUNDAY:
        if (sections.get("sunday") != null) for (String content : sections.get(
          "sunday"
        )) latestPlaylistContents.addAll(resolvePlaylistContent(content));
        break;
    }

    /*
     *
     * bascially i need to keep track of a "to inlcude" sections. 
     *  + hanlde the special cases such as day of week accordingly.
     *
     * 
     *
     * (loop for adding in all sections that aren't day of the week)
    for(String sectionName : sections.keySet())
    {
      
      if(!sectionName.equals("monday") && !sectionName.equals("tuesday") && !sectionName.equals("wednesday") && !sectionName.equals("thursday") && !sectionName.equals("friday") && !sectionName.equals("saturday") && !sectionName.equals("sunday"))
      {
        for(String content : sections.get(sectionName))
          latestPlaylistContents.addAll(resolvePlaylistContent(content));
      }
    }
    */

    return new CircularQueuePlaylist(latestPlaylistContents);
  }

  //could be a single file, a folder of files, ...
  private List<String> resolvePlaylistContent(String content) {
    String[] parts = content.split("/");
    if (parts[parts.length - 1].contains("*")) {
      String stars = parts[parts.length - 1];
      boolean recursive = stars.equals("**");
      if (
        stars.length() != 1 && stars.length() != 2
      ) return Collections.emptyList();
      String first = recursive
        ? content.substring(0, content.length() - 3)
        : content.substring(0, content.length() - 2);
      Queue<File> folders = new LinkedList<>(List.of(new File(first)));
      List<String> nestedContent = new ArrayList<>();
      while (!folders.isEmpty()) {
        File dir = folders.poll();
        if (!dir.exists()) continue;
        File[] files = dir.listFiles();
        for (File file : files) {
          if (file.isDirectory() && recursive) folders.add(file);
          if (!file.isDirectory()) nestedContent.add(file.getAbsolutePath());
        }
      }
      return nestedContent;
    } else if (LocalService.fileExists(content)) return List.of(content);
    else return Collections.emptyList();
  }

  public int sectionCount() {
    return sections.keySet().size();
  }

  public Set<String> sectionNames() {
    return sections.keySet();
  }
}
