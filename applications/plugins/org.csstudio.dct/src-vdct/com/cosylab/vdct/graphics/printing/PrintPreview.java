package com.cosylab.vdct.graphics.printing;

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

import java.awt.*;
import java.awt.print.*;
import java.awt.image.*;

/**
 * Insert the type's description here.
 * Creation date: (12.5.2001 15:24:22)
 * @author Matej Sekoranja
 */
public class PrintPreview extends javax.swing.JDialog {
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private javax.swing.JToolBar ivjJToolBar1 = null;
	private javax.swing.JButton ivjJToolBarButton1 = null;
	private javax.swing.JButton ivjPrintToolBarButton = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JComboBox ivjScaleComboBox = null;
	private javax.swing.JLabel ivjScaleLabel = null;
	// private
	private int pageWidth;
	private int pageHeight;
	private Pageable target = null;
	private boolean loadingPreviews = false;
	private PreviewContainer ivjPreviewContainer = null;
	private javax.swing.JButton ivjPrintToolBarButton1 = null;
	private javax.swing.JLabel ivjStatusLabel = null;
	private javax.swing.JPanel ivjStatusPanel = null;
	private javax.swing.JComboBox ivjModeComboBox = null;
	private javax.swing.JLabel ivjModeLabel = null;
	private javax.swing.JLabel ivjPrecentLabel = null;
	private javax.swing.JTextField ivjUserScaleTextField = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.awt.event.WindowListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PrintPreview.this.getPrintToolBarButton()) 
				connEtoC1();
			if (e.getSource() == PrintPreview.this.getScaleComboBox()) 
				connEtoC2(e);
			if (e.getSource() == PrintPreview.this.getPrintToolBarButton1()) 
				connEtoC3(e);
			if (e.getSource() == PrintPreview.this.getJToolBarButton1()) 
				connEtoC5();
			if (e.getSource() == PrintPreview.this.getUserScaleTextField()) 
				connEtoC6(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == PrintPreview.this.getModeComboBox()) 
				connEtoC7(e);
		};
		public void windowActivated(java.awt.event.WindowEvent e) {};
		public void windowClosed(java.awt.event.WindowEvent e) {
			if (e.getSource() == PrintPreview.this) 
				connEtoC4(e);
		};
		public void windowClosing(java.awt.event.WindowEvent e) {};
		public void windowDeactivated(java.awt.event.WindowEvent e) {};
		public void windowDeiconified(java.awt.event.WindowEvent e) {};
		public void windowIconified(java.awt.event.WindowEvent e) {};
		public void windowOpened(java.awt.event.WindowEvent e) {};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PrintPreview() {
	super();
	initialize();
}
/**
 * PrintPreviewFrame constructor comment.
 * @param title java.lang.String
 */
