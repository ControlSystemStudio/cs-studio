package org.csstudio.platform.ui.internal.console;

import java.io.PrintStream;

import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * The standard css console that, if initialized, becomes the system's standard
 * output.
 * 
 * @author awill
 */
public final class Console {
	/**
	 * The only one instance of this class.
	 */
	private static Console _instance = null;

	/**
	 * The message console.
	 */
	private MessageConsole _console = null;

	/**
	 * The console stream.
	 */
	private MessageConsoleStream _stream = null;

	/**
	 * Constructor is private due to singleton pattern.
	 */
	private Console() {
		_console = new MessageConsole(Messages
				.getString("Console.CONSOLE_TITLE"), null) { //$NON-NLS-1$
			@Override
			public String getHelpContextId() {
				return CSSPlatformUiPlugin.ID + ".console"; //$NON-NLS-1$
			}
		};
		_stream = _console.newMessageStream();
		ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
		consolePlugin.getConsoleManager().addConsoles(
				new IConsole[] { _console });
		consolePlugin.getConsoleManager().showConsoleView(_console);
		System.setOut(new PrintStream(_stream));
	}

	/**
	 * Return the only one instance of this class.
	 * 
	 * @return The only one instance of this class.
	 */
	public static Console getInstance() {
		if (_instance == null) {
			_instance = new Console();
		}
		return _instance;
	}

	/**
	 * Set the text color of the associated output stream.
	 * 
	 * @param color
	 *            The text color of the associated output stream.
	 */
	public void setColor(final Color color) {
		_stream.setColor(color);
	}
}
