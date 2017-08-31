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
package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.csstudio.opibuilder.actions.WidgetActionMenuAction;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgetActions.WritePVAction;
import org.csstudio.opibuilder.widgets.figures.MenuButtonFigure;
import org.csstudio.opibuilder.widgets.model.MenuButtonModel;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.simplepv.VTypeHelper;
import org.csstudio.swt.widgets.figures.ITextFigure;
import org.csstudio.swt.widgets.util.GraphicsUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.diirt.vtype.Scalar;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VType;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 *
 * @author Helge Rickens, Kai Meyer, Xihui Chen
 *
 */
public final class MenuButtonEditPart extends AbstractPVWidgetEditPart {

    private IPVListener loadActionsFromPVListener;

    private List<String>  meta = null;

    @Override
    protected IFigure doCreateFigure() {
        final MenuButtonModel model = (MenuButtonModel) getWidgetModel();
        updatePropSheet(model.isActionsFromPV());
        final MenuButtonFigure figure = new MenuButtonFigure();
        figure.setOpaque(!model.isTransparent());
        figure.setText(model.getLabel());

        figure.setDownArrowVisible(model.showDownArrow());

        if (getExecutionMode() == ExecutionMode.RUN_MODE)
            figure.addMouseListener(new MouseListener() {
                @Override
                public void mouseDoubleClicked(final MouseEvent me) {
                }

                @Override
                public void mousePressed(final MouseEvent me) {
                    if (me.button == 1  &&
                        figure.containsPoint(me.getLocation()))
                        me.consume();
                }

                @Override
                public void mouseReleased(final MouseEvent me) {
                    // Check location to ignore bogus mouse clicks,
                    // see https://github.com/ControlSystemStudio/cs-studio/issues/1818
                    if (me.button == 1  &&
                        getExecutionMode().equals(ExecutionMode.RUN_MODE)  &&
                        figure.containsPoint(me.getLocation())) {
                        final org.eclipse.swt.graphics.Point cursorLocation = Display
                                .getCurrent().getCursorLocation();
                        showMenu(me.getLocation(), cursorLocation.x,
                                cursorLocation.y);
                    }
                }

            });
        figure.addMouseMotionListener(new MouseMotionListener.Stub() {
            @Override
            public void mouseEntered(MouseEvent me) {
                if (getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
                    Color backColor = figure.getBackgroundColor();
                    RGB darkColor = GraphicsUtil.mixColors(backColor.getRGB(),
                            new RGB(0, 0, 0), 0.9);
                    figure.setBackgroundColor(CustomMediaFactory.getInstance()
                            .getColor(darkColor));
                }

            }

            @Override
            public void mouseExited(MouseEvent me) {
                if (getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
                    figure.setBackgroundColor(CustomMediaFactory.getInstance()
                            .getColor(getWidgetModel().getBackgroundColor()));
                }
            }
        });

        markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);
        return figure;
    }

    @Override
    protected void createEditPolicies() {
        super.createEditPolicies();
        if(getExecutionMode() == ExecutionMode.EDIT_MODE)
            installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new TextDirectEditPolicy());
    }


    @Override
    public void performRequest(Request request){
        if (getExecutionMode() == ExecutionMode.EDIT_MODE &&(
                request.getType() == RequestConstants.REQ_DIRECT_EDIT ||
                request.getType() == RequestConstants.REQ_OPEN))
            new TextEditManager(this,
                    new LabelCellEditorLocator(getFigure()), false).show();
    }

    @Override
    public MenuButtonModel getWidgetModel() {
        return (MenuButtonModel) getModel();
    }

    /**
     * Show Menu
     *
     * @param point
     *            the location of the mouse-event in the OPI display
     * @param absolutX
     *            The x coordinate of the mouse on the monitor
     * @param absolutY
     *            The y coordinate of the mouse on the monitor
     */
    private void showMenu(final Point point, final int absolutX,
            final int absolutY) {
        if (getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
            final Shell shell = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell();
            MenuManager menuManager = new MenuManager();
            for (AbstractWidgetAction action : getWidgetModel()
                    .getActionsInput().getActionsList()) {
                menuManager.add(new WidgetActionMenuAction(action));
            }
            Menu menu = menuManager.createContextMenu(shell);

            /*
             * We need to position the menu in absolute monitor coordinates.
             * First we calculate the coordinates of the display, then add the
             * widget coordinates to these so that the menu opens on the
             * bottom left of the widget.
             */
            int x = absolutX - point.x;
            int y = absolutY - point.y;
            x += getWidgetModel().getLocation().x;
            y += getWidgetModel().getLocation().y + getWidgetModel().getSize().height;

            menu.setLocation(x, y);
            menu.setVisible(true);

        }
    }

    @Override
    protected void doActivate() {
        super.doActivate();
        registerLoadActionsListener();
    }

    private void registerLoadActionsListener() {
        if (getExecutionMode() == ExecutionMode.RUN_MODE) {
            if (getWidgetModel().isActionsFromPV()) {
                IPV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
                if (pv != null) {
                    if (loadActionsFromPVListener == null)
                        loadActionsFromPVListener = new IPVListener.Stub() {
                            @Override
                            public void valueChanged(IPV pv) {
                                VType value = pv.getValue();
                                if (value != null
                                        && value instanceof VEnum) {
                                    List<String> new_meta = ((VEnum) value).getLabels();
                                    if (meta == null || !meta.equals(new_meta)) {
                                        meta = new_meta;
                                        ActionsInput actionsInput = new ActionsInput();
                                        for (String writeValue : meta) {
                                            WritePVAction action = new WritePVAction();
                                            action.setPropertyValue(
                                                    WritePVAction.PROP_PVNAME,
                                                    getWidgetModel()
                                                            .getPVName());
                                            action.setPropertyValue(
                                                    WritePVAction.PROP_VALUE,
                                                    writeValue);
                                            action.setPropertyValue(
                                                    WritePVAction.PROP_DESCRIPTION,
                                                    writeValue);
                                            actionsInput.getActionsList().add(
                                                    action);
                                        }
                                        getWidgetModel()
                                                .setPropertyValue(
                                                        AbstractWidgetModel.PROP_ACTIONS,
                                                        actionsInput);

                                    }
                                }
                            }

                        };
                    pv.addListener(loadActionsFromPVListener);
                }
            }
        }
    }

    @Override
    protected void doDeActivate() {
        super.doDeActivate();
        if (getWidgetModel().isActionsFromPV()) {
            IPV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
            if (pv != null && loadActionsFromPVListener != null) {
                pv.removeListener(loadActionsFromPVListener);
            }
        }

    }

    @Override
    protected void registerPropertyChangeHandlers() {
        IWidgetPropertyChangeHandler pvNameHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                registerLoadActionsListener();
                return false;
            }
        };
        setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME,
                pvNameHandler);

        // PV_Value
        IWidgetPropertyChangeHandler pvhandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                if ((newValue != null) && (newValue instanceof Scalar))
                    ((MenuButtonFigure) refreshableFigure).setText(VTypeHelper
                            .getString((VType) newValue));
                return true;
            }
        };
        setPropertyChangeHandler(MenuButtonModel.PROP_PVVALUE, pvhandler);

        // label
        IWidgetPropertyChangeHandler labelHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ((MenuButtonFigure) refreshableFigure).setText(newValue.toString());
                return true;
            }
        };
        setPropertyChangeHandler(MenuButtonModel.PROP_LABEL, labelHandler);

        // Transparent
        IWidgetPropertyChangeHandler transparentHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ((MenuButtonFigure) refreshableFigure).setOpaque(!(Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MenuButtonModel.PROP_TRANSPARENT,
                transparentHandler);

        // Show down arrow
        IWidgetPropertyChangeHandler downArrowHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ((MenuButtonFigure)refreshableFigure).setDownArrowVisible((boolean)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MenuButtonModel.PROP_SHOW_DOWN_ARROW,
                downArrowHandler);

        final IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                updatePropSheet((Boolean) newValue);
                return false;
            }
        };
        getWidgetModel().getProperty(MenuButtonModel.PROP_ACTIONS_FROM_PV)
                .addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        handler.handleChange(evt.getOldValue(),
                                evt.getNewValue(), getFigure());
                    }
                });
    }

    /**
     * @param actionsFromPV
     */
    private void updatePropSheet(final boolean actionsFromPV) {
        getWidgetModel().setPropertyVisible(MenuButtonModel.PROP_ACTIONS,
                !actionsFromPV);
    }

    public int size() {
        // always one sample
        return 1;
    }

    @Override
    public String getValue() {
        return ((MenuButtonFigure) getFigure()).getText();
    }

    @Override
    public void setValue(Object value) {
        ((MenuButtonFigure) getFigure()).setText(value.toString());
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
        if(key == ITextFigure.class)
            return getFigure();

        return super.getAdapter(key);
    }

}
