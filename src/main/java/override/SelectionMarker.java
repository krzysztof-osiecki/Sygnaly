package override;

import lombok.Getter;
import main.MainClass;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.Layer;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

@Getter
public class SelectionMarker extends MouseAdapter {
  private Marker marker;
  private Double markerStart = Double.NaN;
  private Double markerEnd = Double.NaN;
  private ChartPanel panel;
  private MainClass mainClass;
  private ValueMarker valueMarker;

  public SelectionMarker(ChartPanel chartPanel, MainClass mainClass) {
    this.panel = chartPanel;
    this.mainClass = mainClass;
  }

  private void updateMarker() {
    if (marker != null) {
      ((XYPlot) panel.getChart().getPlot()).removeDomainMarker(marker, Layer.BACKGROUND);
    }
    if (!(markerStart.isNaN() && markerEnd.isNaN())) {
      if (markerEnd > markerStart) {
        marker = new IntervalMarker(markerStart, markerEnd);
        marker.setPaint(new Color(0x94, 0x20, 0x00, 0x80));
        marker.setAlpha(0.7f);
        ((XYPlot) panel.getChart().getPlot()).addDomainMarker(marker, Layer.BACKGROUND);
      }
    }
  }

  private Double getPosition(MouseEvent e) {
    Point2D p = panel.translateScreenToJava2D(e.getPoint());
    Rectangle2D plotArea = panel.getScreenDataArea();
    XYPlot plot = (XYPlot) panel.getChart().getPlot();
    return plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  public void playSelection() {
    new Thread(() -> mainClass.playSelection(markerStart.intValue(), markerEnd.intValue(), this)).start();
  }

  @Override
  public void mousePressed(MouseEvent e) {
    switch (mainClass.getSelectedItem()) {
      case START:
        markerStart = getPosition(e);
        break;
      case END:
        markerEnd = getPosition(e);
        break;
      case NONE:
        clearSelection();
        break;
      default:
    }
    if (!markerStart.equals(Double.NaN) && !markerEnd.equals(Double.NaN)) {
      updateMarker();
    }
  }

  public void clearSelection() {
    if (marker != null) {
      ((XYPlot) panel.getChart().getPlot()).removeDomainMarker(marker, Layer.BACKGROUND);
    }
    marker = null;
    markerStart = Double.NaN;
    markerEnd = Double.NaN;
  }

  public void addValueMarker(int framePosition) {
    valueMarker = new ValueMarker(framePosition);  // position is the value on the axis
    valueMarker.setPaint(Color.black);
    XYPlot plot = (XYPlot) panel.getChart().getPlot();
    plot.addDomainMarker(valueMarker, Layer.BACKGROUND);
  }

  public void refreshValueMarker(int framePosition) {
    valueMarker.setValue(framePosition);
  }

  public void removeValueMarker() {
    ((XYPlot) panel.getChart().getPlot()).removeDomainMarker(valueMarker, Layer.BACKGROUND);
  }
}
