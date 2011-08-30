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
 * $Id: SlaveCfgData.java,v 1.1 2009/08/26 07:08:44 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdFileParser;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 16.10.2008
 */
public class SlaveCfgData {
    
    /**
     * Have a Input Data.
     */
    private boolean _input;
    /**
     * Have a Output Data.
     */
    private boolean _output;
    /**
     * If the data Size Word or Byte.
     */
    private boolean _wordSize;
    /**
     * If true the data use at Module mode.<br>
     * If false the data can used by byte/word.<br>
     */
    private boolean _consistency;
    
    private int _size;
    
    private final List<Integer> _parameter = new ArrayList<Integer>();
    
    /**
     * 
     * Constructor for Compact Format
     */
    public SlaveCfgData(@Nonnull final int parameter) {
        setCompactFormat(parameter);
        _parameter.add(parameter);
    }
    
    /**
     * 
     * Constructor for Special Format
     */
    public SlaveCfgData(@Nonnull final int parameter0, final int parameter1) {
        setSpecialFormat(parameter0, parameter1);
        _parameter.add(parameter0);
        _parameter.add(parameter1);
    }
    
    
    public int getByteLength() {
        return getWordSize() * getSize();
    }
    
    
    /**
     * 
     * @return the complete Number of Data (cumulative In.- and Output).
     */
    public final int getNumber() {
        int number = 0;
        if (isInput()) {
            number = getSize();
        }
        if (isOutput()) {
            number += getSize();
        }
        return number;
    }
    
    @Nonnull
    public String getParameterAsHexString() {
        return GsdFileParser.intList2HexString(_parameter);
    }
    
    /**
     * @return
     */
    public int getSize() {
        return _size;
    }
    
    /**
     * 
     * @return The Size as Bit number.
     */
    public final int getWordSize() {
        if (_wordSize) {
            return 16;
        }
        return 8;
    }
    
    /**
     * Modules in Compact format can have a length up to 16 byte or 16 words. With the consistency
     * the dp-Master have a note to data used. Must the dp-Master interpreted all byte or words
     * together for this module or can the interpreted split to one byte or word. It is task of the
     * master to guarantee the demanded consistency.<br>
     * 
     * @return true use Data consistency (Module Mode) and false when can split the Data (Byte
     *         Mode).
     */
    public final boolean isConsistency() {
        return _consistency;
    }
    
    /**
     * @return true when it have Input Data.
     */
    public final boolean isInput() {
        return _input;
    }
    
    /**
     * @return true when it have Output Data.
     */
    public final boolean isOutput() {
        return _output;
    }
    
    /**
     * @return true when the size is Word and false when Byte.
     */
    public final boolean isWordSize() {
        return _wordSize;
    }
    
    /**
     * Set the Parameter from Compact Format.
     * 
     * Bit 0-3: is size + 1 Bit 4: input Bit 5: output Bit 6: 0:byte, 1: word Bit 7: Consistenz
     * 0:byte, 1: Module
     * 
     * @param parameter
     *            The Slave Config Data Value.<br>
     */
    private void setCompactFormat(final int parameter) {
        /*
         */
        setInput((parameter & 16) == 16);
        setOutput((parameter & 32) == 32);
        setWordSize((parameter & 64) == 64);
        setConsistency((parameter & 128) == 128);
        setSize((parameter & 15) + 1);
    }
    
    /**
     * @param consistency
     *            Set the consistency.
     */
    private void setConsistency(final boolean consistency) {
        _consistency = consistency;
    }
    
    /**
     * 
     * @param input
     *            Set true have it Input Data.
     */
    private void setInput(final boolean input) {
        _input = input;
    }
    
    /**
     * 
     * @param output
     *            Set true have it Output Data.
     */
    private void setOutput(final boolean output) {
        _output = output;
    }
    
    /**
     * @param size
     */
    private void setSize(final int size) {
        _size = size;
    }
    
    /**
     * Set the Parameter from Special Format.
     * @param parameter
     *            The Slave Config Data Value.<br>
     */
    private void setSpecialFormat(final int parameter, final int parameter2) {
        setInput((parameter & 64) == 64);
        setOutput((parameter & 128) == 128);
        setWordSize((parameter2 & 64) == 64);
        setConsistency((parameter2 & 128) == 128);
        setSize((parameter2 & 63) + 1);
    }
    
    /**
     * 
     * @param wordSize
     *            Set true if Data size is word.
     */
    private void setWordSize(final boolean wordSize) {
        _wordSize = wordSize;
    }
}
