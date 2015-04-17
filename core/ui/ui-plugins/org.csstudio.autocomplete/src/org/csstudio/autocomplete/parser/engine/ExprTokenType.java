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

public enum ExprTokenType
{
	Decimal,
	Integer,
	String,
	Variable,
	QuotedVariable,
	Function,
	OpenBracket,
	CloseBracket,
	OpenBrace,
	CloseBrace,
	SemiColon,
	Comma,
	Plus,
	Minus,
	Multiply,
	Divide,
	Power,
	LessThan,
	LessThanOrEqualTo,
	GreaterThan,
	GreaterThanOrEqualTo,
	Equal,
	NotEqual,
	Not,
	CondAnd,
	CondOr,
	BitAnd,
	BitOr,
	Remainder,
	QuestionMark,
	Colon,
	SimpleEqual
}
