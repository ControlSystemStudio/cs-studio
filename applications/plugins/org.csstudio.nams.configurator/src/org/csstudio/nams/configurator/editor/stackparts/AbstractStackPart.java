package org.csstudio.nams.configurator.editor.stackparts;

import org.csstudio.ams.configurationStoreService.util.TObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractStackPart {
	
	protected final int NUM_COLUMNS;
	private Class<? extends TObject> _associatedTObject;
	
	public AbstractStackPart(Class<? extends TObject> associatedTObject, int numColumns) {
		_associatedTObject = associatedTObject;
		NUM_COLUMNS = numColumns;
	}
	
	protected Text createTextEntry(Composite parent, String labeltext, boolean editable) {
		Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		Text textWidget = new Text(parent, SWT.BORDER);
		textWidget.setEditable(editable);
		if (!editable) {
			textWidget.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		}
		return textWidget;
	}
	
	protected void addSeparator(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,NUM_COLUMNS,1));
	}
	
	public abstract Control getMainControl();
	
	public Class<? extends TObject> getAssociatedTObject() {
		return _associatedTObject;
	}

}
