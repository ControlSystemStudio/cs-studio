package org.csstudio.display.pvtable;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class MeasuresActionVisibilitySourceProvider extends AbstractSourceProvider {

    public final static String MY_STATE = "org.csstudio.display.pvtable.visible";
    public final static String VISIBLE = "VISIBLE";
    public final static String INVISIBLE = "INVISIBLE";
    private boolean visible = true;

    @Override
    public void dispose() {
    }

    // We could return several values but for this example one value is
    // sufficient
    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { MY_STATE };
    }

    // You cannot return NULL
    @Override
    public Map<String, String> getCurrentState() {
        Map<String, String> map = new HashMap<>(1);
        String value = visible ? VISIBLE : INVISIBLE;
        map.put(MY_STATE, value);
        return map;
    }

    // This method can be used from other commands to change the state
    // Most likely you would use a setter to define directly the state and not
    // use this toogle method
    // But hey, this works well for my example
    public void setVisible(boolean isVisible) {
        System.out.println("MeasuresActionVisibilitySourceProvider.setVisible() " + isVisible);
        visible = isVisible;
        String value = visible ? VISIBLE : INVISIBLE;
        fireSourceChanged(ISources.WORKBENCH, MY_STATE, value);
    }

}
