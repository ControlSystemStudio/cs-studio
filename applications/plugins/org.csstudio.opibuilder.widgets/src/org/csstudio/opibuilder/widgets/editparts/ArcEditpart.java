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
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.ArcFigure;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.ArcModel;
import org.eclipse.draw2d.IFigure;

/**The controller for arc widget.
 * @author jbercic (original author), Xihui Chen (import from SDS since 2009/9)
 *
 */
public class ArcEditpart extends AbstractShapeEditPart {

	
	@Override
	protected IFigure doCreateFigure() {
		ArcFigure figure = new ArcFigure();
		ArcModel model = getWidgetModel();
		figure.setFill(model.isFill());		
		figure.setAntiAlias(model.isAntiAlias());
		figure.setStartAngle(model.getStartAngle());
		figure.setTotalAngle(model.getTotalAngle());		
		return figure;
	}	
	
	@Override
	public ArcModel getWidgetModel() {
		return (ArcModel)getModel();
	}
	

	@Override
	protected void registerPropertyChangeHandlers() {
		super.registerPropertyChangeHandlers();
		// fill
		IWidgetPropertyChangeHandler fillHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ArcFigure figure = (ArcFigure) refreshableFigure;
				figure.setFill((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ArcModel.PROP_FILL, fillHandler);	
		
		
		// anti alias
		IWidgetPropertyChangeHandler antiAliasHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ArcFigure figure = (ArcFigure) refreshableFigure;
				figure.setAntiAlias((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractShapeModel.PROP_ANTIALIAS, antiAliasHandler);
		
		
		//start angle
		IWidgetPropertyChangeHandler startAngleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ArcFigure figure = (ArcFigure) refreshableFigure;
				figure.setStartAngle((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ArcModel.PROP_START_ANGLE, startAngleHandler);
		
		//total angle
		IWidgetPropertyChangeHandler totalAngleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ArcFigure figure = (ArcFigure) refreshableFigure;
				figure.setTotalAngle((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ArcModel.PROP_TOTAL_ANGLE, totalAngleHandler);
		
	}


}
