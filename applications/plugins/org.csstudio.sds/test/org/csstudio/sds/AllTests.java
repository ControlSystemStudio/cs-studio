package org.csstudio.sds;

import org.csstudio.sds.internal.connection.ChannelReferenceValidationUtilTest;
import org.csstudio.sds.internal.connection.ConnectionUtilNewTest;
import org.csstudio.sds.internal.model.WidgetModelFactoryDescriptorTest;
import org.csstudio.sds.internal.model.logic.RuleStateTest;
import org.csstudio.sds.internal.persistence.BooleanPropertyPersistenceHandlerTest;
import org.csstudio.sds.internal.persistence.ColorPropertyPersistenceHandlerTest;
import org.csstudio.sds.internal.persistence.DoubleArrayPropertyPersistenceHandlerTest;
import org.csstudio.sds.internal.persistence.DoublePropertyPersistenceHandlerTest;
import org.csstudio.sds.internal.persistence.FontPropertyPersistenceHandlerTest;
import org.csstudio.sds.internal.persistence.IntegerPropertyPersistenceHandlerTest;
import org.csstudio.sds.internal.persistence.OptionPropertyPersistenceHandlerTest;
import org.csstudio.sds.internal.persistence.PointListPropertyPersistenceHandlerTest;
import org.csstudio.sds.internal.persistence.StringPropertyPersistenceHandlerTest;
import org.csstudio.sds.internal.rules.DirectConnectionRuleTest;
import org.csstudio.sds.internal.rules.LogicExceptionTest;
import org.csstudio.sds.internal.rules.ParameterDescriptorTest;
import org.csstudio.sds.internal.rules.RuleDescriptorTest;
import org.csstudio.sds.internal.rules.SimpleColorRuleTest;
import org.csstudio.sds.internal.rules.VisibilityRuleTest;
import org.csstudio.sds.internal.statistics.StatisticUtilTest;
import org.csstudio.sds.model.AbstractWidgetModelTest;
import org.csstudio.sds.model.DisplayModelTest;
import org.csstudio.sds.model.DynamicsDescriptorTest;
import org.csstudio.sds.model.WidgetPropertyTest;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchemaTest;
import org.csstudio.sds.model.initializers.WidgetInitializationServiceTest;
import org.csstudio.sds.model.optionEnums.TextTypeEnumTest;
import org.csstudio.sds.model.persistence.internal.DisplayModelInputStreamTest;
import org.csstudio.sds.model.persistence.internal.XmlProcessingTest;
import org.csstudio.sds.util.ColorAndFontUtilTest;
import org.csstudio.sds.util.PathUtilTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses( {
	ChannelReferenceValidationUtilTest.class,
	ConnectionUtilNewTest.class,

	RuleStateTest.class,
	WidgetModelFactoryDescriptorTest.class,

	BooleanPropertyPersistenceHandlerTest.class,
	ColorPropertyPersistenceHandlerTest.class,
	DoubleArrayPropertyPersistenceHandlerTest.class,
	DoublePropertyPersistenceHandlerTest.class,
	FontPropertyPersistenceHandlerTest.class,
	IntegerPropertyPersistenceHandlerTest.class,
	OptionPropertyPersistenceHandlerTest.class,
	PointListPropertyPersistenceHandlerTest.class,
	StringPropertyPersistenceHandlerTest.class,

	DirectConnectionRuleTest.class,
	LogicExceptionTest.class,
	ParameterDescriptorTest.class,
	RuleDescriptorTest.class,
	SimpleColorRuleTest.class,
	VisibilityRuleTest.class,

	StatisticUtilTest.class,

	AbstractControlSystemSchemaTest.class,
	WidgetInitializationServiceTest.class,

	TextTypeEnumTest.class,

	DisplayModelInputStreamTest.class,
	XmlProcessingTest.class,

	AbstractWidgetModelTest.class,
	DisplayModelTest.class,
	DynamicsDescriptorTest.class,
	WidgetPropertyTest.class,

	ColorAndFontUtilTest.class,
	PathUtilTest.class,

	SdsPluginTest.class
})
public class AllTests {
    // EMPTY
}
