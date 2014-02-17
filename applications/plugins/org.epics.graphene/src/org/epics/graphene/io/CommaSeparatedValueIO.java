/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.epics.graphene.Point2DDataset;
import org.epics.graphene.Point2DDatasets;
import org.epics.util.array.CircularBufferDouble;

/**
 *
 * @author carcassi
 */
public class CommaSeparatedValueIO {
    
    private static NumberFormat format = new DecimalFormat("0.####################");
    
    public static void write(Point2DDataset dataset, Writer writer) 
    throws IOException {
        writer.write("x,y");
        for (int i = 0; i < dataset.getCount(); i++) {
            writer.append("\n");
            writer.append(format.format(dataset.getXValues().getDouble(i)));
            writer.append(',');
            writer.append(format.format(dataset.getYValues().getDouble(i)));
        }
    }
    
    public static String write(Point2DDataset dataset) 
    throws IOException {
        StringWriter writer = new StringWriter();
        write(dataset, writer);
        return writer.toString();
    }
    
    public static Point2DDataset read(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        CircularBufferDouble xValues = new CircularBufferDouble(Integer.MAX_VALUE);
        CircularBufferDouble yValues = new CircularBufferDouble(Integer.MAX_VALUE);
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split(",");
            if (tokens.length != 2) {
                throw new IllegalArgumentException("Each line must have two values");
            }
            double xValue = Double.parseDouble(tokens[0]);
            double yValue = Double.parseDouble(tokens[1]);
            xValues.addDouble(xValue);
            yValues.addDouble(yValue);
        }
        return Point2DDatasets.lineData(xValues, yValues);
    }
    
    public static Point2DDataset read(String string) throws IOException {
        return read(new StringReader(string));
    }
}
