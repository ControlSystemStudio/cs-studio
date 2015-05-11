/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.sds.ui.internal.properties.view;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog for the "modal" Wizards. It is ensured that there is only one
 * opened dialog per time. If there is alredy a dialog opened, it will be closed
 * and replaced by one that carries the passed in IWizard instance.
 *
 * @author Alexander Will
 * @version $Revision: 1.3 $
 *
 */
public final class ModalWizardDialog extends WizardDialog {

    /**
     * The unique instance of this dialog.
     */
    private static ModalWizardDialog _instance = null;

    /**
     * Private constructor due to singleton pattern.
     *
     * @param parentShell
     *            the parent shell
     * @param newWizard
     *            the wizard this dialog is working on
     */
    private ModalWizardDialog(final Shell parentShell,
            final IWizard newWizard) {
        super(parentShell, newWizard);

        setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE
                | SWT.BORDER | SWT.RESIZE);
    }

    /**
     * Open the dialog. Ensure that there is only one opened dialog per time. If
     * there is alredy a dialog opened, it will be closed and replaced by one
     * that carries the passed in IWizard instance.
     *
     * @param parentShell
     *            the parent shell
     * @param newWizard
     *            the wizard this dialog is working on
     * @return the return code
     */
    public static int open(final Shell parentShell, final IWizard newWizard) {
        if (_instance == null) {
            _instance = new ModalWizardDialog(parentShell, newWizard);
        } else {
            final Rectangle currentBounds = _instance.getShell() == null ? null
                    : _instance.getShell().getBounds();
            /*
             *  XXX: don't close the instance. Workaround for a Eclipse 3.6 Bug. Maybe make a memory leak.
             */
//            _instance.close();
            _instance = new ModalWizardDialog(parentShell, newWizard);
            // if the dialog was previously closed, there was no old shell and
            // no old bounds
            if (currentBounds != null) {
                Display.getCurrent().asyncExec(new Runnable() {
                    public void run() {
                        _instance.getShell().setBounds(currentBounds);
                    }
                });
            }
        }

        return _instance.open();
    }
}
