package com.cosylab.vdct.about;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Insert the type's description here.
 * Creation date: (29.5.2002 15:57:44)
 * @author: 
 */
public class AboutDialog extends javax.swing.JDialog implements AboutTabReceiver {
	private javax.swing.JButton ivjJButtonClose = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JPanel ivjJPanelCloseButtonDivider = null;
	private javax.swing.JTabbedPane ivjJTabbedPane = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == AboutDialog.this.getJButtonClose()) 
				connEtoM1(e);
		};
	};
/**
 * AboutDialog constructor comment.
 */
public AboutDialog() {
	super();
	initialize();
}
/**
 * AboutDialog constructor comment.
 * @param owner java.awt.Dialog
 */
public AboutDialog(java.awt.Dialog owner) {
	super(owner);
}
/**
 * AboutDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 */
public AboutDialog(java.awt.Dialog owner, String title) {
	super(owner, title);
}
/**
 * AboutDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 * @param modal boolean
 */
public AboutDialog(java.awt.Dialog owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * AboutDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param modal boolean
 */
public AboutDialog(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
}
/**
 * AboutDialog constructor comment.
 * @param owner java.awt.Frame
 */
public AboutDialog(java.awt.Frame owner) {
	super(owner);
	initialize();
}
/**
 * AboutDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public AboutDialog(java.awt.Frame owner, String title) {
	super(owner, title);
}
/**
 * AboutDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public AboutDialog(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * AboutDialog constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public AboutDialog(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
	initialize();
}
/**
 * connEtoM1:  (JButtonClose.action.actionPerformed(java.awt.event.ActionEvent) --> AboutDialog.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JButtonClose property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonClose() {
	if (ivjJButtonClose == null) {
		try {
			ivjJButtonClose = new javax.swing.JButton();
			ivjJButtonClose.setName("JButtonClose");
			ivjJButtonClose.setText("Close");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonClose;
}
/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJTabbedPane = new java.awt.GridBagConstraints();
			constraintsJTabbedPane.gridx = 0; constraintsJTabbedPane.gridy = 0;
			constraintsJTabbedPane.gridwidth = 2;
			constraintsJTabbedPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJTabbedPane.weightx = 1.0;
			constraintsJTabbedPane.weighty = 1.0;
			constraintsJTabbedPane.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJTabbedPane(), constraintsJTabbedPane);

			java.awt.GridBagConstraints constraintsJPanelCloseButtonDivider = new java.awt.GridBagConstraints();
			constraintsJPanelCloseButtonDivider.gridx = 0; constraintsJPanelCloseButtonDivider.gridy = 1;
			constraintsJPanelCloseButtonDivider.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelCloseButtonDivider.weightx = 1.0;
			constraintsJPanelCloseButtonDivider.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJPanelCloseButtonDivider(), constraintsJPanelCloseButtonDivider);

			java.awt.GridBagConstraints constraintsJButtonClose = new java.awt.GridBagConstraints();
			constraintsJButtonClose.gridx = 1; constraintsJButtonClose.gridy = 1;
			constraintsJButtonClose.insets = new java.awt.Insets(6, 4, 11, 11);
			getJDialogContentPane().add(getJButtonClose(), constraintsJButtonClose);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelCloseButtonDivider() {
	if (ivjJPanelCloseButtonDivider == null) {
		try {
			ivjJPanelCloseButtonDivider = new javax.swing.JPanel();
			ivjJPanelCloseButtonDivider.setName("JPanelCloseButtonDivider");
			ivjJPanelCloseButtonDivider.setLayout(null);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelCloseButtonDivider;
}
/**
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getJTabbedPane() {
	if (ivjJTabbedPane == null) {
		try {
			ivjJTabbedPane = new javax.swing.JTabbedPane();
			ivjJTabbedPane.setName("JTabbedPane");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonClose().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AboutDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		setSize(600, 400);
		setTitle("About");
		setContentPane(getJDialogContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		AboutDialog aAboutDialog;
		aAboutDialog = new AboutDialog();
		aAboutDialog.setModal(true);
		aAboutDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aAboutDialog.setVisible(true);
		java.awt.Insets insets = aAboutDialog.getInsets();
		aAboutDialog.setSize(aAboutDialog.getWidth() + insets.left + insets.right, aAboutDialog.getHeight() + insets.top + insets.bottom);
		aAboutDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}
	/**
	 * @see com.cosylab.gui.components.about.AboutTabReceiver#AddTab(AboutTab)
	 */
	public void addAboutTab(AboutTab tabToAdd) {
		getJTabbedPane().addTab(tabToAdd.getName(), tabToAdd.getPanel());
	}

	/**
	 * @see com.cosylab.gui.components.about.AboutTabReceiver#ReceiverPerform()
	 */
	public void receiverPerform() {
		this.setVisible(true);
	}

}
