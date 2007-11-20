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
    
    @Test
    @SuppressWarnings("nls")
    public void testBasics() throws Exception
    {
        Formula f = new Formula("0");
        assertEquals("0.0", f.toString());
        assertEquals(0.0, f.eval(), epsilon);
        
        // floating point
        f = new Formula("-3.14");
        assertEquals(-3.14, f.eval(), epsilon);

        // exponential
        f = new Formula("-2.123e4");
        assertEquals(-2.123e4, f.eval(), epsilon);

        // exponential
        f = new Formula("-2.123e-14");
        assertEquals(-2.123e-14, f.eval(), epsilon);
        
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

        f = new Formula("exp(log(2))");
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

        f = new Formula("max(5, 4, 3, 2, 1)");
        assertEquals(5, f.eval(), epsilon);
        
        f = new Formula("sin(" + Math.toRadians(30) + ")");
        assertEquals(0.5, f.eval(), epsilon);
       
        f = new Formula("cos(30)");
        assertEquals(0.1543, f.eval(), epsilon);
        
        f = new Formula("atan2(10.0, 0.0)");
        assertEquals(90.0, Math.toDegrees(f.eval()), epsilon);
        
        f = new Formula("rnd(10.0)");
        for (int i=0; i<50; ++i)
        {
	        double rnd = f.eval();
	        assertTrue(rnd >= 0.0);
	        assertTrue(rnd < 10.0);
	        double rnd2 = f.eval();
	        // usually, should NOT get the same number twice...
	        assertTrue(rnd != rnd2);
        }
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

        f = new Formula("2*PI", v);
        assertEquals(2.0*Math.PI, f.eval(), epsilon);

        v[0] = new VariableNode("PI", 10.0);
        f = new Formula("PI", v);
        assertEquals(10.0, f.eval(), epsilon);
        assertTrue(f.hasSubnode(v[0]));
        assertTrue(! f.hasSubnode(v[1]));
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
            // Can the scanner be fixed to get 'max' instead of 'max2'
            // for the var. name?
            assertEquals("Unknown variable 'max2'", ex.getMessage());
        }
    }
}
