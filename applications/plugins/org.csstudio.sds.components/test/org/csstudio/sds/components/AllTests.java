package org.csstudio.sds.components;

import org.csstudio.sds.components.internal.model.BargraphModeltFactoryTest;
import org.csstudio.sds.components.internal.model.EllipseModelFactoryTest;
import org.csstudio.sds.components.internal.model.LabelModelFactoryTest;
import org.csstudio.sds.components.internal.model.PolygonModelFactoryTest;
import org.csstudio.sds.components.internal.model.PolylineModelFactoryTest;
import org.csstudio.sds.components.internal.model.RectangleModeltFactoryTest;
import org.csstudio.sds.components.model.BargraphElementTest;
import org.csstudio.sds.components.model.EllipseElementTest;
import org.csstudio.sds.components.model.LabelElementTest;
import org.csstudio.sds.components.model.PolygonElementTest;
import org.csstudio.sds.components.model.PolylineElementTest;
import org.csstudio.sds.components.model.RectangleElementTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses( {
        BargraphModeltFactoryTest.class,
		EllipseModelFactoryTest.class,
		LabelModelFactoryTest.class,
		PolygonModelFactoryTest.class,
		PolylineModelFactoryTest.class,
		RectangleModeltFactoryTest.class,

		BargraphElementTest.class,
		EllipseElementTest.class,
		LabelElementTest.class,
		PolygonElementTest.class,
		PolylineElementTest.class,
		RectangleElementTest.class})
public class AllTests {
    // EMPTY
}