public PrintPreview(Frame parent, Pageable target, String title) {
	super(parent);
	setTitle(title);
	this.target = target;
	initialize();
}
/**
 * connEtoC1:  (PrintToolBarButton.action. --> PrintPreviewFrame.printToolBarButton_ActionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.printToolBarButton_ActionEvents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (ScaleComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> PrintPreviewFrame.scaleComboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.scaleComboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (PrintToolBarButton1.action.actionPerformed(java.awt.event.ActionEvent) --> PrintPreview.printToolBarButton1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.printToolBarButton1_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (PrintPreviewFrame.window.windowClosed(java.awt.event.WindowEvent) --> PrintPreview.printPreviewFrame_WindowClosed(Ljava.awt.event.WindowEvent;)V)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.printPreviewFrame_WindowClosed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JToolBarButton1.action. --> PrintPreview.jToolBarButton1_ActionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5() {
	try {
		// user code begin {1}
		// user code end
		this.jToolBarButton1_ActionEvents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JTextField1.action.actionPerformed(java.awt.event.ActionEvent) --> PrintPreview.jTextField1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTextField1_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (ModeComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PrintPreview.modeComboBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.modeComboBox_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new java.awt.BorderLayout());
			getJFrameContentPane().add(getJToolBar1(), "North");
			getJFrameContentPane().add(getStatusPanel(), "South");
			getJFrameContentPane().add(getJScrollPane1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
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
			getJScrollPane1().setViewportView(getPreviewContainer());
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
 * Return the JToolBar1 property value.
 * @return javax.swing.JToolBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToolBar getJToolBar1() {
	if (ivjJToolBar1 == null) {
		try {
			ivjJToolBar1 = new javax.swing.JToolBar();
			ivjJToolBar1.setName("JToolBar1");
			ivjJToolBar1.add(getPrintToolBarButton());
			getJToolBar1().add(getPrintToolBarButton1(), getPrintToolBarButton1().getName());
			getJToolBar1().add(getJToolBarButton1(), getJToolBarButton1().getName());
			ivjJToolBar1.addSeparator();
			getJToolBar1().add(getScaleLabel(), getScaleLabel().getName());
			getJToolBar1().add(getScaleComboBox(), getScaleComboBox().getName());
			ivjJToolBar1.addSeparator();
			getJToolBar1().add(getModeLabel(), getModeLabel().getName());
			getJToolBar1().add(getModeComboBox(), getModeComboBox().getName());
			getJToolBar1().add(getUserScaleTextField(), getUserScaleTextField().getName());
			getJToolBar1().add(getPrecentLabel(), getPrecentLabel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToolBar1;
}
/**
 * Return the JToolBarButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJToolBarButton1() {
	if (ivjJToolBarButton1 == null) {
		try {
			ivjJToolBarButton1 = new javax.swing.JButton();
			ivjJToolBarButton1.setName("JToolBarButton1");
			ivjJToolBarButton1.setMnemonic('c');
			ivjJToolBarButton1.setText("Cancel");
			ivjJToolBarButton1.setMaximumSize(new java.awt.Dimension(83, 30));
			ivjJToolBarButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			ivjJToolBarButton1.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cancel.gif")));
			ivjJToolBarButton1.setPreferredSize(new java.awt.Dimension(83, 30));
			ivjJToolBarButton1.setMargin(new java.awt.Insets(0, 4, 0, 4));
			ivjJToolBarButton1.setMinimumSize(new java.awt.Dimension(83, 30));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToolBarButton1;
}
/**
 * Return the ModeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getModeComboBox() {
	if (ivjModeComboBox == null) {
		try {
			ivjModeComboBox = new javax.swing.JComboBox();
			ivjModeComboBox.setName("ModeComboBox");
			ivjModeComboBox.setMaximumSize(new java.awt.Dimension(130, 23));
			// user code begin {1}
			ivjModeComboBox.addItem("1:1");
			ivjModeComboBox.addItem("Using scale:");
			ivjModeComboBox.addItem("Fit to paper");
			ivjModeComboBox.setSelectedIndex(Page.getPrintMode());
			
			if (Page.getPrintMode()!=Page.USER_SCALE)
			{
				getUserScaleTextField().setVisible(false);
				getPrecentLabel().setVisible(false);
			}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjModeComboBox;
}
/**
 * Return the ModeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getModeLabel() {
	if (ivjModeLabel == null) {
		try {
			ivjModeLabel = new javax.swing.JLabel();
			ivjModeLabel.setName("ModeLabel");
			ivjModeLabel.setText("Print mode: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjModeLabel;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPrecentLabel() {
	if (ivjPrecentLabel == null) {
		try {
			ivjPrecentLabel = new javax.swing.JLabel();
			ivjPrecentLabel.setName("PrecentLabel");
			ivjPrecentLabel.setText("%");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPrecentLabel;
}
/**
 * Return the PreviewContainer property value.
 * @return com.cosylab.vdct.graphics.printing.PreviewContainer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private PreviewContainer getPreviewContainer() {
	if (ivjPreviewContainer == null) {
		try {
			ivjPreviewContainer = new com.cosylab.vdct.graphics.printing.PreviewContainer();
			ivjPreviewContainer.setName("PreviewContainer");
			ivjPreviewContainer.setLocation(0, 0);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPreviewContainer;
}
/**
 * Return the PrintToolBarButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getPrintToolBarButton() {
	if (ivjPrintToolBarButton == null) {
		try {
			ivjPrintToolBarButton = new javax.swing.JButton();
			ivjPrintToolBarButton.setName("PrintToolBarButton");
			ivjPrintToolBarButton.setMnemonic('p');
			ivjPrintToolBarButton.setText("Print");
			ivjPrintToolBarButton.setMaximumSize(new java.awt.Dimension(72, 30));
			ivjPrintToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			ivjPrintToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjPrintToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/print.gif")));
			ivjPrintToolBarButton.setPreferredSize(new java.awt.Dimension(72, 30));
			ivjPrintToolBarButton.setMargin(new java.awt.Insets(0, 4, 0, 4));
			ivjPrintToolBarButton.setMinimumSize(new java.awt.Dimension(72, 30));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPrintToolBarButton;
}
/**
 * Return the PrintToolBarButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getPrintToolBarButton1() {
	if (ivjPrintToolBarButton1 == null) {
		try {
			ivjPrintToolBarButton1 = new javax.swing.JButton();
			ivjPrintToolBarButton1.setName("PrintToolBarButton1");
			ivjPrintToolBarButton1.setMnemonic('g');
			ivjPrintToolBarButton1.setText("Page Setup");
			ivjPrintToolBarButton1.setMaximumSize(new java.awt.Dimension(110, 30));
			ivjPrintToolBarButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			ivjPrintToolBarButton1.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjPrintToolBarButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/page.gif")));
			ivjPrintToolBarButton1.setPreferredSize(new java.awt.Dimension(68, 30));
			ivjPrintToolBarButton1.setMargin(new java.awt.Insets(0, 4, 0, 4));
			ivjPrintToolBarButton1.setMinimumSize(new java.awt.Dimension(68, 30));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPrintToolBarButton1;
}
/**
 * Return the ScaleComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getScaleComboBox() {
	if (ivjScaleComboBox == null) {
		try {
			ivjScaleComboBox = new javax.swing.JComboBox();
			ivjScaleComboBox.setName("ScaleComboBox");
			ivjScaleComboBox.setPreferredSize(new java.awt.Dimension(75, 23));
			ivjScaleComboBox.setMaximumSize(new java.awt.Dimension(75, 23));
			ivjScaleComboBox.setEditable(true);
			ivjScaleComboBox.setMinimumSize(new java.awt.Dimension(75, 23));
			// user code begin {1}
			ivjScaleComboBox.addItem("10%");
			ivjScaleComboBox.addItem("25%");
			ivjScaleComboBox.addItem("50%");
			ivjScaleComboBox.addItem("75%");
			ivjScaleComboBox.addItem("100%");
			ivjScaleComboBox.addItem("125%");
			ivjScaleComboBox.addItem("150%");
			ivjScaleComboBox.setSelectedItem("25%");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScaleComboBox;
}
/**
 * Return the ScaleLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getScaleLabel() {
	if (ivjScaleLabel == null) {
		try {
			ivjScaleLabel = new javax.swing.JLabel();
			ivjScaleLabel.setName("ScaleLabel");
			ivjScaleLabel.setText("View scale: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScaleLabel;
}
/**
 * Return the StatusLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStatusLabel() {
	if (ivjStatusLabel == null) {
		try {
			ivjStatusLabel = new javax.swing.JLabel();
			ivjStatusLabel.setName("StatusLabel");
			ivjStatusLabel.setPreferredSize(new java.awt.Dimension(10, 14));
			ivjStatusLabel.setText("");
			ivjStatusLabel.setMinimumSize(new java.awt.Dimension(10, 14));
			ivjStatusLabel.setMaximumSize(new java.awt.Dimension(10, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusLabel;
}
/**
 * Return the StatusPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getStatusPanel() {
	if (ivjStatusPanel == null) {
		try {
			ivjStatusPanel = new javax.swing.JPanel();
			ivjStatusPanel.setName("StatusPanel");
			ivjStatusPanel.setPreferredSize(new java.awt.Dimension(18, 22));
			ivjStatusPanel.setLayout(new java.awt.GridBagLayout());
			ivjStatusPanel.setMinimumSize(new java.awt.Dimension(18, 22));

			java.awt.GridBagConstraints constraintsStatusLabel = new java.awt.GridBagConstraints();
			constraintsStatusLabel.gridx = 1; constraintsStatusLabel.gridy = 1;
			constraintsStatusLabel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsStatusLabel.weightx = 1.0;
			constraintsStatusLabel.weighty = 1.0;
			constraintsStatusLabel.insets = new java.awt.Insets(4, 14, 4, 4);
			getStatusPanel().add(getStatusLabel(), constraintsStatusLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusPanel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getUserScaleTextField() {
	if (ivjUserScaleTextField == null) {
		try {
			ivjUserScaleTextField = new javax.swing.JTextField();
			ivjUserScaleTextField.setName("UserScaleTextField");
			ivjUserScaleTextField.setPreferredSize(new java.awt.Dimension(50, 23));
			ivjUserScaleTextField.setMaximumSize(new java.awt.Dimension(50, 23));
			ivjUserScaleTextField.setMinimumSize(new java.awt.Dimension(50, 23));
			// user code begin {1}
			String scale = Double.toString(Page.getUserScale()*100);
			ivjUserScaleTextField.setText(scale);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUserScaleTextField;
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
	getPrintToolBarButton().addActionListener(ivjEventHandler);
	getScaleComboBox().addActionListener(ivjEventHandler);
	getPrintToolBarButton1().addActionListener(ivjEventHandler);
	this.addWindowListener(ivjEventHandler);
	getJToolBarButton1().addActionListener(ivjEventHandler);
	getUserScaleTextField().addActionListener(ivjEventHandler);
	getModeComboBox().addItemListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PrintPreviewFrame");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(846, 514);
		setContentPane(getJFrameContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jTextField1_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	try {
		double scale = Double.parseDouble(getUserScaleTextField().getText());
		Page.setUserScale(scale/100.0);
		
		loadingPreviews = false;
		new Thread() {
			public void run() {	
				loadPreview();
			}
		}.start();
		
	}
	catch (Exception e) {}
}
/**
 * Comment
 */
