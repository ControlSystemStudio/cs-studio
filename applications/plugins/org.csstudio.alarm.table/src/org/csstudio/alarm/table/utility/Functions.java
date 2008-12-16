package org.csstudio.alarm.table.utility;

import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javazoom.jl.player.Player;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Collection of functions that can be used in Rhino scripts.
 * 
 * @author swende
 * 
 */
public class Functions {
	private static Player _mp3Player;
	
	private static boolean _sound = true;

	public static boolean is_sound() {
		return _sound;
	}

	public static void set_sound(boolean _sound) {
		Functions._sound = _sound;
	}

	/**
	 * Emit a system beep.
	 */
	public static void beep() {
		Toolkit.getDefaultToolkit().beep();
	}

	/**
	 * Play a MP3 file.
	 * 
	 * @param path the Workspace path
	 */
	public static void playMp3(final String path) {
		if (!_sound) {
			return;
		}
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

	
	/**
	 * Play a MP3.
	 * 
	 * @param stream a MP3 file stream
	 */
	private static synchronized void playMp3(final InputStream stream) {
		if (_mp3Player != null) {
			if (!_mp3Player.isComplete()) {
				return;
			}
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
