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

package org.csstudio.opibuilder.commands;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

/**
 * A command, which applies position and location changes to widget models.
 * 
 * @author Sven Wende(original author), Xihui Chen (since import from SDS 2009/9) 
 * 
 */
public final class SetBoundsCommand extends Command {
	/**
	 * Stores the new size and location of the widget.
	 */
	private final Rectangle newBounds;

	/**
	 * Stores the old size and location.
	 */
	private Rectangle oldBounds;

	/**
	 * The element, whose constraints are to be changed.
	 */
	private final AbstractWidgetModel widgetModel;

	/**
	 * Create a command that can resize and/or move a widget model.
	 * 
	 * @param widgetModel
	 *            the widget model to manipulate
	 * @param newBounds
	 *            the new size and location
	 */
	public SetBoundsCommand(final AbstractWidgetModel widgetModel,
			final Rectangle newBounds) {
		assert widgetModel != null;
		assert newBounds != null;
		this.widgetModel = widgetModel;
		this.newBounds = newBounds.getCopy();
		setLabel("Changing widget bounds");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		// remember old bounds
		oldBounds = new Rectangle(widgetModel.getLocation(), widgetModel.getSize());

		doApplyBounds(newBounds);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		doApplyBounds(oldBounds);
	}

	/**
	 * Applies the specified bounds to the widget model.
	 * @param bounds the bounds
	 */
	private void doApplyBounds(final Rectangle bounds) {
		// change element size
		widgetModel.setSize(bounds.width, bounds.height);

		// change location
		widgetModel.setLocation(bounds.x, bounds.y);
	}
}
