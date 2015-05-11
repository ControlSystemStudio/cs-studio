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

package org.csstudio.dal.tine;

import org.apache.log4j.Logger;
import org.csstudio.dal.DataExchangeException;

import de.desy.tine.dataUtils.TDataType;
import de.desy.tine.definitions.TFormat;
import de.desy.tine.types.SPECTRUM;

/**
 *
 * @author Jaka Bobnar, Cosylab
 *
 */
public class DoubleSeqPropertyProxyImpl extends PropertyProxyImpl<double[]>{

    private Object value;
//    private double[] value;
    private int length;

    /**
     * Constructs a new DoubleSeqPropertyProxy.
     * @param name
     */
    public DoubleSeqPropertyProxyImpl(String name, TINEPlug plug) {
        super(name, plug);
//        value = new double[length];
        switch (TINEPlug.getInstance().getTFormat(getUniqueName()).getValue()) {
            case TFormat.CF_SPECTRUM: {
                this.value = new SPECTRUM();
                break;
            }
            default: {
                try {
                    this.length = (Integer)getCharacteristic("sequenceLength");
                } catch (DataExchangeException e) {
                    Logger.getLogger(this.getClass()).error("Getting characteristic failed.", e);
                }
                this.value = new double[this.length];
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see de.desy.css.dal.tine.PropertyProxyImpl#extractData(de.desy.tine.dataUtils.TDataType)
     */
    @Override
    protected double[] extractData(TDataType out) {
        out.getData(this.value);
        if (this.value != null) {
            //            return value;
            //TODO what should be returned for SPECTRUM? float[] d_spect_array?
            if (this.value instanceof SPECTRUM) {
                return null;
            } else {
                return (double[]) this.value;
            }
        } else {
            return new double[this.length];
        }

    }

    /*
     * (non-Javadoc)
     * @see de.desy.css.dal.tine.PropertyProxyImpl#getDataObject()
     */
    @Override
    protected Object getDataObject() {
        return this.value;
    }

    /*
     * (non-Javadoc)
     * @see de.desy.css.dal.tine.PropertyProxyImpl#setDataToObject(java.lang.Object)
     */
    @Override
    protected Object convertDataToObject(double[] data) {
        return data;
    }
    /* (non-Javadoc)
     * @see de.desy.css.dal.tine.PropertyProxyImpl#getNumericType()
     */
    @Override
    protected Class getNumericType() {
        return Double.class;
    }

}
