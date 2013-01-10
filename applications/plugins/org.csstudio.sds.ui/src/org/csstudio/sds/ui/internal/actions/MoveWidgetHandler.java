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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.sds.ui.internal.actions;

import java.util.Iterator;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.internal.commands.TimestampedSetPropertyCommand;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Moves a widget, over shortcuts, in edit mode on the Display.
 * 
 * @author Helge Rickens <css-desy@desy.de>
 * @author $Author: $
 * @since 20.10.2010
 */
public class MoveWidgetHandler extends AbstractHandler {
    
    private static final int COMMAND_ASSOCIATION_TIME = 510;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
        if (currentSelection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) currentSelection;
            Iterator<?> iterator = structuredSelection.iterator();
            while (iterator.hasNext()) {
                Object object = (Object) iterator.next();
                if (object instanceof AbstractBaseEditPart) {
                    AbstractBaseEditPart widget = (AbstractBaseEditPart) object;
                    AbstractWidgetModel model = widget.getWidgetModel();
                    CommandStack commandStack = widget.getViewer().getEditDomain()
                            .getCommandStack();
                    if (!model.isLive() && commandStack != null) {
                        Event trigger = (Event) event.getTrigger();
                        createAndExecuteCommand(model, trigger, commandStack);
                    }
                }
            }
        }
        return null;
    }
    
    private void createAndExecuteCommand(AbstractWidgetModel model,
                                         Event trigger,
                                         CommandStack commandStack) {
        String propertyName = null;
        Object value = null;
        switch (trigger.keyCode) {
            case SWT.ARROW_UP:
                propertyName = AbstractWidgetModel.PROP_POS_Y;
                value = model.getY() - 1;
                break;
            case SWT.ARROW_DOWN:
                propertyName = AbstractWidgetModel.PROP_POS_Y;
                value = model.getY() + 1;
                break;
            case SWT.ARROW_LEFT:
                propertyName = AbstractWidgetModel.PROP_POS_X;
                value = model.getX() - 1;
                break;
            case SWT.ARROW_RIGHT:
                propertyName = AbstractWidgetModel.PROP_POS_X;
                value = model.getX() + 1;
                break;
        }
        if (propertyName != null) {
            Command command = new TimestampedSetPropertyCommand(model,
                                                                propertyName,
                                                                value,
                                                                COMMAND_ASSOCIATION_TIME);
            commandStack.execute(command);
        }
    }
}
