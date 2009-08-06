package org.csstudio.opibuilder.actions;


import java.util.List;

import org.csstudio.opibuilder.commands.WidgetCreateCommand;
import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Action class that copies all widgets that are currently stored on the
 * Clipboard to the current Editor's display model.
 * 
 * @author Xihui Chen
 * 
 */
public final class PasteWidgetsAction extends SelectionAction {

	/**
	 * Action ID of this action.
	 */
	public static final String ID = "org.csstudio.opibuilder.actions.PasteWidgetsAction";

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
	public PasteWidgetsAction(OPIEditor workbenchPart) {
		super(workbenchPart);
		setId(ID);
		setText("Paste");
		setActionDefinitionId(ActionFactory.PASTE.getId());
		setId(ActionFactory.PASTE.getId());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	/**
	 * Creates the paste command.
	 * 
	 * @return Command The paste command
	 */
	public Command createPasteCommand() {
		
			DisplayModel targetModel = getOPIEditor().getDisplayModel();

			List<AbstractWidgetModel> widgets = getWidgetsFromClipboard();

			if (widgets != null) {
				if (_cursorLocation==null) {
					this.fetchCurrentCursorLocation();
				}
				Control cursorControl = Display.getCurrent().getCursorControl();
				Dimension diff = calculatePositionDifference(cursorControl,
						widgets);

				CompoundCommand cmd = new CompoundCommand("Paste "
						+ widgets.size() + " Widget"
						+ (widgets.size() > 0 ? "s" : ""));

				for (AbstractWidgetModel widgetModel : widgets) {
				
					// create command
					cmd.add(new WidgetCreateCommand(widgetModel, targetModel,
							new Rectangle(widgetModel.getLocation().getTranslated(diff), 
									widgetModel.getSize())));
				
				}

				return cmd;
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
			if (parent.equals(getOPIEditor().getParentComposite())) {
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
		List<AbstractWidgetModel> result = (List<AbstractWidgetModel>) getOPIEditor().getClipboard()
				.getContents(OPIWidgetsTransfer.getInstance());
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

			// calculate the bounds that are used by all widgets
			int upperLeftX = Integer.MAX_VALUE;
			int upperLeftY = Integer.MAX_VALUE;
			int lowerRightX = 0;
			int lowerRightY = 0;

			for (AbstractWidgetModel widgetModel : widgets) {
				int x = widgetModel.getLocation().x;
				int y = widgetModel.getLocation().y;
				int w = widgetModel.getSize().width;
				int h = widgetModel.getSize().height;
				upperLeftX = (upperLeftX > x ? x : upperLeftX);
				upperLeftY = (upperLeftY > y ? y : upperLeftY);
				lowerRightX = (lowerRightX < (x + w) ? (x + w) : lowerRightX);
				lowerRightY = (lowerRightY < (y + h) ? (y + h) : lowerRightY);
			}

			Point point = new Point(upperLeftX, upperLeftY);
			Rectangle bounds = new Rectangle(point, new Point(lowerRightX,
					lowerRightY));

			Dimension diff;

			if (isCursorAboveDisplayEditor(cursorControl)) {
				org.eclipse.swt.graphics.Point cursorLocation = cursorControl
						.toControl(_cursorLocation);
				Point tmp = new Point(cursorLocation.x, cursorLocation.y);
				diff = tmp.getDifference(point);
			} else {
				// calculate the shifting difference for center positioning
				Point tmp = getOPIEditor().getDisplayCenterPosition()
						.getTranslated(-bounds.width / 2, -bounds.height / 2);

				diff = tmp.getDifference(point);
			}
			_cursorLocation = null;
			return diff;
	}

	/**
	 * Returns the currently open OPI editor.
	 * 
	 * @return the currently open OPI editor
	 */
	private OPIEditor getOPIEditor() {

			return (OPIEditor) getWorkbenchPart();

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
