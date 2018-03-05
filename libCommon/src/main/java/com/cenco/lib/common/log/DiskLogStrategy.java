package com.cenco.lib.common.log;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.cenco.lib.common.FileUtils;
import com.orhanobut.logger.LogStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Abstract class that takes care of background threading the file log operation on Android.
 * implementing classes are free to directly perform I/O operations there.
 */
public class DiskLogStrategy implements LogStrategy {

  private final Handler handler;

  public DiskLogStrategy(Handler handler) {
    this.handler = handler;
  }

  @Override public void log(int level, String tag, String message) {
    // do nothing on the calling thread, simply pass the tag/msg to the background thread
    handler.sendMessage(handler.obtainMessage(level, message));
  }

  public static class WriteHandler extends Handler {

    private final String folder;
    private final int maxFileSize;

    public WriteHandler(Looper looper, String folder, int maxFileSize) {
      super(looper);
      this.folder = folder;
      this.maxFileSize = maxFileSize;
    }

    public WriteHandler(Looper looper, String folder) {
      super(looper);
      this.folder = folder;
      this.maxFileSize = 500 * 1024;
    }

    public WriteHandler(Looper looper) {
      super(looper);
      this.folder = FileUtils.getDefaultLogFilePath();
      this.maxFileSize = 500 * 1024;
    }

    @SuppressWarnings("checkstyle:emptyblock")
    @Override public void handleMessage(Message msg) {
      String content = (String) msg.obj;

      FileWriter fileWriter = null;
      File logFile = getLogFile(folder, "logs");

      try {
        fileWriter = new FileWriter(logFile, true);

        writeLog(fileWriter, content);

        fileWriter.flush();
        fileWriter.close();
      } catch (IOException e) {
        if (fileWriter != null) {
          try {
            fileWriter.flush();
            fileWriter.close();
          } catch (IOException e1) { /* fail silently */ }
        }
      }
    }

    /**
     * This is always called on a single background thread.
     * Implementing classes must ONLY write to the fileWriter and nothing more.
     * The abstract class takes care of everything else including close the stream and catching IOException
     *
     * @param fileWriter an instance of FileWriter already initialised to the correct file
     */
    private void writeLog(FileWriter fileWriter, String content) throws IOException {
      fileWriter.append(content);
    }

    private File getLogFile(String folderName, String fileName) {

      File folder = new File(folderName);
      if (!folder.exists()) {
        //TODO: What if folder is not created, what happens then?
        folder.mkdirs();
      }

      int newFileCount = 0;
      File newFile;
      File existingFile = null;

      newFile = new File(folder, String.format("%s_%s.txt", fileName, newFileCount));
      while (newFile.exists()) {
        existingFile = newFile;
        newFileCount++;
        newFile = new File(folder, String.format("%s_%s.txt", fileName, newFileCount));
      }

      if (existingFile != null) {
        if (existingFile.length() >= maxFileSize) {
          return newFile;
        }
        return existingFile;
      }

      return newFile;
    }
  }
}