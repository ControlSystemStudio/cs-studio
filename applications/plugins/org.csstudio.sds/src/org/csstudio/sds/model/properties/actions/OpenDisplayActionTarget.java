package org.csstudio.sds.model.properties.actions;

import org.csstudio.sds.model.IOption;

/**
 * Targets for actions that open displays in run mode.
 *
 * @author Sven Wende
 *
 */
public enum OpenDisplayActionTarget implements IOption {

    SHELL("shell"),

    VIEW("view");

    private String _id;

    private OpenDisplayActionTarget(String id) {
        _id = id;
    }

    public String getIdentifier() {
        return _id;
    }
}
