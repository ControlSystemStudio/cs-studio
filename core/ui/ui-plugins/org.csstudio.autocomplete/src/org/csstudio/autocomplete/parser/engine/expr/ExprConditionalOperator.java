/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Peter Smith
 *******************************************************************************/
package org.csstudio.autocomplete.parser.engine.expr;

public class ExprConditionalOperator extends Expr {

    protected Expr condition;
    protected Expr valueIfTrue;
    protected Expr valueIfFalse;

    public ExprConditionalOperator(Expr condition, Expr valueIfTrue,
            Expr valueIfFalse) {
        super(ExprType.ConditionalOperation);
        this.condition = condition;
        this.valueIfTrue = valueIfTrue;
        this.valueIfFalse = valueIfFalse;
    }

    public Expr getCondition() {
        return condition;
    }

    public void setCondition(Expr condition) {
        this.condition = condition;
    }

    public Expr getValueIfTrue() {
        return valueIfTrue;
    }

    public void setValueIfTrue(Expr valueIfTrue) {
        this.valueIfTrue = valueIfTrue;
    }

    public Expr getValueIfFalse() {
        return valueIfFalse;
    }

    public void setValueIfFalse(Expr valueIfFalse) {
        this.valueIfFalse = valueIfFalse;
    }

}
