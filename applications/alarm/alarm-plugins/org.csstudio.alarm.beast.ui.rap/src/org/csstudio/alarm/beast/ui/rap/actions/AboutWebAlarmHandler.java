package org.csstudio.alarm.beast.ui.rap.actions;

import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.UIPlugin;

/**
 * The action to pop up an About WebAlarm Dialog.
 *
 * @author Davy Dequidt <davy.dequidt@iter.org>
 *
 */
public class AboutWebAlarmHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        new AboutDialog(null).open();
        return null;
    }

    class AboutDialog extends Dialog {

        /**
         * Create the dialog.
         *
         * @param parentShell
         */
        public AboutDialog(Shell parentShell) {
            super(parentShell);

        }

        /**
         * Create contents of the dialog.
         *
         * @param parent
         */
        @SuppressWarnings({ "restriction" })
        @Override
        protected Control createDialogArea(Composite parent) {
            getShell().setText("About WebAlarm");
            String version = "v"
                    + Activator.getDefault().getBundle().getVersion()
                            .toString();
            Composite container = (Composite) super.createDialogArea(parent);
            GridLayout gl_container = new GridLayout(1, false);
            container.setLayout(gl_container);
            GridData gd = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1,
                    1);
            Label emptyLabel = new Label(container, SWT.NONE);
            gd.heightHint = 10;
            emptyLabel.setLayoutData(gd);

            Label lblNewLabel = new Label(container, SWT.NONE);
            gd = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
            gd.heightHint = 20;
            lblNewLabel.setLayoutData(gd);
            lblNewLabel.setFont(CustomMediaFactory.getInstance().getFont(
                    "Verdana", 16, SWT.BOLD));
            lblNewLabel.setText("WebAlarm " + version); //$NON-NLS-1$

            Label lblCompatibleWithBoy = new Label(container, SWT.NONE);
            GridData gd_lblCompatibleWithBoy = new GridData(SWT.CENTER,
                    SWT.CENTER, false, false, 1, 1);
            gd_lblCompatibleWithBoy.heightHint = 20;
            lblCompatibleWithBoy.setLayoutData(gd_lblCompatibleWithBoy);
            lblCompatibleWithBoy.setText("Compatible with Alarm BEAST "
                    + version);

            Label rapLabel = new Label(container, SWT.NONE);
            gd_lblCompatibleWithBoy = new GridData(SWT.CENTER, SWT.CENTER,
                    false, false, 1, 1);
            gd_lblCompatibleWithBoy.heightHint = 20;
            rapLabel.setLayoutData(gd_lblCompatibleWithBoy);
            rapLabel.setText("Built on RAP " + "v" + UIPlugin. //$NON-NLS-2$
                    getDefault().getBundle().getVersion().toString());

            return container;
        }

        protected void createButtonsForButtonBar(Composite parent) {
            // create OK and Cancel buttons by default
            createButton(parent, IDialogConstants.OK_ID,
                    JFaceResources.getString(IDialogLabelKeys.OK_LABEL_KEY),
                    true);
        }

    }
}
