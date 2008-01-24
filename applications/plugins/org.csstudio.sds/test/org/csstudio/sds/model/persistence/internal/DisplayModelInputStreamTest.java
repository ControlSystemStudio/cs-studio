package org.csstudio.sds.model.persistence.internal;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.csstudio.sds.internal.model.test.TestWidgetModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.logic.DirectConnectionRule;
import org.csstudio.sds.model.logic.ParameterDescriptor;
import org.csstudio.sds.model.persistence.DisplayModelInputStream;
import org.eclipse.swt.graphics.RGB;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.context.ConnectionState;
import org.junit.Test;

/**
 * 
 * @version $Revision$
 * 
 */
public final class DisplayModelInputStreamTest {
	/**
	 * The XML contents that the display model input stream should generate.
	 */
	private static final String[] XML_CONTENTS = new String[] {
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>", //$NON-NLS-1$
			"<display modelVersion=\"1.0\">", //$NON-NLS-1$
			"<property type=\"sds.color\" id=\"color.background\">", //$NON-NLS-1$
			"<color red=\"128\" green=\"0\" blue=\"255\" />", //$NON-NLS-1$
			"<dynamicsDescriptor ruleId=\"directConnection\">", //$NON-NLS-1$
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
			"<dynamicsDescriptor ruleId=\"directConnection\">", //$NON-NLS-1$
			"<inputChannel name=\"channel2\" type=\"java.lang.Double\" />", //$NON-NLS-1$
			"<connectionState state=\"CONNECTED\" value=\"20\" />", //$NON-NLS-1$
			"<dynamicValueState state=\"ALARM\" value=\"30\" />", //$NON-NLS-1$
			"</dynamicsDescriptor>", //$NON-NLS-1$
			"</property>", //$NON-NLS-1$
			"</widget>", "</display>" }; //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Test the display model input stream.
	 */
	@Test
	public void testContentModel() {
		DisplayModel model = new DisplayModel();
		model.setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND,
				new RGB(128, 0, 255));
		model.setDynamicsDescriptor(AbstractWidgetModel.PROP_COLOR_BACKGROUND,
				createDynamicsDescriptor(DirectConnectionRule.TYPE_ID,
						"channel1", Integer.class)); //$NON-NLS-1$

		AbstractWidgetModel widgetModel = new TestWidgetModel();
		model.addWidget(widgetModel);

		model.addAlias("aliasName", "aliasValue"); //$NON-NLS-1$ //$NON-NLS-2$

		DynamicsDescriptor dynamicsDescriptor = createDynamicsDescriptor(
				DirectConnectionRule.TYPE_ID, "channel2", Double.class); //$NON-NLS-1$

		HashMap<ConnectionState, Object> connectionStateMap = new HashMap<ConnectionState, Object>();
		connectionStateMap.put(ConnectionState.CONNECTED, new Integer(20));

		HashMap<DynamicValueState, Object> dynamicValueStateMap = new HashMap<DynamicValueState, Object>();
		dynamicValueStateMap.put(DynamicValueState.ALARM, new Integer(30));

		dynamicsDescriptor
				.setConnectionStateDependentPropertyValues(connectionStateMap);
		dynamicsDescriptor
				.setConditionStateDependentPropertyValues(dynamicValueStateMap);

		widgetModel.setDynamicsDescriptor(AbstractWidgetModel.PROP_POS_X,
				dynamicsDescriptor);

		DisplayModelInputStream dmis = new DisplayModelInputStream(model);

		List<Integer> intList = new ArrayList<Integer>();
		while (dmis.available() > 0) {
			intList.add(dmis.read());
		}

		byte[] byteArr = new byte[intList.size()];
		for (int i = 0; i < intList.size(); i++) {
			byteArr[i] = intList.get(i).byteValue();
		}

		String xmlString = new String(byteArr);

		for (String s : XML_CONTENTS) {
			assertTrue(s, xmlString.indexOf(s) > -1);
		}
	}

	/**
	 * Create a dynamics descriptor with the given parameters.
	 * 
	 * @param ruleId
	 *            the used rule ID.
	 * @param channelName
	 *            the channel name.
	 * @param channelType
	 *            the channel type.
	 * @return a dynamics descriptor with the given parameters.
	 */
	@SuppressWarnings("unchecked")
	protected DynamicsDescriptor createDynamicsDescriptor(String ruleId,
			String channelName, Class channelType) {
		DynamicsDescriptor result = new DynamicsDescriptor(ruleId);
		ParameterDescriptor parameterDescr = new ParameterDescriptor(
				channelName, channelType);

		result.addInputChannel(parameterDescr);

		return result;
	}
}
