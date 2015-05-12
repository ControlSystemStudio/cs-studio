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
 package org.csstudio.sds.util;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Text;

/**
 * A util class for adding Drag & Drop support to a {@link Text}.
 * @author Kai Meyer
 *
 */
public final class TextDnDUtil {

    /**
     * Private Constructor.
     */
    private TextDnDUtil() {
        //do nothing
    }

    /**
     * Adds Drop & Drop support for the given {@link Text}.
     * @param textWidget The {@link Text} widget
     */
    public static void addDnDSupport(final Text textWidget) {
        addDropSupport(textWidget);
        addDragSupport(textWidget);
    }

    /**
     * Adds Drop support for the given {@link Text}.
     * @param textWidget The {@link Text} widget
     */
    public static void addDropSupport(final Text textWidget) {
        DropTarget dropTarget = new DropTarget(textWidget, DND.DROP_COPY | DND.DROP_MOVE);
        Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
        dropTarget.setTransfer(transferTypes);
        dropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void dragEnter(final DropTargetEvent event) {
                for (TransferData transfer : event.dataTypes) {
                    if (TextTransfer.getInstance().isSupportedType(
                            transfer)) {
                        event.detail = DND.DROP_COPY;
                        break;
                    }
                }
                super.dragEnter(event);
            }

            @Override
            public void drop(final DropTargetEvent event) {
                if (event.data instanceof String) {
                    textWidget.setText((String) event.data);
                }
            }
        });
    }

    /**
     * Adds Drag support for the given {@link Text}.
     * @param textWidget The {@link Text} widget
     */
    public static void addDragSupport(final Text textWidget) {
        DragSource dragSource = new DragSource(textWidget, DND.DROP_COPY);
        Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
        dragSource.setTransfer(transferTypes);
        dragSource.addDragListener(new DragSourceAdapter() {
            @Override
            public void dragStart(final DragSourceEvent event) {
                if (textWidget.getText().trim().length()==0) {
                    event.doit = false;
                }
            }

            @Override
            public void dragSetData(final DragSourceEvent event) {
                if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
                    String text = textWidget.getText();
                    if (text != null && text.trim().length()>0) {
                        event.data = text;
                    }
                }
            }
        });
    }

}
