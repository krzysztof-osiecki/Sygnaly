package data;


import lombok.Data;
import override.SelectionMarker;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

@Data
public class WaveFile implements LineListener {
  private final AudioInputStream loadedWaveFile;
  private final File file;
  private int selectedChannelNumerOfSamples;
  private byte[] waveData;
  private int[][] samples;
  private boolean playCompleted;
  private Clip audioClip;
  private int to;
  private SelectionMarker marker;

  public WaveFile(File file) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
    this.loadedWaveFile = AudioSystem.getAudioInputStream(file);
    this.file = file;
    int frameLength = (int) loadedWaveFile.getFrameLength();
    int frameSize = loadedWaveFile.getFormat().getFrameSize();
    waveData = new byte[frameLength * frameSize];
    int result;
    try {
      result = loadedWaveFile.read(waveData);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot read wave data");
    }
    if (result == 0) throw new IllegalStateException("Couldn't read data from wav file");
    int channels = loadedWaveFile.getFormat().getChannels();
    samples = new int[channels][(int) loadedWaveFile.getFrameLength()];
    int sampleIndex = 0;
    for (int t = 0; t < waveData.length; ) {
      for (int channel = 0; channel < channels; channel++) {
        int low = (int) waveData[t];
        t++;
        int high = (int) waveData[t];
        t++;
        int sample = getSixteenBitSample(high, low);
        samples[channel][sampleIndex] = sample;
      }
      sampleIndex++;
    }
  }

  public void play(int from, int to, SelectionMarker marker) {
    this.to = to;
    this.marker = marker;
    try {
      marker.addValueMarker(from);
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
      AudioFormat format = audioStream.getFormat();
      DataLine.Info info = new DataLine.Info(Clip.class, format);
      audioClip = (Clip) AudioSystem.getLine(info);
      audioClip.open(audioStream);
      audioClip.setFramePosition(from);
      audioClip.addLineListener(this);
      audioClip.start();
      monitorPlay();
      marker.removeValueMarker();
      playCompleted = false;
    } catch (Exception e) {
      System.out.printf("Something went wrong during audio play");
    }
  }

  public AudioFormat getFormat() {
    return loadedWaveFile.getFormat();
  }

  public int getNumberOfSamples() {
    return samples[0].length;
  }

  private int getSixteenBitSample(int high, int low) {
    return (high << 8) + (low & 0x00ff);
  }

  @Override
  public void update(LineEvent event) {
    LineEvent.Type type = event.getType();
    if (type == LineEvent.Type.STOP) {
      playCompleted = true;
    }
  }

  public void pause() {
    int framePosition = audioClip.getFramePosition();
    if (audioClip.isActive()) {
      audioClip.stop();
    } else {
      new Thread(() -> {
        audioClip.setFramePosition(framePosition);
        audioClip.start();
        marker.addValueMarker(audioClip.getFramePosition());
        monitorPlay();
        marker.removeValueMarker();
        playCompleted = false;
      }).start();
    }
  }

  private void monitorPlay() {
    while (!playCompleted) {
      int newFramePosition = audioClip.getFramePosition();
      marker.refreshValueMarker(newFramePosition);
      if (newFramePosition >= to) {
        audioClip.stop();
      }
    }
  }
}

