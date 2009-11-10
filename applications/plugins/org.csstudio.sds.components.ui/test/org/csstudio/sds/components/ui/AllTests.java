package org.csstudio.sds.components.ui;

import org.csstudio.sds.components.ui.internal.editparts.AdvancedSliderEditPartTest;
import org.csstudio.sds.components.ui.internal.editparts.BargraphEditPartTest;
import org.csstudio.sds.components.ui.internal.editparts.EllipseEditPartTest;
import org.csstudio.sds.components.ui.internal.editparts.LabelEditPartTest;
import org.csstudio.sds.components.ui.internal.editparts.PolygonEditPartTest;
import org.csstudio.sds.components.ui.internal.editparts.PolylineEditPartTest;
import org.csstudio.sds.components.ui.internal.editparts.RectangleEditPartTest;
import org.csstudio.sds.components.ui.internal.editparts.SimpleSliderEditPartTest;
import org.csstudio.sds.components.ui.internal.editparts.TextInputEditPartTest;
import org.csstudio.sds.components.ui.internal.editparts.WaveformEditPartTest;
import org.csstudio.sds.components.ui.internal.feedback.PointListHelperTest;
import org.csstudio.sds.components.ui.internal.figures.LinearAxisTest;
import org.csstudio.sds.components.ui.internal.figures.LogarithmicAxisTest;
import org.csstudio.sds.components.ui.internal.figures.TickCalculatorTest;
import org.csstudio.sds.components.ui.internal.figures.TickTest;
import org.csstudio.sds.components.ui.internal.figures.WidgetFigureTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses( { AdvancedSliderEditPartTest.class,
	BargraphEditPartTest.class,
	EllipseEditPartTest.class,
	LabelEditPartTest.class,
	PolygonEditPartTest.class,
	PolylineEditPartTest.class,
	RectangleEditPartTest.class,
	SimpleSliderEditPartTest.class,
	TextInputEditPartTest.class,
	WaveformEditPartTest.class,
	
	PointListHelperTest.class,
	
	LinearAxisTest.class,
	LogarithmicAxisTest.class,
	TickCalculatorTest.class,
	TickTest.class,
} )
public class AllTests {
}
//public class AllTests {
//
//	public static Test suite() {
//		TestSuite suite = new TestSuite("Test for de.desy.language.libraries");
//		//$JUnit-BEGIN$
//		suite.addTestSuite(Contract_Test.class);
//		//$JUnit-END$
//		return suite;
//	}
//
//}
