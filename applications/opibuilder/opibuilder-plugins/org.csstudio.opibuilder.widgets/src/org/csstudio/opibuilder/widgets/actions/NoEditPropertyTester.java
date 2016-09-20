package org.csstudio.opibuilder.widgets.actions;

import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.eclipse.core.expressions.PropertyTester;

public class NoEditPropertyTester extends PropertyTester {

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (property.equals("isNoEdit")) {
            return PreferencesHelper.isNoEdit();
        }
        return false;
    }

}
