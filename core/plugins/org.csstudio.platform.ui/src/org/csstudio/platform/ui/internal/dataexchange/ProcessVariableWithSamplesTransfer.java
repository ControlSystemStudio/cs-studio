/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.platform.ui.internal.dataexchange;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.IStringValue;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.IValue.Quality;
import org.csstudio.platform.model.IProcessVariableWithSamples;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 *  * Drag-and-Drop transfer type for <code>IProcessVariableWithSample</code>.
 * <p>
 * This transfer type expects the data to transfer to implement the
 * <code>IProcessVariableWithSample</code> interface, and the resulting data
 * is provided as an array of <code>ProcessVariableWithSample</code>.
 * <p>
 * Most of this implementation is from the javadoc for ByteArrayTransfer.
 *
 * Order of sending and reciving elements
 *  - Header
 *  1. PV Name [String]
 *  2. TypeID [String]
 *  3. Number of Samples [int]
 *  - IValues
 *  4. Format [String]
 *  -- MetaData
 *  5. MetaDataType
 *  --- Nummeric
 *  6. Precision
 *  7. Units
 *  8. AlarmHigh
 *  9. AlarmLow
 * 10. DisplayHigh
 * 11. DisplayLow
 * 12. WarnHigh
 * 13. WarnLow
 * --- Enumerte
 *  6. Size of Enumerate
 *  7- All Enumerates Status elements
 * --
 * 14.Quality Name
 * 15.Quality Ordinal
 * 16.
 

 * x. ValueTyp [enum ValueTyp]
 * x. Status [String]
 * 
 *  
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 06.06.2007
 */
public final class ProcessVariableWithSamplesTransfer extends ByteArrayTransfer {
    /**
     * 
     */
    private static ProcessVariableWithSamplesTransfer _singletonInstance = new ProcessVariableWithSamplesTransfer();
    /**
     * 
     */
    private static final String TYPE_NAME = "pv_with_samples_data";
    /**
     *
     */
    private static final int TYPE_ID = registerType(TYPE_NAME);
    /**
     * 
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 06.06.2007
     */
     private static enum ValueTyp {
         /**
          * Double Value Typ.
          */
         Double,
         /**
          * String Value Typ.
          */
         String,
         /**
          * IValue Typ.
          */
         IValue
         // TODO add all Typs
    }
    /**
     * 
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 06.06.2007
     */
    private static enum MetaData {
        Enumerate,
        Numeric
    }
             

    private ByteArrayOutputStream out;
    private DataOutputStream writeOut;
    /**
     * 
     */
    private ProcessVariableWithSamplesTransfer() {
    }

    /** @return The singleton instance of the ArchiveDataSourceTransfer. */
    public static ProcessVariableWithSamplesTransfer getInstance() {
        return _singletonInstance;
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
     */
    /**
     * @return TypeID 
     */
    @Override
    protected int[] getTypeIds() {
        return new int[] { TYPE_ID };
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
     */
    /**
     * @return Type Name
     */
    @Override
    protected String[] getTypeNames() {
        return new String[] { TYPE_NAME };
    }

    /**
     * @param object Transfer Object
     * @param transferData Transfer Data Typ 
     * 
     */
    @Override
    public void javaToNative(final Object object, final TransferData transferData) {
        if (!isSupportedType(transferData)) {
            return;
        }

        IProcessVariableWithSamples[] data;
        if (object == null) {
            return;
        }
        if (object instanceof IProcessVariableWithSamples[]){
            data = (IProcessVariableWithSamples[]) object;
        }else if(object instanceof IProcessVariableWithSamples){
            data = new IProcessVariableWithSamples[] {((IProcessVariableWithSamples) object)};
        }else {
            return;
        }
        out = new ByteArrayOutputStream();
        writeOut = new DataOutputStream(out);
        for (IProcessVariableWithSamples samples : data) {
            IValue[] values = samples.getSamples();
            fillHeader(samples);
            for (IValue value : values) {
                ValueTyp valueTyp;
                if (value instanceof IDoubleValue) {
                    valueTyp = ValueTyp.Double;
                    IDoubleValue doubleValue = (IDoubleValue) value;
                    doubleValue.getMetaData();
                    doubleValue.getQuality();
                    
                }else if (value instanceof IStringValue) {
                    valueTyp = ValueTyp.String;
                    IStringValue stringValue = (IStringValue) value;
//                  TODO Implemets other IStringValues   
                }else {// TODO Implemets other I...Values
                    valueTyp = ValueTyp.IValue;
                }
                fillValue(value, valueTyp);
            }
        }
    }
    
    /**
     * @param value the IValue
     * @param valueTyp Value Typ
     */
    private void fillValue(final IValue value, final ValueTyp valueTyp){
        send(value.format());
        // MetaDate
        IMetaData md = value.getMetaData();
        if (md instanceof INumericMetaData) {
            INumericMetaData nmd = (INumericMetaData) md;
            send(MetaData.Numeric.name());
            send(nmd.getPrecision());
            send(nmd.getUnits());
            send(nmd.getAlarmHigh());
            send(nmd.getAlarmLow());
            send(nmd.getDisplayHigh());
            send(nmd.getDisplayLow());
            send(nmd.getWarnHigh());
            send(nmd.getWarnLow());
        }else if (md instanceof IEnumeratedMetaData) {
            IEnumeratedMetaData emd = (IEnumeratedMetaData) md;
            send(MetaData.Enumerate.name());
            String[] states = emd.getStates();
            send(states.length);
            for (String string : states) {
                send(string);
            }
        }
        Quality quality = value.getQuality();
        send(quality.name());
        send(quality.ordinal());
        ISeverity serv = value.getSeverity();
        send(valueTyp.name());

        send(value.getStatus());


    }

    /**
     * @param samples Samples 
     */
    private void fillHeader(final IProcessVariableWithSamples samples){
        send(samples.getName());
        send(samples.getTypeId());
        send(samples.size());
    }

    @Override
    public Object nativeToJava(final TransferData transferData) {
        return null;
        //TODO
    }
    /**
     * @param string write to sendStream 
     */
    private void send(final String string) {
        send(string.getBytes());
    }
    /**
     * @param integer write to sendStream 
     */
    private void send(final int integer) {
        send(new Integer(integer).toString().getBytes());
    }

    /**
     * @param doub write to sendStream
     */
    private void send(final double doub) {
        send(new Double(doub).toString().getBytes());
    }

    /**
     * @param buffer write to sendStream 
     */
    private void send(final byte[] buffer) {
        try {
            writeOut.writeInt(buffer.length);
            writeOut.write(buffer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
