package org.csstudio.saverestore.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.saverestore.ui.ReadbackProvider;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class Readback implements ReadbackProvider {

    @Override
    public Map<String, String> getReadbacks(List<String> setpointNames) {
        Map<String, String> ret = new HashMap<>();
        setpointNames.forEach(e -> ret.put(e, e));
        Display.getDefault()
            .syncExec(() -> FXMessageDialog.openInformation(
                PlatformUI.getWorkbench().getWorkbenchWindows()[0]
                    .getShell(),
                "Get Readbacks", "I am a dummy implementation of the readback names provider, which returns"
                    + " the list of readbacks identical to the list of setpoints. At some point I will evolve into a smarter thing."));
        return ret;
    }

}
