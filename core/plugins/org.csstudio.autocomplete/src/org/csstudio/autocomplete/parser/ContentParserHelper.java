/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.parser;

import java.io.IOException;
import java.util.logging.Level;

import org.csstudio.autocomplete.AutoCompletePlugin;
import org.csstudio.autocomplete.parser.engine.ExprParser;
import org.csstudio.autocomplete.parser.engine.expr.Expr;
import org.csstudio.autocomplete.parser.engine.expr.ExprBoolean;
import org.csstudio.autocomplete.parser.engine.expr.ExprDouble;
import org.csstudio.autocomplete.parser.engine.expr.ExprException;
import org.csstudio.autocomplete.parser.engine.expr.ExprFunction;
import org.csstudio.autocomplete.parser.engine.expr.ExprInteger;
import org.csstudio.autocomplete.parser.engine.expr.ExprString;
import org.csstudio.autocomplete.parser.engine.expr.ExprType;
import org.csstudio.autocomplete.parser.engine.expr.ExprVariable;

/**
 * Helper for content parsing.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class ContentParserHelper {

	/**
	 * @return {@link FunctionDescriptor} filled from the content.
	 */
	public static FunctionDescriptor parseStandardFunction(String content) {
		FunctionDescriptor token = new FunctionDescriptor();
		if (content == null || content.isEmpty())
			return token;
		ExprFunction function = null;
		try {
			Expr e = ExprParser.parse(content);
			if (e == null)
				return token;
			else if (e.type == ExprType.Function)
				function = (ExprFunction) e;
			else if (e.type == ExprType.Variable) {
				function = new ExprFunction(((ExprVariable) e).getName(), null);
			}
		} catch (IOException | ExprException e) {
			AutoCompletePlugin.getLogger().log(Level.SEVERE,
					"Failed to parse function \"" + content + "\": " + e.getMessage());
		}
		if (function == null)
			return token;
		token.setValue(content);
		token.setFunctionName(function.getName());
		if (function.getArgs() != null) {
			token.setOpenBracket(true);
			token.setCurrentArgIndex(function.size() > 0 ? function.size() - 1 : 0);
			for (Expr e : function.getArgs()) {
				switch (e.type) {
				case Boolean:
					token.addArgument(((ExprBoolean) e).value);
					break;
				case Double:
					token.addArgument(((ExprDouble) e).value);
					break;
				case Integer:
					token.addArgument(((ExprInteger) e).value);
					break;
				case String:
					token.addArgument(((ExprString) e).str);
					break;
				default: // ignore other types
					token.addArgument(new Object());
					break;
				}
			}
		}
		token.setComplete(function.isComplete());
		return token;
	}
}
