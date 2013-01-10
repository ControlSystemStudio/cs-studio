package org.csstudio.channel.widgets;

import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;

/**
 * 
 * @author shroffk
 *
 */
public interface ProcessVariableAdaptable {

	public Collection<ProcessVariable> toProcesVariables();
}
