package com.kusa.util;

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
 * Playlist file containing helpful utilites to bridge
 * a playlist file to com.kusa.playlist.Playlist object.
 */
public class PlaylistFile {

  private File playlistFile;
  private String path;
  private Map<String, List<String>> sections;

  public PlaylistFile(File config) throws IOException {
    this.path = config.getAbsolutePath();
    this.sections = new HashMap<>();
    this.playlistFile = config;
    this.generate();
  }

  public PlaylistFile(String p) throws IOException {
    this(new File(p));
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

  /**
   * Will attempt to read playlist from file and update
   * the internal structures.
   *
   * by succesful we mean
   * doesn't include any checks to see if there
   * were any changes or not. it will return true
   * if there was no exception and generate was executed.
   *
   * we might want to handle the exception later..
   *
   * @return true if the reload was succesful.
   */
  public boolean reload() {
    try {
      sections.clear();
      generate();
      return true;
    } catch (Exception ex) {
      System.out.printf("[ERROR] %s : %s\n", ex, ex.getMessage());
    }
    return false;
  }

  /**
   * Returns all the tracks from a specific section.
   *
   * @param section name
   * @return list of mrls in the section or empty.
   */
  public Playlist playlistFromSection(String section) {
    List<String> ret = new ArrayList<>();
    if (sections.get(section) != null) for (String content : sections.get(
      section
    )) ret.addAll(resolvePlaylistContent(content));
    return Playlist.fromList(ret);
  }

  public int sectionCount() {
    return sections.keySet().size();
  }

  public Set<String> sectionNames() {
    return sections.keySet();
  }

  /*
   * this method is part of the parsing process of .playlist files.
   * when reading each line under a section it could represent any of
   * the following...
   *  - single file
   *  - a folder of files
   *  - a folder including all nested folders
   *  - a direct link (http/https)
   *
   * we resolve that here to turn those representations into
   * valid mrls. (commonly absolute local file paths).
   *
   * ideally we want to make sure the video player could
   * play the strings we return here.
   */
  private List<String> resolvePlaylistContent(String content) {
    if (content.contains("http")) return List.of(content);
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
}
