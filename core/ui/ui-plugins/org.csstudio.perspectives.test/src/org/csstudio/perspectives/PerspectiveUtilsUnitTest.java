package org.csstudio.perspectives;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.e4.ui.model.application.ui.advanced.MAdvancedFactory;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.impl.AdvancedFactoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

public class PerspectiveUtilsUnitTest {

    private MAdvancedFactory factory;

    private PerspectiveUtils perspectiveUtils;

    private MPerspective perspective;

    @Before
    public void setUp() throws Exception {
        factory = new AdvancedFactoryImpl();
        perspectiveUtils = new PerspectiveUtils();
        perspective = factory.createPerspective();
    }

    @Test
    public void perspToStringReturnsStringForEmptyPerspective() throws IOException {
        String s = perspectiveUtils.perspToString(perspective);
        assertNotNull(s);
        assertTrue(s.contains("advanced:Perspective"));
    }

    @Test
    public void testPerspToStringIncludesPersistedState() throws IOException {
        perspective.getPersistedState().put("Hello", "World");
        String s = perspectiveUtils.perspToString(perspective);
        assertTrue(s.contains("Hello"));
        assertTrue(s.contains("World"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPerspToStringThrowsIllegalArgumentExceptionForNull() throws IOException {
        String s = perspectiveUtils.perspToString(null);
    }

}
