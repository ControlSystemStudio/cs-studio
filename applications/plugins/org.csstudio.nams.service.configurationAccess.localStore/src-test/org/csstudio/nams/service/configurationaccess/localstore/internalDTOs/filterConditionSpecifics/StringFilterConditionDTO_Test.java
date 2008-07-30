package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.common.testutils.AbstractObject_TestCase;

public class StringFilterConditionDTO_Test extends
		AbstractObject_TestCase<StringFilterConditionDTO> {

	@Override
	protected StringFilterConditionDTO getNewInstanceOfClassUnderTest() {
		final StringFilterConditionDTO neueFilterCondition = new StringFilterConditionDTO();
		neueFilterCondition.setCName("Test");
		neueFilterCondition.setCompValue("TestValue");
		neueFilterCondition.setKeyValue(MessageKeyEnum.DESTINATION);
		neueFilterCondition
				.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);

		return neueFilterCondition;
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected StringFilterConditionDTO[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		final StringFilterConditionDTO[] neueFilterConditions = new StringFilterConditionDTO[3];

		neueFilterConditions[0] = new StringFilterConditionDTO();
		neueFilterConditions[0].setCName("Test");
		neueFilterConditions[0].setCompValue("TestValue");
		neueFilterConditions[0].setKeyValue(MessageKeyEnum.DESTINATION);
		neueFilterConditions[0]
				.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);

		neueFilterConditions[1] = new StringFilterConditionDTO();
		neueFilterConditions[1].setCName("Test2");
		neueFilterConditions[1].setCompValue("42");
		neueFilterConditions[1].setKeyValue(MessageKeyEnum.EVENTTIME);
		neueFilterConditions[1]
				.setOperatorEnum(StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL);

		neueFilterConditions[2] = new StringFilterConditionDTO();
		neueFilterConditions[2].setCName("Test2");
		neueFilterConditions[2].setCompValue("23");
		neueFilterConditions[2].setKeyValue(MessageKeyEnum.EVENTTIME);
		neueFilterConditions[2]
				.setOperatorEnum(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL);

		return neueFilterConditions;
	}

}
