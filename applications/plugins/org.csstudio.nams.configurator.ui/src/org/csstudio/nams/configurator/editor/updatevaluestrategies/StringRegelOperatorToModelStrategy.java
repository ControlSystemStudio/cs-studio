
package org.csstudio.nams.configurator.editor.updatevaluestrategies;

import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.eclipse.core.databinding.UpdateValueStrategy;

public class StringRegelOperatorToModelStrategy extends UpdateValueStrategy {
	@Override
	public Object convert(final Object value) {
		return StringRegelOperator.valueOf((String) value);
	}
}
