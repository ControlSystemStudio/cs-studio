/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.opibuilder.widgetActions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.csstudio.java.string.StringSplitter;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.osgi.util.NLS;

/** Helper for executing a (system) command.
 *  On Unix, that could be anything in the PATH.
 *  <p>
 *  Several things can happen:
 *  <ul>
 *  <li>Command finishes OK right away
 *  <li>Command gives error right away
 *  <li>Command runs for a long time, eventually giving error or OK.
 *  </ul>
 *  The command executor waits a little time to see if the command
 *  finishes, and calls back in case of an error.
 *  When the command finishes right away OK or runs longer,
 *  we leave it be.
 *
 * @author Kay Kasemir, Xihui Chen
 */
@SuppressWarnings("nls")
public final class CommandExecutor
{
    final private String dir_name;
    final private String command;
    final private int wait;

    /** Initialize
     *  @param command Command to run. Format depends on OS.
     *  @param dir_name Directory where to run the command
     *  @param wait Time to wait for completion in seconds
     */
    public CommandExecutor(final String command, final String dir_name,
            final int wait)
    {
        this.command = command;
        this.dir_name = dir_name;
        this.wait = wait;
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                runAndCheckCommand();
            }
        }, "CommandExecutor");
        t.start();
    }

    /** Derived class must implement this callback that's invoked
     *  in case of an error within the 'wait' time.
     *  <p>
     *  Will be invoked from CommandExecutor thread.
     *  @param exit_code Exit code of program
     *  @param stderr Standard error output of program
     */
     public void error(final int exit_code, final String stderr){
         ConsoleService.getInstance().writeError(stderr);

    }

    private void runAndCheckCommand()
    {
        // Execute command in a certain directory
        final File dir = new File(dir_name);
        final Process process;
        try
        {
                final String[] cmd = StringSplitter.splitIgnoreInQuotes(command, ' ', true);
            process = Runtime.getRuntime().exec(cmd, null, dir);
        }
        catch (Throwable ex)
        {
            error(-1, ex.getMessage());
            ConsoleService.getInstance().writeInfo(NLS.bind(
                    "Command \"{0}\" executing finished with exit code: FAILED", command));

            return;
        }

        //create a thread for listening on error output
        Thread errorThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // .. with error; check error output
                BufferedReader br = null;
                try
                {
                    final InputStream is = process.getErrorStream();
                    final InputStreamReader isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    String line;
                    while ((line = br.readLine()) != null){
                        ConsoleService.getInstance().writeString(
                                command + " error: ",
                                CustomMediaFactory.COLOR_PURPLE);
                        ConsoleService.getInstance()
                                .writeString(line + "\n", CustomMediaFactory.COLOR_RED);
                    }
                }
                catch (IOException e)
                {
                    ErrorHandlerUtil.handleError("Command Executing error" , e);
                    return;
                }finally{
                    if(br != null)
                        try {
                            br.close();
                        } catch (IOException e) {
                            ErrorHandlerUtil.handleError("Command Executing error" , e);
                        }
                }
            }
        });
        errorThread.start();

        //write output to console
        try {
            int c = 0;
            while(c != -1){
                c = process.getInputStream().read();
                if(c!=-1)
                    ConsoleService.getInstance().writeString(""+(char)c);
            }
        } catch (IOException e1) {
            ErrorHandlerUtil.handleError("Command Executing error" , e1);
        }


        // Poll exit code during 'wait' time
        Integer exit_code = null;
        for (int w=0; w<wait; ++w)
        {

            try
            {
                exit_code = process.exitValue();
                break;
            }
            catch (IllegalThreadStateException ex)
            {  //still running...
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {   // Ignore
                }
            }
        }

        ConsoleService.getInstance().writeInfo(NLS.bind(
                "Command \"{0}\" executing finished with exit code: ", command)
                + (exit_code == null ? "NULL" : (exit_code ==0 ? "OK" : "FAILED")));
        // Process runs so long that we no longer care
        if (exit_code == null)
            return;

        // Process ended
        if (exit_code == 0)
            return;




    }
}
