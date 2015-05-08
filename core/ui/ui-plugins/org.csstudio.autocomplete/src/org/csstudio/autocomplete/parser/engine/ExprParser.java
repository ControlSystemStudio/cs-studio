/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Peter Smith
 *******************************************************************************/
package org.csstudio.autocomplete.parser.engine;

import java.io.IOException;
import java.util.ArrayList;

import org.csstudio.autocomplete.parser.engine.expr.Expr;
import org.csstudio.autocomplete.parser.engine.expr.ExprArray;
import org.csstudio.autocomplete.parser.engine.expr.ExprBinaryOperator;
import org.csstudio.autocomplete.parser.engine.expr.ExprConditionalOperator;
import org.csstudio.autocomplete.parser.engine.expr.ExprDouble;
import org.csstudio.autocomplete.parser.engine.expr.ExprException;
import org.csstudio.autocomplete.parser.engine.expr.ExprExpression;
import org.csstudio.autocomplete.parser.engine.expr.ExprFunction;
import org.csstudio.autocomplete.parser.engine.expr.ExprInteger;
import org.csstudio.autocomplete.parser.engine.expr.ExprMissing;
import org.csstudio.autocomplete.parser.engine.expr.ExprPV;
import org.csstudio.autocomplete.parser.engine.expr.ExprString;
import org.csstudio.autocomplete.parser.engine.expr.ExprType;
import org.csstudio.autocomplete.parser.engine.expr.ExprVariable;
import org.csstudio.autocomplete.parser.engine.expr.IBinaryOperator;

public class ExprParser {

    private Expr current;

    public static Expr parse(String text) throws IOException, ExprException {
        ExprParser p = new ExprParser();
        p.parse(new ExprLexer(text));
        return p.get();
    }

    public void parse(ExprLexer lexer) throws IOException, ExprException {
        ExprToken e = null;
        while ((e = lexer.next()) != null) {
            parseToken(lexer, e);
        }
    }

    private void parseToken(ExprLexer lexer, ExprToken token)
            throws ExprException, IOException {
        switch (token.type) {
        case Plus:
        case Minus:
        case Multiply:
        case Divide:
        case Power:
        case LessThan:
        case LessThanOrEqualTo:
        case GreaterThan:
        case GreaterThanOrEqualTo:
        case Equal:
        case NotEqual:
        case Not:
        case CondAnd:
        case CondOr:
        case BitAnd:
        case BitOr:
        case Remainder:
            parseOperator(token, lexer);
            break;
        case QuestionMark:
            parseConditionalOperator(token, lexer);
            break;
        case Decimal:
        case Integer:
        case String:
        case Variable:
        case QuotedVariable:
            parseValue(token);
            break;
        case Function:
            parseFunction(token, lexer);
            break;
        case OpenBracket:
            parseExpression(lexer);
            break;
        case OpenBrace:
            parseArray(lexer);
            break;
        case SimpleEqual: // avoid error when typing
            break;
        default:
            throw new ExprException("Unexpected " + token.type + " found");
        }
    }

    private void parseFunction(ExprToken token, ExprLexer lexer)
            throws ExprException, IOException {
        Expr c = current;
        current = null;
        ExprToken e = null;
        ArrayList<Expr> args = new ArrayList<Expr>();
        boolean complete = false;
        while ((e = lexer.next()) != null) {
            if (e.type.equals(ExprTokenType.Comma)) {
                if ((e = lexer.next()) != null) {
                    current = null;
                    parseToken(lexer, e);
                    args.add(current == null ? new ExprMissing() : current);
                } else {
                    args.add(new ExprMissing());
                }
            } else if (e.type.equals(ExprTokenType.CloseBracket)) { // end
                complete = true;
                current = c;
                break;
            } else { // first arg
                parseToken(lexer, e);
                args.add(current == null ? new ExprMissing() : current);
            }
        }
        ExprFunction f = new ExprFunction(token.val,
                (Expr[]) args.toArray(new Expr[0]));
        f.setComplete(complete);

        setValue(f);
    }

    private void parseExpression(ExprLexer lexer) throws IOException,
            ExprException {
        Expr c = current;
        current = null;
        ExprToken e = null;
        while ((e = lexer.next()) != null) {
            if (e.type.equals(ExprTokenType.CloseBracket)) {
                Expr t = current;
                current = c;
                setValue(new ExprExpression(t));
                break;
            } else {
                parseToken(lexer, e);
            }
        }
    }

