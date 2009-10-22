package org.csstudio.dct;

import org.csstudio.dct.metamodel.internal.ChoiceTest;
import org.csstudio.dct.metamodel.internal.DatabaseDefinitionTest;
import org.csstudio.dct.metamodel.internal.FieldDefinitionTest;
import org.csstudio.dct.metamodel.internal.MenuDefinitionTest;
import org.csstudio.dct.metamodel.internal.RecordDefinitionTest;
import org.csstudio.dct.nameresolution.RecordFinderTest;
import org.csstudio.dct.nameresolution.internal.FieldFunctionServiceTest;
import org.csstudio.dct.util.AliasResolutionUtilTest;
import org.csstudio.dct.util.CompareUtilTest;
import org.csstudio.dct.util.ModelValidationUtilTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { ChoiceTest.class, DatabaseDefinitionTest.class,
	FieldDefinitionTest.class, MenuDefinitionTest.class, RecordDefinitionTest.class,
	FieldFunctionServiceTest.class, RecordFinderTest.class, AliasResolutionUtilTest.class,
	CompareUtilTest.class, ModelValidationUtilTest.class})
public class AllTests {
}


