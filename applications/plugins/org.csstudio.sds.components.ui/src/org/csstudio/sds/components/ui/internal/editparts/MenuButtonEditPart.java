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
package org.csstudio.sds.components.ui.internal.editparts;

import java.util.List;

import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.sds.components.model.ActionButtonModel;
import org.csstudio.sds.components.model.MenuButtonModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.model.LabelModel;
import org.csstudio.sds.model.properties.actions.AbstractWidgetActionModel;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.ui.widgetactionhandler.WidgetActionHandlerService;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * 
 * @author Helge Rickens, Kai Meyer
 * 
 */
public final class MenuButtonEditPart extends AbstractWidgetEditPart {

	
    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        final MenuButtonModel model = (MenuButtonModel) getWidgetModel();

        RefreshableLabelFigure label = new RefreshableLabelFigure();

        label.setTextValue(model.getLabel());
        label.setFont(getModelFont(MenuButtonModel.PROP_FONT));
        label.setTextAlignment(model.getTextAlignment());
        label.setTransparent(false);
        label.setEnabled(model.isAccesible() && getExecutionMode().equals(ExecutionMode.RUN_MODE));
        label.addMouseListener(new MouseListener() {
            public void mouseDoubleClicked(final MouseEvent me) {
            }

            public void mousePressed(final MouseEvent me) {
                if (me.button == 1 && getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
                    final org.eclipse.swt.graphics.Point cursorLocation = Display.getCurrent()
                            .getCursorLocation();
                    performDirectEdit(me.getLocation(), cursorLocation.x, cursorLocation.y);
                }
            }

            public void mouseReleased(final MouseEvent me) {
            }

        });

        return label;
    }

    /**
     * Open the cell editor for direct editing.
     * 
     * @param point
     *            the location of the mouse-event
     * @param absolutX
     *            The x coordinate of the mouse in the display
     * @param absolutY
     *            The y coordinate of the mouse in the display
     */
    private void performDirectEdit(final Point point, final int absolutX, final int absolutY) {
        if (this.getCastedModel().isAccesible()
                && getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
            final List<AbstractWidgetActionModel> widgetActions = ((MenuButtonModel) this
                    .getCastedModel()).getActionData().getWidgetActions();
            if (widgetActions.size() == 1) {
                Display.getCurrent().asyncExec(new Runnable() {
                    public void run() {
                        WidgetActionHandlerService.getInstance().performAction(getCastedModel(),
                                widgetActions.get(0));
                    }
                });
            } else {
                final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                MenuManager menuManager = new MenuManager();
                for (AbstractWidgetActionModel action : widgetActions) {
                    menuManager.add(new MenuAction(action));
                }
                Menu menu = menuManager.createContextMenu(shell);

                int x = absolutX;
                int y = absolutY;
                x = x - point.x + this.getCastedModel().getX();
                y = y - point.y + this.getCastedModel().getY() + this.getCastedModel().getHeight();

                menu.setLocation(x, y);
                menu.setVisible(true);
            }

            // 2008-10-14: swende: The following lines prevent the menu actions from being executed
            // consequently.
            // What was the sense for that? A problem on Mac systems?
            // while (!menu.isDisposed() && menu.isVisible()) {
            // if (!Display.getCurrent().readAndDispatch()) {
            // Display.getCurrent().sleep();
            // }
            // }
            // menu.dispose();
            // shell.setFocus();

        }
    }

    /**
     * Returns the Figure of this EditPart.
     * 
     * @return RefreshableActionButtonFigure The RefreshableActionButtonFigure of this EditPart
     */
    protected RefreshableLabelFigure getCastedFigure() {
        return (RefreshableLabelFigure) getFigure();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        // label
        IWidgetPropertyChangeHandler labelHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshableLabelFigure figure = getCastedFigure();
                figure.setTextValue(newValue.toString());
                return true;
            }
        };
        setPropertyChangeHandler(ActionButtonModel.PROP_LABEL, labelHandler);
        
        // font
        setPropertyChangeHandler(LabelModel.PROP_FONT, new FontChangeHandler<RefreshableLabelFigure>(){

			@Override
			protected void doHandle(RefreshableLabelFigure figure, Font font) {
				figure.setFont(font);
			}
			
		});

        // text alignment
        IWidgetPropertyChangeHandler alignmentHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshableLabelFigure figure = getCastedFigure();
                figure.setTextAlignment((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(ActionButtonModel.PROP_TEXT_ALIGNMENT, alignmentHandler);

    }

    /**
     * An Action, which encapsulates a {@link AbstractWidgetActionModel}.
     * 
     * @author Kai Meyer
     * 
     */
    private final class MenuAction extends Action {
        /**
         * The {@link AbstractWidgetActionModel}.
         */
        private AbstractWidgetActionModel _widgetAction;

        /**
         * Constructor.
         * 
         * @param widgetAction
         *            The encapsulated {@link AbstractWidgetActionModel}
         */
        public MenuAction(final AbstractWidgetActionModel widgetAction) {
            _widgetAction = widgetAction;
            this.setText(_widgetAction.getActionLabel());
            IWorkbenchAdapter adapter = (IWorkbenchAdapter) Platform.getAdapterManager()
                    .getAdapter(widgetAction, IWorkbenchAdapter.class);
            if (adapter != null) {
                this.setImageDescriptor(adapter.getImageDescriptor(widgetAction));
            }
            this.setEnabled(_widgetAction.isEnabled());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            Display.getCurrent().asyncExec(new Runnable() {
                public void run() {
                    WidgetActionHandlerService.getInstance().performAction(getCastedModel(),
                            _widgetAction);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    public IValue getSample(final int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException(index + " is not a valid sample index");
        }

        MenuButtonModel model = (MenuButtonModel) getWidgetModel();
        ITimestamp timestamp = TimestampFactory.now();

        // Note: the IValue implementations require a Severity, otherwise the
        // format() method will throw a NullPointerException. We don't really
        // have a severity here, so we fake one. This may cause problems for
        // clients who rely on getting a meaningful severity from the IValue.
        ISeverity severity = ValueFactory.createOKSeverity();

        IValue result = ValueFactory.createStringValue(timestamp, severity, null, Quality.Original,
                new String[] { model.getLabel() });

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        // always one sample
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean forceDisabledInEditMode() {
        return true;
    }

}
