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
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import java.util.HashMap;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 21.07.2008
 */
public class ExtUserPrmData {

    /**
     * The Parent GSD Slave Model.
     */
    private GsdSlaveModel _gsdSlaveModel;

    /**
     * The ref index of this ext user prm data.
     */
    private String _index;
    /**
     * The Name/Desc of this ext user prm data.
     */
    private String _text;
    /**
     * The dataType of this ext user prm data as plain text.<br>
     * (e.G. Bit(1) or BitArea(4-7))
     */
    private String _dataType;
    /**
     * The default value.
     */
    private int _default;
    /**
     * The lowest bit to manipulate.
     */
    private int _minBit;
    /**
     * The highest bit to manipulate.
     */
    private int _maxBit;
    /**
     * The ref index for the Prm Text.
     */
    private String _prmTextRef;
    /**
     * The min value.
     */
    private int _minValue;
    /**
     * The maximum Value.
     */
    private int _maxValue;

    // private String[] _values;

    /**
     * @param gsdSlaveModel
     *            The Parent GSD Slave Model.
     * @param index
     *            The ref index of this ext user prm data.
     * @param text
     *            The Name/Desc of this ext user prm data.
     */
    public ExtUserPrmData(final GsdSlaveModel gsdSlaveModel, final String index, final String text) {
        _gsdSlaveModel = gsdSlaveModel;
        _index = index;
        setText(text);
    }

    /**
     * 
     * @return The ref index of this ext user prm data.
     */
    public final String getIndex() {
        return _index;
    }

    /**
     * 
     * @param index
     *            Set the ref index of this ext user prm data.
     */
    public final void setIndex(final String index) {
        _index = index;
    }

    /**
     * 
     * @return The Name/Desc of this ext user prm data.
     */
    public final String getText() {
        return _text;
    }

    /**
     * 
     * @param text
     *            Set the Name/Desc of this ext user prm data.
     */
    public final void setText(final String text) {
        _text = text.split(";")[0].trim();
    }

    /**
     * The dataType of this ext user prm data as plain text.<br>
     * (e.G. Bit(1), BitArea(4-7), UnsignedX)
     * 
     * @return the plain text dataType.
     */
    public final String getDataType() {
        return _dataType;
    }

    /**
     * 
     * @param dataType
     *            set the plain text DataType.
     */
    public final void setDataType(final String dataType) {
        String[] split = dataType.split("[\\(\\)]");
        if (split.length > 1) {
            if (split[1].contains("-")) {
                split = split[1].split("-");
                if (split.length == 2) {
                    setValueRange(split[0], split[1]);
                }
            } else {
                setValueRange(split[1], split[1]);
            }

        }

        _dataType = dataType;
    }

    /**
     * 
     * @return the default value.
     */
    public final int getDefault() {
        return _default;
    }

    /**
     * Set a numeric int value, given as string.
     * 
     * @param def
     *            set the default value.
     */
    public final void setDefault(final String def) {
        try {
            _default = Integer.parseInt(def);
        } catch (NumberFormatException nfe) {
            _default = 0;
        }
    }

    /**
     * 
     * @return The lowest bit to manipulate.
     */
    public final int getMinBit() {
        return _minBit;
    }

    /**
     * 
     * @param minBit
     *            Set the lowest bit to manipulate.
     */
    public final void setMinBit(final String minBit) {
        try {
            _minBit = Integer.parseInt(minBit);
        } catch (NumberFormatException nfe) {
            _minBit = 0;
        }
    }

    /**
     * 
     * @return The highest bit to manipulate.
     */
    public final int getMaxBit() {
        return _maxBit;
    }

    /**
     * 
     * @param maxBit
     *            Set the highest bit to manipulate.
     */
    public final void setMaxBit(final String maxBit) {
        try {
            _maxBit = Integer.parseInt(maxBit);
        } catch (NumberFormatException nfe) {
            _maxBit = 0;
        }
    }

    /**
     * @return minimum Value;
     */
    public final int getMinValue() {
        return _minValue;
    }

    /**
     * @param minValue
     *            Set the minimum Value.
     * @param maxValue
     *            Set the maximum Value.
     */
    public final void setValueRange(final String minValue, String maxValue) {
        int min;
        int max;
        try {
            min = Integer.parseInt(minValue);
            max = Integer.parseInt(maxValue);
        } catch (NumberFormatException nfe) {
            min = 0;
            max = 0;
        }

        assert (min <= max);
        setValues(new String[] { minValue, maxValue });
    }

    /**
     * @return maximum Value;
     */
    public final int getMaxValue() {
        return _maxValue;
    }

    /**
     * @param maxValue
     *            Set the maximum Value.
     */
    public final void setMaxValue(final String maxValue) {
        try {
            _maxValue = Integer.parseInt(maxValue);
        } catch (NumberFormatException nfe) {
            _maxValue = 0;
        }
    }

    /**
     * @return The Parameter Text Reference.
     */
    public final String getPrmTextRef() {
        return _prmTextRef;
    }

    /**
     * 
     * @param prmTextRef
     *            Set the Parameter Text Reference.
     */
    public final void setPrmTextRef(final String prmTextRef) {
        _prmTextRef = prmTextRef;
    }

    /**
     * 
     * @return The Parameter Text Map.
     */
    public final HashMap<Integer, PrmText> getPrmText() {
        return _gsdSlaveModel.getPrmTextMap().get(getPrmTextRef());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return getIndex() + " : " + getText()+"("+getDataType()+")";
    }

    public void setValues(String[] values) {
        // _values = values.clone();
        if (values != null && values.length > 0) {
            _minValue = Integer.parseInt(values[0]);
            _maxValue = Integer.parseInt(values[values.length - 1]);
        }
        values = null;

    }

}
