/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/** Utility for Hamcrest-based file comparison
 *
 *  <p>Example usage:
 *  <code>
 *  assertThat(linesInFile("file1.txt"), matchLinesIn(linesInFile("file1.txt")));
 *  </code>
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FileTestUtil
{
    /** Read lines of file for comparison
     *  @param filename
     *  @return
     *  @throws Exception
     */
    public static String[] linesInFile(final String filename) throws Exception
    {
        final List<String> lines = new ArrayList<>();
        final BufferedReader input = new BufferedReader(new FileReader(filename));
        for (String line = input.readLine();  line != null;   line = input.readLine())
            lines.add(line);
        input.close();
        return lines.toArray(new String[lines.size()]);
    }

    /** @param lines_from_file Content of one file
     *  @return {@link Matcher} for comparing lines of a file
     */
    public static  Matcher<String[]> matchLinesIn(final String[] lines_from_file)
    {
        return new TypeSafeMatcher<String[]>()
        {
            final private StringBuilder diffs = new StringBuilder();

            @Override
            public void describeTo(Description desc)
            {
                desc.appendText(lines_from_file.length + " lines of matching text");
            }

            @Override
            protected boolean matchesSafely(final String[] other)
            {
                boolean result = true;
                if (lines_from_file.length != other.length)
                {
                    diffs.append("other file has " + other.length + " lines\n");
                    result = false;
                }
                final int N = Math.min(lines_from_file.length, other.length);
                for (int i=0; i<N; ++i)
                {
                    if (! lines_from_file[i].equals(other[i]))
                    {
                        diffs.append("Line " + i + "\n");
                        diffs.append("> " + lines_from_file[i] + "\n");
                        diffs.append("< " + other[i] + "\n");
                        result = false;
                    }
                }
                return result;
            }

            @Override
            protected void describeMismatchSafely(String[] item,
                    Description mismatchDescription)
            {
                mismatchDescription.appendText(diffs.toString());
            }
        };
    }
}
