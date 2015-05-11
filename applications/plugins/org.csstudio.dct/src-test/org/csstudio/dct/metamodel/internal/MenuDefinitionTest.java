/**
 *
 */
package org.csstudio.dct.metamodel.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.csstudio.dct.metamodel.IChoice;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link MenuDefinition}.
 *
 * @author Sven Wende
 *
 */
public final class MenuDefinitionTest {

    private MenuDefinition menuDefinition;
    private IChoice yesChoice;
    private IChoice noChoice;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        yesChoice = new Choice("menuYesNoYES", "Yes");
        noChoice = new Choice("menuYesNoNO", "No");
        menuDefinition = new MenuDefinition("menuYesNo");
        menuDefinition.addChoice(yesChoice);
        menuDefinition.addChoice(noChoice);
    }

    /**
     * Test method for {@link org.csstudio.dct.metamodel.internal.MenuDefinition#getChoices()}.
     */
    @Test
    public void testGetChoices() {
        List<IChoice> choices = menuDefinition.getChoices();
        assertEquals(2, choices.size());
        assertTrue(choices.contains(yesChoice));
        assertTrue(choices.contains(noChoice));
    }

    /**
     * Test method for {@link org.csstudio.dct.metamodel.internal.MenuDefinition#addChoice(org.csstudio.dct.metamodel.IChoice)}.
     */
    @Test
    public void testAddChoice() {
        IChoice maybeChoice = new Choice("menuYesNoMAYBE", "Maybe");
        assertFalse(menuDefinition.getChoices().contains(maybeChoice));
        menuDefinition.addChoice(maybeChoice);
        assertTrue(menuDefinition.getChoices().contains(maybeChoice));
    }

    /**
     * Test method for {@link org.csstudio.dct.metamodel.internal.MenuDefinition#removeChoice(org.csstudio.dct.metamodel.IChoice)}.
     */
    @Test
    public void testRemoveChoice() {
        assertTrue(menuDefinition.getChoices().contains(yesChoice));
        assertTrue(menuDefinition.getChoices().contains(noChoice));
        menuDefinition.removeChoice(yesChoice);
        menuDefinition.removeChoice(noChoice);
        assertTrue(menuDefinition.getChoices().isEmpty());
    }

    /**
     * Test method for {@link org.csstudio.dct.metamodel.internal.MenuDefinition#getName()}.
     */
    @Test
    public void testGetName() {
        assertEquals("menuYesNo", menuDefinition.getName());
    }

}
