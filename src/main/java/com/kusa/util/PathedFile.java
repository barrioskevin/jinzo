package com.kusa.util;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

/**
 *
 * Google drive file with an associated path.
 * we use this to easily keep paths between drive and
 * file system in sync.
 */
public class PathedFile {

  private File file;
  private String path;

  public PathedFile(File f, String p) {
    file = f;
    path = p;
  }

  public String path() {
    return path;
  }

  public File file() {
    return file;
  }
}
