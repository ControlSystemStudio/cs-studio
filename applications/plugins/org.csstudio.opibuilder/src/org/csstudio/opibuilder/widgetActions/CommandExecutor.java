package org.csstudio.opibuilder.widgetActions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.platform.util.StringUtil;
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
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
@SuppressWarnings("nls")
public class CommandExecutor
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
        new Thread(new Runnable()
        {
            public void run()
            {
                runAndCheckCommand();
            }
        }, "CommandExecutor").start();
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
   	     	final String[] cmd = StringUtil.splitIgnoreInQuotes(command, ' ', true);
            process = Runtime.getRuntime().exec(cmd, null, dir);
        }
        catch (Throwable ex)
        {
        	error(-1, ex.getMessage());
        	ConsoleService.getInstance().writeInfo(NLS.bind(
        			"Command \"{0}\" executing finished with exit code: FAILED", command));
            
            return;
        }
        
        //write output to console
        try {
			int c = 0;
			while(c != -1){
				c = process.getInputStream().read();				
				ConsoleService.getInstance().writeString(""+(char)c);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
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
        		+ (exit_code ==0 ? "OK" : "FAILED") );
        // Process runs so long that we no longer care
        if (exit_code == null)
            return;
        
        // Process ended
        if (exit_code == 0)
            return;
        
        // .. with error; check error output
        final StringBuilder stderr = new StringBuilder();
        try
        {
            final InputStream is = process.getErrorStream();
            final InputStreamReader isr = new InputStreamReader(is);
            final BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null)
                stderr.append(line + "\n");
        }
        catch (IOException e)
        {
            error(-1, e.getMessage());
            return;
        }
        error(exit_code, stderr.toString());
      
        
    }
}
