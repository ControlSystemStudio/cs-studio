package org.csstudio.rap.core.debug;

import java.util.logging.Level;

import org.csstudio.rap.core.DisplayManager;
import org.csstudio.rap.core.RAPCorePlugin;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Then entry point for debug info display.
 * 
 * @author Xihui Chen
 * 
 */
public class RAPDebugEntryPoint implements IEntryPoint {
	private int lines = 60;
	private String fileName;
	private long lastLength;
	@Override
	public int createUI() {
		final Display display = new Display();
		try {
			Shell shell = new Shell(display, SWT.TITLE | SWT.MAX | SWT.RESIZE
					| SWT.NO_TRIM);
			DisplayManager.getInstance().registerDisplay(display, true);
			shell.setMaximized(true);

			shell.setText("CSS RAP Debug");
			shell.setLayout(new FillLayout());
			final Text text = new Text(shell, SWT.READ_ONLY | SWT.MULTI
					| SWT.BORDER);
			text.setText(DisplayManager.getInstance().getDebugInfo());
			final Text consoleText = new Text(shell, SWT.READ_ONLY | SWT.MULTI
					| SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			
			String s = RWT.getRequest().getParameter("lines");
			if(s != null)
				lines = Integer.parseInt(s);
			
			fileName = System.getProperty("catalina.home");
			if (fileName == null || fileName.trim().isEmpty()) {
				fileName = "C:/Users/5hz/Desktop/webapps/catalina.out";
			} else
				fileName = fileName + "/logs/catalina.out"; //$NON-NLS-1$	
			
			ReverseFileReader reader = new ReverseFileReader(fileName);
			lastLength = reader.length();
			consoleText.setText(readCatalinaOut(reader, lines));
			reader.close();
			shell.layout();
			shell.open();
			display.timerExec(1000, new Runnable() {

				@Override
				public void run() {
					text.setText(DisplayManager.getInstance().getDebugInfo());
					try {						
						ReverseFileReader reader = new ReverseFileReader(fileName);
						if(reader.length() != lastLength){
							lastLength = reader.length();
							consoleText.setText(readCatalinaOut(reader, lines));
						}
						reader.close();
					} catch (Exception e) {
					}
					display.timerExec(1000, this);
				}
			});

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			RAPCorePlugin.getLogger().log(Level.WARNING,
					"Failed to output debug info", e);
		} finally {
			display.dispose();
		}
		return 0;
	}

	public String readCatalinaOut(ReverseFileReader reader, int lastLinesNo) throws Exception {
			
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lastLinesNo; i++) {
			String line = reader.readLine();
			if (line == null)
				break;
			sb.insert(0, line + "\n");
		}

		return sb.toString();
	}
}
