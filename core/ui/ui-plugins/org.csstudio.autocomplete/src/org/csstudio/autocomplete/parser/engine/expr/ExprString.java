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

import org.csstudio.autocomplete.parser.engine.ExprLexer;

public class ExprString extends Expr {

	public static final String EMPTY = "";

	public final String str;

	public ExprString(String str) {
		super(ExprType.String);
		this.str = str;
	}

	public String toString() {
		return ExprLexer.escapeString(str);
	}

	public int hashCode() {
		return str.hashCode();
	}

	public boolean equals(Object obj) {
		return obj instanceof ExprString && str.equals(((ExprString) obj).str);
	}
}
