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
package org.csstudio.opibuilder.runmode;

import org.eclipse.draw2d.ToolTipHelper;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.parts.DomainEventDispatcher;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;

/**
 * Patched Scrolling graphical viewer implementation.
 *
 * @author swende (original author), Xihui Chen (since import from SDS 2009/9)
 *
 */
public class PatchedScrollingGraphicalViewer extends ScrollingGraphicalViewer {
    private MenuManager contextMenu;

    /**
     * The original implementation in
     * {@link GraphicalViewerImpl#setContextMenu(MenuManager)} registers a menu
     * listener on the context menu. This causes a memory leak, because that
     * listener is never removed.
     */
    @Override
    public void setContextMenu(MenuManager manager) {
        // code from AbstractEditPartViewer base class (=super.super)
        if (contextMenu != null) {
            contextMenu.dispose();
        }

        contextMenu = manager;

        if (getControl() != null && !getControl().isDisposed()) {
            getControl().setMenu(contextMenu.createContextMenu(getControl()));
        }

        // code from GraphicalViewerImpl (=super)

        // ... is left out

        // ... and rewritten here
        if (contextMenu != null) {
            final IMenuListener menuListener = new IMenuListener() {
                @Override
                public void menuAboutToShow(IMenuManager manager) {
                    flush();
                }
            };

            contextMenu.addMenuListener(menuListener);

            final Control control = getControl();

            if (control != null) {
                control.addDisposeListener(new DisposeListener() {
                    @Override
                    public void widgetDisposed(DisposeEvent e) {
                        contextMenu.removeMenuListener(menuListener);
                        control.removeDisposeListener(this);
                    }
                });
            }
        }
    }

    @Override
    public MenuManager getContextMenu() {
        return contextMenu;
    }

    @Override
    public void setEditDomain(EditDomain domain) {
        super.setEditDomain(domain);
        DomainEventDispatcher eventDispatcher = new EventDispatcherWithToolTipConfiguration(domain, this);
        getLightweightSystem().setEventDispatcher(eventDispatcher);
    }

    /**
     * Custom EventDispatcher to configure the internal ToolTipHelper to use a longer
     * tooltip display time.
     */
    private class EventDispatcherWithToolTipConfiguration extends DomainEventDispatcher{

        private final int toolTipHideDelay = 10000000; // millisecs

        public EventDispatcherWithToolTipConfiguration(EditDomain d, EditPartViewer v) {
            super(d, v);
        }

        @Override
        protected ToolTipHelper getToolTipHelper() {
            ToolTipHelper tooltipHelper = super.getToolTipHelper();
            tooltipHelper.setHideDelay(toolTipHideDelay);
            return tooltipHelper;
        }
    }

}