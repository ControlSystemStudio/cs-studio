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
package org.csstudio.opibuilder.util;




import java.util.HashMap;
import java.util.Map;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.GuideModel;



/**
 * A class, which contains the information which {@link AbstractWidgetModel} is attached 
 * to which vertical and horizontal Guide. 
 * @author Kai Meyer
 *
 */
public final class GuideUtil {
	/**
	 * A Map which contains the vertical guide of the AbstractWidgetModels.
	 */
	private Map<AbstractWidgetModel, GuideModel> _verticalGuides = new HashMap<AbstractWidgetModel, GuideModel>();
	/**
	 * A Map which contains the horizontal guide of the AbstractWidgetModels.
	 */
	private Map<AbstractWidgetModel, GuideModel> _horizontalGuides = new HashMap<AbstractWidgetModel, GuideModel>();
	
	/**
	 * The instance of the GuideUtil.
	 */
	private static GuideUtil _instance;
	
	/**
	 * Constructor.
	 */
	private GuideUtil() {
		//nothing to do yet
	}
	
	/**
	 * Returns the instance of the GuideUtil.
	 * @return GuideUtil
	 * 			The GuideUtil
	 */
	public static GuideUtil getInstance() {
		if (_instance==null) {
			_instance = new GuideUtil();
		}
		return _instance;
	}
	
	/**
	 * Sets the Guide for the given {@link AbstractWidgetModel}.
	 * Regarding if the guide is vertical or horizontal
	 * @param model
	 * 			The AbstractWidgetModel, which is attached to the guide
	 * @param guide
	 * 			The guide (may not be null)
	 */
	public void setGuide(final AbstractWidgetModel model, final GuideModel guide) {
		assert guide!=null;
		if (guide.isHorizontal()) {
			// if the is already attached to another guide, cancel this connection 
			if (_horizontalGuides.containsKey(model)) {
				if (guide!=_horizontalGuides.get(model)) {
					_horizontalGuides.get(model).detachPart(model);
				}
			}
			_horizontalGuides.put(model, guide);
		} else {
			// if the is already attached to another guide, cancel this connection
			if (_verticalGuides.containsKey(model)) {
				if (guide!=_verticalGuides.get(model)) {
					_verticalGuides.get(model).detachPart(model);
				}
			}
			_verticalGuides.put(model, guide);
		}
	}
	
	/**
	 * Removes the vertical or the horizontal Guide of a {@link AbstractWidgetModel}
	 * depending on the boolean flag.
	 * @param model
	 * 			The AbstractWidgetModel, which is attached to the guide
	 * @param horizontal
	 * 			The orientation of the guide, which should be removed
	 */
	public void removeGuide(final AbstractWidgetModel model, final boolean horizontal) {
		if (horizontal) {
			_horizontalGuides.remove(model);
		} else {
			_verticalGuides.remove(model);
		}
	}
	
	/**
	 * Returns the guide to which the given {@link AbstractWidgetModel} is attached 
	 * in the given orientation.
	 * @param model
	 * 			The AbstractWidgetModel, which is attached to the guide
	 * @param horizontal
	 * 			The orientation of the guide, which should be returned
	 * @return	GuideModel
	 * 			The guide to which the given {@link AbstractWidgetModel}
	 * 			is attached in the given orientation or null.
	 */
	public GuideModel getGuide(final AbstractWidgetModel model, final boolean horizontal) {
		if (horizontal) {
			return _horizontalGuides.get(model);
		}
		return _verticalGuides.get(model);
	}

}