    private void parseArray(ExprLexer lexer) throws ExprException, IOException {
        Expr c = current;
        current = null;
        ExprToken e = null;
        int cols = -1;
        int count = 0;
        ArrayList<Expr> args = new ArrayList<Expr>();
        while ((e = lexer.next()) != null) {
            if (e.type.equals(ExprTokenType.Comma)) {
                // if (current == null)
                // throw new ExprException("Arrays cannot contain empty values");
                args.add(current == null ? new ExprMissing() : current);
                current = null;
                count++;
            } else if (e.type.equals(ExprTokenType.SemiColon)) {
                // if (current == null)
                // throw new ExprException("Arrays cannot contain empty values");
                args.add(current == null ? new ExprMissing() : current);
                current = null;
                count++;
                // if (count == 0) {
                // throw new ExprException("Array rows must contain at least one element");
                // }
                // if (cols != -1 && count != cols) {
                // throw new ExprException("Array rows must be equal sizes");
                // }
                cols = count;
                count = 0;
            } else if (e.type.equals(ExprTokenType.CloseBrace)) {
                args.add(current == null ? new ExprMissing() : current);
                current = c;
                int rows = 1;
                if (cols == -1) cols = args.size();
                else rows = args.size() / cols;
                ExprArray a = new ExprArray(rows, cols);
                for (int i = 0; i < args.size(); i++) {
                    a.set(0, i, (Expr) args.get(i));
                }
                setValue(a);
                break;
            } else {
                parseToken(lexer, e);
            }
        }
    }

    private void parseValue(ExprToken e) throws ExprException {
        Expr value = null;
        switch (e.type) {
        case Decimal:
            value = new ExprDouble(e.doubleValue);
            break;
        case Integer:
            value = new ExprInteger(e.integerValue);
            break;
        case String:
            value = new ExprString(e.val);
            break;
        case Variable:
            value = new ExprVariable(e.val);
            break;
        case QuotedVariable:
            value = new ExprPV(e.val);
            break;
        default:
            break;
        }
        setValue(value);
    }

    private void setValue(Expr value) throws ExprException {
        Expr c = current;
        if (c instanceof IBinaryOperator) {
            ((IBinaryOperator) c).setRHS(value);
        } else {
            current = value;
        }
    }

    private void parseOperator(ExprToken e, ExprLexer lexer)
            throws ExprException, IOException {
        // handle negative numbers
        if ((e.type == ExprTokenType.Minus || e.type == ExprTokenType.Plus)
                && current == null) {
            ExprToken nextToken = lexer.next();
            if (nextToken == null)
                return;
            Expr value = null;
            switch (nextToken.type) {
            case Decimal:
                value = new ExprDouble(
                        e.type == ExprTokenType.Minus ? -nextToken.doubleValue : nextToken.doubleValue);
                setValue(value);
                return;
            case Integer:
                value = new ExprInteger(
                        e.type == ExprTokenType.Minus ? -nextToken.integerValue : nextToken.integerValue);
                setValue(value);
                return;
            default:
                break;
            }
            current = new ExprBinaryOperator(ExprType.BinaryOperation, null, null);
            parseToken(lexer, nextToken);
            return;
        }
        current = new ExprBinaryOperator(ExprType.BinaryOperation, current,
                null);
    }

    private void parseConditionalOperator(ExprToken token, ExprLexer lexer)
            throws ExprException, IOException {
        Expr c = current;
        current = null;
        ExprToken e = null;
        ExprConditionalOperator co = new ExprConditionalOperator(c, null, null);
        while ((e = lexer.next()) != null) {
            if (e.type.equals(ExprTokenType.Colon)) {
                if ((e = lexer.next()) != null) {
                    current = null;
                    parseToken(lexer, e);
                    co.setValueIfFalse(current == null ? new ExprMissing() : current);
                } else {
                    co.setValueIfFalse(new ExprMissing());
                }
                break;
            } else {
                parseToken(lexer, e);
                co.setValueIfTrue(current == null ? new ExprMissing() : current);
            }
        }
        setValue(co);
    }

    public Expr get() {
        return current;
    }
}
