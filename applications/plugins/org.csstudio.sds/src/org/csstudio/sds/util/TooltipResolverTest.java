/**
 *
 */
package org.csstudio.sds.util;

import static org.junit.Assert.assertEquals;

import org.csstudio.sds.internal.model.test.TestWidgetModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link TooltipResolver}.
 *
 * @author swende
 *
 */
public class TooltipResolverTest {

    private TestWidgetModel _widget;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _widget = new TestWidgetModel();
        _widget.setAliasValue("channel", "epics://pv1");
    }

    /**
     * Test method for
     * {@link org.csstudio.sds.util.TooltipResolver#resolve(java.lang.String)}.
     */
    @Test
    public void testResolve() {
        // test single replacements
        doTest("abc ${"+AbstractWidgetModel.PROP_ALIASES+"} cde", "abc {channel=epics://pv1} cde");
        doTest("abc ${"+AbstractWidgetModel.PROP_BORDER_COLOR+"} cde", "abc "+_widget.getColor(AbstractWidgetModel.PROP_BORDER_COLOR)+" cde");
        doTest("abc ${"+AbstractWidgetModel.PROP_BORDER_WIDTH+"} cde", "abc "+_widget.getBorderWidth()+" cde");
    }

    private void doTest(String tooltipPattern, String expectedResult) {
        String result = TooltipResolver.resolveToValue(tooltipPattern, _widget);
        assertEquals(expectedResult, result);
    }

}
