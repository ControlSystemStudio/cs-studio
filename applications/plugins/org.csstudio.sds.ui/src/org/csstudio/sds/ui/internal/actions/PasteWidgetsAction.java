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

import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.ui.internal.commands.AddWidgetCommand;
import org.csstudio.sds.ui.internal.commands.SetSelectionCommand;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Action class that copies all widgets that are currently stored on the
 * Clipboard to the current Editor´s display model.
 *
 * @author Sven Wende
 *
 */
public final class PasteWidgetsAction extends SelectionAction {

    /**
     * Action ID of this action.
     */
    public static final String ID = "org.csstudio.sds.ui.internal.actions.PasteWidgetsAction";

    /**
     * Stores the mouse pointer location.
     */
    private org.eclipse.swt.graphics.Point _cursorLocation = null;

    /**
     * Constructor.
     *
     * @param workbenchPart
     *            a workbench part
     */
    public PasteWidgetsAction(final IWorkbenchPart workbenchPart) {
        super(workbenchPart);
        setId(ID);
        setText("Paste");
        setActionDefinitionId("org.eclipse.ui.edit.paste");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean calculateEnabled() {
        List<AbstractWidgetModel> widgets = getWidgetsFromClipboard();
        return widgets != null && !widgets.isEmpty();
    }

    /**
     * Creates the paste command.
     *
     * @return Command The paste command
     */
    public Command createPasteCommand() {
        if (getDisplayEditor() != null) {
            DisplayModel targetModel = getDisplayEditor().getDisplayModel();

            List<AbstractWidgetModel> widgets = getWidgetsFromClipboard();

            if (widgets != null) {
                if (_cursorLocation == null) {
                    this.fetchCurrentCursorLocation();
                }
                Control cursorControl = Display.getCurrent().getCursorControl();
                Dimension diff = calculatePositionDifference(cursorControl, widgets);

                CompoundCommand cmd = new CompoundCommand("Paste " + widgets.size() + " Widget"
                        + (widgets.size() > 0 ? "s" : ""));

                // adjust positions
                for (AbstractWidgetModel widgetModel : widgets) {
                    int newX = widgetModel.getX() + diff.width;
                    int newY = widgetModel.getY() + diff.height;

                    widgetModel.setLocation(newX, newY);
                }

                // add all widgets
                cmd.add(new AddWidgetCommand(targetModel, widgets));

                // select all widgets
                cmd.add(new SetSelectionCommand(getDisplayEditor().getGraphicalViewer(), widgets));

                return cmd;
            }
        }

        return null;

    }

    /**
     * Detects if the given control (where the mouse is currently above) is from
     * the {@link DisplayEditor}.
     *
     * @param cursorControl
     *            The control where the mouse is above
     * @return true if the control is from the {@link DisplayEditor}, false
     *         otherwise
     */
    private boolean isCursorAboveDisplayEditor(final Control cursorControl) {
        Control parent = cursorControl;
        while (parent != null) {
            if (parent.equals(getDisplayEditor().getParentComposite())) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    /**
     * Returns a list with widget models that are currently stored on the
     * clipboard.
     *
     * @return a list with widget models or an empty list
     */
    @SuppressWarnings("unchecked")
    private List<AbstractWidgetModel> getWidgetsFromClipboard() {
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        List<AbstractWidgetModel> result = (List<AbstractWidgetModel>) clipboard
                .getContents(WidgetModelTransfer.getInstance());
        return result;
    }

    /**
     * Calculates the position offset for all widgets that will be added to the
     * current editor´s display model.
     *
     * The position is calculated in a way that all new widgets are horizontally
     * and vertically centered in the currently open editor.
     *
     * @param cursorControl
     *            the control where the mouse cursor is above
     *
     * @param widgets
     *            all widgets that should be added
     *
     * @return a position offset that has to be applied to all widgets before
     *         they are added to the current editor´s display model
     */
    private Dimension calculatePositionDifference(final Control cursorControl,
                                                  final List<AbstractWidgetModel> widgets) {

        if (getDisplayEditor() != null) {
            Point centerPosition = calculateEditorPosition(cursorControl);
            _cursorLocation = null;

            Rectangle bounds = calculateWidgetBounds(widgets);
            centerPosition.translate(-bounds.width / 2, -bounds.height / 2);

            return centerPosition.getDifference(bounds.getLocation());
        }

        return new Dimension(0, 0);
    }

    private Point calculateEditorPosition(final Control cursorControl) {
        if (isCursorAboveDisplayEditor(cursorControl)) {
            org.eclipse.swt.graphics.Point cursorLocation = cursorControl
                    .toControl(_cursorLocation);

            Point tmpPoint = new Point(cursorLocation.x, cursorLocation.y);
            return getDisplayEditor().translateCursorLocation(tmpPoint);

        }

        return getDisplayEditor().getEditorCenterPosition();
    }

    private Rectangle calculateWidgetBounds(final List<AbstractWidgetModel> widgets) {
        // calculate the bounds that are used by all widgets
        int upperLeftX = Integer.MAX_VALUE;
        int upperLeftY = Integer.MAX_VALUE;
        int lowerRightX = 0;
        int lowerRightY = 0;

        for (AbstractWidgetModel widgetModel : widgets) {
            int x = widgetModel.getX();
            int y = widgetModel.getY();
            int w = widgetModel.getWidth();
            int h = widgetModel.getHeight();
            upperLeftX = (upperLeftX > x ? x : upperLeftX);
            upperLeftY = (upperLeftY > y ? y : upperLeftY);
            lowerRightX = (lowerRightX < (x + w) ? (x + w) : lowerRightX);
            lowerRightY = (lowerRightY < (y + h) ? (y + h) : lowerRightY);
        }

        Point upperLeft = new Point(upperLeftX, upperLeftY);
        Point lowerRight = new Point(lowerRightX, lowerRightY);

        return new Rectangle(upperLeft, lowerRight);
    }

    /**
     * Returns the currently open display editor.
     *
     * @return the currently open display editor
     */
    private DisplayEditor getDisplayEditor() {
        if (getWorkbenchPart() instanceof DisplayEditor) {
            return (DisplayEditor) getWorkbenchPart();
        }
        return null;
    }

    /**
     * Determines and stores the current mouse pointer location. The widgets
     * from the clipboard will be pasted at the mouse pointer location, if the
     * mouse pointer is within the editor. If this action is added to a context
     * menu, this method should be called when the menu displays, so that the
     * paste location is the location at which the user opened the context
     * menu.
     */
    public void fetchCurrentCursorLocation() {
        _cursorLocation = Display.getCurrent().getCursorLocation();
    }

    /**
     * Performs the delete action on the selected objects.
     */
    @Override
    public void run() {
        execute(createPasteCommand());
    }
}
