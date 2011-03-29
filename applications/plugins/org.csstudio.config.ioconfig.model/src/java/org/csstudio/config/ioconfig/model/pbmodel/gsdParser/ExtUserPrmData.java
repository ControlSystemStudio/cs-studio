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
 * $Id: ExtUserPrmData.java,v 1.3 2010/08/20 13:33:08 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.3 $
 * @since 21.07.2008
 */
public class ExtUserPrmData {
    
    /**
     * The Parent GSD Slave Model.
     */
    private final ParsedGsdFileModel _gsdFileModel;
    
    /**
     * The ref index of this ext user prm data.
     */
    private final Integer _index;
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
    private Integer _prmTextRef;
    /**
     * The min value.
     */
    private int _minValue;
    /**
     * The maximum Value.
     */
    private int _maxValue;
    
    /**
     * @param gsdSlaveModel
     *            The Parent GSD Slave Model.
     * @param index
     *            The ref index of this ext user prm data.
     * @param text
     *            The Name/Desc of this ext user prm data.
     */
    public ExtUserPrmData(@Nonnull final ParsedGsdFileModel gsdFileModel,
                          @Nonnull final Integer index,
                          @Nonnull final String text) {
        _gsdFileModel = gsdFileModel;
        _index = index;
        setText(text);
    }
    
    /**
     *
     * @return The ref index of this ext user prm data.
     */
    @Nonnull 
    public final Integer getIndex() {
        return _index;
    }
    
    /**
     *
     * @return The Name/Desc of this ext user prm data.
     */
    @Nonnull 
    public final String getText() {
        return _text;
    }
    
    /**
     *
     * @param text
     *            Set the Name/Desc of this ext user prm data.
     */
    public final void setText(@Nonnull final String text) {
        if ((text != null) && !text.isEmpty()) {
            _text = text.split(";")[0].trim();
        } else {
            _text = "";
        }
    }
    
    /**
     * The dataType of this ext user prm data as plain text.<br>
     * (e.G. Bit(1), BitArea(4-7), UnsignedX)
     *
     * @return the plain text dataType.
     */
    @Nonnull 
    public final String getDataType() {
        if (_dataType == null) {
            _dataType = "";
        }
        return _dataType;
    }
    
    /**
     *
     * @param dataType
     *            set the plain text DataType.
     */
    public final void setDataType(@Nonnull final String dataType) {
        String[] split = dataType.split(";")[0].split("[\\(\\)]");
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
    public final void setDefault(@Nonnull final String def) {
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
    public final void setMinBit(@Nonnull final String minBit) {
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
    public final void setMaxBit(@Nonnull final String maxBit) {
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
    public final void setValueRange(@Nonnull final String minValue, @Nonnull final String maxValue) {
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
        setValues(new String[] {minValue, maxValue });
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
    public final void setMaxValue(@Nonnull final String maxValue) {
        try {
            _maxValue = Integer.parseInt(maxValue);
        } catch (NumberFormatException nfe) {
            _maxValue = 0;
        }
    }
    
    /**
     * @return The Parameter Text Reference.
     */
    @Nonnull
    public final Integer getPrmTextRef() {
        return _prmTextRef;
    }
    
    /**
     *
     * @param integer
     *            Set the Parameter Text Reference.
     */
    public final void setPrmTextRef(@Nonnull final Integer prmTextRef) {
        _prmTextRef = prmTextRef;
    }
    
    /**
     *
     * @return The Parameter Text Map.
     */
    @CheckForNull
    public final PrmText getPrmText() {
        return _gsdFileModel.getPrmTextMap().get(getPrmTextRef());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull 
    public final String toString() {
        return getIndex() + " : " + getText() + "(" + getDataType() + ")";
    }
    
    public void setValues(@Nullable String[] values) {
        // _values = values.clone();
        if ((values != null) && (values.length > 0)) {
            _minValue = Integer.parseInt(values[0]);
            _maxValue = Integer.parseInt(values[values.length - 1]);
        }
        values = null;
        
    }
    
}
