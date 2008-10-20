package org.csstudio.apputil.formula.test;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.csstudio.apputil.formula.Formula;
import org.csstudio.apputil.formula.VariableNode;
import org.csstudio.platform.logging.CentralLogger;
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
        Logger log = CentralLogger.getInstance().getLogger(this);
        Formula f = new Formula(log, "0");
        assertEquals("0.0", f.toString());
        assertEquals(0.0, f.eval(), epsilon);
        
        // floating point
        f = new Formula(log, "-3.14");
        assertEquals(-3.14, f.eval(), epsilon);

        // exponential
        f = new Formula(log, "-2.123e4");
        assertEquals(-2.123e4, f.eval(), epsilon);

        // exponential
        f = new Formula(log, "-2.123e-14");
        assertEquals(-2.123e-14, f.eval(), epsilon);
        
        f = new Formula(log, "-3.14 + 2");
        assertEquals("(-3.14 + 2.0)", f.toString());
        assertEquals(-1.14, f.eval(), epsilon);

        f = new Formula(log, "-3.14 - 2");
        assertEquals(-5.14, f.eval(), epsilon);

        f = new Formula(log, "-3.14 + 2 - 1.10");
        assertEquals(-2.24, f.eval(), epsilon);
 
        f = new Formula(log, "-12/-3");
        assertEquals(4.0, f.eval(), epsilon);

        f = new Formula(log, "1 + 2 * 3 - 4");
        assertEquals(3.0, f.eval(), epsilon);

        f = new Formula(log, "(1 + 2) * (3 - 4)");
        assertEquals(-3.0, f.eval(), epsilon);
    }

    @Test
    @SuppressWarnings("nls")
    public void testBool() throws Exception
    {
        Logger log = CentralLogger.getInstance().getLogger(this);
        Formula f = new Formula(log, "2 & 3");
        assertEquals(1.0, f.eval(), epsilon);
        
        f = new Formula(log, "2 == 3");
        assertEquals(0.0, f.eval(), epsilon);
        
        f = new Formula(log, "2 != 3");
        assertEquals(1.0, f.eval(), epsilon);

        f = new Formula(log, "!0");
        assertEquals(1.0, f.eval(), epsilon);

        f = new Formula(log, "2 & 0");
        assertEquals(0.0, f.eval(), epsilon);

        f = new Formula(log, "0 | 3");
        assertEquals(1.0, f.eval(), epsilon);
        
        f = new Formula(log, "0 | 0");
        assertEquals(0.0, f.eval(), epsilon);
    }
    
    @Test
    @SuppressWarnings("nls")
    public void testFunctions() throws Exception
    {
        Logger log = CentralLogger.getInstance().getLogger(this);
        Formula f = new Formula(log, "sqrt(2) ^ 2");
        assertEquals(2.0, f.eval(), epsilon);

        f = new Formula(log, "exp(log(2))");
        assertEquals(2.0, f.eval(), epsilon);

        f = new Formula(log, "2 ? 3 : 4");
        assertEquals(3.0, f.eval(), epsilon);

        f = new Formula(log, "0 ? 3 : 4");
        assertEquals(4.0, f.eval(), epsilon);

        // Sequence of  x ? 1 : x ? 2 : 3
        // Get 1, 2, 3:
        f = new Formula(log, "10<20 ? 1 : 10>20 ? 2 : 3");
        assertEquals(1.0, f.eval(), epsilon);

        f = new Formula(log, "10>20 ? 1 : 10<20 ? 2 : 3");
        assertEquals(2.0, f.eval(), epsilon);

        f = new Formula(log, "10>20 ? 1 : 10>20 ? 2 : 3");
        assertEquals(3.0, f.eval(), epsilon);
        
        f = new Formula(log, "2>1 ? 3 : 4");
        assertEquals(3.0, f.eval(), epsilon);

        f = new Formula(log, "!(2>1 ? 0 : 1)");
        assertEquals(1.0, f.eval(), epsilon);

        f = new Formula(log, "(2<1) ? 3 : 4");
        assertEquals(4.0, f.eval(), epsilon);
        
        f = new Formula(log, "(2<=2) ? 3 : 4");
        assertEquals(3.0, f.eval(), epsilon);

        f = new Formula(log, "sqrt(2)");
        assertEquals(1.414, f.eval(), epsilon);

        f = new Formula(log, "min(5, 4, 3, 2, 1)");
        assertEquals(1, f.eval(), epsilon);

        f = new Formula(log, "max(5, 4, 3, 2, 1)");
        assertEquals(5, f.eval(), epsilon);
        
        f = new Formula(log, "sin(" + Math.toRadians(30) + ")");
        assertEquals(0.5, f.eval(), epsilon);
        
        f = new Formula(log, "sin(toRadians(30))");
        assertEquals(0.5, f.eval(), epsilon);
        
        f = new Formula(log, "cos(30)");
        assertEquals(0.1543, f.eval(), epsilon);
        
        f = new Formula(log, "atan2(10.0, 0.0)");
        assertEquals(90.0, Math.toDegrees(f.eval()), epsilon);
        
        f = new Formula(log, "rnd(10.0)");
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
        Logger log = CentralLogger.getInstance().getLogger(this);

        VariableNode v[] = new VariableNode[2];
        v[0] = new VariableNode("volt");
        v[1] = new VariableNode("curr");
        v[0].setValue(2.0);
        v[1].setValue(3.0);
        
        Formula f = new Formula(log, "0.5 * volt * curr", v);
        assertEquals(3.0, f.eval(), epsilon);

        v[0].setValue(20.0);
        v[1].setValue(30.0);
        assertEquals(300.0, f.eval(), epsilon);
        
        v[0].setValue(2.0);
        v[1].setValue(3.0);
        assertEquals(3.0, f.eval(), epsilon);
        
        f = new Formula(log, "max(volt, curr, -2)", v);
        assertEquals(3.0, f.eval(), epsilon);

        f = new Formula(log, "2*PI", v);
        assertEquals(2.0*Math.PI, f.eval(), epsilon);

        v[0] = new VariableNode("PI", 10.0);
        f = new Formula(log, "PI", v);
        assertEquals(10.0, f.eval(), epsilon);
        assertTrue(f.hasSubnode(v[0]));
        assertTrue(! f.hasSubnode(v[1]));
    }
    
    @Test
    @SuppressWarnings("nls")
    public void testErrors() throws Exception
    {
        Logger log = CentralLogger.getInstance().getLogger(this);
        Formula f;
        try
        {
            f = new Formula(log, "-");
            fail("Didn't catch parse error");
        }
        catch (Exception ex)
        {
            assertEquals("Unexpected end of formula.", ex.getMessage());
        }

        try
        {
            f = new Formula(log, "max 2");
            fail("Didn't catch parse error");
        }
        catch (Exception ex)
        {
            // Can the scanner be fixed to get 'max' instead of 'max2'
            // for the var. name?
            assertEquals("Unknown variable 'max2'", ex.getMessage());
        }
        
        f = new Formula(log, "1/0");
        f.eval();

        f = new Formula(log, "sqrt(-1)");
        f.eval();
    }
}
