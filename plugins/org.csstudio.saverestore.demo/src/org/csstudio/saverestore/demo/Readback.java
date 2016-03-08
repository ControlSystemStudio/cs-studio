/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.Threshold;
import org.csstudio.saverestore.ui.ParametersProvider;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.diirt.vtype.VByte;
import org.diirt.vtype.VDouble;
import org.diirt.vtype.VFloat;
import org.diirt.vtype.VInt;
import org.diirt.vtype.VLong;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VShort;
import org.diirt.vtype.VType;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class Readback implements ParametersProvider {

    @Override
    public Map<String, String> getReadbackNames(List<String> setpointNames) {
        Map<String, String> ret = new HashMap<>();
        setpointNames.forEach(e -> {
            if (e.startsWith("static")) {
                ret.put(e, e.substring(6));
            } else {
                ret.put(e, e);
            }
        });
        Display.getDefault()
            .syncExec(() -> FXMessageDialog.openInformation(
                PlatformUI.getWorkbench().getWorkbenchWindows()[0]
                    .getShell(),
                "Get Readbacks", "I am a dummy implementation of the readback names provider, which returns"
                    + " the list of readbacks identical to the list of setpoints. At some point I will evolve into a smarter thing."));
        return ret;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, Threshold> getThresholds(List<String> pvNames, List<VType> values,
        Optional<BaseLevel> baseLevel) {
        Map<String, Threshold> ret = new HashMap<>();
        pvNames.forEach(e -> ret.put(e, getThreshold(getType())));
        return ret;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends VNumber>[] types = new Class[] { VByte.class, VShort.class, VInt.class, VLong.class,
        VFloat.class, VDouble.class };

    private static Class<? extends VNumber> getType() {
        return types[(int) (Math.random() * 6)];
    }

    private Threshold<?> getThreshold(Class<? extends VNumber> type) {
        if (type.isAssignableFrom(VByte.class)) {
            return new Threshold<>((byte) 20, (byte) -20);
        } else if (type.isAssignableFrom(VShort.class)) {
            return new Threshold<>((short) 20, (short) -20);
        } else if (type.isAssignableFrom(VInt.class)) {
            return new Threshold<>(20, -20);
        } else if (type.isAssignableFrom(VLong.class)) {
            return new Threshold<>(20L, -20L);
        } else if (type.isAssignableFrom(VFloat.class)) {
            return new Threshold<>(20f, -20f);
        } else if (type.isAssignableFrom(VDouble.class)) {
            return new Threshold<>(20., -20.);
        } else {
            return null;
        }
    }

}
