package org.csstudio.util.formula;

import java.util.Vector;

import org.csstudio.util.formula.node.AbsNode;
import org.csstudio.util.formula.node.AcosNode;
import org.csstudio.util.formula.node.AddNode;
import org.csstudio.util.formula.node.AndNode;
import org.csstudio.util.formula.node.AsinNode;
import org.csstudio.util.formula.node.Atan2Node;
import org.csstudio.util.formula.node.AtanNode;
import org.csstudio.util.formula.node.ConstantNode;
import org.csstudio.util.formula.node.CosNode;
import org.csstudio.util.formula.node.DivNode;
import org.csstudio.util.formula.node.EqualNode;
import org.csstudio.util.formula.node.ExpNode;
import org.csstudio.util.formula.node.GreaterEqualNode;
import org.csstudio.util.formula.node.GreaterThanNode;
import org.csstudio.util.formula.node.IfNode;
import org.csstudio.util.formula.node.LessEqualNode;
import org.csstudio.util.formula.node.LessThanNode;
import org.csstudio.util.formula.node.MathFuncNode;
import org.csstudio.util.formula.node.MaxNode;
import org.csstudio.util.formula.node.MinNode;
import org.csstudio.util.formula.node.MulNode;
import org.csstudio.util.formula.node.NotEqualNode;
import org.csstudio.util.formula.node.NotNode;
import org.csstudio.util.formula.node.OrNode;
import org.csstudio.util.formula.node.PwrNode;
import org.csstudio.util.formula.node.SqrtNode;
import org.csstudio.util.formula.node.SubNode;
import org.csstudio.util.formula.node.TanNode;

/** A formula interpreter.
 *  <p>
 *  While I found some on the internet,
 *  I didn't see an open one that included the if-then-else
 *  operation nor one that I understood within 15 minutes
 *  so that I could add that operation.
 *  <p>
 *  Supported, in descending order of precedence:
 *  <ul>
 *  <li>Numeric constant 3.14, -47; named variables
 *  <li> (sub-formula in braces), sqrt(x), ln(x), exp(x),
 *       min(a, b, ...), max(a, b, ...).
 *  <li>*, /, ^
 *  <li>+, -
 *  <li>comparisons <, >, >=, <=, ==, !=
 *  <li>boolean logic !, &, |,  .. ? .. : ..
 *  </ul>
 *  <p>
 *  The formula string is parsed into a tree, so that subsequent
 *  evaluations, possibly with modified values for input variables,
 *  are reasonably fast.
 *  
 *  @author Kay Kasemir
 *  @author 
 */
@SuppressWarnings("nls")
public class Formula implements Node
{
    private Node tree;
    private VariableNode variables[];
    
    /** Create formula from string.
     *  @param formula The formula to parse
     *  @throws Exception on parse error
     */
    public Formula(String formula)  throws Exception
    {
        this.variables = null;
        parse(formula);
    }

    /** Create formula from string with variables.
     *  @param formula The formula to parse
     *  @param variables Array of variables
     *  @throws Exception on parse error
     */
    public Formula(String formula, VariableNode[] variables)  throws Exception
    {
        this.variables = variables;
        parse(formula);
    }
    
    /** @return Array of variables or <code>null</code> if none are used. */
    public VariableNode[] getVariables()
    {
        return variables;
    }

    /** {@inheritDoc} */
    public double eval()
    {
        return tree.eval();
    }    
    
    /** Parse -0.1234 or variable or sub-expression in braces. */
    private Node parseConstant(Scanner s) throws Exception
    {
        final String digits = "0123456789.";
        StringBuffer buf = new StringBuffer();     
        boolean negative = false;
        
        if (s.isDone())
            throw new Exception("Unexpected end of formula.");
        // Possible leading '-'
        if (s.get() == '-')
        {
            negative = true;
            s.next();
        }
        if (s.isDone())
            throw new Exception("Unexpected end of formula.");
        Node result = null;
        // Sub-formula in '( ... )' ?
        if (s.get() == '(')
            result = parseBracedExpression(s);
        else
        {   // Digits?
            if (digits.indexOf(s.get()) >= 0)
            {
                do
                {
                    buf.append(s.get());
                    s.next();
                }
                while (!s.isDone()  &&  digits.indexOf(s.get()) >= 0);
                // Details of number format left to parseDouble()
                double value = Double.parseDouble(buf.toString());        
                return new ConstantNode(negative ? -value : value);
            }
            // Else: assume variable or function.
            while (!s.isDone()  &&  Character.isLetterOrDigit(s.get()))
            {
                buf.append(s.get());
                s.next();
            }
            String name = buf.toString();
            if (s.get() == '(')
                result = findFunction(s, name);
            else
                result = findVariable(name);
        }
        if (negative)
            return new SubNode(new ConstantNode(0), result);
        return result;
    }

