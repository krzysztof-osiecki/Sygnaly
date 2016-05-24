package data;


import lombok.Data;

import javax.sound.sampled.*;
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

  public WaveFile(File file) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
    this.loadedWaveFile = AudioSystem.getAudioInputStream(file);
    this.file = file;
    int frameLength = (int) loadedWaveFile.getFrameLength();
    int frameSize = loadedWaveFile.getFormat().getFrameSize();
    waveData = new byte[frameLength * frameSize];
    int result = 0;
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

  public void play(int from, int to) {
    try {
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
      AudioFormat format = audioStream.getFormat();
      DataLine.Info info = new DataLine.Info(Clip.class, format);
      Clip audioClip = (Clip) AudioSystem.getLine(info);
      audioClip.open(audioStream);
      audioClip.setFramePosition(from);
      audioClip.addLineListener(this);
      audioClip.start();
      while (!playCompleted) {
        if (audioClip.getFramePosition() >= to) {
          audioClip.stop();
        }
      }
      audioClip.close();
      audioStream.close();
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
}

