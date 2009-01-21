package org.csstudio.dct.ui.editor;

import java.util.Map;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.metamodel.IFieldDefinition;
import org.csstudio.dct.metamodel.IMenuDefinition;
import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.ChangeFieldValueCommand;
import org.csstudio.dct.ui.Activator;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.dct.util.ResolutionUtil;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

/**
 * Table adapter for record fields.
 * 
 * @author Sven Wende
 * 
 */
public final class RecordFieldTableRowAdapter extends AbstractTableRowAdapter<IRecord> {
	private String fieldKey;

	/**
	 * Constructor.
	 * 
	 * @param record the record
	 * @param fieldKey the field name
	 */
	public RecordFieldTableRowAdapter(IRecord record, String fieldKey) {
		super(record);
		this.fieldKey = fieldKey;
	}

	/**
	 * Returns the name of the field.
	 * @return the field name
	 */
	public String getFieldKey() {
		return fieldKey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RGB doGetForegroundColorForValue(IRecord delegate) {
		Map<String, Object> localFields = delegate.getFields();
		boolean inherited = !localFields.containsKey(fieldKey);
		RGB rgb = inherited ? ColorSettings.INHERITED_VALUE : ColorSettings.OVERRIDDEN_VALUE;
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
	protected String doGetValue(IRecord delegate) {
		Object o = delegate.getFinalFields().get(fieldKey);
		return o != null ? o.toString() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String doGetValueForDisplay(IRecord record) {
		String result = null;

		Object value = doGetValue(record);

		if (value == null || "".equals(value.toString().trim())) {
			result = "<empty>";
		} else {
			if (record.isInherited()) {
				// resolve functions and parameters
				try {
					result = value.toString();

					result = ResolutionUtil.resolve(value.toString(), record);

					result = DctActivator.getDefault().getFieldFunctionService().evaluate(result, record, fieldKey);

					result = ResolutionUtil.resolve(result, record);
				} catch (Exception e) {
					setError(e.getMessage());
				}
			} else {
				result = value.toString();
			}
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor doGetValueCellEditor(IRecord delegate, Composite parent) {
		CellEditor result = new TextCellEditor(parent);

		IRecordDefinition rdef = delegate.getRecordDefinition();

		if (rdef != null) {
			IFieldDefinition fdef = rdef.getFieldDefinitions(this.fieldKey);

			if (fdef != null) {
				IMenuDefinition mdef = fdef.getMenu();

				if (mdef != null) {
					result = new MenuCellEditor(parent, mdef);
				}
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int doCompareTo(ITableRow row) {
		int result = 0;
		if (row instanceof RecordFieldTableRowAdapter) {
			result = fieldKey.compareTo(((RecordFieldTableRowAdapter) row).fieldKey);
		}

		return result;
	}
}
