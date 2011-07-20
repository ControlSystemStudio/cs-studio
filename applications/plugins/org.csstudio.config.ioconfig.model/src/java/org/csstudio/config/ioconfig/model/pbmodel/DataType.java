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
 * $Id: DataType.java,v 1.2 2009/12/08 07:36:02 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel;

import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 25.11.2008
 */
public enum DataType {
    
    /**
     * Data Type for a single Bit.  
     */
    BIT(1, 1,"BIT",Messages.getString("DataType.Bit")), //$NON-NLS-1$
    /**
     * Data Type for a one byte long Integer.   
     */
    INT8(2, 8,"INT8",Messages.getString("DataType.INT8"),BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT), //$NON-NLS-1$
    /**
     * Data Type for a two byte long Integer.   
     */
    INT16(3, 16,"INT16",Messages.getString("DataType.INT16"),BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT), //$NON-NLS-1$
    /**
     * Data Type for a byte long unsigned Integer.   
     */
    UINT8(4, 8,"UNSIGN8",Messages.getString("DataType.UINT8"),BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT), //$NON-NLS-1$
    /**
     * Data Type for a two byte long unsigned Integer.   
     */
    UINT16(5, 16,"UNSIGN16",Messages.getString("DataType.UINT16"),BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT), //$NON-NLS-1$
    /**
     * Data Type for a four byte long unsigned Integer.   
     */
    UINT32(6, 32,"UNSIGN32",Messages.getString("DataType.UINT32"),BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT), //$NON-NLS-1$
    /**
     * Data Type for a Floating point.   
     */
    FLOAT(7, 32,"FLOAT",Messages.getString("DataType.FLOAT"),BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT), //$NON-NLS-1$
    /**
     * Data Type for a Floating point with a status bytes.   
     */
    DS33(8, 40,"DS-33",Messages.getString("DataType.DS33"),FLOAT,UINT8), //$NON-NLS-1$
    /**
     * A simple Channel.
     */
    SIMPLE(9, 8,"Simple", Messages.getString("DataType.Simple"),INT8), //$NON-NLS-1$
    /**
     * Data Type for a four byte long signed Integer.   
     */
    INT32(10, 32,"INT32",Messages.getString("DataType.INT32"),BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT), //$NON-NLS-1$
    /**
     * Wago analog input unipolar.
     */
    WAGO_AI(11, 16,"WAGO_AI", Messages.getString("DataType.WAGO_AI"), UINT16), //$NON-NLS-1$
    /**
     * Wago analog input bipolar 
     */
    WAGO_AI2(12, 16,"WAGO_AI2", Messages.getString("DataType.WAGO_AI2"), UINT16), //$NON-NLS-1$
    /**
     * Wago RTD temperature platinum 
     */
    WAGO_PT(13, 16,"WAGO_PT", Messages.getString("DataType.WAGO_PT"), UINT16),  //$NON-NLS-1$
    /**
     * WAGO analog input 4-20 mA.
     */
    WAGO_AILVZ(14, 16,"WAGO_AILVZ", Messages.getString("DataType.WAGO_AILVZ"), UINT16), //$NON-NLS-1$
    /**
     * Data Type for a Byte with a status bytes.   
     */
    DS33_1(15, 16,"DS-33-1",Messages.getString("DataType.DS33-1"),BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT,BIT); //$NON-NLS-1$
    
    private static final Map<Integer, DataType> ID_MAP = Maps.newHashMapWithExpectedSize(DataType.values().length);
    static {
        for (DataType type : DataType.values()) {
            Integer key = Integer.valueOf(type.getId());
            if (ID_MAP.containsKey(key)) {
                throw new IllegalStateException("Type ids for " + DataType.class.getName() + " should have been unique.");
            }
            ID_MAP.put(key, type);
        }
    }
    
    /**
     * The id (instead of ordinal() which is potentially more unsafe)
     */
    private final int _id;
    
    /**
     * The size in Bit.
     */
    private int _size;
    /**
     * The long description.
     */
    private String _desc;
    /**
     * The structure of the Data-Type. 
     */
    private final DataType[] _structure;
    /**
     * The default low range.
     */
    private String _low;
    /**
     * The default high range.
     */
    private String _high;
    
    private final String _type;
    
    

    /**
     * 
     * @param sizeOfBit The size in bit.
     * @param desc The long description.
     */
    private DataType(final int id,
                     final int sizeOfBit, 
                     @Nonnull final String type, 
                     @Nonnull final String desc, 
                     @Nonnull DataType...structure){
        this(id, sizeOfBit,type, desc, null, null, structure);
    }

    
    /**
     * 
     * @param sizeOfBit The size in bit.
     * @param desc The long description.
     */
    private DataType(final int id, 
                     final int sizeOfBit, 
                     @Nonnull final String type, 
                     @Nonnull final String desc, 
                     @CheckForNull Long low, 
                     @CheckForNull Long high, 
                     @Nonnull DataType...structure){
        _id = id;
        _size = sizeOfBit;
        _type = type;
        _desc = desc;
        if(low != null) {
            _low = ",L="+low;
        }else {
            _low = "";
        }
        if(high != null) {
            _high = ",H="+high;
        }else {
            _high = "";
        }

        _structure = structure;
    }
    
    /**
     * @return the id as int of this type
     */
    public int getId() {
        return _id;
    }
    
    /**
     * 
     * @return Data Type Name.
     */
    @Nonnull
    public String getType() {
        return _type;
    }


    /**
     * 
     * @return the size in bit.
     */
    public int getBitSize(){
        return _size;
    }
    
    /**
     * The size is the full used Byte.
     * A DataType was only used one bit return 0 Byte. 
     * 
     * @return the size in Byte
     */
    public int getByteSize(){
        return _size/8;
    }
    
    /**
     * Get the Size as String with unit.
     * @return The size.
     */
    @Nonnull 
    public String getSize(){
        if(_size%1024==0) {
            return _size/1024+" kByte";
        }else if(_size%8==0) {
            return _size/8+" Byte";
        } 
        return _size+" Bit";
    }
    
    /**
     * 
     * @return the long description.
     */
    @Nonnull 
    public String getDescription(){
        return _desc;
    }
    
    /**
     * 
     * @return all names.
     */
    @Nonnull 
    public static String[] getNames(){
        String[] names = new String[DataType.values().length];
        for (int i = 0; i < DataType.values().length; i++) {
         names[i] = DataType.values()[i].getType();   
        }
        return names;
    }

    /**
     * 
     * @return the Data-Type structure.
     */
    @Nonnull 
    public DataType[] getStructure() {
        return _structure;
    }
    
    /**
     * 
     * @return the default low range.
     */
    @Nonnull 
    public  String getDefaultLow(){
     return _low;   
    }

    /**
     * 
     * @return the default high range.
     */
    @Nonnull 
    public  String getDefaultHigh(){
        return _high;   
       }


    /**
     * @param channelId
     * @return
     */
    @Nonnull
    public static DataType forId(final int channelId) {
        DataType type = ID_MAP.get(Integer.valueOf(channelId));
        if (type == null) {
            throw new IllegalArgumentException("No " + DataType.class.getName() + " object exists for id " + channelId );
        }
        return type;
    }
    
    
}
