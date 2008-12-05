/*
 * Created on Aug 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cosylab.vdct.util;

import java.awt.Component;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

/**
 * @author ilist
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ComboBoxFileChooser extends JFileChooser {

	/**
	 * 
	 */
	public ComboBoxFileChooser() {
		super();
	}

	/**
	 * @param currentDirectory
	 */
	public ComboBoxFileChooser(File currentDirectory) {
		super(currentDirectory);
	}

	/**
	 * @param currentDirectoryPath
	 */
	public ComboBoxFileChooser(String currentDirectoryPath) {
		super(currentDirectoryPath);
	}

	/**
	 * @param fsv
	 */
	public ComboBoxFileChooser(FileSystemView fsv) {
		super(fsv);
	}

	/**
	 * @param currentDirectory
	 * @param fsv
	 */
	public ComboBoxFileChooser(File currentDirectory, FileSystemView fsv) {
		super(currentDirectory, fsv);
	}

	/**
	 * @param currentDirectoryPath
	 * @param fsv
	 */
	public ComboBoxFileChooser(
		String currentDirectoryPath,
		FileSystemView fsv) {
		super(currentDirectoryPath, fsv);
	}

	ComboBoxFileChooserDialog dialog = null;
	/* (non-Javadoc)
	 * @see javax.swing.JFileChooser#createDialog(java.awt.Component)
	 */
	protected JDialog createDialog(Component parent) throws HeadlessException {		
		Frame frame = parent instanceof Frame ? (Frame) parent
					  : (Frame)SwingUtilities.getAncestorOfClass(Frame.class, parent);

			String title = getUI().getDialogTitle(this);
				getAccessibleContext().setAccessibleDescription(title);

				dialog = new ComboBoxFileChooserDialog(frame, this);
				dialog.setTitle(title);

				if (JDialog.isDefaultLookAndFeelDecorated()) {
					boolean supportsWindowDecorations = 
					UIManager.getLookAndFeel().getSupportsWindowDecorations();
					if (supportsWindowDecorations) {
						dialog.getRootPane().setWindowDecorationStyle(JRootPane.FILE_CHOOSER_DIALOG);
					}
				}

				dialog.pack();
				dialog.setLocationRelativeTo(parent);
			
			return dialog;
	}
	
	public javax.swing.JCheckBox getJCheckBoxAbsoluteDBD() {
		return dialog.getJCheckBoxAbsoluteDBD();
	}
}
