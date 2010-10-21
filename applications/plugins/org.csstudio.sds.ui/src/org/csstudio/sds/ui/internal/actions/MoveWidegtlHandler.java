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
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * TODO (hrickens) :
 * 
 * @author hrickens
 * @author $Author: $
 * @since 20.10.2010
 */
public class MoveWidegtlHandler extends AbstractHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
		// IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		StructuredSelection currentSelection = (StructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		Iterator<?> iterator = currentSelection.iterator();
		while (iterator.hasNext()) {
			Object object = (Object) iterator.next();
			if (object instanceof AbstractWidgetEditPart) {
				AbstractWidgetEditPart widget = (AbstractWidgetEditPart) object;
				AbstractWidgetModel model = widget.getWidgetModel();
				if (!model.isLive()) {
					Event trigger = (Event) event.getTrigger();
					switch (trigger.keyCode) {
					case SWT.ARROW_UP:
						model.setY(model.getY() - 1);
						break;
					case SWT.ARROW_DOWN:
						model.setY(model.getY() + 1);
						break;
					case SWT.ARROW_LEFT:
						model.setX(model.getX() - 1);
						break;
					case SWT.ARROW_RIGHT:
						model.setX(model.getX() + 1);
						break;
					}
				}
			}
		}

		// ISelection showInSelection = HandlerUtil.getShowInSelection(event);
		// Object showInInput = HandlerUtil.getShowInInput(event);

		return null;
	}

}
