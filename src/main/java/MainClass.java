import data.EmgFile;
import data.WaveFile;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import override.MyScrollableValueAxis;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static utils.DataProcessingUtil.parseData;

public class MainClass extends JFrame {

  private static final String GRAPH_HEADER = "Oscylogram";
  private EmgFile loadedEmgFile;
  private ChartPanel chartPanel;
  private JComboBox<String> comboBox;
  private WaveFile loadedWaveFile;

  public static void main(String[] args) {
    new MainClass();
  }

  private MainClass() {
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("Sygnaly - Krzysztof Osiecki");
    createMenu();
    creteView();
    createLayouts();
    setVisible(true);
    setSize(1400, 800);
  }

  private void creteView() {
    chartPanel = new ChartPanel(null);
    chartPanel.setPreferredSize(new java.awt.Dimension(700, 500));
    comboBox = new JComboBox<>();
    comboBox.addItemListener(comboItemListener());
  }

  private ItemListener comboItemListener() {
    return ae -> {
      if (loadedWaveFile != null) {
        repaintWaveGraph();
      } else if (loadedEmgFile != null) {
        repaintEmgGraph();
      }
      repaint();
    };
  }

  private void createLayouts() {
    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createSequentialGroup()
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                    .addComponent(chartPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                )
                .addGroup(layout.createParallelGroup()
                    .addComponent(chartPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                )
            )
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(chartPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    )))

    );
  }

  private void createMenu() {
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    //submenu file
    JMenu menu = new JMenu("File");
    menuBar.add(menu);
    //item otwierania pliku
    menuItem(menu, "Open", openFileListener());
    menu.addSeparator();
    //item wyjscia z aplikacji
    menuItem(menu, "Exit", ae -> System.exit(0));

    JMenu menu2 = new JMenu("Current");
    menuBar.add(menu2);
    //item otwierania pliku
    menuItem(menu2, "Show header", showHeaderInfoListener());
  }

  private void menuItem(JMenu menu, String name, ActionListener actionListener) {
    JMenuItem mitem = new JMenuItem(name);
    mitem.addActionListener(actionListener);
    menu.add(mitem);
  }

  private ActionListener showHeaderInfoListener() {
    return ae -> {
      if (loadedEmgFile != null) {
        new HeaderInfoForm(this.loadedEmgFile.getHeader());
      } else if (loadedWaveFile != null) {
        new HeaderInfoForm(this.loadedWaveFile.getFormat());
      } else {
        //// TODO: 2016-05-02 pokaz dialog z info ze nie wybrano pliku
      }
    };
  }

  private ActionListener openFileListener() {
    return ae -> {
      JFileChooser fc = getTypedFileChooser();
      int returnVal = fc.showOpenDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        if (file.getPath().endsWith(".wav")) {
          handleWave(file);
        } else {
          handleEmg(file);
        }
      }
      repaint();
    };
  }

  private void handleEmg(File file) {
    try {
      List<String> strings = Files.readAllLines(file.toPath());
      List<String> loadedLines = new ArrayList<>(strings.size());
      for (int i = 0; i < strings.size(); i += 2) {
        loadedLines.add(strings.get(i));
      }
      loadedWaveFile = null;
      loadedEmgFile = parseData(loadedLines, comboBox);
      repaintEmgGraph();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleWave(File file) {
    try {
      loadedEmgFile = null;
      loadedWaveFile = new WaveFile(AudioSystem.getAudioInputStream(file));
      List<String> collect = IntStream.iterate(0, i -> i + 1).limit(loadedWaveFile.getFormat().getChannels()).mapToObj(String::valueOf).collect(toList());
      comboBox.setModel(new DefaultComboBoxModel<>(collect.toArray(new String[collect.size()])));
      comboBox.setSelectedIndex(0);
      comboBox.repaint();
      repaintWaveGraph();
    } catch (IOException | UnsupportedAudioFileException e) {
      e.printStackTrace();
    }
  }

  private JFileChooser getTypedFileChooser() {
    JFileChooser fc = new JFileChooser();
    fc.setFileFilter(new FileFilter() {
      @Override
      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        } else {
          String filename = f.getName().toLowerCase();
          return filename.endsWith(".txt") || filename.endsWith(".emg") || filename.endsWith(".wav");
        }
      }

      @Override
      public String getDescription() {
        return "EMG/Wave";
      }
    });
    return fc;
  }

  private void repaintEmgGraph() {
    JFreeChart chart = ChartFactory.createXYLineChart(
        GRAPH_HEADER,
        "", "",
        createEmgDataset(),
        PlotOrientation.VERTICAL,
        true, false, false);
    fixAxes(chart, loadedEmgFile.getHeader().getDblLength(), -10000, 10000);
    chartPanel.setChart(chart);
    chartPanel.setMouseWheelEnabled(true);
    chartPanel.setRangeZoomable(false);
  }

  private void repaintWaveGraph() {
    JFreeChart chart = ChartFactory.createXYLineChart(
        GRAPH_HEADER,
        "", "",
        createDataset(),
        PlotOrientation.VERTICAL,
        true, false, false);
    Integer selectedChannel = Integer.valueOf((String) comboBox.getSelectedItem());
    fixAxes(chart, loadedWaveFile.getNumberOfSamplesForChannel(selectedChannel), -32768, 32767);
    chartPanel.setChart(chart);
    chartPanel.setMouseWheelEnabled(true);
    chartPanel.setRangeZoomable(false);
  }

  private void fixAxes(JFreeChart chart, double length, double lowerBound, double upperBound) {
    XYPlot plot = (XYPlot) chart.getPlot();
    MyScrollableValueAxis myDomainAxis = new MyScrollableValueAxis(plot.getDomainAxis().getLabel());
    myDomainAxis.setLowerBound(0);
    myDomainAxis.setUpperBound(length);
    myDomainAxis.setRange(0, length);
    myDomainAxis.setMinLeft(0);
    myDomainAxis.setMaxRight(length);
    plot.setDomainAxis(myDomainAxis);
    MyScrollableValueAxis myRangeAxis = new MyScrollableValueAxis(plot.getRangeAxis().getLabel());
    myRangeAxis.setLowerBound(lowerBound);
    myRangeAxis.setUpperBound(upperBound);
    myRangeAxis.setMinLeft(lowerBound);
    myRangeAxis.setMaxRight(upperBound);
    plot.setRangeAxis(myRangeAxis);
  }

  private XYDataset createDataset() {
    XYSeriesCollection dataset = new XYSeriesCollection();
    final XYSeries series = new XYSeries("");
    Integer selectedChannel = Integer.valueOf((String) comboBox.getSelectedItem());
    for (int i = 1; i < loadedWaveFile.getNumberOfSamplesForChannel(selectedChannel); i++) {
      series.add(i, loadedWaveFile.getSamples()[selectedChannel][i - 1]);
    }
    dataset.addSeries(series);
    return dataset;
  }

  private XYDataset createEmgDataset() {
    XYSeriesCollection dataset = new XYSeriesCollection();
    final XYSeries series = new XYSeries("");
    List<Double> doubles = loadedEmgFile.getValues().get((String) comboBox.getSelectedItem());
    List<Double> times = loadedEmgFile.getValues().get(loadedEmgFile.getHeader().getTimeColumn());
    for (int i = 1; i < doubles.size(); i++) {
      series.add(times.get(i - 1), doubles.get(i - 1));
    }
    dataset.addSeries(series);
    return dataset;
  }
}
