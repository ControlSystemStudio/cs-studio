package org.csstudio.utility.toolbox.framework.celleditors;

import org.csstudio.utility.toolbox.entities.ArticleDescription;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public abstract class CustomDialogCellEditor extends DialogCellEditor {

	protected CustomDialogCellEditor(Composite parent) {
		super(parent, SWT.NONE);
	}

	public void fireApplyEditorValueChanged() {
		fireApplyEditorValue();
	}
	
	protected void updateContents1(Object value) {
		Label defaultLabel = getDefaultLabel();
		if (defaultLabel == null) {
			return;
		}
		String text = "";//$NON-NLS-1$
		if (value != null) {
			if (!(value instanceof ArticleDescription)) {
				throw new IllegalStateException("Unsupported type");
			}
			text = ((ArticleDescription) value).getBeschreibung();
		}
		defaultLabel.setText(text);
		setValueValid(true);
	}
}
