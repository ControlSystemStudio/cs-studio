package org.csstudio.dct;

import org.csstudio.dct.metamodel.internal.ChoiceTest;
import org.csstudio.dct.metamodel.internal.DatabaseDefinitionTest;
import org.csstudio.dct.metamodel.internal.FieldDefinitionTest;
import org.csstudio.dct.metamodel.internal.MenuDefinitionTest;
import org.csstudio.dct.metamodel.internal.RecordDefinitionTest;
import org.csstudio.dct.model.commands.AddInstanceCommandTest;
import org.csstudio.dct.model.commands.AddPrototypeCommandTest;
import org.csstudio.dct.model.commands.AddRecordCommandTest;
import org.csstudio.dct.model.commands.ChangeFieldValueCommandTest;
import org.csstudio.dct.model.commands.ChangePropertyKeyCommandTest;
import org.csstudio.dct.model.commands.ChangePropertyValueCommandTest;
import org.csstudio.dct.model.commands.InitInstanceCommandTest;
import org.csstudio.dct.model.commands.RemoveInstanceCommandTest;
import org.csstudio.dct.model.commands.RemoveRecordCommandTest;
import org.csstudio.dct.model.internal.AbstractElementTest;
import org.csstudio.dct.model.internal.AbstractPropertyContainerTest;
import org.csstudio.dct.model.internal.FolderTest;
import org.csstudio.dct.model.internal.InstanceTest;
import org.csstudio.dct.model.internal.ParameterTest;
import org.csstudio.dct.model.internal.PrototypeTest;
import org.csstudio.dct.model.internal.RecordTest;
import org.csstudio.dct.model.persistence.internal.ProjectToXmlTest;
import org.csstudio.dct.nameresolution.RecordFinderTest;
import org.csstudio.dct.nameresolution.internal.FieldFunctionServiceTest;
import org.csstudio.dct.util.AliasResolutionUtilTest;
import org.csstudio.dct.util.CompareUtilTest;
import org.csstudio.dct.util.ModelValidationUtilTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    ChoiceTest.class,
	DatabaseDefinitionTest.class,
	FieldDefinitionTest.class,
	MenuDefinitionTest.class,
	RecordDefinitionTest.class,

	AddInstanceCommandTest.class,
	AddPrototypeCommandTest.class,
	AddRecordCommandTest.class,
	ChangeFieldValueCommandTest.class,
	ChangePropertyKeyCommandTest.class,
	ChangePropertyValueCommandTest.class,
	InitInstanceCommandTest.class,
	RemoveInstanceCommandTest.class,
	RemoveRecordCommandTest.class,

	AbstractElementTest.class,
	AbstractPropertyContainerTest.class,
	FolderTest.class,
	InstanceTest.class,
	ParameterTest.class,
	PrototypeTest.class,
	RecordTest.class,

	ProjectToXmlTest.class,

	FieldFunctionServiceTest.class,

	RecordFinderTest.class,

	AliasResolutionUtilTest.class,
	CompareUtilTest.class,
	ModelValidationUtilTest.class
})
public class AllTests {
}


