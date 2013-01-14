/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.model.siemens;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.DataType;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 20.07.2011
 */
public class WinModChannel {

    private final char[] _convertedChannelType = new char[2];
    private int _lines;
    private String _desc;
    private char _io1;
    private String _mbbChannelType;
    private String _def;
    private String _io2;
    private Short _bit;
    private int _bytee;

    /**
     * Constructor.
     * @param channelDBO
     */
    public WinModChannel(@Nonnull final ChannelDBO channelDBO) {
        setIsInput(channelDBO.isInput());
        setIsDigital(channelDBO);
        addDescription(channelDBO);
        setByteNo(channelDBO);
    }

    /**
     * @param channelDBO
     */
    public void addDescription(@Nonnull final ChannelDBO channelDBO) {
        final String description = channelDBO.getDescription();
        if(description!=null) {
            _desc += description.replaceAll("[\r\n]", " ");
        }
    }

    /**
     * @return
     */
    public short getBit() {
        return _bit;
    }

    /**
     * @return
     */
    public int getByteNo() {
        return _bytee;
    }

    @Nonnull
    public String getConvertedChannelType() {
        return String.valueOf(_convertedChannelType);
    }

    /**
     * @return
     */
    @Nonnull
    public String getDef() {
        return _def;
    }

    /**
     * @return
     */
    @Nonnull
    public String getDesc() {
        return _desc;
    }

    /**
     * @return
     */
    @Nonnull
    public String getIO() {
        return _io1+_io2;
    }

    /**
     * @return
     */
    public int getLineSize() {
        return _lines;
    }

    /**
     * @return
     */
    @Nonnull
    public String getMbbChannelType() {
        return _mbbChannelType;
    }

    /**
     * @param channelDBO
     */
    public void setByteNo(@Nonnull final ChannelDBO channelDBO) {
        if(channelDBO.isDigital()) {
            _bit = channelDBO.getSortIndex();
            //          bytee = bit / 8;
            //          if(bytee>0) {
            //              bit = (short) (bit - (8*bytee));
            //          }

            //          bytee = channelDBO.getFullChannelNumber();

            _bytee = channelDBO.getChannelNumber();
        } else {
            _bit = 0;
            //          bytee = channelDBO.getStruct();
            //            _bytee = channelDBO.getFullChannelNumber();
            _bytee = channelDBO.getChannelNumber();
        }
    }
    /**
     * @param channelType
     */
    // CHECKSTYLE OFF: CyclomaticComplexity
    public void setChannelType(@Nonnull final DataType channelType) {
        switch (channelType) {
            case BIT:
                _convertedChannelType[0] = 'B'; // Binary
                break;
            case INT8:
            case UINT8:
                _convertedChannelType[0] = 'A'; // Analog
                break;
            case INT16:
            case UINT16:
                _convertedChannelType[0] = 'A'; // Analog
                _lines = 2;
                break;
            case INT32:
            case UINT32:
                _lines = 4;
                _convertedChannelType[0] = 'A'; // Analog
                break;
            case DS33:
                _lines = channelType.getByteSize();
                _desc = "> " + channelType;
                _convertedChannelType[0] = 'A'; // Analog
                break;
            default:
                _convertedChannelType[0] = 'A'; // Analog
                break;
        }
    }
    // CHECKSTYLE ON: CyclomaticComplexity

    /**
     * @param string
     */
    public void setDef(@Nonnull final String def) {
        _def = def;
    }

    /**
     * @param c
     */
    public void setIO2(@Nonnull final String io2) {
        _io2 = io2;
    }

    /**
     * @param channelDBO
     */
    public void setIsDigital(@Nonnull final ChannelDBO channelDBO) {
        final DataType channelType = channelDBO.getChannelType();
        if (channelDBO.isDigital()) {
            _def = "0";
        } else {
            _def = "0,00";
            final int byteSize = channelType.getByteSize();
            switch (byteSize) {
                case 1:
                    _io2 = "B"; // Byte
                    break;
                case 2:
                    _io2 = "W"; // Word
                    break;
                case 4:
                    _io2 = "D"; // Double Word
                    break;
                case 5:
                    _io2 = "D"; // hier kommt sicherlich was anderes hin!
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * @param isInput
     */
    public void setIsInput(final boolean isInput) {
        if (isInput) {
            _io1 = 'E'; // Eingang
            _convertedChannelType[1] = 'I'; // Input
            _mbbChannelType = "DI";
        } else {
            _io1 = 'A'; // Ausgang
            _convertedChannelType[1] = 'O'; // Output
            _mbbChannelType = "DO";
        }
    }

    /**
     * @return
     */
    public boolean single() {
        return _lines>1;
    }



}
