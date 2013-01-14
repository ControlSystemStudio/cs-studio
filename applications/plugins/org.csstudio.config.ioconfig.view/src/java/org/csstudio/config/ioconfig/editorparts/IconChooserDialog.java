/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.editorparts;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.helper.IconManageView;
import org.csstudio.config.ioconfig.model.NodeImageDBO;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 *
 * Make a Dialog to select a Icon for this node.
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @since 21.05.2010
 */
public class IconChooserDialog extends Dialog {

    private IconManageView _iconManageView;

    protected IconChooserDialog(@Nullable final Shell parentShell) {
        super(parentShell);
        this.setShellStyle(SWT.RESIZE | SWT.BORDER | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.TITLE
                           | SWT.ON_TOP | // SWT.TOOL| SWT.SHEET|
                           SWT.PRIMARY_MODAL);
    }

    @CheckForNull
    // TODO: prüfen ob hier wirklich null kommen kann.
    public NodeImageDBO getImage() {
        return _iconManageView.getSelectedImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureShell(@Nonnull final Shell shell) {
        super.configureShell(shell);
        shell.setText("Icon chooser");
        shell.setMaximized(true);
    }

    @Override
    @Nonnull
    protected Control createDialogArea(@Nonnull final Composite parent) {
        final Composite createDialogArea = (Composite) super.createDialogArea(parent);
        createDialogArea.setLayout(GridLayoutFactory.fillDefaults().create());
        _iconManageView = new IconManageView(createDialogArea, SWT.NONE);
        return _iconManageView;
    }

}
