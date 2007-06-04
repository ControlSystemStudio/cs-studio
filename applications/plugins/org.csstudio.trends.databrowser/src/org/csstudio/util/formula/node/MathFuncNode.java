package org.csstudio.util.formula.node;

import java.lang.reflect.Method;

import org.csstudio.util.formula.Node;


public class MathFuncNode implements Node
{
	final private String function;
    final private Node n;
	final private Method method;
    
	/** Construct node for math function.
	 * 
	 *  @param function One of the java.lang.Math.* method names
	 *  @param n Argument node
	 *  @throws Exception On error
	 */
    public MathFuncNode(final String function, Node n) throws Exception
    {
    	this.function = function;
        this.n = n;
        Method methods[] = Math.class.getDeclaredMethods();
        for (Method m : methods)
        {
        	System.out.println(m.getName());
		}
    	method = Math.class.getDeclaredMethod(function, double.class);
    }
    
    public double eval()
    {
        double a = n.eval();
        try
        {
			Object result = method.invoke(null, new Object[] { new Double(a) } );
			if (result instanceof Double)
				return ((Double) result).doubleValue();
		}
        catch (Exception e)
        {
			e.printStackTrace();
		}
        return 0.0;
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return function + "(" + n + ")";
    }
}
