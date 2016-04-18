/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.scriptTest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class RunScriptFileDemo {
  public static void main(String[] args) throws IOException {


      final String testFile = "src/org/csstudio/opibuilder/scriptTest/ComplexSWTDialogs.js";

      // use Java 1.6 script engine
      /*ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("javascript");
      try {
          FileReader reader = new FileReader(testFile);

          // this will make the object stay in engine
          engine.put("x", new Hello("Hello js 1"));
          CompiledScript script = ((Compilable) engine).compile(reader);
          script.eval();
          Object o = engine.eval(reader);

          //this will make the object only stay for this eval.
          ScriptContext newContext = new SimpleScriptContext();
          Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);
          engineScope.put("x", new Hello("Hello js 2"));

          reader.close();
          reader = new FileReader(testFile);
          engine.eval(reader, newContext);

          reader.close();
          reader = new FileReader(testFile);
          engine.eval(reader);

          System.out.println(o);

          reader.close();
        } catch (Exception e) {
          e.printStackTrace();
        }*/



    //use Rhino script engine
  //Refer to sds.ui.scripting.RunnableScript.java
    Context scriptContext = Context.enter();
    final Scriptable scriptScope = new ImporterTopLevel(scriptContext);

    BufferedReader reader = new BufferedReader(new FileReader(testFile));
    //the object will only stay in the scope.
    Object hello = Context.javaToJS(new Hello("hello Rhino 1"), scriptScope);
    ScriptableObject.putProperty(scriptScope, "x", hello);
    scriptContext.evaluateReader(scriptScope, reader, "script file", 1, null);
    reader.close();
    Context.exit();
    Display display = Display.getCurrent();
    final Shell shell = new Shell();
    shell.setSize(800, 500);
    shell.open();
    shell.setText("XY Graph Test");



    display.asyncExec(new Runnable() {

        @Override
        public void run() {
            try {
                Context scriptContext2 = Context.enter();
                final Scriptable scriptScope2 = new ImporterTopLevel(scriptContext2);

                BufferedReader reader2 = new BufferedReader(new FileReader(testFile));
                //compile and executes
                Object hello2 = Context.javaToJS(new Hello("hello Rhino 2"), scriptScope2);
                ScriptableObject.putProperty(scriptScope2, "x", hello2);
                Script script = scriptContext2.compileReader(reader2, "script", 1, null);
                reader2.close();
                script.exec(scriptContext2, scriptScope2);
                Object hello3 = Context.javaToJS(new Hello("hello Rhino 3"), scriptScope2);
                ScriptableObject.putProperty(scriptScope2, "x", hello3);
                script.exec(scriptContext2, scriptScope2);
                //.evaluateReader(scriptScope2, reader2, "script file", 1, null);
                Context.exit();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    });

    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }


  }
}

class Hello{
    private String x = "sdfsdf";


    public Hello(String x) {
        super();
        this.x = x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getX() {
        return x;
    }

}
