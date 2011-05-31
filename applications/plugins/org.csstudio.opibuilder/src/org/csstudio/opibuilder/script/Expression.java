/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.script;

/**The expression data for a rule.
 * @author Xihui Chen
 *
 */
public class Expression {
	
	private String booleanExpression;
	private Object value;
	
	public Expression(String booleanExpression, Object value) {
		this.booleanExpression = booleanExpression;
		this.value = value;
	}

	/**
	 * @return the booleanExpression
	 */
	public final String getBooleanExpression() {
		return booleanExpression;
	}

	/**
	 * @param booleanExpression the booleanExpression to set
	 */
	public final void setBooleanExpression(String booleanExpression) {
		this.booleanExpression = booleanExpression;
	}

	/**
	 * @return the value
	 */
	public final Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public final void setValue(Object value) {
		this.value = value;
	}
	
	public Expression getCopy(){
		return new Expression(booleanExpression, value);
	}
	
	
}
