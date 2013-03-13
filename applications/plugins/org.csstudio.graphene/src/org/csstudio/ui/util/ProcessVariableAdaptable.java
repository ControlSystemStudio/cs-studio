package org.csstudio.ui.util;

import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;

/**
 * @author shroffk
 *
 */
public interface ProcessVariableAdaptable {

    public Collection<ProcessVariable> toProcessVariables();
}
