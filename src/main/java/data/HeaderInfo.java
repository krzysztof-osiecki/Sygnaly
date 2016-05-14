package data;

import lombok.Data;

@Data
public class HeaderInfo {
    private String timeColumn;
    private String type;
    private String name;
    private String frequency;
    private String date;
    private String length;
    private double dblLength;

    public HeaderInfo(String timeColumn, String type, String name, String frequency, String date, Double length) {
        this.timeColumn = timeColumn;
        this.length = "Length " + length;
        this.dblLength = length;
        this.type = type.replace("\t", " ");
        this.name = name.replace("\t", " ");
        this.frequency = frequency.replace("\t", " ");
        this.date = date.replace("\t", " ");
    }
}
