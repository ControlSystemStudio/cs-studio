package org.csstudio.scan.server.pvaccess;

import org.epics.pvdata.pv.PVBoolean;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.Scalar;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Type;

/**
 * Created with IntelliJ IDEA.
 * User: berryman
 * Date: 5/20/13
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class PVRequestUtils {

    public static boolean getProcess(PVStructure pvRequest) {
        PVField pvField = pvRequest.getSubField("record._options.process");
        if(pvField==null || pvField.getField().getType()!=Type.scalar) return false;
        Scalar scalar = (Scalar)pvField.getField();
        if(scalar.getScalarType()==ScalarType.pvString) {
            PVString pvString = (PVString)pvField;
            return (pvString.get().equalsIgnoreCase("true")) ? true : false;
        } else if(scalar.getScalarType()==ScalarType.pvBoolean) {
            PVBoolean pvBoolean = (PVBoolean)pvField;
            return pvBoolean.get();
        }
        return false;
    }
}
