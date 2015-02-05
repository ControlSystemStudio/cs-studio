/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.parser;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.autocomplete.IAutoCompleteProvider;

/**
 * Descriptor used in {@link IContentParser} and {@link IAutoCompleteProvider}
 * to describe a content matching a function.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class FunctionDescriptor extends ContentDescriptor {

	private String functionName = "";
	/**
	 * The ordered list of the arguments.
	 */
	private List<Object> args;
	/**
	 * <code>true</code> if close bracket recorded.
	 */
	private boolean complete = false;
	private boolean hasOpenBracket = false;
	private int currentArgIndex = -1;

	public FunctionDescriptor() {
		super();
		args = new ArrayList<Object>();
	}

	public void addArgument(Object arg) {
		args.add(arg);
	}

	public List<Object> getArgs() {
		return args;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public boolean hasOpenBracket() {
		return hasOpenBracket;
	}

	public void setOpenBracket(boolean hasOpenBracket) {
		this.hasOpenBracket = hasOpenBracket;
	}

	public int getCurrentArgIndex() {
		return currentArgIndex;
	}

	public void setCurrentArgIndex(int currentArgIndex) {
		this.currentArgIndex = currentArgIndex;
	}

	@Override
	public String toString() {
		return "FunctionDescriptor [functionName=" + functionName + ", args="
				+ args + ", complete=" + complete + ", hasOpenBracket="
				+ hasOpenBracket + ", currentArgIndex=" + currentArgIndex
				+ ", toString()=" + super.toString() + "]";
	}

}
