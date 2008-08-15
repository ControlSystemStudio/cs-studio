package org.csstudio.config.kryonamebrowser.ui.handler;

import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;
import org.csstudio.config.kryonamebrowser.ui.dialog.AddByExampleDialog;
import org.csstudio.config.kryonamebrowser.ui.dialog.KryoNameDialog;
import org.eclipse.swt.widgets.Shell;

public class AddByExample extends AbstractNameHandler {

	@Override
	public KryoNameDialog getDialog(Shell shell, KryoNameResolved element) {

		return new AddByExampleDialog(shell, element);
	}

}
