/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.ui.internal.editparts;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.model.PropertyChangeAdapter;
import org.csstudio.sds.ui.CheckedUiRunnable;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

/**
 * A <code>IPropertyChangeListener</code> implementation, which delegates
 * property change events to a handler, which is registered separately.
 *
 * This is used to implement a fine grained property handling in the UI.
 *
 * @author Sven Wende
 *
 */
public final class WidgetPropertyChangeListener extends PropertyChangeAdapter {

    /**
     * Backlink to an EditPart.
     */
    private AbstractBaseEditPart _editPart;

    /**
     * The handlers, which really does the action in case of a property change
     * event. Is empty by default.
     */
    private List<IWidgetPropertyChangeHandler> _handlers;

    /**
     * Constructs a listener.
     *
     * @param editPart
     *            backlink to the EditPart, which uses this listener
     */
    public WidgetPropertyChangeListener(final AbstractBaseEditPart editPart) {
        assert editPart != null;
        _editPart = editPart;
        _handlers = new ArrayList<IWidgetPropertyChangeHandler>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyValueChanged(final Object oldValue,
            final Object newValue) {
        new CheckedUiRunnable() {
            @Override
            protected void doRunInUi() {
                for (IWidgetPropertyChangeHandler h : _handlers) {
                    IFigure figure = _editPart.getFigure();

                    boolean repaint = h
                            .handleChange(oldValue, newValue, figure);

                    if (repaint) {
                        figure.repaint();
                    }
                }
            }
        };
    }

    /**
     * Sets a handler, which is informed, when a property change occurs.
     *
     * @param handler
     *            the handler
     */
    public void addHandler(final IWidgetPropertyChangeHandler handler) {
        assert handler != null;
        _handlers.add(handler);
    }
}
