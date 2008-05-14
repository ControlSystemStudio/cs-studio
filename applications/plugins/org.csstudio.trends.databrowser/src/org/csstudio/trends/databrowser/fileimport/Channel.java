/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.trends.databrowser.fileimport;

import java.util.Calendar;

/**
 * This is a imported and from User manipulated Channel from File.
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 28.04.2008
 */
public class Channel {

    /**
     * Is this Channel selected.
     */
    private boolean _selected;

    /**
     * The Name of this Channel.
     */
    private String _name;

    /**
     * The time of the first sample.
     */
    private Calendar _startTime;

    /**
     * The time of the last sample.
     */
    private Calendar _endTime;

    /**
     * The number of total samples.
     */
    private int _samplesTotal;

    /**
     * The unit of channel.
     */
    private String _unit;

    /**
     * A JEP conform formula to manipulate the value.
     */
    private String _formula;

    /**
     * The resolution of the channel.
     */
    private long _resolution;

    /**
     * The global file Settings.
     */
    private SampleFileImportSettings _parent;

    /**
     * Constructor to generate a import Channel.
     * 
     * @param name
     *            the name of the channel.
     * @param parent
     *            the global file settings.
     * @param startTime
     *            the Time of the first sample.
     * @param resolution
     *            the resolution of channel.
     */
    public Channel(final String name, final SampleFileImportSettings parent,
            final Calendar startTime, final int resolution) {
        this(name, parent);
        _startTime = startTime;
        _resolution = resolution;

    }

    /**
     * Constructor to generate a import Channel.
     * 
     * @param name
     *            The name of the channel.
     * @param parent
     *            the global file settings.
     */
    public Channel(final String name, final SampleFileImportSettings parent) {
        _name = name;
        _parent = parent;
    }

    /**
     * 
     * @return Only true if this channel a selected.
     */
    public final boolean isSelected() {
        return _selected;
    }

    /**
     * 
     * @param selected
     *            set true to select this channel.
     */
    public final void setSelected(final boolean selected) {
        this._selected = selected;
    }

    /**
     * 
     * @return the Name of this channel.
     */
    public final String getName() {
        return _name;
    }

    /**
     * 
     * @param name
     *            change the name of this channel.
     */
    public final void setName(final String name) {
        _name = name;
    }

    /**
     * 
     * @return the time of the first channel.
     */
    public final Calendar getStartTime() {
        return _startTime;
    }

    /**
     * @param startTime
     *            Set a new time for the first channel. (All samples are
     *            relative to the start time).
     */
    public final void setStartTime(final Calendar startTime) {
        _startTime = startTime;
    }

    /**
     * 
     * @return the time of the last sample.
     */
    public final Calendar getEndTime() {
        _endTime = (Calendar) getStartTime().clone();
        // channel.getEndTime().add(Calendar.MILLISECOND,
        // (int)(channel.getResolution() / 1000 * channel.getSamplesTotal()));
        _endTime.add(Calendar.MILLISECOND,
                (int) (Math.ceil(getResolution() * getSamplesTotal()) / 1000));// Erweitert
                                                                                // den
                                                                                // Zeitbereich
                                                                                // um
                                                                                // angefangende
                                                                                // Millisekunden.
        // channel.getEndTime().add(Calendar.MILLISECOND,
        // (int)(channel.getResolution() / 1000 * channel.getSamplesTotal()));
        return _endTime;
    }

    /**
     * 
     * @return the number of selected samples.
     */
    public final int getSamplesSelected() {
        long diff = _parent._endTime.getTimeInMillis() - _parent._startTime.getTimeInMillis();
        diff *= 1000; // to microsec
        long sample = diff / getResolution();
        sample = sample / _parent.getFactor();
        int sum = (int) sample;
        // if(sum>getSamplesTotal()){
        // return getSamplesTotal();
        // }
        return sum;
    }

    /**
     * 
     * @return the number of total samples.
     */
    public final int getSamplesTotal() {
        return _samplesTotal;
    }

    /**
     * 
     * @param samplesTotal
     *            the number of total samples. (Idea is use only the first
     *            time).
     */
    public final void setSamplesTotal(final int samplesTotal) {
        _samplesTotal = samplesTotal;
    }

    /**
     * 
     * @return the Unit of this channel.
     */
    public final String getUnit() {
        return _unit;
    }

    /**
     * 
     * @param unit
     *            the unit of the channel.
     */
    public final void setUnit(final String unit) {
        _unit = unit;
    }

    /**
     * The formula was parsed by {@link JEP}.
     * 
     * @return the formula as String
     */
    public final String getFormula() {
        return _formula;
    }

    /**
     * Each value can be manipulated by a string given formula. The formula was
     * parsed by {@link JEP}.
     * 
     * @param formula
     *            the formula.
     */
    public final void setFormula(final String formula) {
        _formula = formula;
    }

    /**
     * 
     * @return resolution in microsec.
     */
    public final long getResolution() {
        return _resolution;
    }

    /**
     * 
     * @param resolution
     *            set the resolution in microsec
     */
    public final void setResolution(final long resolution) {
        _resolution = resolution;
    }

}
