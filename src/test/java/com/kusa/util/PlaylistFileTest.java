package com.kusa.util;

import static org.junit.jupiter.api.Assertions.*;

import com.kusa.playlist.Playlist;
import com.kusa.util.PlaylistFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

/**
 * Basic test for the PlaylistFile object.
 *
 * a playlist file is the .playlist configuration
 * that is created when the app first runs.
 *
 * this test class needs more 'sad' tests.
 *
 * temporary files and directorys are created throughout
 * these test cases. delete on exit is invoked but if
 * anything check your systems tmp folder and delete from
 * there if the files don't automatically delete.
 */
public class PlaylistFileTest {

  private File createTempConfig(List<String> content) throws IOException {
    File file = File.createTempFile("test-", ".playlist");
    Files.write(Paths.get(file.getAbsolutePath()), content);
    file.deleteOnExit();
    return file;
  }

  /**
   * Returns a playlist config with all the days of the week pointing
   * to the same default section.
   *
   * uses $default to import a section.
   */
  private static List<String> allDefaults() {
    return List.of(
      "[default]",
      "https//something-fake",
      "",
      String.format("[monday]\n%s", "$default"),
      String.format("[tuesday]\n%s", "$default"),
      String.format("[wednesday]\n%s", "$default"),
      String.format("[thursday]\n%s", "$default"),
      String.format("[friday]\n%s", "$default"),
      String.format("[saturday]\n%s", "$default"),
      String.format("[sunday]\n%s", "$default")
    );
  }

  private static List<String> empty() {
    return List.of(
      "[monday]\n",
      "[tuesday]\n",
      "[wednesday]\n",
      "[thursday]\n",
      "[friday]\n",
      "[saturday]\n",
      "[sunday]\n"
    );
  }

  @Test
  public void testPlaylistFileFromSection() throws IOException {
    File testConfig = createTempConfig(allDefaults());
    PlaylistFile playlistFile = new PlaylistFile(testConfig);

    //testing grabing section tracks.
    List<String> sectionTracks = playlistFile
      .playlistFromSection("wednesday")
      .trackList();
    assertEquals(sectionTracks.size(), 1);
    assertEquals(sectionTracks.get(0), "https//something-fake");
  }

  @Test
  public void testPlaylistFileSections() throws IOException {
    File testConfig = createTempConfig(allDefaults());
    PlaylistFile playlistFile = new PlaylistFile(testConfig);

    //section count and names are accurate.
    assertEquals(playlistFile.sectionCount(), 8);
    assertEquals(playlistFile.sectionNames().contains("default"), true);
    assertEquals(playlistFile.sectionNames().contains("monday"), true);
    assertEquals(playlistFile.sectionNames().contains("tuesday"), true);
    assertEquals(playlistFile.sectionNames().contains("wednesday"), true);
    assertEquals(playlistFile.sectionNames().contains("thursday"), true);
    assertEquals(playlistFile.sectionNames().contains("friday"), true);
    assertEquals(playlistFile.sectionNames().contains("saturday"), true);
    assertEquals(playlistFile.sectionNames().contains("sunday"), true);
  }

  @Test
  public void testPlaylistFileResolveLinks() throws IOException {
    List<String> content = List.of(
      "[test]",
      "https://somestream.mp4",
      "https://somestream2.mp4",
      "https://somestream3.mp4"
    );
    File testConfig = createTempConfig(content);
    PlaylistFile playlistFile = new PlaylistFile(testConfig);

    //testing grabing section tracks.
    List<String> sectionTracks = playlistFile
      .playlistFromSection("test")
      .trackList();
    assertEquals(sectionTracks.size(), 3);
    assertEquals(
      sectionTracks,
      List.of(
        "https://somestream.mp4",
        "https://somestream2.mp4",
        "https://somestream3.mp4"
      )
    );
  }

