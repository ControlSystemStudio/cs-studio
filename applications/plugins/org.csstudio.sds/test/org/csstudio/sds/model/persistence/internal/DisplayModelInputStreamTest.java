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
package org.csstudio.sds.model.persistence.internal;

import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.DynamicsDescriptor;

/**
 * TODO: test methode wurde entfernt ???
 * @version $Revision: 1.14 $
 * 
 */
public final class DisplayModelInputStreamTest {
	/**
	 * The XML contents that the display model input stream should generate.
	 */
	private static final String[] XML_CONTENTS = new String[] {
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>", //$NON-NLS-1$
			"<display modelVersion=\"1.1\">", //$NON-NLS-1$
			"<property type=\"sds.color\" id=\"color.background\">", //$NON-NLS-1$
			"<color hex=\"#8000FF\" />", //$NON-NLS-1$
			"<dynamicsDescriptor ruleId=\"directConnection\" useConnectionStates=\"false\">", //$NON-NLS-1$
			"<inputChannel name=\"channel1\" type=\"java.lang.Integer\" />", //$NON-NLS-1$
			"<connectionState state=\"CONNECTED\" value=\"20\" />", //$NON-NLS-1$
			"<dynamicValueState state=\"ALARM\" value=\"30\" />", //$NON-NLS-1$
			"</dynamicsDescriptor>", //$NON-NLS-1$			
			"</property>", //$NON-NLS-1$
			"<layer layer_name=\"DEFAULT\" layer_index=\"0\" layer_visibility=\"true\" />", //$NON-NLS-1$
			"<widget type=\"element.test\">", //$NON-NLS-1$
			"<property type=\"sds.map\" id=\"aliases\">", //$NON-NLS-1$
			"<map>", //$NON-NLS-1$
			"<mapEntry name=\"aliasName\" value=\"aliasValue\" />", //$NON-NLS-1$
			"</map>", //$NON-NLS-1$
			"<property type=\"sds.integer\" id=\"position.x\" value=\"10\">", //$NON-NLS-1$
			"<dynamicsDescriptor ruleId=\"directConnection\" useConnectionStates=\"false\">", //$NON-NLS-1$
			"<inputChannel name=\"channel2\" type=\"java.lang.Double\" />", //$NON-NLS-1$
			"<connectionState state=\"CONNECTED\" value=\"20\" />", //$NON-NLS-1$
			"<dynamicValueState state=\"ALARM\" value=\"30\" />", //$NON-NLS-1$
			"</dynamicsDescriptor>", //$NON-NLS-1$
			"</property>", //$NON-NLS-1$
			"</widget>", "</display>" }; //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Create a dynamics descriptor with the given parameters.
	 * 
	 * @param ruleId
	 *            the used rule ID.
	 * @param channelName
	 *            the channel name.
	 * @return a dynamics descriptor with the given parameters.
	 */
	protected DynamicsDescriptor createDynamicsDescriptor(String ruleId,
			String channelName) {
		DynamicsDescriptor result = new DynamicsDescriptor(ruleId);
		ParameterDescriptor parameterDescr = new ParameterDescriptor(
				channelName);

		result.addInputChannel(parameterDescr);

		return result;
	}
}
