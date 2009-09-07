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

import java.util.Iterator;
import java.util.TreeMap;
import javax.swing.JPanel;

/**
 * @author
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SystemTabPanel extends AboutTabPanel {

	private javax.swing.JPanel JPanel1 = null;
	private javax.swing.JPanel JPanel2 = null;

	private javax.swing.JLabel JLabelTitle = null;
	private javax.swing.JLabel JLabelDivider = null;

	private javax.swing.JLabel JLabelSystemOS = null;
	private javax.swing.JLabel JLabelSystemOSProperty = null;
	private javax.swing.JLabel JLabelSystemJava = null;
	private javax.swing.JLabel JLabelSystemJavaProperty = null;
	private javax.swing.JLabel JLabelSystemJavaClasspath = null;

	private javax.swing.JScrollPane JScrollPaneSystemJavaClasspathProperty = null;	

	private javax.swing.JTextArea JTextAreaSystemJavaClasspathProperty = null;
	/**
	 * Constructor for SystemTabPanel.
	 * @param tabModel
	 */
	public SystemTabPanel(AboutTabModel tabModel) {
		super(tabModel);
	}

	/**
	 * @see com.cosylab.gui.components.about.AboutTabPanel#initializePanel()
	 */
	protected void initializePanel() {
	
	try {

			setName("System");
			setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			
			add(getJPanel1(), constraintsJPanel1);

			java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
			constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 1;
			constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel2.weightx = 1.0;
			constraintsJPanel2.weighty = 1.0;
			constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
			add(getJPanel2(), constraintsJPanel2);

			
		} catch (Exception e) {
			e.printStackTrace();
		}

	
	
	
	}



/**
 * Insert the method's description here.
 * Creation date: (29.5.2002 17:54:41)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelDivider() {
		
	if (JLabelDivider == null) {
		try {
			JLabelDivider = new javax.swing.JLabel();
			JLabelTitle.setName("JLabelDivider");
			JLabelDivider.setText("  ");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	return JLabelDivider;
}
/**
 * Insert the method's description here.
 * Creation date: (29.5.2002 18:12:14)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelSystemJava() {

	if (JLabelSystemJava == null) {
		try {
			JLabelSystemJava = new javax.swing.JLabel();
			JLabelSystemJava.setText("Java Version :");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	return JLabelSystemJava;
}
/**
 * Insert the method's description here.
 * Creation date: (29.5.2002 18:12:14)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelSystemJavaClasspath() {

	if (JLabelSystemJavaClasspath == null) {
		try {
			JLabelSystemJavaClasspath = new javax.swing.JLabel();
			JLabelSystemJavaClasspath.setText("System Properties :");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	return JLabelSystemJavaClasspath;
}

/**
 * Insert the method's description here.
 * Creation date: (29.5.2002 18:12:14)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelSystemJavaProperty() {

	if (JLabelSystemJavaProperty == null) {
		try {
			JLabelSystemJavaProperty = new javax.swing.JLabel();
			JLabelSystemJavaProperty.setText("Java Version");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	return JLabelSystemJavaProperty;
}
/**
 * Insert the method's description here.
 * Creation date: (29.5.2002 18:12:14)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelSystemOS() {

	if (JLabelSystemOS == null) {
		try {
			JLabelSystemOS = new javax.swing.JLabel();
			JLabelSystemOS.setText("Operating System :");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	return JLabelSystemOS;
}
/**
 * Insert the method's description here.
 * Creation date: (29.5.2002 18:12:14)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelSystemOSProperty() {

	if (JLabelSystemOSProperty == null) {
		try {
			JLabelSystemOSProperty = new javax.swing.JLabel();
			/*java.util.Properties properties =*/ ((SystemTabModel)model).getSystemProperties();
			JLabelSystemOSProperty.setText("os");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	return JLabelSystemOSProperty;
}
/**
 * Insert the method's description here.
 * Creation date: (29.5.2002 17:54:41)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelTitle() {
		if (JLabelTitle == null) {
		try {
			JLabelTitle = new javax.swing.JLabel();
			JLabelTitle.setName("JLabelTitle");
			JLabelTitle.setFont(new java.awt.Font("dialog", 1, 18));
			JLabelTitle.setText("System Information");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	return JLabelTitle;
}
/**
 * Insert the method's description here.
 * Creation date: (29.5.2002 17:45:41)
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel1() {

		if (JPanel1 == null) {
		try {
			JPanel1 = new javax.swing.JPanel();
			JPanel1.setName("JPanel1");
			JPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelTitle = new java.awt.GridBagConstraints();
			constraintsJLabelTitle.gridx = 0; constraintsJLabelTitle.gridy = 0;
			constraintsJLabelTitle.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelTitle.insets = new java.awt.Insets(6, 6, 4, 4);
			getJPanel1().add(getJLabelTitle(), constraintsJLabelTitle);

			java.awt.GridBagConstraints constraintsJLabelDivider = new java.awt.GridBagConstraints();
			constraintsJLabelDivider.gridx = 1; constraintsJLabelDivider.gridy = 0;
			constraintsJLabelDivider.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJLabelDivider.weightx = 1.0;
			constraintsJLabelDivider.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabelDivider(), constraintsJLabelDivider);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	return JPanel1;
}
/**
 * Insert the method's description here.
 * Creation date: (29.5.2002 17:45:41)
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel2() {
		if (JPanel2 == null) {
		try {
			JPanel2 = new javax.swing.JPanel();
			JPanel2.setName("JPanel2");
			JPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsLabelSystemOS = new java.awt.GridBagConstraints();
			constraintsLabelSystemOS.gridx = 0; constraintsLabelSystemOS.gridy = 0;
			constraintsLabelSystemOS.anchor = java.awt.GridBagConstraints.EAST;
			constraintsLabelSystemOS.insets = new java.awt.Insets(6, 12, 6, 6);
			getJPanel2().add(getJLabelSystemOS(), constraintsLabelSystemOS);

			java.awt.GridBagConstraints constraintsLabelSystemOSProperty = new java.awt.GridBagConstraints();
			constraintsLabelSystemOSProperty.gridx = 1; constraintsLabelSystemOSProperty.gridy = 0;
			constraintsLabelSystemOSProperty.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLabelSystemOSProperty.insets = new java.awt.Insets(6, 4, 6, 4);
			getJPanel2().add(getJLabelSystemOSProperty(), constraintsLabelSystemOSProperty);

			java.awt.GridBagConstraints constraintsLabelSystemJava = new java.awt.GridBagConstraints();
			constraintsLabelSystemJava.gridx = 0; constraintsLabelSystemJava.gridy = 1;
			constraintsLabelSystemJava.anchor = java.awt.GridBagConstraints.EAST;
			constraintsLabelSystemJava.insets = new java.awt.Insets(6, 12, 6, 6);
			getJPanel2().add(getJLabelSystemJava(), constraintsLabelSystemJava);

			java.awt.GridBagConstraints constraintsLabelSystemJavaProperty = new java.awt.GridBagConstraints();
			constraintsLabelSystemJavaProperty.gridx = 1; constraintsLabelSystemJavaProperty.gridy = 1;
			constraintsLabelSystemJavaProperty.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLabelSystemJavaProperty.insets = new java.awt.Insets(6, 4, 6, 4);
			getJPanel2().add(getJLabelSystemJavaProperty(), constraintsLabelSystemJavaProperty);

			java.awt.GridBagConstraints constraintsLabelSystemJavaClasspath = new java.awt.GridBagConstraints();
			constraintsLabelSystemJavaClasspath.gridx = 0; constraintsLabelSystemJavaClasspath.gridy = 2;
			constraintsLabelSystemJavaClasspath.anchor = java.awt.GridBagConstraints.NORTHEAST;
			constraintsLabelSystemJavaClasspath.insets = new java.awt.Insets(6, 12, 6, 6);
			getJPanel2().add(getJLabelSystemJavaClasspath(), constraintsLabelSystemJavaClasspath);

			java.awt.GridBagConstraints constraintsJScrollPaneSystemJavaClasspath = new java.awt.GridBagConstraints();
			constraintsJScrollPaneSystemJavaClasspath.gridx = 1; constraintsJScrollPaneSystemJavaClasspath.gridy = 2;
			constraintsJScrollPaneSystemJavaClasspath.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneSystemJavaClasspath.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJScrollPaneSystemJavaClasspath.weightx = 1.0;
			constraintsJScrollPaneSystemJavaClasspath.weighty = 1.0;
			constraintsJScrollPaneSystemJavaClasspath.insets = new java.awt.Insets(6, 4, 12, 12);
			getJPanel2().add(getJScrollPaneSystemJavaClasspathProperty(), constraintsJScrollPaneSystemJavaClasspath);

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	return JPanel2;
}
/**
 * Insert the method's description here.
 * Creation date: (29.5.2002 18:12:14)
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPaneSystemJavaClasspathProperty() {

	if (JScrollPaneSystemJavaClasspathProperty == null) {
		try {
			JScrollPaneSystemJavaClasspathProperty = new javax.swing.JScrollPane();
			JScrollPaneSystemJavaClasspathProperty.setPreferredSize(new java.awt.Dimension(22, 80));
			JScrollPaneSystemJavaClasspathProperty.setMinimumSize(new java.awt.Dimension(22, 80));
			getJScrollPaneSystemJavaClasspathProperty().setViewportView(getJTextAreaSystemJavaProperties());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	return JScrollPaneSystemJavaClasspathProperty;
}

/**
 * Insert the method's description here.
 * Creation date: (29.5.2002 18:12:14)
 * @return javax.swing.JTextArea
 */
