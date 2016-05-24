package data;

import javax.sound.sampled.*;
import java.io.*;

public class WaveRecorder {

  private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
  private TargetDataLine line;

  public void start(File wavFile) {
    try {
      AudioFormat format = new AudioFormat((float) 16000, 8, 2, true, true);
      DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
      if (!AudioSystem.isLineSupported(info)) {
        System.out.println("Line not supported");
        System.exit(0);
      }
      line = (TargetDataLine) AudioSystem.getLine(info);
      line.open(format);
      line.start();
      AudioInputStream ais = new AudioInputStream(line);
      AudioSystem.write(ais, fileType, wavFile);
    } catch (LineUnavailableException | IOException ex) {
      ex.printStackTrace();
    }
  }

  public void finish() {
    line.stop();
    line.close();
  }

}