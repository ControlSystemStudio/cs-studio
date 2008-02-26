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
 package org.csstudio.sds.components.epics;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.eclipse.swt.graphics.RGB;

/**
 * This initialization schema holds the property values of the EPICS control
 * system.
 * 
 * @author Stefan Hofer
 * @version $Revision$
 * 
 */
public final class EpicsSchema extends AbstractControlSystemSchema {

	/**
	 * Identifier the default foreground color property.
	 */
	public static final String DEFAULT_FOREGROUND_COLOR = "DEFAULT_FOREGROUND_COLOR";

	/**
	 * Identifier the default background color property.
	 */
	public static final String DEFAULT_BACKGROUND_COLOR = "DEFAULT_BACKGROUND_COLOR";

	/**
	 * Identifier the default error color property.
	 */
	public static final String DEFAULT_ERROR_COLOR = "DEFAULT_ERROR_COLOR";

	/**
	 * Identifier the default timelag color property.
	 */
	public static final String DEFAULT_TIMELAG_COLOR = "DEFAULT_TIMELAG_COLOR";
	
	/**
	 * Identifier the record alias property.
	 */
	public static final String RECORD_ALIAS_NAME = "RECORD_ALIAS_NAME";

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeWidget(final AbstractWidgetModel widgetModel) {
		widgetModel.setPrimarPv("$channel$");
		// default colors
		widgetModel.setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND,
				getColorProperty(DEFAULT_BACKGROUND_COLOR));
		widgetModel.setPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND,
				getColorProperty(DEFAULT_FOREGROUND_COLOR));
		
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeProperties() {
		addGlobalProperty(DEFAULT_BACKGROUND_COLOR, new RGB(230, 230, 230));
		addGlobalProperty(DEFAULT_FOREGROUND_COLOR, new RGB(0, 0, 192));
		addGlobalProperty(DEFAULT_ERROR_COLOR, new RGB(255, 0, 0));
		addGlobalProperty(DEFAULT_TIMELAG_COLOR, new RGB(255, 0, 255));
		addGlobalProperty(RECORD_ALIAS_NAME, "channel");

		// and so on ..
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeAliases(final AbstractWidgetModel widgetModel) {
		widgetModel.addAlias("channel", "");
	}
}
