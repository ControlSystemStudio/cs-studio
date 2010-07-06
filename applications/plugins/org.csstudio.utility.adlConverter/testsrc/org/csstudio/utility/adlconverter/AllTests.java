package org.csstudio.utility.adlconverter;


import org.csstudio.utility.adlconverter.utility.ADLBasicAttributeTest;
import org.csstudio.utility.adlconverter.utility.ADLHelperTest;
import org.csstudio.utility.adlconverter.utility.ADLObjectTest;
import org.csstudio.utility.adlconverter.utility.RGBColorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
    ADLBasicAttributeTest.class,
	ADLHelperTest.class,
	ADLObjectTest.class,
	RGBColorTest.class
})
public class AllTests {
    // EMPTY
}
