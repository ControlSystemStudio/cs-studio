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

/**
 *
 */
package org.csstudio.dal.tine;

import org.apache.log4j.Logger;
import org.csstudio.dal.DataExchangeException;

import de.desy.tine.dataUtils.TDataType;
import de.desy.tine.definitions.TFormat;
import de.desy.tine.types.NAME16;
import de.desy.tine.types.NAME32;
import de.desy.tine.types.NAME48;
import de.desy.tine.types.NAME64;
import de.desy.tine.types.NAME8;
import de.desy.tine.types.TCompoundDataObject;

/**
 * @author Tilen Kusterle, Cosylab
 *
 */
public class StringPropertyProxyImpl extends PropertyProxyImpl<String> {

    private Object value;
    private int length;

    /**
     * Constructs a new StringPropertyProxy.
     * @param name
     */
    public StringPropertyProxyImpl(String name, TINEPlug plug) {
        super(name, plug);
        try {
            this.length = (Integer)getCharacteristic("sequenceLength");
        } catch (DataExchangeException e) {
            Logger.getLogger(this.getClass()).error("Getting characteristic failed.", e);
        }
        switch (TINEPlug.getInstance().getTFormat(getUniqueName()).getValue()) {
            case TFormat.CF_TEXT: {
                this.value = new char[this.length];
                break;
            }
            case TFormat.CF_NAME8 : {
                this.value= new NAME8[]{new NAME8()};
                break;
            }
            case TFormat.CF_NAME16 : {
                this.value= new NAME16[]{new NAME16()};
                break;
            }
            case TFormat.CF_NAME32 : {
                this.value= new NAME32[]{new NAME32()};
                break;
            }
            case TFormat.CF_NAME48 : {
                this.value= new NAME48[]{new NAME48()};
                break;
            }
            case TFormat.CF_NAME64 : {
                this.value= new NAME64[]{new NAME64()};
                break;
            }
            default: {
                this.value = new StringBuffer(this.length);
            }
        }
    }

    /* (non-Javadoc)
     * @see de.desy.css.dal.tine.PropertyProxyImpl#convertDataToObject(java.lang.Object)
     */
    @Override
    protected Object convertDataToObject(String data) {
        switch (this.dataFormat.getValue()) {
            case TFormat.CF_TEXT: {
                return data.toCharArray();
            }
            case TFormat.CF_NAME8 : {
                return new NAME8[]{new NAME8(data)};
            }
            case TFormat.CF_NAME16 : {
                return new NAME16[]{new NAME16(data)};
            }
            case TFormat.CF_NAME32 : {
                return new NAME32[]{new NAME32(data)};
            }
            case TFormat.CF_NAME48 : {
                return new NAME48[]{new NAME48(data)};
            }
            case TFormat.CF_NAME64 : {
                return new NAME64[]{new NAME64(data)};
            }
            default: {
                return new StringBuffer(data);
            }
        }
    }

    /* (non-Javadoc)
     * @see de.desy.css.dal.tine.PropertyProxyImpl#extractData(de.desy.tine.dataUtils.TDataType)
     */
    @Override
    protected String extractData(TDataType out) {
        if (this.value instanceof StringBuffer) {
            this.value= new StringBuffer(out.getArrayLength());
        }
        out.getData(this.value);
        if (this.value instanceof char[]) {
            return new String((char[])this.value).trim();
        }
        if (this.value instanceof StringBuffer) {
            return new String(this.value.toString().trim());
        }
        if (this.value == null || !this.value.getClass().isArray()) {
            return null;
        }
        TCompoundDataObject tcdo= ((TCompoundDataObject[])this.value)[0];
        return tcdo.toString();
    }

    /* (non-Javadoc)
     * @see de.desy.css.dal.tine.PropertyProxyImpl#getDataObject()
     */
    @Override
    protected Object getDataObject() {
        return this.value;
    }

    /* (non-Javadoc)
     * @see de.desy.css.dal.tine.PropertyProxyImpl#getNumericType()
     */
    @Override
    protected Class getNumericType() {
        return String.class;
    }

}
