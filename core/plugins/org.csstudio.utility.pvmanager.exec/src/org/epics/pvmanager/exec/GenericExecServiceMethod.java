/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.exec;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceMethodDescription;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.epics.pvmanager.WriteFunction;
import org.epics.vtype.VString;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.io.CSVIO;

/**
 *
 * @author carcassi
 */
class GenericExecServiceMethod extends ServiceMethod {

    public GenericExecServiceMethod() {
        super(new ServiceMethodDescription("run", "Executes a command.")
                .addArgument("command", "The command", VString.class)
                .addResult("output", "The output of the command", VType.class));
    }

    @Override
    public void executeMethod(final Map<String, Object> parameters, final WriteFunction<Map<String, Object>> callback, final WriteFunction<Exception> errorCallback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        String shell = defaultShell();
        String shellArg = defaultShellArg();
        String command = ((VString) parameters.get("command")).getValue();
        executeCommand(parameters, callback, errorCallback, executor, shell, shellArg, command);
    }
    
    static String defaultShell() {
        if (isWindows()) {
            return "cmd";
        } else {
            return "/bin/bash";
        }
    }
    
    static String defaultShellArg() {
        if (isWindows()) {
            return "/c";
        } else {
            return "-c";
        }
    }
    
    static boolean isWindows() {
        return System.getProperties().get("os.name").toString().toLowerCase().indexOf("win") >= 0;
    }

    static void executeCommand(final Map<String, Object> parameters, final WriteFunction<Map<String, Object>> callback, final WriteFunction<Exception> errorCallback,
            final ExecutorService executor, final String shell, final String shellArg, final String command) {
        executor.submit(new Runnable() {

            @Override
            public void run() {
                Process process = null;
                try {
                    process = new ProcessBuilder(shell, shellArg, command).start();
                    
                    // Read output to a text buffer
                    StringBuilder buffer = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }
                    String output = buffer.toString();
                    
                    // Try parsing output as a table
                    try {
                        CSVIO io = new CSVIO();
                        VTable table = io.importVTable(new StringReader(output));
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("output", table);
                        callback.writeValue(resultMap);
                        return;
                    } catch(Exception ex) {
                        // Can't parse output to a table
                    }
                    
                    // Return output as a String
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("output", ValueFactory.newVString(output, ValueFactory.alarmNone(), ValueFactory.timeNow()));
                    callback.writeValue(resultMap);

                } catch (Exception ex) {
                    if (process != null) {
                        // Try to kill the process if it was created
                        try {
                            process.destroy();
                        } catch (Exception ex1) {
                            // Ignore any error
                        }
                    }
                    errorCallback.writeValue(ex);
                }
            }
        });
    }
}
