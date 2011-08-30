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

package org.csstudio.platform.ui.internal.developmentsupport.util;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;

/**
 * Copies the names of the selected process variables to the clipboard. This
 * action is similar to {@link PrintPvsAction}, but directly copies the
 * selected PVs into the clipboard instead
 * of displaying a dialog box like <code>PrintPvsAction</code>.
 * 
 * @author Joerg Rathlev
 * @author Kay Kasemir
 */
public class CopyPvNameToClipboardAction extends ProcessVariablePopupAction {
    /**
     * {@inheritDoc}
     */
    @Override
    public void handlePVs(final IProcessVariable[] pv_names) {
        // Create string with "PV" or "PV1, PV2, PV3"
        final StringBuilder pvs = new StringBuilder();
        for (IProcessVariable pv : pv_names) {
            if (pvs.length() > 0)
                pvs.append(", "); //$NON-NLS-1$
            pvs.append(pv.getName());
        }
        
        // TextTransfer doesn't except empty String
        if (pvs.length() == 0) {
            pvs.append(" ");
        }
        
        // Copy as text to clipboard
        final Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench().getDisplay());
        clipboard.setContents(new String[] { pvs.toString() },
                              new Transfer[] { TextTransfer.getInstance() });
    }
}
