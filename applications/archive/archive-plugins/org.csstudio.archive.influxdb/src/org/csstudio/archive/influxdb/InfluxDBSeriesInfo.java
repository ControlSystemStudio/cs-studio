package org.csstudio.archive.influxdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class InfluxDBSeriesInfo {
    protected final String measurement;
    protected final String field;
    protected final Map<String, String> tags;

    public InfluxDBSeriesInfo(final String measurement, final Map<String, String> tags, final String field) {
        this.measurement = measurement;
        this.field = field;
        this.tags = tags;
    }

    public String getMeasurement() {
        return measurement;
    }

    public String getField() {
        return field;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public List<String> getTagClauses() {
        if (tags.size() < 1)
            return null;

        List<String> ret = new ArrayList<String>();
        for (Entry<String, String> tag : tags.entrySet()) {
            ret.add(String.format("\"%s\" = \'%s\'", tag.getKey(), tag.getValue()));
        }
        return ret;
    }

    private static int incrementCheck(int idx, final String name) throws Exception {
        if ((idx + 1) >= name.length())
            throw new Exception("Unexpected end of string: " + name);
        return idx + 1;
    }

    private static int appendNonEscaped(int idx, final String name, final StringBuilder sb) throws Exception {
        if (name.charAt(idx) == '\\')
            idx = incrementCheck(idx, name);
        sb.append(name.charAt(idx));
        return idx;
    }

    public static InfluxDBSeriesInfo decodeLineProtocol(final String name) throws Exception {
        StringBuilder cur_sb = new StringBuilder();

        final String measure;
        final Map<String, String> tags = new HashMap<String, String>();
        final String field;

        int idx = 0;
        // Get measurement name
        while ((name.charAt(idx) != ',') && (!Character.isWhitespace(name.charAt(idx)))) {
            idx = appendNonEscaped(idx, name, cur_sb);
            idx = incrementCheck(idx, name);
        }
        // Check measurement name
        measure = cur_sb.toString();
        if (measure.length() < 1)
            throw new Exception("Got empty string measure at " + idx + ": " + name);

        // Get tags
        while (!Character.isWhitespace(name.charAt(idx))) {
            // Move past comma
            idx = incrementCheck(idx, name);

            // Current tagname/tagvalue pair
            final String tagname, tagval;

            // Get tag name
            cur_sb = new StringBuilder();
            while ((name.charAt(idx) != '=') && (name.charAt(idx) != ',')
                    && (!Character.isWhitespace(name.charAt(idx)))) {
                idx = appendNonEscaped(idx, name, cur_sb);
                idx = incrementCheck(idx, name);
            }
            // Move past = sign
            if (name.charAt(idx) != '=')
                throw new Exception("Malformed tag=value at " + idx + ": " + name);
            idx = incrementCheck(idx, name);

            // Check tag name
            tagname = cur_sb.toString();
            if (tagname.length() < 1)
                throw new Exception("Got empty string tagname at " + idx + ": " + name);

            // Get tag value
            cur_sb = new StringBuilder();
            while ((name.charAt(idx) != '=') && (name.charAt(idx) != ',')
                    && (!Character.isWhitespace(name.charAt(idx)))) {
                idx = appendNonEscaped(idx, name, cur_sb);
                idx = incrementCheck(idx, name);
            }
            if (name.charAt(idx) == '=')
                throw new Exception("Malformed tag=value at " + idx + ": " + name);

            // Check tag value
            tagval = cur_sb.toString();
            if (tagval.length() < 1)
                throw new Exception("Got empty string tagval at " + idx + ": " + name);

            tags.put(tagname, tagval);
        }

        // Move past white space
        while (Character.isWhitespace(name.charAt(idx)))
            idx = incrementCheck(idx, name);

        // Get field name
        cur_sb = new StringBuilder();
        while ((idx < name.length()) && (name.charAt(idx) != '=') && (name.charAt(idx) != ',')
                && (!Character.isWhitespace(name.charAt(idx)))) {
            idx = appendNonEscaped(idx, name, cur_sb);
            idx++;
        }
        if (idx < name.length())
            throw new Exception("Unexpected trailing characters: " + name);

        // Check field name
        field = cur_sb.toString();
        if (field.length() < 1)
            throw new Exception("Got empty string field: " + name);

        return new InfluxDBSeriesInfo(measure, tags, field);
    }
}
