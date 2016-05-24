//package override;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//
//import javax.swing.BorderFactory;
//import javax.swing.JPanel;
//import javax.swing.JSlider;
//import javax.swing.border.Border;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.axis.DateAxis;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.plot.XYPlot;
//import org.jfree.chart.renderer.xy.XYItemRenderer;
//import org.jfree.data.Range;
//import org.jfree.data.general.DatasetChangeEvent;
//import org.jfree.data.general.DatasetChangeListener;
//import org.jfree.data.general.DatasetUtilities;
//import org.jfree.data.time.Minute;
//import org.jfree.data.time.RegularTimePeriod;
//import org.jfree.data.time.TimeSeries;
//import org.jfree.data.time.TimeSeriesCollection;
//import org.jfree.data.xy.AbstractXYDataset;
//import org.jfree.data.xy.XYDataset;
//import org.jfree.ui.ApplicationFrame;
//import org.jfree.ui.RectangleInsets;
//import org.jfree.ui.RefineryUtilities;
//
///**
// * A demo that uses a "wrapper" dataset that provides a translation of the
// * underlying dataset.
// */
//public class TranslateDemo1 extends ApplicationFrame {
//
//  private static class DemoPanel extends JPanel
//      implements ChangeListener {
//
//    private TimeSeries series;
//
//    private ChartPanel chartPanel;
//
//    private JFreeChart chart;
//
//    private JSlider slider;
//
//    private TranslatingXYDataset dataset;
//
//    static class TranslatingXYDataset extends AbstractXYDataset
//        implements XYDataset, DatasetChangeListener {
//      private XYDataset underlying;
//      private double translate;
//
//      /**
//       * Creates a new <code>TranslatingXYDataset</code> class that
//       * applies a dynamically updateable translation to the underlying
//       * dataset.
//       *
//       * @param underlying  the underlying dataset (<code>null</code> not
//       *     permitted).
//       */
//      public TranslatingXYDataset(XYDataset underlying) {
//        this.underlying = underlying;
//        this.underlying.addChangeListener(this);
//        this.translate = 0.0;
//      }
//
//      /**
//       * Returns the current translation factor.
//       *
//       * @return The translation factor.
//       */
//      public double getTranslate() {
//        return this.translate;
//      }
//
//      /**
//       * Sets the translation factor.
//       *
//       * @param t  the translation factor.
//       */
//      public void setTranslate(double t) {
//        this.translate = t;
//        fireDatasetChanged();
//      }
//      public int getItemCount(int series) {
//        return this.underlying.getItemCount(series);
//      }
//      public double getXValue(int series, int item) {
//        return this.underlying.getXValue(series, item) + translate;
//      }
//      public Number getX(int series, int item) {
//        return new Double(getXValue(series, item));
//      }
//      public Number getY(int series, int item) {
//        return new Double(getYValue(series, item));
//      }
//      public double getYValue(int series, int item) {
//        return this.underlying.getYValue(series, item);
//      }
//      public int getSeriesCount() {
//        return this.underlying.getSeriesCount();
//      }
//      public Comparable getSeriesKey(int series) {
//        return underlying.getSeriesKey(series);
//      }
//      public void datasetChanged(DatasetChangeEvent event) {
//        // underlying dataset has changed, so notify our listeners
//        this.fireDatasetChanged();
//      }
//    }
//
//    /**
//     * Creates a new demo panel.
//     */
//    public DemoPanel() {
//      super(new BorderLayout());
//      this.chart = createChart();
//      this.chartPanel = new ChartPanel(this.chart);
//      this.chartPanel.setPreferredSize(new java.awt.Dimension(600, 270));
//      this.chartPanel.setDomainZoomable(true);
//      this.chartPanel.setRangeZoomable(true);
//      Border border = BorderFactory.createCompoundBorder(
//          BorderFactory.createEmptyBorder(4, 4, 4, 4),
//          BorderFactory.createEtchedBorder()
//      );
//      this.chartPanel.setBorder(border);
//      add(this.chartPanel);
//
//      JPanel dashboard = new JPanel(new BorderLayout());
//      dashboard.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
//      // make the slider units "minutes"
//      this.slider = new JSlider(-200, 200, 0);
//      slider.setPaintLabels(true);
//      slider.setMajorTickSpacing(50);
//      slider.setPaintTicks(true);
//      this.slider.addChangeListener(this);
//      dashboard.add(this.slider);
//      add(dashboard, BorderLayout.SOUTH);
//    }
//
//
//    /**
//     * Handles a state change event.
//     *
//     * @param event  the event.
//     */
//    public void stateChanged(ChangeEvent event) {
//      int value = this.slider.getValue();
//      // value is in minutes
//      this.dataset.setTranslate(value * 60 * 1000.0);
//    }
//
//  }
//
//
//}