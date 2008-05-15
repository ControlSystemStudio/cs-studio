package org.csstudio.nams.configurator.editor.stackparts;

import org.csstudio.ams.configurationStoreService.knownTObjects.UserTObject;
import org.csstudio.ams.configurationStoreService.util.TObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class UserStackPart extends AbstractStackPart {
	
	private Text _idTextEntry;
	private Text _nameTextEntry;
	private Composite _main;
	private Text _emailTextEntry;

	public UserStackPart(Composite parent) {
		super(UserTObject.class, 2);
		_main = new Composite(parent, SWT.NONE);
		_main.setLayout(new GridLayout(NUM_COLUMNS,false));
		_idTextEntry = this.createTextEntry(_main, "ID", false);
		_idTextEntry.setText("User");
		this.addSeparator(_main);
		_nameTextEntry = this.createTextEntry(_main, "Name", true);
		_emailTextEntry = this.createTextEntry(_main, "Email", true);
	}

	@Override
	public Control getMainControl() {
		return _main;
	}
	
	
}
