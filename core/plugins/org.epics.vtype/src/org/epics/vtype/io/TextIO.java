/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.vtype.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ListDouble;
import static org.epics.util.text.StringUtil.DOUBLE_REGEX_WITH_NAN;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.ValueUtil;

/**
 * Utility class to serialize/de-serialize VTypes to text files.
 *
 * @author carcassi
 */
public class TextIO {
    private static final Pattern pDouble = Pattern.compile(DOUBLE_REGEX_WITH_NAN);

    /**
     * Reads a file where each line represents a value in an array.
     * If all lines are numbers the result is a VNumberArray, otherwise
     * is a VStringArray.
     * 
     * @param reader the stream to read
     * @return a type
     * @throws IOException all exceptions go through
     */
    public static VType readList(Reader reader) throws IOException {
        Matcher doubleMatcher = pDouble.matcher("");
        List<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(reader);
        String line;
        boolean numbers = true;
        while ((line = br.readLine()) != null) {
            lines.add(line);
            if (!doubleMatcher.reset(line).matches()) {
                numbers = false;
            }
        }

        if (numbers) {
            ListDouble numberList = convertToListDouble(lines);
            return ValueFactory.toVType(numberList);
        } else {
            return ValueFactory.toVType(lines);
        }
    }
    
    /**
     * Writes a file where each line represents a value in an array.
     * <p>
     * No buffering, flushing or closing is performed by this function.
     * 
     * @param vType the type to be written
     * @param writer the writer
     * @throws IOException all exceptions go through
     */
    public static void writeList(VType vType, Writer writer) throws IOException {
        PrintWriter out = new PrintWriter(writer);
        if (vType instanceof VNumberArray) {
            VNumberArray array = (VNumberArray) vType;
            for (int i = 0; i < array.getData().size(); i++) {
                out.println(array.getData().getDouble(i));
            }
        } else if (vType instanceof VStringArray) {
            VStringArray array = (VStringArray) vType;
            for (int i = 0; i < array.getData().size(); i++) {
                out.println(array.getData().get(i));
            }
        } else {
            throw new UnsupportedOperationException("Can't serialize " + ValueUtil.typeOf(vType).getSimpleName() + " to a list");
        }
        out.flush();
    }
    
    // TODO: copied from CsvParser
    private static ListDouble convertToListDouble(List<String> tokens) {
        double[] values = new double[tokens.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = Double.parseDouble(tokens.get(i));
        }
        return new ArrayDouble(values);
    }
}
