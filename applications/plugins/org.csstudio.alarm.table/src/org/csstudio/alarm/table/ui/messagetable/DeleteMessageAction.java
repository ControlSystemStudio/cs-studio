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
package org.csstudio.alarm.table.ui.messagetable;

import org.csstudio.alarm.table.dataModel.AbstractMessageList;
import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to delete selected items from {@link MessageTable}.
 * The Action is provided by the context menu of the {@link MessageTable}.
 * 
 * @author jhatje
 *
 */
public class DeleteMessageAction extends Action {
    
    private static final Logger LOG = LoggerFactory.getLogger(DeleteMessageAction.class);
    
    private final MessageTable _messageTable;

    private final AbstractMessageList _messageList;

    public DeleteMessageAction(final MessageTable messageTable,
            final AbstractMessageList msgList) {
        _messageList = msgList;
        _messageTable = messageTable;
        setText("Delete");
        setToolTipText("Delete selected messages");
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
        setEnabled(false);
        //Enable this action only if items are selected
        _messageTable.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                boolean anything = (!event.getSelection().isEmpty() && !_messageTable.getMessageUpdatePause());
                setEnabled(anything);
            }
        });
    }

    @Override
    public void run() {
        TableItem[] selection = _messageTable.getTableViewer().getTable().getSelection();
        BasicMessage[] messageSelection = new BasicMessage[selection.length];
        for (int i = 0; i < selection.length; i++) {
//        for (TableItem tableItem : selection) {
//            if (tableItem.getData() instanceof BasicMessage) {
            	if (selection[i].getData() instanceof BasicMessage) {
                messageSelection[i] = (BasicMessage) selection[i].getData();
//                _messageList.removeMessage((BasicMessage) tableItem.getData());
            } else {
                LOG.warn("Unknown object in selection!");
            }
        }
        _messageList.removeMessages(messageSelection);
    }
}
