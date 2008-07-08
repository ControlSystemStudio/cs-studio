/**
 * 
 */
package org.csstudio.nams.configurator.editor.updatevaluestrategies;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.eclipse.core.databinding.UpdateValueStrategy;

public class MessageKeyToModelStrategy extends UpdateValueStrategy {
	@Override
	public Object convert(Object value) {
		return MessageKeyEnum.getEnumFor((String) value);
	}
}