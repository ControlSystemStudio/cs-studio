package org.csstudio.utility.pvmanager.widgets;

import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;

/**
 * @author shroffk
 *
 */
public interface ProcessVariableAdaptable {

    public Collection<ProcessVariable> toProcessVariables();
}
