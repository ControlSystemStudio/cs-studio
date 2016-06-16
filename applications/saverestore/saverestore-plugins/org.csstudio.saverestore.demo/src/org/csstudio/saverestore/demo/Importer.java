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

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.saverestore.ui.ValueImporter;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class Importer implements ValueImporter {

    @Override
    public Map<String, VType> getValuesForPVs(List<String> pvNames, Instant timestamp) {
        Map<String, VType> ret = new HashMap<>();
        pvNames.forEach(e -> {
            VType v = ValueFactory.newVDouble(Math.random() * 100);
            ret.put(e, v);
        });
        Display.getDefault()
            .syncExec(() -> FXMessageDialog.openInformation(
                PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell(), "Import Values",
                "I am a dummy implementation of the value importer, which returns"
                    + " the list of random double values. At some point I will evolve into a smarter thing and I"
                    + " will know how to get data from the optics server."));
        return ret;
    }

}