public void jToolBarButton1_ActionEvents() {
	loadingPreviews = false;
	dispose();
}
/**
 * Insert the method's description here.
 * Creation date: (13.5.2001 11:46:28)
 */
public void loadPreview() {

	getPreviewContainer().removeAll();
	getPreviewContainer().repaint();
	getPreviewContainer().getParent().getParent().validate();
	Thread.yield();
	
	System.gc();

	
	// render pages
	PageFormat pageFormat = Page.getPageFormat();
	if (pageFormat.getHeight()==0 ||
		pageFormat.getWidth()==0) {
		com.cosylab.vdct.Console.getInstance().println("Unable to determine default page size");
		return;
	}
		
	pageWidth = (int)(pageFormat.getWidth());
	pageHeight = (int)(pageFormat.getHeight());

	String str = getScaleComboBox().getSelectedItem().toString();
	if (str.endsWith("%"))
		str = str.substring(0, str.length() - 1);
		str = str.trim();

	int scale = 0;
	try {
		scale = Integer.parseInt(str);
	} catch (NumberFormatException ex) {
		return;
	}

	double ratioFix = 1; //getToolkit().getScreenResolution()/(double)72;	// so that 100% is real A4 on the screen
	
	final int w = (int)(ratioFix*pageWidth*scale/100);
	final int h = (int)(ratioFix*pageHeight*scale/100);

	int pageIndex = 0;
	int pages = target.getNumberOfPages();

	loadingPreviews = true;

	getStatusLabel().setText("Rendering...");

	try {

		Thread.sleep(1000);

		while (loadingPreviews && pageIndex<pages)
		{
			getStatusLabel().setText("Rendering page "+(pageIndex+1)+" of "+pages+"...");
			
			final BufferedImage img = 
				new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_INT_RGB);
			Graphics g = img.getGraphics();
			
			g.setColor(Color.white);
			g.fillRect(0, 0, pageWidth, pageHeight);
			
			if (target.getPrintable(pageIndex).print(g, target.getPageFormat(pageIndex), pageIndex)!=Printable.PAGE_EXISTS)
				break;

			System.gc();
			javax.swing.SwingUtilities.invokeAndWait( new Runnable() {
				public void run()
				{
					PagePreview pp = new PagePreview(w, h, img);
					getPreviewContainer().add(pp);
					getPreviewContainer().doLayout();
					getPreviewContainer().getParent().getParent().validate();
				}
			});
				
			pageIndex++;
			getStatusLabel().setText("Rendering page "+pageIndex+" of "+pages+"... Done.");
			Thread.yield();
		}
	}
	catch (PrinterException ex) {
		ex.printStackTrace();
		com.cosylab.vdct.Console.getInstance().println("Rendering error: "+ex);
	}
	catch (Exception ex2) {
		ex2.printStackTrace();
		com.cosylab.vdct.Console.getInstance().println("Exception occured while rendering: "+ex2);
	}

	getStatusLabel().setText("Rendering done.");

}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		PrintPreview aPrintPreviewFrame;
		aPrintPreviewFrame = new PrintPreview();
		aPrintPreviewFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aPrintPreviewFrame.setVisible(true);
		java.awt.Insets insets = aPrintPreviewFrame.getInsets();
		aPrintPreviewFrame.setSize(aPrintPreviewFrame.getWidth() + insets.left + insets.right, aPrintPreviewFrame.getHeight() + insets.top + insets.bottom);
		aPrintPreviewFrame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void modeComboBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	if (Page.getPrintMode()==getModeComboBox().getSelectedIndex())
		return;
	
	if (getModeComboBox().getSelectedIndex()!=Page.USER_SCALE)
	{
		getUserScaleTextField().setVisible(false);
		getPrecentLabel().setVisible(false);
	}
	else
	{
		getUserScaleTextField().setVisible(true);
		getPrecentLabel().setVisible(true);
	}
	Page.setPrintMode(getModeComboBox().getSelectedIndex());

	loadingPreviews = false;
	new Thread() {
		public void run() {	
			loadPreview();
		}
	}.start();
}
/**
 * Comment
 */
