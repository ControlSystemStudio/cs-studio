package org.csstudio.archive.influxdb;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class TableBuilder
{
    List<String[]> rows = new LinkedList<String[]>();
    String indent;

    TableBuilder()
    {
        this.indent = "";
    }

    void setIndent(String indent)
    {
        this.indent = indent;
    }

    public void addRow(String... cols)
    {
        rows.add(cols);
    }

    public void addRow(Object... cols)
    {
        rows.add(Arrays.copyOf(cols, cols.length, String[].class));
    }

    private int[] colWidths()
    {
        int cols = -1;

        for(String[] row : rows)
            cols = Math.max(cols, row.length);

        int[] widths = new int[cols];

        for(String[] row : rows) {
            for(int colNum = 0; colNum < row.length; colNum++) {
                widths[colNum] =
                        Math.max(
                                widths[colNum],
                                StringUtils.length(row[colNum]));
            }
        }

        return widths;
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        int[] colWidths = colWidths();

        for(String[] row : rows) {
            buf.append(indent);
            buf.append("| ");
            for(int colNum = 0; colNum < row.length; colNum++) {
                buf.append(
                        StringUtils.rightPad(
                                StringUtils.defaultString(
                                        row[colNum]), colWidths[colNum]));
                buf.append(" | ");
            }

            buf.append('\n');
        }

        return buf.toString();
    }
}
