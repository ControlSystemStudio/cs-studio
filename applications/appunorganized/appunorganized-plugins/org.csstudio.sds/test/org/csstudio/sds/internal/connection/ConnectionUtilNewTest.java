package org.csstudio.sds.internal.connection;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.dal.simple.RemoteInfo;
import org.junit.Test;

public class ConnectionUtilNewTest {


    @Test
    public final void testTranslate() {
        IProcessVariableAddress pv1 = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress("dal-epics://Chiller:Pressure:1");
        IProcessVariableAddress pv2 = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress("dal-epics://Chiller:Pressure:1[graphMin]");
        RemoteInfo ri1 = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"EPICS", "Chiller:Pressure:1", null, null);
        RemoteInfo ri2 = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX+"EPICS", "Chiller:Pressure:1", "graphMin", null);
        verifyEquality(ri1, ConnectionUtilNew.translateWithoutCharacteristic(pv1), null);
        verifyEquality(ri2, ConnectionUtilNew.translateWithoutCharacteristic(pv2), "graphMin");
    }

    private void verifyEquality(RemoteInfo r1, RemoteInfo r2, String characteristic) {
        assertEquals(r1.getPlugType(), r2.getPlugType());
        assertEquals(r1.getRemoteName(), r2.getRemoteName());
        assertEquals(characteristic, r1.getCharacteristic());
        assertNull(r2.getCharacteristic());
        assertEquals(r1.getQuery(), r2.getQuery());
    }
}
