package org.csstudio.util.formula.node;

import java.lang.reflect.Method;

import org.csstudio.util.formula.Node;

/** Node for evaluating any of the java.lang.Math.* functions
 *  @author Xiaosong Geng
 *  @author Kay Kasemir
 */
public class MathFuncNode implements Node
{
	final private String function;
    final private Node args[];
	final private Method method;
    
	/** Construct node for math function.
	 * 
	 *  @param function One of the java.lang.Math.* method names
	 *  @param n Argument node
	 *  @throws Exception On error
	 */
    public MathFuncNode(final String function, Node args[]) throws Exception
    {
    	this.function = function;
        this.args = args;
        Class argcls[] = new Class[args.length];
        for (int i = 0; i < args.length; i++)
            argcls[i] = double.class;
        method = Math.class.getDeclaredMethod(function, argcls);
    }
    
    public double eval()
    {
        final Object arglist[] = new Object[args.length];
        for (int i = 0; i < args.length; i++)
        {
            arglist[i] = new Double(args[i].eval());
        }
        
        try
        {
        	//System.out.println("%s, %f, %f\n", method.toString(), a, b);
        	Object result = method.invoke(null, arglist );
			if (result instanceof Double)
				return ((Double) result).doubleValue();
		}
        catch (Exception e)
        {
			e.printStackTrace();
		}
        return 0.0;
    }
    
    /** {@inheritDoc} */
    public boolean hasSubnode(Node node)
    {
        for (Node arg : args)
            if (arg == node  ||  arg.hasSubnode(node))
                return true;
        return false;
    }
    
    @Override
	@SuppressWarnings("nls")
	public String toString()
    {
        final StringBuffer b = new StringBuffer(function);
        b.append("(");
        for (int i = 0; i < args.length; i++)
        {
            if (i>0)
                b.append(", ");
            b.append(args[i].toString());
        }
        b.append(")");
        return b.toString();
    }
}
