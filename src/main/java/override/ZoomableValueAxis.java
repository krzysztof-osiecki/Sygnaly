package override;

import lombok.Getter;
import lombok.Setter;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.Range;

public class ZoomableValueAxis extends NumberAxis {
  @Getter
  @Setter
  private double minLeft;
  @Getter
  @Setter
  private double maxRight;


  @Override
  public void resizeRange(double percent, double anchorValue) {
    setRange(new Range(minLeft, maxRight));
  }

  @Override
  public void resizeRange2(double percent, double anchorValue) {
    if (percent > 0.0) {
      double left = anchorValue - getLowerBound();
      double right = getUpperBound() - anchorValue;
      double calculatedLeft = anchorValue - left * percent;
      double calculatedRight = anchorValue + right * percent;
      Range adjusted = new Range(calculatedLeft > minLeft ? calculatedLeft : minLeft,
          calculatedRight < maxRight ? calculatedRight : maxRight);
      setRange(adjusted);
    } else {
      setAutoRange(true);
    }
  }

  @Override
  public void pan(double percent) {
    Range range = getRange();
    if ((range.getUpperBound() >= maxRight && percent > 0) || (range.getLowerBound() <= minLeft && percent < 0)) return;
    double length = range.getLength();
    double adj = length * percent;
    double lower = range.getLowerBound() + adj < minLeft ? minLeft : range.getLowerBound() + adj;
    double upper = range.getUpperBound() + adj > maxRight ? maxRight : range.getUpperBound() + adj;
    setRange(lower, upper);
  }

  public ZoomableValueAxis(String label) {
    super(label);
  }
}