package data;


import lombok.Data;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;

@Data
public class WaveFile {
  private final AudioInputStream loadedWaveFile;
  private int selectedChannelNumerOfSamples;
  private byte[] waveData;
  private int[][] samples;

  public WaveFile(AudioInputStream loadedWaveFile) {
    this.loadedWaveFile = loadedWaveFile;
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

  public AudioFormat getFormat() {
    return loadedWaveFile.getFormat();
  }

  public int getNumberOfSamplesForChannel(int channel) {
    return samples[channel].length;
  }

  private int getSixteenBitSample(int high, int low) {
    return (high << 8) + (low & 0x00ff);
  }
}

