package org.csstudio.opibuilder.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.simplepv.IPV;

/**
 * This is the implementation of {@link AbstractScriptStore} for the default javascript script engine embedded in Java.
 * The default javascript engine is Rhino for Java 7, Nashorn for Java 8.
 */
public class JavaScriptStore extends AbstractScriptStore {

    private ScriptEngine engine;
    private Bindings bindings;
    private CompiledScript script;

    public JavaScriptStore(final ScriptData scriptData, final AbstractBaseEditPart editpart,
            final IPV[] pvArray) throws Exception {
        super(scriptData, editpart, pvArray);

    }

    @Override
    protected void initScriptEngine() throws Exception {
        engine = ScriptStoreFactory.getJavaScriptEngine();
        bindings = engine.createBindings();
        bindings.put(ScriptService.WIDGET, getEditPart());
        bindings.put(ScriptService.PVS, getPvArray());
        bindings.put(ScriptService.DISPLAY, getDisplayEditPart());
        bindings.put(ScriptService.WIDGET_CONTROLLER_DEPRECIATED, getEditPart());
        bindings.put(ScriptService.PV_ARRAY_DEPRECIATED, getPvArray());
    }

    @Override
    protected void compileString(String string) throws Exception {
        script = ((Compilable) engine).compile(string);
    }

    @Override
    protected void compileInputStream(File dir, InputStream s) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(s))) {
            script = ((Compilable) engine).compile(reader);
        }

    }

    @Override
    protected void execScript(IPV triggerPV) throws Exception {
        bindings.put(ScriptService.TRIGGER_PV, triggerPV);
        script.eval(bindings);
    }
}
