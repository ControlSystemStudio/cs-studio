package org.csstudio.opibuilder.commands;


import java.util.Iterator;


import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.GuideModel;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

/**
 * A Command to move a Guide.
 * @author Xihui Chen
 */
public final class MoveGuideCommand extends Command {
	/**
	 * The distance.
	 */
	private int pDelta;
	/**
	 * The guide, which position has changed.
	 */
	private GuideModel guide;
	
	/**
	 * Constructor.
	 * @param guide
	 * 			the guide, which position has changed
	 * @param pDelta
	 * 			the distance
	 */
	public MoveGuideCommand(final GuideModel guide, final int pDelta) {
		this.pDelta = pDelta;
		this.guide = guide;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		guide.setPosition(guide.getPosition() + pDelta);
		Iterator<AbstractWidgetModel> iter = guide.getAttachedModels().iterator();
		while (iter.hasNext()) {
			AbstractWidgetModel model = iter.next();
			Point location = model.getLocation(); 
			if (guide.isHorizontal()) {
				location.y += pDelta;
			} else {
				location.x += pDelta;
			}
			model.setLocation(location.x, location.y);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		guide.setPosition(guide.getPosition() - pDelta);
		Iterator<AbstractWidgetModel> iter = guide.getAttachedModels().iterator();
		while (iter.hasNext()) {
			AbstractWidgetModel model = iter.next();
			Point location = model.getLocation(); 
			if (guide.isHorizontal()) {
				location.y -= pDelta;
			} else {
				location.x -= pDelta;
			}
			model.setLocation(location.x, location.y);
		}
	}

}
