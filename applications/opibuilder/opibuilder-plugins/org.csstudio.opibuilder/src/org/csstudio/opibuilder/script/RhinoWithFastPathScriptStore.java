/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.scriptUtil.PVUtil;
import org.csstudio.simplepv.IPV;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * This is the implementation of {@link AbstractScriptStore} for Rhino script engine, with
 * special-case handlers for commonly used rule expressions. If possible, this script engine
 * implementation will avoid calling into javascript for rules where all expressions are on
 * the "fast path". This significantly improves CPU & memory performance for these rules.
 *
 */
public class RhinoWithFastPathScriptStore extends AbstractScriptStore{

    private Context scriptContext;

    private Scriptable scriptScope;

    private Script script = null;
    
    // Need to default to true to stop superclass constructor initialising rhino
    private final boolean usesFastPath;
    private final RuleScriptData ruleScriptData;
    private String scriptString;
    private final Object initialRulePropertyValue;
    
    public interface FastPathHandler {
    	public boolean handle(IPV[] pvList);
    }
    
    public static final FastPathHandler PV0_EQ_0 = pvArr -> PVUtil.getDouble(pvArr[0]) == 0.0;
    public static final FastPathHandler PV0_EQ_1 = pvArr -> PVUtil.getDouble(pvArr[0]) == 1.0;
    public static final FastPathHandler PV0_NEQ_0 = pvArr -> PVUtil.getDouble(pvArr[0]) != 0.0;
    public static final FastPathHandler PVINT0_EQ_0 = pvArr -> PVUtil.getLong(pvArr[0]) == 0;
    public static final FastPathHandler PVINT0_EQ_1 = pvArr -> PVUtil.getLong(pvArr[0]) == 1; 
    public static final FastPathHandler PVINT0_NEQ_0 = pvArr -> PVUtil.getLong(pvArr[0]) != 0;
    public static final FastPathHandler ALWAYS_TRUE = pvArr -> true;
    public static final FastPathHandler ALWAYS_FALSE = pvArr -> false;

    private static final Map<String, FastPathHandler> FAST_PATH_EXPRESSIONS = new HashMap<>();
    
    static {
    	FAST_PATH_EXPRESSIONS.put("pv0==0", PV0_EQ_0);
    	FAST_PATH_EXPRESSIONS.put("pv0 == 0", PV0_EQ_0);
    	
    	FAST_PATH_EXPRESSIONS.put("pv0==1", PV0_EQ_1);
    	FAST_PATH_EXPRESSIONS.put("pv0 == 1", PV0_EQ_1);
    	
    	FAST_PATH_EXPRESSIONS.put("pvInt0==0", PVINT0_EQ_0);
    	FAST_PATH_EXPRESSIONS.put("pvInt0 == 0", PVINT0_EQ_0);
    	
    	FAST_PATH_EXPRESSIONS.put("pvInt0==1", PVINT0_EQ_1);
    	FAST_PATH_EXPRESSIONS.put("pvInt0 == 1", PVINT0_EQ_1);
    	
    	FAST_PATH_EXPRESSIONS.put("pv0!=0", PV0_NEQ_0);
    	FAST_PATH_EXPRESSIONS.put("pv0 != 0", PV0_NEQ_0);
    	FAST_PATH_EXPRESSIONS.put("!(pv0==0)", PV0_NEQ_0);
    	FAST_PATH_EXPRESSIONS.put("!(pv0 == 0)", PV0_NEQ_0);
    	FAST_PATH_EXPRESSIONS.put("pv0", PV0_NEQ_0);
    	
    	FAST_PATH_EXPRESSIONS.put("pvInt0!=0", PVINT0_NEQ_0);
    	FAST_PATH_EXPRESSIONS.put("pvInt0 != 0", PVINT0_NEQ_0);
    	FAST_PATH_EXPRESSIONS.put("!(pvInt0==0)", PVINT0_NEQ_0);
    	FAST_PATH_EXPRESSIONS.put("!(pvInt0 == 0)", PVINT0_NEQ_0);
    	FAST_PATH_EXPRESSIONS.put("pvInt0", PVINT0_NEQ_0);
    	
    	FAST_PATH_EXPRESSIONS.put("true", ALWAYS_TRUE);
    	FAST_PATH_EXPRESSIONS.put("1", ALWAYS_TRUE);
    	FAST_PATH_EXPRESSIONS.put("1==1", ALWAYS_TRUE);
    	FAST_PATH_EXPRESSIONS.put("1 == 1", ALWAYS_TRUE);
    	FAST_PATH_EXPRESSIONS.put("1===1", ALWAYS_TRUE);
    	FAST_PATH_EXPRESSIONS.put("1 === 1", ALWAYS_TRUE);
    	
    	FAST_PATH_EXPRESSIONS.put("false", ALWAYS_FALSE);
    	FAST_PATH_EXPRESSIONS.put("0", ALWAYS_FALSE);
    }
    
