/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
 */

package org.csstudio.dal;


/**
 * An instance of this class represents a constraint, or a selector,
 * sent along with the history request query, that parametrizes exactly which are the records from
 * the history storage that must be returned through a history iterator. The constructors
 * of this object prevent inconsistent constraint specification and prevent the constraint modification
 * after the constraint object has been instantiated.
 * <p>
 * By default constrains does not reverse order of returned data.
 * </p>
 *
 * @author        Gasper Tkacik (gasper.tkacik@cosylab.com)
 */
public class HistoryConstraints
{
    private long startTime = 0;
    private long stopTime = 0;
    private int nelements = 0;
    private String selector = null;
    private long deltaTime = 0;
    private boolean reverse = false;

    /**
     * Creates an instance of history constraint by specifying the start and stop times
     * between which all returned history records should lie. Specifies also delta time,
     * i.e. the time raster (time lapse) between data points. This data item is used
     * as a hint to the history data source.
     *
     * @param    startTime    the Java like timestamp of the earliest history entry,
     *                         if 0, the decision is left to the implementation
     * @param    stopTime    the Java like timestamp of the latest history entry,
     *                         if 0, the stop time is the current moment
     * @param    deltaTime    suggested delta time between returned history elements
     * @param    source        an optional history source argument, may be <code>null</code>
     */
    public HistoryConstraints(long startTime, long stopTime, long deltaTime,
        String source)
    {
        this(startTime, stopTime, source);
        assert (deltaTime > 0);
        this.deltaTime = deltaTime;
    }

    /**
     * Creates an instance of history constraint by specifying the start and stop times
     * between which all returned history records should lie. Specifies also delta time,
     * i.e. the time raster (time lapse) between data points. This data item is used
     * as a hint to the history data source.
     *
     * @param    startTime    the Java like timestamp of the earliest history entry,
     *                         if 0, the decision is left to the implementation
     * @param    stopTime    the Java like timestamp of the latest history entry,
     *                         if 0, the stop time is the current moment
     * @param    deltaTime    suggested delta time between returned history elements
     * @param    source        an optional history source argument, may be <code>null</code>
     * @param reverse if returned order is to be reversed (first returned oldest timestamp)
     */
    public HistoryConstraints(long startTime, long stopTime, long deltaTime,
        String source, boolean reverse)
    {
        this(startTime, stopTime, deltaTime, source);
        this.reverse = reverse;
    }

    /**
     * Creates an instance of history constraint by specifying the start and stop times
     * between which all returned history records should lie.
     *
     * @param    startTime    the Java like timestamp of the earliest history entry,
     *                         if 0, the decision is left to the implementation
     * @param    stopTime    the Java like timestamp of the latest history entry,
     *                         if 0, the stop time is the current moment
     * @param    source        an optional history source argument, may be <code>null</code>
     */
    public HistoryConstraints(long startTime, long stopTime, String source)
    {
        assert (startTime >= 0);
        assert (stopTime >= 0);
        assert (stopTime >= startTime);

        this.startTime = startTime;
        this.stopTime = stopTime;
        this.selector = source;
    }

    /**
     * Creates an instance of history constraint by specifying the start and stop times
     * between which all returned history records should lie.
     *
     * @param    startTime    the Java like timestamp of the earliest history entry,
     *                         if 0, the decision is left to the implementation
     * @param    stopTime    the Java like timestamp of the latest history entry,
     *                         if 0, the stop time is the current moment
     * @param    source        an optional history source argument, may be <code>null</code>
     * @param reverse if returned order is to be reversed (first returned oldest timestamp)
     */
    public HistoryConstraints(long startTime, long stopTime, String source,
        boolean reverse)
    {
        this(startTime, stopTime, source);
        this.reverse = reverse;
    }

    /**
     * Creates an instance of history constraint by specifying the maximum number of history
     * entries. The entries start with the latest history element and go back in time. The
     * actual number of entries may be less than <code>maximumElements</code>, but it will not
     * be more.
     *
     * @param    maximumElements    the maximum number of data / timestamp elements in history, if
     *                             0, the decision is left to the implementation
     * @param    source            the optional history source argument, may be <code>null</code>
     */
    public HistoryConstraints(int maximumElements, String source)
    {
        assert (maximumElements >= 0);

        this.nelements = maximumElements;
        this.selector = source;
    }

    /**
     * Creates an instance of history constraint by specifying the maximum number of history
     * entries. The entries start with the latest history element and go back in time. The
     * actual number of entries may be less than <code>maximumElements</code>, but it will not
     * be more.
     *
     * @param    maximumElements    the maximum number of data / timestamp elements in history, if
     *                             0, the decision is left to the implementation
     * @param    source            the optional history source argument, may be <code>null</code>
     * @param reverse if returned order is to be reversed (first returned oldest timestamp)
     */
    public HistoryConstraints(int maximumElements, String source,
        boolean reverse)
    {
        this(maximumElements, source);
        this.reverse = reverse;
    }

    /**
     * Returns the starting timestamp of the history data in Java time format.
     * All returned records must be after this date. Can be 0, in this case the
     * implementation is free to choose the start time.
     *
     * @return Java like timestamp determining the earliest record time
     * @see    System.currentTimeMillis
     */
    public long getStartTime()
    {
        return startTime;
    }

    /**
     * Returns the latest timestamp of the history data in Java time format.
     * All returned records must be before this date. Can be 0, in this case the
     * implementation must regard the stop time as now.
     *
     * @return    Java like timestamp determining the latest record time
     * @see    System.currentTimeMillis
     */
    public long getStopTime()
    {
        return stopTime;
    }

    /**
     * Returns the maximum amount of history data items requested. If 0, there
     * is no limit.
     *
     * @return the maximum number of history entries, with timestamps starting with
     *             the current moment and going back into time
     */
    public int getMaximumHistoryElements()
    {
        return nelements;
    }

    /**
     * Returns the string that distinguishes between (potentially) multiple data
     * storages, if present. If only one data storage is present, the return
     * value of this method may be ignored. For example, the data can be stored
     * in a local history, on an archiving server or on a post-mortem server or some
     * other source.
     *
     * @return    the archive selector
     */
    public String getHistorySource()
    {
        return selector;
    }

    /**
     * Gets the delta time. This is the suggested time sample size (time raster) that
     * the history source should use when returning the history elements.
     *
     * @return        the delta time between successive history elements, in milliseconds
     */
    public long getDeltaTime()
    {
        return deltaTime;
    }

    /**
     * Returns <code>true</code> if order of returned values should be reversed
     * regarding to the default order. By default first value with earlyest
     * timestamp is returned. Default value is <code>false</code>.
     * @return boolean <code>true</code> if order of returned values should be reversed
     */
    public boolean isReverse()
    {
        return reverse;
    }
}

/* __oOo__ */
