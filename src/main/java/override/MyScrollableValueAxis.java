package override;

import lombok.Getter;
import lombok.Setter;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.Range;

public class MyScrollableValueAxis extends NumberAxis {
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

  public MyScrollableValueAxis(String label) {
    super(label);
  }
}