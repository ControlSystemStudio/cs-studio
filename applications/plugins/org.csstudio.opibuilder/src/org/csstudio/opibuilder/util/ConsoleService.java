package org.csstudio.opibuilder.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;


/**The console service which manage the console output.
 * @author Xihui Chen
 *
 */
public class ConsoleService {

	private static final String ENTER = "\n"; //$NON-NLS-1$

	private static ConsoleService instance;

	/**
	 * The message console.
	 */
	private MessageConsole console = null;

	/**
	 * The console output stream.
	 */
	private MessageConsoleStream errorStream, warningStream, infoStream;
	
	/**
	 * Return the only one instance of this class.
	 * 
	 * @return The only one instance of this class.
	 */
	public synchronized static ConsoleService getInstance() {
		if (instance == null) {
			instance = new ConsoleService();
		}
		return instance;
	}
	
	private ConsoleService() {
		console = new MessageConsole("OPI Builder Console", null);

		
		// Values are from https://bugs.eclipse.org/bugs/show_bug.cgi?id=46871#c5
		console.setWaterMarks(80000, 100000);
		
		ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
		consolePlugin.getConsoleManager().addConsoles(
				new IConsole[] { console });

	}


	
	private String getTimeString(){
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
	    return sdf.format(cal.getTime());

	}
	
	/**Write error information to the OPI console.
	 * @param output the output string.
	 */
	public void writeError(String output){
		output = getTimeString() + " ERROR: " + output + ENTER;
		if(errorStream == null){
			errorStream = console.newMessageStream();
			errorStream.setColor(CustomMediaFactory.getInstance().getColor(
					CustomMediaFactory.COLOR_RED));
		}
		writeToConsole(errorStream, output);
		
	}
	
	/**Write warning information to the OPI console.
	 * @param output the output string.
	 */
	public void writeWarning(String output){
		output = getTimeString() + " WARNNING: " + output+ ENTER;
		if(warningStream == null){
			warningStream = console.newMessageStream();
			warningStream.setColor(CustomMediaFactory.getInstance().getColor(
					CustomMediaFactory.COLOR_ORANGE));
		}
		writeToConsole(warningStream, output);
	}
	
	/**Write information to the OPI console.
	 * @param output the output string.
	 */
	public void writeInfo(String output){
		output = getTimeString() + " INFO: " + output+ ENTER;
		if(infoStream == null){
			infoStream = console.newMessageStream();
			infoStream.setColor(CustomMediaFactory.getInstance().getColor(
					CustomMediaFactory.COLOR_BLACK));
		}
		writeToConsole(infoStream, output);
	}
	

	
	/**Write string to the console.
	 * @param output
	 */
	private void writeToConsole(MessageConsoleStream stream, String output){
		try {
			stream.write(output);
		} catch (IOException e) {
			CentralLogger.getInstance().error(this, "Write Console error",e);
		}
	}
	
}
