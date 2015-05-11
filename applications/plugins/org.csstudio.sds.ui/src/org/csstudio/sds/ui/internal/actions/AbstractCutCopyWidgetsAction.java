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

package org.csstudio.sds.ui.internal.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Abstract base class which contains the common parts of the cut and copy
 * actions.
 *
 * @author Joerg Rathlev
 */
public abstract class AbstractCutCopyWidgetsAction extends SelectionAction {

    /**
     * Creates a new cut or copy action.
     *
     * @param part the workbench part.
     */
    public AbstractCutCopyWidgetsAction(final IWorkbenchPart part) {
        super(part);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final boolean calculateEnabled() {
        List<AbstractWidgetModel> widgets = getSelectedWidgetModels();
        return (widgets.size()==1 && !( widgets.get(0) instanceof DisplayModel)) || widgets.size()>1;
    }

    /**
     * Sets the clipboard contents to the given list of widgets.
     *
     * @param widgets
     *            the list of widgets.
     */
    protected final void copyToClipboard(final List<AbstractWidgetModel> widgets) {
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        clipboard.setContents(new Object[] { widgets },
                new Transfer[] { WidgetModelTransfer.getInstance() });
    }

    /**
     * Gets the widget models of all currently selected EditParts.
     *
     * @return a list with all widget models that are currently selected
     */
    @SuppressWarnings("rawtypes")
    protected final List<AbstractWidgetModel> getSelectedWidgetModels() {
        List selection = getSelectedObjects();

        List<AbstractWidgetModel> selectedWidgetModels = new ArrayList<AbstractWidgetModel>();

        for (Object o : selection) {
            if (o instanceof AbstractBaseEditPart) {
                selectedWidgetModels.add(((AbstractBaseEditPart) o)
                        .getWidgetModel());
            }
        }
        sortWidgetModels(selectedWidgetModels);
        return selectedWidgetModels;
    }

    private void sortWidgetModels(final List<AbstractWidgetModel> widgetModels) {
        Collections.sort(widgetModels, new Comparator<AbstractWidgetModel>() {
            @Override
            public int compare(AbstractWidgetModel model1, AbstractWidgetModel model2) {
                int indexOfModel1 = model1.getParent().getIndexOf(model1);
                int indexOfModel2 = model1.getParent().getIndexOf(model2);
                if (indexOfModel1 < indexOfModel2) {
                    return -1;
                } else if (indexOfModel1 > indexOfModel2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

}
