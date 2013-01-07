/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.jmatio;

import java.util.ArrayList;

import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.archive.vtype.VTypeHelper;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;
import org.junit.Test;

import com.jmatio.io.MatFileIncrementalWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLStructure;

/** Demo for using the JMatIO library
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JMatioDemo
{
    @Test
    public void writeMatlabFile1() throws Exception
    {
      //1. First create example arrays
      double[] src = new double[] { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 };
      MLDouble mlDouble = new MLDouble( "double_arr", src, 2 );
      MLChar mlChar = new MLChar( "char_arr", "I am dummy" );

      //2. write arrays to file
      ArrayList<MLArray> list = new ArrayList<MLArray>();
      list.add( mlDouble );
      list.add( mlChar );

      MatFileIncrementalWriter writer = new MatFileIncrementalWriter("mat_file.mat");
      writer.write(mlDouble);
      writer.write(mlChar);
      writer.close();
    }

    private void setCellText(final MLCell cell, final int index, final String text)
    {
        cell.set(new MLChar(null, text), index);
    }

    @Test
    public void writeMatlabFile2() throws Exception
    {
        // Example values
        final VType[] values = new VType[10];
        for (int i=0; i<10; ++i)
            values[i] = new ArchiveVNumber(Timestamp.now(), AlarmSeverity.NONE, "OK",
                    ValueFactory.displayNone(),
                    Math.exp(-((5.0-i)*(5.0-i))) );

        // Turn values into Matlab data
        final int[] dims = new int[] { values.length, 1 };
        final MLDouble value = new MLDouble(null, dims);
        final MLCell time = new MLCell(null, dims);
        final MLCell severity = new MLCell(null, dims);
        final MLCell status = new MLCell(null, dims);
        for (int i=0; i<values.length; ++i)
        {
            value.set(VTypeHelper.toDouble(values[i]), i);
            setCellText(time, i, TimestampHelper.format(VTypeHelper.getTimestamp(values[i])));
            setCellText(severity, i, VTypeHelper.getSeverity(values[i]).toString());
            setCellText(severity, i, VTypeHelper.getMessage(values[i]));
        }
        final MLStructure struct = new MLStructure("channel0", new int[] { 1, 1 });
        struct.setField("value", value);
        struct.setField("time", time);
        struct.setField("severity", severity);
        struct.setField("status", status);

        // Write to file
        final MatFileIncrementalWriter writer = new MatFileIncrementalWriter("mat_file2.mat");
        writer.write(struct);
        writer.close();
    }
}
