/**
 * 
 */
package org.csstudio.ui.util;

import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;

/**
 * @author shroffk
 * 
 */
public class YAxisProcessVariable {

    private Collection<ProcessVariable> processVariables;

    public YAxisProcessVariable(Collection<ProcessVariable> processVariables) {
	this.processVariables = processVariables;
    }

    public Collection<ProcessVariable> getProcessVariables() {
	return this.processVariables;
    }
}
