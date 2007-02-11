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
package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.AbstractPolyElement;
import org.csstudio.sds.components.model.PolygonElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshablePolygonFigure;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.ui.editparts.IElementPropertyChangeHandler;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.eclipse.draw2d.geometry.PointList;

/**
 * EditPart controller for <code>PolygonElement</code> elements.
 * 
 * @author Sven Wende & Stefan Hofer
 * 
 */
public final class PolygonEditPart extends AbstractElementEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IRefreshableFigure doCreateFigure() {
		RefreshablePolygonFigure polygon = new RefreshablePolygonFigure();
		PolygonElement elementModel = (PolygonElement) getCastedModel();
		polygon.setPoints(elementModel.getPoints());
		polygon.setFill(elementModel.getFill());

		return polygon;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// fill
		IElementPropertyChangeHandler fillHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshablePolygonFigure polygon = (RefreshablePolygonFigure) refreshableFigure;
				polygon.setFill((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPolyElement.PROP_FILL, fillHandler);

		// points
		IElementPropertyChangeHandler pointsHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IRefreshableFigure refreshableFigure) {
				RefreshablePolygonFigure polygon = (RefreshablePolygonFigure) refreshableFigure;
				PointList points = (PointList) newValue;
				polygon.setPoints(points);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPolyElement.PROP_POINTS, pointsHandler);
	}
}