  /**
   * This tests the * notation in a playlist config file.
   *
   * when writing a .playlist file you can specify a folder
   * followed by a '*' when you only put ONE '*' it will
   * only include all the files in THAT folder not any nested folders.
   */
  @Test
  public void testPlaylistFileResolveFolders() throws IOException {
    Path folderPath = Files.createTempDirectory("testPF");
    Path folderPathNested = Files.createTempDirectory(
      folderPath,
      "testPFNested"
    );
    File folder = folderPath.toFile();
    File folderNested = folderPathNested.toFile();
    File innerVid1 = File.createTempFile("test-fake-video1", ".mp4", folder);
    File innerVid2 = File.createTempFile("test-fake-video2", ".mp4", folder);
    File nestedVid = File.createTempFile(
      "test-fake-video-nested",
      ".mp4",
      folderNested
    );

    innerVid1.deleteOnExit();
    innerVid2.deleteOnExit();
    nestedVid.deleteOnExit();
    folderNested.deleteOnExit();
    folder.deleteOnExit();

    List<String> content = List.of(
      "[test]",
      String.format("%s/*", folderPath.toString())
    );

    File testConfig = createTempConfig(content);
    PlaylistFile playlistFile = new PlaylistFile(testConfig);

    //should be 2 since we aren't supposed to grab the nested one.
    List<String> sectionTracks = playlistFile
      .playlistFromSection("test")
      .trackList();
    List<String> expectedTracks = List.of(
      innerVid1.getAbsolutePath(),
      innerVid2.getAbsolutePath()
    );
    assertEquals(sectionTracks.size(), 2);
    assertEquals(sectionTracks.containsAll(expectedTracks), true);
  }

  /**
   * This tests the ** notation in a playlist config file.
   *
   * when writing a .playlist file you can specify a folder
   * followed by a '**' when you only put TWO '*' it will
   * include ALL the files in THAT folder and any NESTED folders.
   */
  @Test
  public void testPlaylistFileResolveNestedFolders() throws IOException {
    Path folderPath = Files.createTempDirectory("testPF");
    Path folderPathNested = Files.createTempDirectory(
      folderPath,
      "testPFNested"
    );
    File folder = folderPath.toFile();
    File folderNested = folderPathNested.toFile();
    File innerVid1 = File.createTempFile("test-fake-video1", ".mp4", folder);
    File innerVid2 = File.createTempFile("test-fake-video2", ".mp4", folder);
    File nestedVid = File.createTempFile(
      "test-fake-video-nested",
      ".mp4",
      folderNested
    );

    innerVid1.deleteOnExit();
    innerVid2.deleteOnExit();
    nestedVid.deleteOnExit();
    folderNested.deleteOnExit();
    folder.deleteOnExit();

    List<String> content = List.of(
      "[test]",
      String.format("%s/**", folderPath.toString())
    );

    File testConfig = createTempConfig(content);
    PlaylistFile playlistFile = new PlaylistFile(testConfig);

    //should be all of the tracks since we used double star.
    List<String> sectionTracks = playlistFile
      .playlistFromSection("test")
      .trackList();
    List<String> expectedTracks = List.of(
      innerVid1.getAbsolutePath(),
      innerVid2.getAbsolutePath(),
      nestedVid.getAbsolutePath()
    );
    assertEquals(sectionTracks.size(), 3);
    assertEquals(sectionTracks.containsAll(expectedTracks), true);
  }

  /**
   * This test makes sure that a playlist file can
   * still get the latest changes from it's config
   * as long as reload is called.
   */
  @Test
  public void testPlaylistFileReload() throws IOException {
    List<String> initialContent = List.of("[default]", "https://sometrack.mp4");
    File testConfig = createTempConfig(initialContent);
    PlaylistFile playlistFile = new PlaylistFile(testConfig);
    List<String> tracks = playlistFile
      .playlistFromSection("default")
      .trackList();
    assertEquals(playlistFile.sectionCount(), 1);
    assertEquals(tracks.size(), 1);
    assertEquals(tracks, List.of("https://sometrack.mp4"));

    //write another track to the config.
    FileUtils.writeLines(
      testConfig,
      List.of("https://someOTHERtrack.mp4"),
      true
    );

    //CALL RELOAD
    playlistFile.reload();

    tracks = playlistFile.playlistFromSection("default").trackList();
    assertEquals(tracks.size(), 2);
    assertEquals(
      tracks,
      List.of("https://sometrack.mp4", "https://someOTHERtrack.mp4")
    );
  }
}
