package org.csstudio.config.kryonamebrowser.ui.handler;

import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;
import org.csstudio.config.kryonamebrowser.ui.dialog.EditNameDialog;
import org.csstudio.config.kryonamebrowser.ui.dialog.KryoNameDialog;
import org.eclipse.swt.widgets.Shell;

public class EditCommand extends AbstractNameHandler {

	public static final String ID = "editEntry.command";

	@Override
	public KryoNameDialog getDialog(Shell shell, KryoNameResolved element) {

		return new EditNameDialog(shell, element);

	}

}
