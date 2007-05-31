package org.csstudio.util.formula.test;

import junit.framework.TestCase;

import org.csstudio.util.formula.Formula;
import org.csstudio.util.formula.VariableNode;
import org.junit.Test;

/** Formula tests.
 *  @author Kay Kasemir
 */
public class FormulaTest extends TestCase
{
    private final static double epsilon = 0.001;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    @Test
    @SuppressWarnings("nls")
    public void testBasics() throws Exception
    {
        Formula f = new Formula("0");
        assertEquals("0.0", f.toString());
        assertEquals(0.0, f.eval(), epsilon);
        
        f = new Formula("-3.14 + 2");
        assertEquals("(-3.14 + 2.0)", f.toString());
        assertEquals(-1.14, f.eval(), epsilon);

        f = new Formula("-3.14 - 2");
        assertEquals(-5.14, f.eval(), epsilon);

        f = new Formula("-3.14 + 2 - 1.10");
        assertEquals(-2.24, f.eval(), epsilon);
 
        f = new Formula("-12/-3");
        assertEquals(4.0, f.eval(), epsilon);

        f = new Formula("1 + 2 * 3 - 4");
        assertEquals(3.0, f.eval(), epsilon);

        f = new Formula("(1 + 2) * (3 - 4)");
        assertEquals(-3.0, f.eval(), epsilon);
    }

    @Test
    @SuppressWarnings("nls")
    public void testBool() throws Exception
    {
        Formula f = new Formula("0");
        
        f = new Formula("2 & 3");
        assertEquals(1.0, f.eval(), epsilon);
        
        f = new Formula("2 == 3");
        assertEquals(0.0, f.eval(), epsilon);
        
        f = new Formula("2 != 3");
        assertEquals(1.0, f.eval(), epsilon);

        f = new Formula("!0");
        assertEquals(1.0, f.eval(), epsilon);

        f = new Formula("2 & 0");
        assertEquals(0.0, f.eval(), epsilon);

        f = new Formula("0 | 3");
        assertEquals(1.0, f.eval(), epsilon);
        
        f = new Formula("0 | 0");
        assertEquals(0.0, f.eval(), epsilon);
    }
    
    @Test
    @SuppressWarnings("nls")
    public void testFunctions() throws Exception
    {
        Formula f = new Formula("0");

        f = new Formula("sqrt(2) ^ 2");
        assertEquals(2.0, f.eval(), epsilon);

        f = new Formula("exp(ln(2))");
        assertEquals(2.0, f.eval(), epsilon);

        f = new Formula("2 ? 3 : 4");
        assertEquals(3.0, f.eval(), epsilon);

        f = new Formula("0 ? 3 : 4");
        assertEquals(4.0, f.eval(), epsilon);

        // Sequence of  x ? 1 : x ? 2 : 3
        // Get 1, 2, 3:
        f = new Formula("10<20 ? 1 : 10>20 ? 2 : 3");
        assertEquals(1.0, f.eval(), epsilon);

        f = new Formula("10>20 ? 1 : 10<20 ? 2 : 3");
        assertEquals(2.0, f.eval(), epsilon);

        f = new Formula("10>20 ? 1 : 10>20 ? 2 : 3");
        assertEquals(3.0, f.eval(), epsilon);
        
        f = new Formula("2>1 ? 3 : 4");
        assertEquals(3.0, f.eval(), epsilon);

        f = new Formula("!(2>1 ? 0 : 1)");
        assertEquals(1.0, f.eval(), epsilon);

        f = new Formula("(2<1) ? 3 : 4");
        assertEquals(4.0, f.eval(), epsilon);
        
        f = new Formula("(2<=2) ? 3 : 4");
        assertEquals(3.0, f.eval(), epsilon);

        f = new Formula("sqrt(2)");
        assertEquals(1.414, f.eval(), epsilon);

        f = new Formula("min(5, 4, 3, 2, 1)");
        assertEquals(1, f.eval(), epsilon);
    }
    
    @Test
    @SuppressWarnings("nls")
    public void testVariables() throws Exception
    {
        Formula f = new Formula("0");

        VariableNode v[] = new VariableNode[2];
        v[0] = new VariableNode("volt");
        v[1] = new VariableNode("curr");
        v[0].setValue(2.0);
        v[1].setValue(3.0);
        
        f = new Formula("0.5 * volt * curr", v);
        assertEquals(3.0, f.eval(), epsilon);

        v[0].setValue(20.0);
        v[1].setValue(30.0);
        assertEquals(300.0, f.eval(), epsilon);
        
        v[0].setValue(2.0);
        v[1].setValue(3.0);
        assertEquals(3.0, f.eval(), epsilon);
        
        f = new Formula("max(volt, curr, -2)", v);
        assertEquals(3.0, f.eval(), epsilon);
    }
    
    @Test
    @SuppressWarnings("nls")
    public void testErrors()
    {
        try
        {
            @SuppressWarnings("unused")
            Formula f = new Formula("-");
            fail("Didn't catch parse error");
        }
        catch (Exception ex)
        {
            assertEquals("Unexpected end of formula.", ex.getMessage());
        }

        try
        {
            @SuppressWarnings("unused")
            Formula f = new Formula("max 2");
            fail("Didn't catch parse error");
        }
        catch (Exception ex)
        {
            // TODO Can the scanner be fixed to get 'max' instead of 'max2'
            //      for the var. name?
            assertEquals("Unknown variable 'max2'", ex.getMessage());
        }
    }
}
