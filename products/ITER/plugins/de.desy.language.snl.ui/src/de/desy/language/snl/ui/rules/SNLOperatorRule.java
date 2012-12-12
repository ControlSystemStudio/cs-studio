package de.desy.language.snl.ui.rules;

/*******************************************************************************
 * Copyright (c) 2005 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - initial API and implementation
 *******************************************************************************/

import org.eclipse.jface.text.rules.IToken;

import de.desy.language.editor.ui.editor.highlighting.predefinedRules.SingleCharRule;


/**
 * Rule to recognize operators.
 * 
 * @author P.Tomaszewski
 */
public class SNLOperatorRule extends SingleCharRule {
	/**
	 * Creates new rule.
	 * 
	 * @param token
	 *            Style token.
	 */
	public SNLOperatorRule(final IToken token) {
		super(token);
	}

	/**
	 * @see SingleCharRule#isRuleChar(int)
	 */
	@Override
	public boolean isRuleChar(final int ch) {
		return ((ch == ';') || (ch == '.') || (ch == ':') || (ch == '=')
				|| (ch == '-') || (ch == '+') || (ch == '\\') || (ch == '*')
				|| (ch == '!') || (ch == '%') || (ch == '^') || (ch == '&')
				|| (ch == '~') || (ch == '>') || (ch == '<'))
				|| (ch == '|');
	}
}
