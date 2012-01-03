/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.view.actions;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Rename the selected Node on the tree.
 *
 * @author hrickens
 * @since 05.10.2011
 */
public class RenameNodeAction extends Action {
    private final TreeEditor _editor;
    private final ProfiBusTreeView _profiBusTreeView;

    public RenameNodeAction(@Nonnull final ProfiBusTreeView profiBusTreeView,
                     @Nonnull final TreeEditor editor) {
        _profiBusTreeView = profiBusTreeView;
        _editor = editor;
    }

    @Override
    public void run() {
        final Tree tree = _profiBusTreeView.getViewer().getTree();
        final NamedDBClass node = (NamedDBClass) ((StructuredSelection) _profiBusTreeView
                .getViewer().getSelection()).getFirstElement();
        final TreeItem item = tree.getSelection()[0];
        // Create a text field to do the editing
        String editText = "";
        if (node instanceof ChannelDBO) {
            editText = ((ChannelDBO) node).getIoName();
        } else {
            editText = node.getName();
        }
        if (editText == null) {
            editText = "";
        }
        final Text text = new Text(tree, SWT.BORDER);
        text.setText(editText);
        text.selectAll();
        text.setFocus();

        // If the text field loses focus, set its text into the tree and end the editing session
        text.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(@Nonnull final FocusEvent event) {
                text.dispose();
            }
        });

        // Set the text field into the editor
        _editor.setEditor(text, item);
    }

}
