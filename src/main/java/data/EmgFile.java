package data;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EmgFile {
  private final Map<String, List<Double>> values;
  private HeaderInfo header;

  public EmgFile(HeaderInfo header, Map<String, List<Double>> values) {
    this.header = header;
    this.values = values;
  }

  public Map<String, List<Double>> getValues() {
    return values;
  }
}