private javax.swing.JTextArea getJTextAreaSystemJavaProperties() {

	if (JTextAreaSystemJavaClasspathProperty == null) {
		try {
			JTextAreaSystemJavaClasspathProperty = new javax.swing.JTextArea();
			JTextAreaSystemJavaClasspathProperty.setBounds(0, 0, 160, 120);
			JTextAreaSystemJavaClasspathProperty.setMargin(new java.awt.Insets(1, 3, 1, 3));
			JTextAreaSystemJavaClasspathProperty.setEditable(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	return JTextAreaSystemJavaClasspathProperty;
}
		public JPanel getPanel() {
		return this;
	}

	/**
	 * @see com.cosylab.gui.components.about.AboutTabPanel#processData()
	 */
	protected void processData() {
		java.util.Properties properties = ((SystemTabModel)model).getSystemProperties();
		JLabelSystemJavaProperty.setText(properties.getProperty("java.runtime.name")+" "+properties.getProperty("java.runtime.version"));
		JLabelSystemOSProperty.setText(" "+properties.getProperty("os.name")+" "+properties.getProperty("os.version")+
									   " on "+properties.getProperty("os.arch"));
		
		TreeMap tree = new TreeMap(properties);
		Iterator i = tree.keySet().iterator();
		StringBuffer text = new StringBuffer();
		while (i.hasNext())
		{
			String key = i.next().toString();
			String value = properties.get(key).toString();
			text.append(key);	
			text.append("=");	
			text.append(value);	
			text.append("\n");	
		}
		
		JTextAreaSystemJavaClasspathProperty.setText(text.toString());
		JTextAreaSystemJavaClasspathProperty.setCaretPosition(0);
	}
}
