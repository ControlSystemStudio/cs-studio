package org.csstudio.sds.ui.scripting;

import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javazoom.jl.player.Player;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.platform.simpledal.ValueType;
import org.csstudio.sds.ui.CheckedUiRunnable;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.csstudio.sds.util.PathUtil;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Collection of functions that can be used in Rhino scripts.
 * 
 * @author swende, Xihui Chen
 * 
 */
public class Functions {
	private static Player _mp3Player;

	/**
	 * Open the specified display in a shell.
	 * 
	 * @param path
	 *            the display path
	 */
	public static void openDisplay(final String path) {
		new CheckedUiRunnable() {
			@Override
			protected void doRunInUi() {
				IPath fullPath = new Path(path);
				if (isWorkspaceRelativepath(path)) {
					fullPath = getFullPath(path);
				}
				RunModeService.getInstance().openDisplayShellInRunMode(
						fullPath);
			}
		};
	}
	
	/**Write double value to a dal channel. 
	 * @param name the name of the channel
	 * @param value the value to be sent.
	 */
	public static void writeDalChannel(final String name, final double value){
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory
				.getDefault().getProcessVariableConnectionService();
		ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance();
		IProcessVariableAddress pv = pvFactory.createProcessVariableAdress(name);
		try {
			service.writeValueSynchronously(pv, value, ValueType.DOUBLE);
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Close all running instances of the specified display.
	 * 
	 * @param path
	 *            the display path
	 */
	public static void closeDisplay(final String path) {
		new CheckedUiRunnable() {
			@Override
			protected void doRunInUi() {
				IPath fullPath = new Path(path);
				if (isWorkspaceRelativepath(path)) {
					fullPath = getFullPath(path);
				}
				RunModeService.getInstance().closeDisplayShellInRunMode(
						fullPath);
			}
		};
	}
	
	private static boolean isWorkspaceRelativepath(String path) {
		return path.startsWith("%workspace%");
	}
	
	private static IPath getFullPath(String path) {
		IPath relativePath = new Path(path.replace("%workspace%", ""));
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		return PathUtil.getFullPath(relativePath, workspaceRoot.getLocation());
	}

	/**
	 * Emit a system beep.
	 */
	public static void beep() {
		Toolkit.getDefaultToolkit().beep();
	}

	/**
	 * Pause the script execution for the specified number of milliseconds.
	 * 
	 * @param seconds
	 *            number of milliseconds
	 */
	public static void wait(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Play a siren sound.
	 */
	public static void siren() {
		playSoundFile("siren");
	}

	/**
	 * Play an alert sound.
	 */
	public static void alert() {
		playSoundFile("alarm");
	}

	/**
	 * Play a MP3 file.
	 * 
	 * @param path the Workspace path
	 */
	public static void playMp3(final String path) {
		    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		    File f;
		    if (path.startsWith("%workspace%")) {
		    	IPath relativePath = new Path(path.replace("%workspace%", ""));
		    	IPath fullPath = PathUtil.getFullPath(relativePath, workspaceRoot.getLocation());
		    	f = fullPath.toFile();
		    } else {
		    	f = workspaceRoot.getLocation().append(path).toFile();
		    }
            try {
                FileInputStream fis = new FileInputStream(f);
                playMp3(fis);
            } catch (FileNotFoundException e) {
                CentralLogger.getInstance().error(Functions.class, e);
            }
	}
	public static void stopPlaying() {
		if (_mp3Player != null) {
			_mp3Player.close();
		}
	}

	/**
	 * Play the specified sound file.
	 * 
	 * @param name
	 *            the name (without file-extension) of a MP3 file that is
	 *            located in the sounds/ folder that is contained in this
	 *            bundle.
	 */
	private static void playSoundFile(String name) {
		IPath path = new Path("sounds/" + name + ".mp3");
		URL fullPathString = FileLocator.find(SdsUiPlugin.getDefault().getBundle(), path, null);
		try {
			playMp3(fullPathString.openConnection().getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Play a MP3.
	 * 
	 * @param stream a MP3 file stream
	 */
	private static synchronized void playMp3(final InputStream stream) {
		if (_mp3Player != null) {
			_mp3Player.close();
		}
		// run in new thread to play in background
		new Thread() {
			public void run() {
				try {
					BufferedInputStream bis = new BufferedInputStream(stream);
					_mp3Player = new Player(bis);
					_mp3Player.play();
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}.start();
	}
}