    /** @param name Function name
     *  @return Returns Node that evaluates the function.
     *  @throws Exception
     */
    private Node findFunction(Scanner s, String name) throws Exception
    {
        // TODO Add more functions.
        // TODO add rnd, cos, tan, asin, acos, atan, atan2
        // TODO add int, floor, ceil
        // TODO add a MathNode that uses introspection to pull
        //      all of java.lang.math.Math.* in?
        Node [] args = parseArgExpressions(s);
        if (name.equalsIgnoreCase("sqrt"))
        {
            if (args.length != 1)
                throw new Exception("Expected 1 arg, got " + args.length);
            return new SqrtNode(args[0]);
        }
        else if (name.equalsIgnoreCase("abs"))
        {
            if (args.length != 1)
                throw new Exception("Expected 1 arg, got " + args.length);
            return new AbsNode(args[0]);
        }
        else if (name.equalsIgnoreCase("ln"))
        {
            if (args.length != 1)
                throw new Exception("Expected 1 arg, got " + args.length);
            return new MathFuncNode("log", args[0]);
        }
        else if (name.equalsIgnoreCase("exp"))
        {
            if (args.length != 1)
                throw new Exception("Expected 1 arg, got " + args.length);
            return new ExpNode(args[0]);
        }
        else if (name.equalsIgnoreCase("min"))
        {
            if (args.length < 2)
                throw new Exception("Expected >=2 arg, got " + args.length);
            return new MinNode(args);
        }
        else if (name.equalsIgnoreCase("max"))
        {
            if (args.length < 2)
                throw new Exception("Expected >=2 arg, got " + args.length);
            return new MaxNode(args);
        }
        else if (name.equalsIgnoreCase("sin") 
        	  || name.equalsIgnoreCase("cos"))
        {
            if (args.length != 1)
                throw new Exception("Expected 1 arg, got " + args.length);
            return new MathFuncNode(name, args[0]);
        }
        else if (name.equalsIgnoreCase("tan"))
        {
            if (args.length != 1)
                throw new Exception("Expected 1 arg, got " + args.length);
            return new TanNode(args[0]);
        }
        else if (name.equalsIgnoreCase("asin"))
        {
            if (args.length != 1)
                throw new Exception("Expected 1 arg, got " + args.length);
            return new AsinNode(args[0]);
        }
        else if (name.equalsIgnoreCase("acos"))
        {
            if (args.length != 1)
                throw new Exception("Expected 1 arg, got " + args.length);
            return new AcosNode(args[0]);
        }
        else if (name.equalsIgnoreCase("atan"))
        {
            if (args.length != 1)
                throw new Exception("Expected 1 arg, got " + args.length);
            return new AtanNode(args[0]);
        }
        else if (name.equalsIgnoreCase("atan2"))
        {
            if (args.length != 2)
                throw new Exception("Expected 2 args, got " + args.length);
            return new Atan2Node(args[0], args[1]);
        }
        else
            throw new Exception("Unknown function '" + name +"'");
    }

    /** @return node for sub-expression arguments in (a1, a2, .. ) braces.
     *  @throws Exception when no closing ')' is found.
     */
    private Node[] parseArgExpressions(Scanner s) throws Exception
    {
        Vector<Node> args = new Vector<Node>();
        if (s.get() != '(')
            throw new Exception("Expected '(', found '" + s.get() + "'");
        s.next();
        while (!s.isDone())
        {
            Node arg = parseBool(s);
            args.add(arg);
            // Expect ',' and another arg or ')'
            if (s.get() != ',')
                break;
            s.next();
        }
        if (s.get() != ')')
            throw new Exception("Expected closing ')'");
        s.next(); // ')'
        // Convert to array
        Node [] arg_nodes = new Node[args.size()];
        args.toArray(arg_nodes);
        return arg_nodes;
    }

