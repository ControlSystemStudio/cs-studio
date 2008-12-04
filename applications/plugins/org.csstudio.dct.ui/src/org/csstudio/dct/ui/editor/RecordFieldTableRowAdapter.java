package org.csstudio.dct.ui.editor;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.ChangeFieldValueCommand;
import org.csstudio.dct.ui.Activator;
import org.csstudio.dct.util.ReplaceAliasesUtil;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

public class RecordFieldTableRowAdapter extends AbstractTableRowAdapter<IRecord> {
	private String fieldKey;

	public RecordFieldTableRowAdapter(IRecord delegate, String fieldKey, CommandStack commandStack) {
		super(delegate, commandStack);
		this.fieldKey = fieldKey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RGB doGetForegroundColorForValue(IRecord delegate) {
		Map<String, Object> localFields = delegate.getFields();
		boolean inherited = !localFields.containsKey(fieldKey);
		RGB rgb = inherited ? ColorSettings.INHERITED_RECORD_FIELD_VALUE : ColorSettings.OVERRIDDEN_RECORD_FIELD_VALUE;
		return rgb;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String doGetKey(IRecord delegate) {
		return fieldKey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String doGetKeyDescription(IRecord delegate) {
		// TODO: Maybe deliver field description from DBD-Files.
		return fieldKey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doGetValue(IRecord delegate) {
		return delegate.getFinalFields().get(fieldKey);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doGetValueForDisplay(IRecord delegate) {
		Object result = doGetValue(delegate);
		
		try {
			String input = delegate.getFinalFields().get(fieldKey).toString();
			
			Map<String, String> params = ((IContainer)delegate.getContainer()).getFinalParameterValues();
					
			result = ReplaceAliasesUtil
					.createCanonicalName(
							input,
							params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command doSetValue(IRecord delegate, Object value) {
		return new ChangeFieldValueCommand(delegate, fieldKey, value); 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Image doGetImage(IRecord delegate) {
		return CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/field.png");
	}

	
}
