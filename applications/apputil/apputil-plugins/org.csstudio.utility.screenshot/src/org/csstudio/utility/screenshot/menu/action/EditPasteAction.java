
/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.utility.screenshot.menu.action;

import org.csstudio.utility.screenshot.ScreenshotWorker;
import org.csstudio.utility.screenshot.internal.localization.ScreenshotMessages;
import org.csstudio.utility.screenshot.util.ClipboardHandler;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;

public class EditPasteAction extends Action  implements IMenuListener
{
    private ScreenshotWorker worker = null;

    public EditPasteAction(ScreenshotWorker w)
    {
        worker = w;

        this.setText(ScreenshotMessages.getString("ScreenshotView.MENU_EDIT_PASTE"));

        this.setToolTipText(ScreenshotMessages.getString("ScreenshotView.MENU_EDIT_PASTE_TT"));

        if(ClipboardHandler.getInstance().isImageAvailable())
        {
            setEnabled(true);
        }
        else
        {
            setEnabled(false);
        }
    }

    @Override
    public void menuAboutToShow(IMenuManager manager)
    {
        if(ClipboardHandler.getInstance().isImageAvailable())
        {
            this.setEnabled(true);
        }
        else
        {
            this.setEnabled(false);
        }
    }

    @Override
    public void run()
    {
        Image image = null;

        image = ClipboardHandler.getInstance().getClipboardImage(worker.getDisplay());
        if(image != null)
        {
            worker.setDisplayedImage(image);
            image.dispose();
            image = null;
        }
        else
        {
            MessageDialog.openError(worker.getShell(), worker.getNameAndVersion() + " - EditPasteAction", "Sorry, not possible to paste an image from the clipboard.");
        }
    }
}