    /** @param name Variable name.
     *  @return Returns VariableNode
     *  @throws Exception when not found.
     */
    private Node findVariable(String name) throws Exception
    {
        if (variables != null)
        {   // Find the variable.
            for (VariableNode var : variables)
                if (var.getName().equals(name))
                    return var;
        }
        throw new Exception("Unknown variable '" + name + "'");
    }

    /** @return node for sub-expression in ( .. ) braces.
     *  @throws Exception when no closing ')' is found.
     */
    private Node parseBracedExpression(Scanner s) throws Exception
    {
        Node result;
        if (s.get() != '(')
            throw new Exception("Expected '(', found '" + s.get() + "'");
        s.next();
        result = parseBool(s);
        if (s.get() != ')')
            throw new Exception("Expected closing ')'");
        s.next();
        return result;
    }

    /** Parse multiplication, division, ... */
    private Node parseMulDiv(Scanner s) throws Exception
    {
        // Expect a ...
        Node n = parseConstant(s);
        // possibly followed by  * b / c ....
        while (! s.isDone())
        {
            if (s.get() == '^')
            {
                s.next();
                n = new PwrNode(n, parseConstant(s));
            }
            else if (s.get() == '*')
            {
                s.next();
                n = new MulNode(n, parseConstant(s));
            }
            else if (s.get() == '/')
            {
                s.next();
                n = new DivNode(n, parseConstant(s));
            }
            else break;
        }
        return n;
    }
  
    /** Parse addition, subtraction, ... */
    private Node parseAddSub(Scanner s) throws Exception
    {
        // Expect a ...
        Node n = parseMulDiv(s);
        // possibly followed by  + b - c ....
        while (! s.isDone())
        {
            if (s.get() == '+')
            {
                s.next();
                n = new AddNode(n, parseMulDiv(s));
            }
            else if (s.get() == '-')
            {
                s.next();
                n = new SubNode(n, parseMulDiv(s));
            }
            else break;
        }
        return n;
    }

    /** Comparisons */
    private Node parseCompare(Scanner s) throws Exception
    {
        // Expect a ...
        Node n = parseAddSub(s);
        // possibly followed by  > b >= c ....
        while (! s.isDone())
        {
            if (s.get() == '!')
            {
                s.next();
                if (s.get() == '=')
                {
                    s.next();
                    n = new NotEqualNode(n, parseAddSub(s));
                }
                else
                    throw new Exception("Expected '!=', found '!"
                            + s.get() + "'");
            }
            else if (s.get() == '=')
            {
                s.next();
                if (s.get() == '=')
                {
                    s.next();
                    n = new EqualNode(n, parseAddSub(s));
                }
                else
                    throw new Exception("Expected '==', found '="
                            + s.get() + "'");
            }
            else if (s.get() == '>')
            {
                s.next();
                if (s.get() == '=')
                {
                    s.next();
                    n = new GreaterEqualNode(n, parseAddSub(s));
                }
                else
                    n = new GreaterThanNode(n, parseAddSub(s));
            }
            else if (s.get() == '<')
            {
                s.next();
                if (s.get() == '=')
                {
                    s.next();
                    n = new LessEqualNode(n, parseAddSub(s));
                }
                else
                    n = new LessThanNode(n, parseAddSub(s));
            }
            else break;
        }
        return n;
    }

    /** Boolean &, | */
    private Node parseBool(Scanner s) throws Exception
    {
        if (s.get() == '!')
        {
            s.next();
            return new NotNode(parseBool(s));
        }
        // Expect a ...
        Node n = parseCompare(s);
        // possibly followed by  & b | c ....
        while (! s.isDone())
        {
            if (s.get() == '&')
            {
                s.next();
                n = new AndNode(n, parseCompare(s));
            }
            else if (s.get() == '|')
            {
                s.next();
                n = new OrNode(n, parseCompare(s));
            }
            else if (s.get() == '?')
            {
                s.next();
                Node yes = parseCompare(s);
                if (s.get() != ':')
                    throw new Exception("Expected ':' to follow the (cond) ? ...");
                s.next();
                n = new IfNode(n, yes, parseBool(s));
            }
            else break;
        }
        return n;
    }

    /** Parse formula. */
    private void parse(String formula) throws Exception
    {
        Scanner scanner = new Scanner(formula);
        tree = parseBool(scanner);
        if (! scanner.isDone())
            throw new Exception("Parse error at '" + scanner.rest() + "'");
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return (tree != null) ? tree.toString() : "null";
    }
}
