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
package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.DoubleArrayProperty;

/**
 * This class defines a simple waverform widget model.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class WaveformModel extends AbstractWidgetModel {
	/**
	 * Property ID for the <i>wave</i>.
	 */
	public static final String PROP_WAVE_FORM = "wave"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(PROP_WAVE_FORM, new DoubleArrayProperty("Waveform Array",
				WidgetPropertyCategory.Behaviour, new double[] { 20.0, 15.0,
						33.0, 44.0, 22.0, 3.0, 25.0, 4.0 }));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return "element.waveform"; //$NON-NLS-1$
	}

	/**
	 * Return the waveform data.
	 * 
	 * @return The waveform data.
	 */
	public double[] getData() {
		return (double[]) getProperty(PROP_WAVE_FORM).getPropertyValue();
	}
}
