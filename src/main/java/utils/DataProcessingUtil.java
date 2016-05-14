package utils;

import data.EmgFile;
import data.HeaderInfo;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class DataProcessingUtil {

    public static EmgFile parseData(List<String> loadedLines, JComboBox<String> comboBox) {
        List<String> columns = Stream.of(loadedLines.get(4).split("\t")).collect(toList());
        List<String> copy = columns.stream().collect(Collectors.toList());
        copy.remove(0);
        copy.remove(copy.size() - 1);
        comboBox.setModel(new DefaultComboBoxModel<>(copy.toArray(new String[copy.size()])));
        comboBox.setSelectedIndex(0);
        comboBox.repaint();
        Map<String, List<Double>> stringListHashMap = new HashMap<>();
        for (String columnName : columns) {
            stringListHashMap.put(columnName, new ArrayList<>());
        }
        for (int i = 5; i < loadedLines.size(); i++) {
            List<String> values = Stream.of(loadedLines.get(i).split("\t")).collect(toList());
            for (int j = 0; j < columns.size(); j++) {
                stringListHashMap.get(columns.get(j)).add(Double.parseDouble(values.get(j)));
            }
        }
        List<Double> timeValues = stringListHashMap.get(columns.get(0));
        Double maxTime = timeValues.get(timeValues.size() - 1);
        HeaderInfo headerInfo = new HeaderInfo(columns.get(0), loadedLines.get(0), loadedLines.get(1), loadedLines.get(2), loadedLines.get(3), maxTime);
        return new EmgFile(headerInfo, stringListHashMap);
    }

}
