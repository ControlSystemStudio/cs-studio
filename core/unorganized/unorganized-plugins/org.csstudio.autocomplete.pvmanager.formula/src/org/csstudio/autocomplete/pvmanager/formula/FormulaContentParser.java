/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.formula;

import java.io.IOException;
import java.util.logging.Level;

import org.csstudio.autocomplete.AutoCompletePlugin;
import org.csstudio.autocomplete.AutoCompleteType;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.parser.FunctionDescriptor;
import org.csstudio.autocomplete.parser.IContentParser;
import org.csstudio.autocomplete.parser.engine.ExprParser;
import org.csstudio.autocomplete.parser.engine.expr.Expr;
import org.csstudio.autocomplete.parser.engine.expr.ExprBinaryOperator;
import org.csstudio.autocomplete.parser.engine.expr.ExprConditionalOperator;
import org.csstudio.autocomplete.parser.engine.expr.ExprException;
import org.csstudio.autocomplete.parser.engine.expr.ExprFunction;
import org.csstudio.autocomplete.parser.engine.expr.ExprPV;
import org.csstudio.autocomplete.parser.engine.expr.ExprVariable;

/**
 * PV formula content parser.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class FormulaContentParser implements IContentParser {

	private FunctionDescriptor currentToken;
	private String contentToParse;

	@Override
	public boolean accept(final ContentDescriptor desc) {
		String content = desc.getValue();
		AutoCompleteType type = desc.getAutoCompleteType();
		if (type.equals(AutoCompleteType.Formula) && content.startsWith("="))
			return true;
		return false;
	}

	@Override
	public ContentDescriptor parse(final ContentDescriptor desc) {
		currentToken = null;
		// remove first '='
		contentToParse = new String(desc.getValue()).substring(1);
		try {
			Expr e = ExprParser.parse(contentToParse);
			handleExpr(e);
		} catch (IOException | ExprException e) {
			AutoCompletePlugin.getLogger().log(Level.WARNING, e.getMessage());
		}
		return currentToken;
	}

	private void handleExpr(Expr e) {
		if (e == null)
			return;
		switch (e.type) {
		case Variable: // no variables, only functions
			handleVariable((ExprVariable) e);
			break;

		case PV: // call PV parsers
			handlePV((ExprPV) e);
			break;

		case Function: // complete last argument
			handleFunction((ExprFunction) e);
			break;

		case BinaryOperation: // complete right argument
			handleBinaryOperation((ExprBinaryOperator) e);
			break;

		case ConditionalOperation: // complete values
			handleConditionalOperation((ExprConditionalOperator) e);
			break;
			
		default:
			break;
		}
	}

	private void handleFunction(ExprFunction f) {
		if (f.isComplete())
			return;
		currentToken = new FunctionDescriptor();
		currentToken.setValue(f.toString());
		currentToken.setFunctionName(f.getName());
		currentToken.setContentType(ContentType.FormulaFunction);
		currentToken.setOpenBracket(true);
		if (f.size() == 0) {
			currentToken.setCurrentArgIndex(0);
			return;
		}
		currentToken.setCurrentArgIndex(f.size() - 1);
		Expr lastArg = f.getArg(f.size() - 1);
		if (lastArg == null)
			return;
		handleExpr(lastArg);
	}

	private void handleBinaryOperation(ExprBinaryOperator bo) {
		Expr rhs = bo.getRHS();
		if (rhs == null)
			return;
		handleExpr(rhs);
	}

	private void handleConditionalOperation(ExprConditionalOperator co) {
		Expr value = co.getValueIfFalse();
		if (value == null)
			value = co.getValueIfTrue();
		if (value == null)
			return;
		handleExpr(value);
	}

	private void handlePV(ExprPV pv) {
		String name = pv.getName();
		if (!name.endsWith("'")) {
			String value = name.substring(1);
			int startIndex = contentToParse.length() - value.length() + 1;
			currentToken = new FunctionDescriptor();
			currentToken.setValue(value);
			currentToken.setStartIndex(startIndex);
			currentToken.setContentType(ContentType.PV);
			currentToken.setReplay(true);
		}
	}

	private void handleVariable(ExprVariable v) {
		// No variables, only functions
		String value = v.getName();
		int startIndex = contentToParse.length() - value.length() + 1;
		currentToken = new FunctionDescriptor();
		currentToken.setValue(value);
		currentToken.setStartIndex(startIndex);
		currentToken.setFunctionName(value);
		currentToken.setContentType(ContentType.FormulaFunction);
		currentToken.setCurrentArgIndex(-1);
		currentToken.setOpenBracket(false);
	}

}