    public static void addFastPathHandler(String expression, FastPathHandler handler) {
    	FAST_PATH_EXPRESSIONS.put(expression, handler);
    }

    public RhinoWithFastPathScriptStore(final ScriptData scriptData, final AbstractBaseEditPart editpart,
            final IPV[] pvArray) throws Exception {
        super(scriptData, editpart, pvArray);
        usesFastPath = canUseFastPath(scriptData);
        if (usesFastPath) {
        	ruleScriptData = (RuleScriptData) scriptData;
        	initialRulePropertyValue = editpart.getWidgetModel().getProperty(ruleScriptData.getRuleData().getPropId()).getPropertyValue();
        } else {
        	ruleScriptData = null;
        	initialRulePropertyValue = null;
        }
    }

    @Override
    protected void initScriptEngine() throws Exception {
    	// Don't actually do init here as it is called from superclass constructor before
    	// we're able to figure out if we need to use fast or slow path.
    	// Instead we lazily init only when needed.
    }
    
    private void initIfNeeded() throws Exception {
    	if (scriptScope != null) {
    		return;
    	}
    	
    	scriptContext = ScriptStoreFactory.getRhinoContext();
        scriptScope = new ImporterTopLevel(scriptContext);
        Object widgetController = Context.javaToJS(getEditPart(), scriptScope);
        Object pvArrayObject = Context.javaToJS(getPvArray(), scriptScope);
        Object displayObject = Context.javaToJS(getDisplayEditPart(), scriptScope);

        ScriptableObject.putProperty(scriptScope, ScriptService.WIDGET, widgetController);
        ScriptableObject.putProperty(scriptScope, ScriptService.PVS, pvArrayObject);
        ScriptableObject.putProperty(scriptScope, ScriptService.DISPLAY, displayObject);
        ScriptableObject.putProperty(scriptScope,
                ScriptService.WIDGET_CONTROLLER_DEPRECIATED, widgetController);
        ScriptableObject.putProperty(scriptScope,
                ScriptService.PV_ARRAY_DEPRECIATED, pvArrayObject);
    	
    }

    /**
     * Store the provided script, to be compiled lazily later if needed.
     * 
     * Note: this is called from superclass constructor so usesFastPath not available yet.
     */
    @Override
    protected void compileString(String string) throws Exception{
        scriptString = string;
    }

    /**
     * Store the provided script, to be compiled lazily later if needed.
     * 
     * Note: this is called from superclass constructor so usesFastPath not available yet.
     */
    @Override
    protected void compileInputStream(File file, InputStream s) throws Exception {
    	var isr = new InputStreamReader(s);
        var br = new BufferedReader(isr);
        scriptString = br.lines().collect(Collectors.joining("\n"));
        isr.close();
        br.close();
    	s.close();
    }

    /**
     * Execute the script, using the fast path if possible or falling back to JS if not.
     * 
     * This will lazily compile the script the first time it is executed, if it cannot use
     * the fast path.
     */
    @Override
    protected void execScript(final IPV triggerPV) throws Exception {
    	if (usesFastPath) {
    	    execFast();
    	} else {
    		initIfNeeded();
 	        if (script == null) {
 		        if (scriptString == null) {
 		        	throw new IllegalStateException("script string was never set before execScript()");
 		        }
	        	script = scriptContext.compileString(scriptString, "script", 1, null);
	        }
	        ScriptableObject.putProperty(scriptScope,
	                ScriptService.TRIGGER_PV, Context.javaToJS(triggerPV, scriptScope));
	        script.exec(scriptContext, scriptScope);
    	}
    }
    
    private boolean canUseFastPath(final ScriptData scriptData) {
    	if (scriptData instanceof RuleScriptData) {
    		var ruleData = ((RuleScriptData) scriptData).getRuleData();
    		if (ruleData.isOutputExpValue()) {
    			// Cannot use fast path for output-expression rules.
    			return false;
    		}
    		return ruleData.getExpressionList().stream().allMatch(this::expressionCanUseFastPath);
    	}
    	return false;
    }
    
    private boolean expressionCanUseFastPath(final Expression expression) {
    	return FAST_PATH_EXPRESSIONS.containsKey(expression.getBooleanExpression());
    }
    
    private void execFast() throws Exception {
    	final IPV[] pvArray = getPvArray();
    	
    	final var ruleData = ruleScriptData.getRuleData();
    	final var widgetModel = ruleData.getWidgetModel();
    	
    	for (Expression e : ruleData.getExpressionList()) {
    		if (evaluateExpression(e, pvArray)) {
    			widgetModel.setPropertyValue(ruleData.getPropId(), e.getValue());
    			return;
    		}
    	}
    	widgetModel.setPropertyValue(ruleData.getPropId(), initialRulePropertyValue);
    }
    
    private boolean evaluateExpression(Expression e, IPV[] pvArray) {
    	return FAST_PATH_EXPRESSIONS.get(e.getBooleanExpression()).handle(pvArray);
    }

}