public void printPreviewFrame_WindowClosed(java.awt.event.WindowEvent windowEvent) {
	loadingPreviews = false;
}
/**
 * Comment
 */
public void printToolBarButton_ActionEvents() {
	// !!!
	com.cosylab.vdct.VisualDCT.getInstance().printMenuItem_ActionPerformed();
}
/**
 * Comment
 */
public void printToolBarButton1_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	PageFormat pf = Page.getPageFormat();
	//!!!
	com.cosylab.vdct.VisualDCT.getInstance().pageSetupMenuItem_ActionPerformed(actionEvent);

	// update if necessary
	if (pf!=Page.getPageFormat())
		new Thread() {
			public void run() {
				loadPreview();
			}
		}.start();

}
/**
 * Comment
 */
public void scaleComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	Thread runner = new Thread() {
		public void run() {
			String str = getScaleComboBox().getSelectedItem().toString();
			if (str.endsWith("%"))
				str = str.substring(0, str.length() - 1);
			str = str.trim();

			int scale = 0;
			try {
				scale = Integer.parseInt(str);
			} catch (NumberFormatException ex) {
				return;
			}

			double ratioFix = 1; //getToolkit().getScreenResolution()/(double)72;	// so that 100% is real A4 on the screen
			int w = (int) (ratioFix * pageWidth * scale / 100);
			int h = (int) (ratioFix * pageHeight * scale / 100);

			int pageIndex = 1;
			Component[] comps = getPreviewContainer().getComponents();
			for (int k = 0; k < comps.length; k++) {
				if (!(comps[k] instanceof PagePreview))
					continue;

				getStatusLabel().setText("Rendering page "+pageIndex+"...");
				Thread.yield();
					
				PagePreview pp = (PagePreview) comps[k];
				pp.setScaledSize(w, h);
				
				getStatusLabel().setText("Rendering page "+(pageIndex++)+"...Done.");
				Thread.yield();
			}

			getStatusLabel().setText("Rendering done.");

			getPreviewContainer().doLayout();
			getPreviewContainer().getParent().getParent().validate();

		}
	};

	runner.start();

}
}
