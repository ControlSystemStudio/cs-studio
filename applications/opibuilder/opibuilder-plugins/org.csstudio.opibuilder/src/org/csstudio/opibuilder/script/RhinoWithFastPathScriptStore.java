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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.scriptUtil.PVUtil;
import org.csstudio.simplepv.IPV;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * This is the implementation of {@link AbstractScriptStore} for Rhino script engine.
 * @author Xihui Chen
 *
 */
public class RhinoWithFastPathScriptStore extends AbstractScriptStore{

    private Context scriptContext;

    private Scriptable scriptScope;

    private Script script;
    
    private final boolean canUseFastPath;
    private final RuleScriptData ruleScriptData;
    
    public interface FastPathHandler {
    	public boolean handle(IPV[] pvList);
    }
    
    public static final FastPathHandler PV0_EQ_0 = pvArr -> PVUtil.getDouble(pvArr[0]) == 0.0;
    public static final FastPathHandler PV0_EQ_1 = pvArr -> PVUtil.getDouble(pvArr[0]) == 1.0;
    public static final FastPathHandler PVINT0_EQ_0 = pvArr -> PVUtil.getLong(pvArr[0]) == 0;
    public static final FastPathHandler PVINT0_EQ_1 = pvArr -> PVUtil.getLong(pvArr[0]) == 1; 
    public static final FastPathHandler PV0_NEQ_0 = pvArr -> PVUtil.getDouble(pvArr[0]) != 0.0;
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
    	
    	FAST_PATH_EXPRESSIONS.put("pvInt0!=0", PVINT0_NEQ_0);
    	FAST_PATH_EXPRESSIONS.put("pvInt0 != 0", PVINT0_NEQ_0);
    	
    	FAST_PATH_EXPRESSIONS.put("true", ALWAYS_TRUE);
    	FAST_PATH_EXPRESSIONS.put("1==1", ALWAYS_TRUE);
    	FAST_PATH_EXPRESSIONS.put("1 == 1", ALWAYS_TRUE);
    	FAST_PATH_EXPRESSIONS.put("1===1", ALWAYS_TRUE);
    	FAST_PATH_EXPRESSIONS.put("1 === 1", ALWAYS_TRUE);
    	
    	FAST_PATH_EXPRESSIONS.put("false", ALWAYS_FALSE);
    }
    
    public static void addFastPathHandler(String expression, FastPathHandler handler) {
    	FAST_PATH_EXPRESSIONS.put(expression, handler);
    }

    public RhinoWithFastPathScriptStore(final ScriptData scriptData, final AbstractBaseEditPart editpart,
            final IPV[] pvArray) throws Exception {
        super(scriptData, editpart, pvArray);
        canUseFastPath = canUseFastPath(scriptData);
        if (canUseFastPath) {
        	ruleScriptData = (RuleScriptData) scriptData;
        } else {
        	ruleScriptData = null;
        }
    }

    @Override
    protected void initScriptEngine() throws Exception {
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


    @Override
    protected void compileString(String string) throws Exception{
    	if (!canUseFastPath) {
            script = scriptContext.compileString(string, "rule", 1, null);
    	}
    }

    @Override
    protected void compileInputStream(File file, InputStream s) throws IOException {
    	if (!canUseFastPath) {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(s));
	        script = scriptContext.compileReader(reader, "script", 1, null); //$NON-NLS-1$
	        s.close();
	        reader.close();
    	} else {
    		s.close();
    	}
    }

    @Override
    protected void execScript(final IPV triggerPV) throws Exception {
    	if (canUseFastPath) {
    		System.out.println("Using fast path script");
    	    execFast(triggerPV);
    	} else {
    		System.out.println("Using slow path script");
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
    		return ruleData.getExpressionList().stream().allMatch(expression -> canUseFastPath(expression));
    	}
    	return false;
    }
    
    private boolean canUseFastPath(final Expression expression) {
    	return FAST_PATH_EXPRESSIONS.containsKey(expression.getBooleanExpression());
    }
    
    private void execFast(final IPV triggerPV) throws Exception {
    	final IPV[] pvArray = getPvArray();
    	
    	final var ruleData = ruleScriptData.getRuleData();
    	final var widgetModel = ruleData.getWidgetModel();
    	
    	for (Expression e : ruleData.getExpressionList()) {
    		if (evaluateExpression(e, pvArray)) {
    			widgetModel.setPropertyValue(ruleData.getPropId(), e.getValue());
    			return;
    		}
    	}
    	widgetModel.setPropertyValue(ruleData.getPropId(), widgetModel.getPropertyValue(ruleData.getPropId()));
    }
    
    private boolean evaluateExpression(Expression e, IPV[] pvArray) {
    	return FAST_PATH_EXPRESSIONS.get(e.getBooleanExpression()).handle(pvArray);
    }

}
