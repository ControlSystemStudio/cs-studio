package org.csstudio.opibuilder.commands;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.opibuilder.actions.OPIWidgetsTransfer;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.model.GuideModel;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

/**
 * A Command to clone the selected widgets.
 * @author Kai Meyer, Xihui Chen
 *
 */
public final class CloneCommand extends Command {
	/**
	 * The list of {@link AbstractWidgetModel}.
	 */
	private List<AbstractWidgetModel> _models;
	/**
	 * The list of cloned {@link AbstractWidgetModel}.
	 */
	private List<AbstractWidgetModel> _clonedWidgets;
	/**
	 * The parent for the AbstractWidgetModels.
	 */
	private DisplayModel _parent;
	/**
	 * The horizontal Guide.
	 */
	private GuideModel _hGuide;
	/**
	 * The vertical Guide.
	 */
	private GuideModel _vGuide;
	/**
	 * The horizontal alignment.
	 */
	private int _hAlignment;
	/**
	 * The vertical alignment.
	 */
	private int _vAlignment;
	/**
	 * The difference between the original location and the new location.
	 */
	private Dimension _difference;
	/**
	 * The internal {@link CompoundCommand}.
	 */
	private CompoundCommand _compoundCommand;

	/**
	 * Constructor.
	 * @param parent
	 * 			The parent {@link DisplayModel} for the widgets
	 */
	public CloneCommand(final DisplayModel parent) {
		super("Clone Widgets");
		_models = new LinkedList<AbstractWidgetModel>();
		_parent = parent;
	}
	
	/**
	 * Adds the given {@link AbstractWidgetModel} with the given {@link Rectangle} to this Command.
	 * @param model
	 * 			The AbstractWidgetModel
	 * @param newBounds
	 * 			The new bounds for the AbstractWidgetModel
	 */
	public void addPart(final AbstractWidgetModel model, final Rectangle newBounds) {
		_models.add(model);
		_difference = this.calculateDifference(model, newBounds);
	}

	/**
	 * Calculates the difference between the original location of the widget and the new location.
	 * @param model
	 * 			The {@link AbstractWidgetModel}
	 * @param newBounds
	 * 			The new bounds for the widget
	 * @return Dimension
	 * 			The difference between the original location of the widget and the new location
	 */
	private Dimension calculateDifference(final AbstractWidgetModel model, final Rectangle newBounds) {
		Dimension dim = newBounds.getLocation().getDifference(model.getLocation());
		return dim;
	}
	
	/**
	 * Sets the given {@link GuideModel} for the given orientation.
	 * @param guide
	 * 			The guide
	 * @param alignment
	 * 			The alignment for the guide
	 * @param isHorizontal
	 * 			The orientation of the guide
	 */
	public void setGuide(final GuideModel guide, final int alignment, final boolean isHorizontal) {
		if (isHorizontal) {
			_hGuide = guide;
			_hAlignment = alignment;
		} else {
			_vGuide = guide;
			_vAlignment = alignment;
		}
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
				.getContents(OPIWidgetsTransfer.getInstance());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public void execute() {		
		Clipboard clipboard = new Clipboard(Display.getCurrent());
		clipboard.setContents(new Object[] { _models },
				new Transfer[] { OPIWidgetsTransfer.getInstance() });
		
		_clonedWidgets = getWidgetsFromClipboard();
		
		_compoundCommand = new CompoundCommand();
		
		for (AbstractWidgetModel widgetModel : _clonedWidgets) {
			if (_difference!=null) {				
				widgetModel.setLocation((widgetModel.getLocation().x+_difference.width),
						(widgetModel.getLocation().y+_difference.height));
			} else {
				widgetModel.setLocation((widgetModel.getLocation().x+10),
						(widgetModel.getLocation().y+10));
			}
			_compoundCommand.add(new WidgetCreateCommand(widgetModel, _parent, 
					new Rectangle(widgetModel.getLocation(), widgetModel.getSize())));
			
			if (_hGuide != null) {
				ChangeGuideCommand hGuideCommand = new ChangeGuideCommand(widgetModel, true);
				hGuideCommand.setNewGuide(_hGuide, _hAlignment);
				_compoundCommand.add(hGuideCommand);
			}				
			if (_vGuide != null) {
				ChangeGuideCommand vGuideCommand = new ChangeGuideCommand(widgetModel, false);
				vGuideCommand.setNewGuide(_vGuide, _vAlignment);
				_compoundCommand.add(vGuideCommand);
			}
		}
		_compoundCommand.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void redo() {
		_compoundCommand.redo();
	}

	/**
	 * {@inheritDoc}
	 */
	public void undo() {
		_compoundCommand.undo();
	}

}
