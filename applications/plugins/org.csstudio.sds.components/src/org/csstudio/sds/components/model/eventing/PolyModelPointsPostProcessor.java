/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.sds.components.model.eventing;

import org.csstudio.sds.components.model.AbstractPolyModel;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.commands.Command;

/**
 * TODO (hrickens) : 
 * 
 * @author hrickens
 * @author $Author: $
 * @since 19.10.2010

 */
public class PolyModelPointsPostProcessor extends
		AbstractWidgetPropertyPostProcessor<AbstractPolyModel> {

	/**
	 * Constructor.
	 */
	public PolyModelPointsPostProcessor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command doCreateCommand(AbstractPolyModel widget) {
		assert widget != null : "widget != null";
		return new SetPointsCommand(widget);
	}

	
	private static class SetPointsCommand extends Command {

		private final AbstractPolyModel widget;

		public SetPointsCommand(AbstractPolyModel widget) {
			this.widget = widget;
		}
		
		@Override
		public void execute() {
			PointList pointList = widget.getPoints();
			widget.setHeight(pointList.getBounds().height);
			widget.setWidth(pointList.getBounds().width);
			widget.setX(pointList.getBounds().x);
			widget.setY(pointList.getBounds().y);
		}
		
	}
}
