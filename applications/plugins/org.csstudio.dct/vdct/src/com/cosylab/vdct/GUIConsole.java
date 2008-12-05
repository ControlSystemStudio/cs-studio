package com.cosylab.vdct;

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
 * Creation date: (8.1.2001 18:21:54)
 * @author Matej Sekoranja
 */

public class GUIConsole extends javax.swing.JFrame implements ConsoleInterface {

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GUIConsole.this.getClearButton()) 
				connEtoM1(e);
		};
	}
	
	private javax.swing.JButton ivjClearButton = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JPanel ivjJInternalFrameContentPane = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTextArea ivjTextPane = null;
/**
 * Console constructor comment.
 */
public GUIConsole() {
	super();
	initialize();
}
/**
 * connEtoM1:  (ClearButton.action.actionPerformed(java.awt.event.ActionEvent) --> JTextArea1.text)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getTextPane().setText("");
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (20.07.99 14:37:16)
 */
public void flush() {
	getTextPane().setText("");
}
/**
 * Return the ClearButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getClearButton() {
	if (ivjClearButton == null) {
		try {
			ivjClearButton = new javax.swing.JButton();
			ivjClearButton.setName("ClearButton");
			ivjClearButton.setText("Clear");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClearButton;
}

/**
 * Return the JInternalFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJInternalFrameContentPane() {
	if (ivjJInternalFrameContentPane == null) {
		try {
			ivjJInternalFrameContentPane = new javax.swing.JPanel();
			ivjJInternalFrameContentPane.setName("JInternalFrameContentPane");
			ivjJInternalFrameContentPane.setLayout(new java.awt.BorderLayout());
			getJInternalFrameContentPane().add(getClearButton(), "South");
			getJInternalFrameContentPane().add(getJScrollPane1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJInternalFrameContentPane;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPane1().setViewportView(getTextPane());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the JTextArea1 property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getTextPane() {
	if (ivjTextPane == null) {
		try {
			ivjTextPane = new javax.swing.JTextArea();
			ivjTextPane.setName("TextPane");
			ivjTextPane.setBackground(new java.awt.Color(255,255,225));
			ivjTextPane.setBounds(0, 0, 160, 120);
			ivjTextPane.setMargin(new java.awt.Insets(10, 10, 10, 10));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextPane;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getClearButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		setLocation(100, 100);
		// user code end
		setName("Console");
		setTitle("VisualDCT Console");
		setSize(764, 270);
		setResizable(true);
		setContentPane(getJInternalFrameContentPane());
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
		GUIConsole aConsole;
		aConsole = new GUIConsole();
		aConsole.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aConsole.setVisible(true);
		java.awt.Insets insets = aConsole.getInsets();
		aConsole.setSize(aConsole.getWidth() + insets.left + insets.right, aConsole.getHeight() + insets.top + insets.bottom);
		aConsole.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (20.07.99 14:15:05)
 * @param text java.lang.String
 */
public void print(String text) {
	getTextPane().append(text);
	if (!isVisible()) setVisible(true);
	setExtendedState(NORMAL);
	toFront();
}
/**
 * Insert the method's description here.
 * Creation date: (20.07.99 14:22:21)
 */
public void println() {
	getTextPane().append("\n");
	if (!isVisible()) setVisible(true);
	setExtendedState(NORMAL);
	toFront();
}
/**
 * Insert the method's description here.
 * Creation date: (20.07.99 14:15:05)
 * @param text java.lang.String
 */
public void println(String text) {
	getTextPane().append(text+"\n");
	getTextPane().setCaretPosition(getTextPane().getText().length());
	if (!isVisible()) setVisible(true);
	setExtendedState(NORMAL);
	toFront();
}
/**
 * Insert the method's description here.
 * Creation date: (20.07.99 14:24:41)
 * @param thr java.lang.Throwable
 */
public void println(Throwable thr) {
	StringBuffer exceptionTrace = new StringBuffer(300);
    StackTraceElement[] trace = thr.getStackTrace();
    if (trace != null)
	    for (int i=0; i < trace.length; i++)
	        exceptionTrace.append("\tat " + trace[i]).append('\n');

    getTextPane().append(thr.toString()+"\n"+exceptionTrace.toString()+"\n");
	
	// some debug info in development phase
	System.err.println();
	thr.printStackTrace(System.err);
	System.err.println();

	getTextPane().setCaretPosition(getTextPane().getText().length());
	if (!isVisible()) setVisible(true);
	setExtendedState(NORMAL);
	toFront();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 21:19:09)
 * @param string java.lang.String
 */
public void silent(String string) {
	getTextPane().append(string);
}
}
