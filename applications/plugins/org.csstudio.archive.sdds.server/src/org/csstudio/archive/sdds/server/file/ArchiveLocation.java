
/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 */

package org.csstudio.archive.sdds.server.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.csstudio.archive.sdds.server.util.TimeInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

/**
 * @author Markus Moeller
 *
 */
public class ArchiveLocation {

    /** File separator */
    static final String FILE_SEPARATOR = System.getProperty("file.separator");

    /** Class logger */
    private static Logger LOG = LoggerFactory.getLogger(ArchiveLocation.class);

    /**
     * TODO: A job has to read in the data paths every xx hours!!!!!
     *
     * TreeMap object that contains all pathes to the years. The key of the map is the year and the
     * value is the full path to the folder of the year.
     */
    private final TreeMap<Integer, String> dataPath;


    /**
     *
     */
    public ArchiveLocation() {
        dataPath = new TreeMap<Integer, String>(new YearComparator());
    }

    /**
     *
     * @param year
     * @return The matching path
     */
    @Nonnull
    public String getPathByYear(final int year) {
        return dataPath.get(year);
    }

    /**
     *
     * @return All matching paths
     */
    @Nonnull
    public String[] getAllPaths(final long startTimeInS, final long endTimeInS) {

        final Vector<String> result = new Vector<String>();
        final TimeInterval timeInterval = new TimeInterval(startTimeInS, endTimeInS);
        String path = null;
        int lastMonth;
        int lastYear;
        int month;

        final int[] years = timeInterval.getYears();

        if(years.length > 0) {

            lastYear = years[years.length - 1];
            lastMonth = timeInterval.getEndMonth();

            for(int y = years[0];y <= lastYear;y++) {
                if(y == years[0]) {
                    month = timeInterval.getStartMonth();
                } else {
                    month = 1;
                }

                if(y < lastYear) {
                    for(int m = month;m <= 12;m++) {
                        path = dataPath.get(y) + getMonthAsString(m) + FILE_SEPARATOR;
                        result.add(path);
                    }
                } else {
                    for(int m = month;m <= lastMonth;m++) {
                        path = dataPath.get(y) + getMonthAsString(m) + FILE_SEPARATOR;
                        result.add(path);
                    }
                }
            }
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     *
     * @param month
     * @return The month as string
     */
    @Nonnull
    public String getMonthAsString(final int month) {
        return month > 9 ? Integer.toString(month) : new String("0" + month);
    }

    /**
     *
     * @param filePath - The name and path of the file that contains the SDDS file location list
     */
    public void loadLocationList(@Nonnull final String filePath) throws DataPathNotFoundException {

        List<String> paths = Collections.emptyList();
        try {
            paths =
                Files.readLines(new File(filePath),
                                Charset.defaultCharset(),
                                new LineProcessor<List<String>>() {

                                    private final List<String> _result = Lists.newArrayList();

                                    @Override
                                    public boolean processLine(@Nonnull final String line) throws IOException {
                                        if (!Strings.isNullOrEmpty(line) && !line.startsWith("#")) {
                                            if(!line.endsWith(FILE_SEPARATOR) && !line.endsWith("/")) {
                                                _result.add(line + FILE_SEPARATOR);
                                            } else {
                                                _result.add(line);
                                            }
                                        }
                                        return true;
                                    }

                                    @Override
                                    @Nonnull
                                    public List<String> getResult() {
                                        return _result;
                                    }
                                });
        } catch(final FileNotFoundException fnfe) {
            throw new DataPathNotFoundException("File with the location paths cannot be found: " + fnfe.getMessage());
        } catch(final IOException ioe) {
            throw new DataPathNotFoundException("Reading error: " + ioe.getMessage());
        }

        processPaths(paths);
    }

    private void processPaths(@Nonnull final List<String> paths) throws DataPathNotFoundException {
        final Pattern pattern = Pattern.compile("\\d{4}");
        for (final String path : paths) {
            final File file = new File(path);
            final File[] fileList = file.listFiles();
            if(fileList == null) {
                throw new DataPathNotFoundException("Path '" + path + "' cannot be found or is empty.");
            }
            processPathEntries(pattern, fileList);
        }
    }

    private void processPathEntries(@Nonnull final Pattern pattern, @Nonnull final File[] fileList) {

        for(final File fi : fileList) {

            final String name = fi.getName().trim();
            final Matcher matcher = pattern.matcher(name);

            if (matcher.matches()) {

                try {
                    final int y = Integer.parseInt(name);
                    String fullPath = fi.getPath().trim();

                    if(!fullPath.endsWith(FILE_SEPARATOR)) {
                        fullPath += FILE_SEPARATOR;
                    }

                    if (!dataPath.containsKey(y)) {
                        dataPath.put(y, fullPath);
                    } else {
                        if(containsMoreSubDirs(fullPath, dataPath.get(y))) {
                            dataPath.put(y, fullPath);
                        }
                    }

                } catch(final NumberFormatException nfe) {
                    LOG.error("[*** NumberFormatException ***]: {}", nfe.getMessage());
                }
            }
        }
    }

    private boolean containsMoreSubDirs(@Nonnull final String newPath, @Nonnull final String oldPath) {

        final File newFile = new File(newPath);
        final File oldFile = new File(oldPath);

        if(newFile.list().length > oldFile.list().length) {
            return true;
        }

        return false;
    }
}
