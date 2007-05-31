package org.csstudio.util.formula.node;

import org.csstudio.util.formula.Node;

public class MaxNode implements Node
{
    private Node args[];
    
    public MaxNode(Node args[])
    {
        this.args = args;
    }
    
    public double eval()
    {
        double result = 0.0;
        for (int i = 0; i < args.length; i++)
        {
            double v = args[i].eval();
            if (i==0  ||  v > result)
                result = v;
        }
        return result;
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        StringBuffer b = new StringBuffer("max(");
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
