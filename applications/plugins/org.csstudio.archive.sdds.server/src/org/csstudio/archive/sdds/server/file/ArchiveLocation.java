
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.archive.sdds.server.util.TimeInterval;

/**
 * @author Markus Moeller
 *
 */
public class ArchiveLocation {

    /**
     * TODO: A job has to read in the data paths every xx hours!!!!!
     *
     * TreeMap object that contains all pathes to the years. The key of the map is the year and the
     * value is the full path to the folder of the year.
     */
    private final TreeMap<Integer, String> dataPath;

    /** File separator */
    private final String FILE_SEPARATOR;

    /**
     *
     */
    public ArchiveLocation() {
        dataPath = new TreeMap<Integer, String>(new YearComparator());
        FILE_SEPARATOR = System.getProperty("file.separator");
    }

    /**
     *
     * @param year
     * @return The matching path
     */
    public String getPathByYear(final int year) {
        String result = null;
        result = dataPath.get(year);
        return result;
    }

    /**
     *
     * @param startTime
     * @return The matching path
     */
    public String getPath(final long startTime) {
        final TimeInterval timeInterval = new TimeInterval(startTime, startTime);
        return dataPath.get(timeInterval.getStartYear()) + timeInterval.getStartMonthAsString() + FILE_SEPARATOR;
    }

    /**
     *
     * @return All matching paths
     */
    public String[] getAllPaths(final long startTime, final long endTime) {

        final Vector<String> result = new Vector<String>();
        final TimeInterval timeInterval = new TimeInterval(startTime, endTime);
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
    public String getMonthAsString(final int month) {
        return month > 9 ? Integer.toString(month) : new String("0" + month);
    }

    /**
     *
     * @param filePath - The name and path of the file that contains the SDDS file location list
     */
    public void loadLocationList(final String filePath) throws DataPathNotFoundException {

        Matcher matcher = null;
        BufferedReader br = null;
        String line = null;
        String name = null;
        String fullPath;
        File[] fileList = null;

        final Vector<String> path = new Vector<String>();

        try {

        	br = new BufferedReader(new FileReader(filePath));
            while(br.ready()) {
                line = br.readLine();
                line = line.trim();
                if(line.length() > 0) {

                	if(line.startsWith("#") == false) {

                		if(line.endsWith(FILE_SEPARATOR) == false && line.endsWith("/") == false) {
                            line = line + FILE_SEPARATOR;
                        }

                        path.add(line);
                    }
                }
            }
        } catch(final FileNotFoundException fnfe) {
            throw new DataPathNotFoundException("File with the location paths cannot be found: " + fnfe.getMessage());
        } catch(final IOException ioe) {
            throw new DataPathNotFoundException("Reading error: " + ioe.getMessage());
        } finally {
            if(br != null) {
            	try{br.close();}catch(final Exception e){/* Can be ignored */}
            	br = null;
            }
        }

        final Pattern pattern = Pattern.compile("\\d{4}");

        for(final String f : path) {

        	final File file = new File(f);
            fileList = file.listFiles();
            if(fileList == null) {
                throw new DataPathNotFoundException("Path '" + f + "' cannot be found or is empty.");
            }

            for(final File fi : fileList) {

            	name = fi.getName().trim();
                matcher = pattern.matcher(name);
                if(matcher.matches()) {

                	try {

                		// subDir.add(name);
                        final int y = Integer.parseInt(name);
                        fullPath = fi.getPath().trim();
                        if(fullPath.endsWith(FILE_SEPARATOR) == false) {
                            fullPath += FILE_SEPARATOR;
                        }

                        if (dataPath.containsKey(y) == false) {
                        	dataPath.put(y, fullPath);
                        } else {
                        	if(containsMoreSubDirs(fullPath, dataPath.get(y))) {
                        		dataPath.put(y, fullPath);
                        	}
                        }

                    } catch(final NumberFormatException nfe) {/* Can be ignored */}
                }
            }
        }
    }

    /**
     *
     * @param newPath
     * @param oldPath
     * @return
     */
    private boolean containsMoreSubDirs(final String newPath, final String oldPath) {

    	boolean result = false;

    	final File newFile = new File(newPath);
    	final File oldFile = new File(oldPath);

    	if(newFile.list().length > oldFile.list().length) {
    		result = true;
    	}

    	return result;
    }
}
