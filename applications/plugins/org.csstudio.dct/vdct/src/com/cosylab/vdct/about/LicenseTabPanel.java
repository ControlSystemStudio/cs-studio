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

import javax.swing.JPanel;

/**
 * @author
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LicenseTabPanel extends AboutTabPanel {


    private javax.swing.JPanel JPanel1 = null;
    private javax.swing.JPanel JPanel2 = null;

   	private javax.swing.JLabel JLabelTitle = null;
	private javax.swing.JLabel JLabelDivider = null;

	private javax.swing.JScrollPane JScrollPaneLicense = null;
	private javax.swing.JTextArea JTextAreaLicense = null;
	
	public String name = "License";
	
	/**
	 * Constructor for LicenseTab.
	 * @param tabModel
	 */
	public LicenseTabPanel(LicenseTabModel tabModel) {
		super(tabModel);
	}

	/**
	 * @see com.cosylab.gui.components.about.AboutTabPanel#initializePanel()
	 */
	protected void initializePanel() {
	
    try {

        setName("License");
        setLayout(new java.awt.GridBagLayout());

        java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
        constraintsJPanel1.gridx = 0;
        constraintsJPanel1.gridy = 0;
        constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
        constraintsJPanel1.weightx = 1.0;
        constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
        add(getJPanel1(), constraintsJPanel1);

        java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
        constraintsJPanel2.gridx = 0;
        constraintsJPanel2.gridy = 1;
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
	 * @see com.cosylab.gui.components.about.AboutTabPanel#processData()
	 */
	protected void processData() {

	getJTextAreaLicense().setText(((LicenseTabModel)model).getLicense());
	getJTextAreaLicense().setCaretPosition(0);
	
	}



	

/**
 * Insert the method's description here.
 * Creation date: (3.6.2002 12:35:54)
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
 * Creation date: (3.6.2002 12:35:54)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelTitle() {
	if (JLabelTitle == null) {
		try {
			JLabelTitle = new javax.swing.JLabel();
			JLabelTitle.setName("JLabelTitle");
			JLabelTitle.setFont(new java.awt.Font("dialog", 1, 18));
			JLabelTitle.setText("License");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	return JLabelTitle;
}
/**
 * Insert the method's description here.
 * Creation date: (3.6.2002 12:15:16)
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel1() {

    if (JPanel1 == null) {
        try {
            JPanel1 = new javax.swing.JPanel();

            JPanel1.setLayout(new java.awt.GridBagLayout());

            java.awt.GridBagConstraints constraintsJLabelTitle = new java.awt.GridBagConstraints();
            constraintsJLabelTitle.gridx = 0;
            constraintsJLabelTitle.gridy = 0;
            constraintsJLabelTitle.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelTitle.insets = new java.awt.Insets(6, 6, 4, 4);
            getJPanel1().add(getJLabelTitle(), constraintsJLabelTitle);

            java.awt.GridBagConstraints constraintsJLabelDivider = new java.awt.GridBagConstraints();
            constraintsJLabelDivider.gridx = 1;
            constraintsJLabelDivider.gridy = 0;
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
 * Creation date: (3.6.2002 12:15:16)
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel2() {
	if (JPanel2 == null) {
		try {
			JPanel2 = new javax.swing.JPanel();
			JPanel2.setName("JPanel2");
			JPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJScrollPaneLicense = new java.awt.GridBagConstraints();
			constraintsJScrollPaneLicense.gridx = 0; constraintsJScrollPaneLicense.gridy = 0;
			constraintsJScrollPaneLicense.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneLicense.weightx = 1.0;
			constraintsJScrollPaneLicense.weighty = 1.0;
			constraintsJScrollPaneLicense.insets = new java.awt.Insets(4, 12, 12, 12);
			getJPanel2().add(getJScrollPaneLicense(), constraintsJScrollPaneLicense);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	return JPanel2;
}
/**
 * Insert the method's description here.
 * Creation date: (3.6.2002 15:17:43)
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPaneLicense() {

    if (JScrollPaneLicense == null) {
        try {
            JScrollPaneLicense = new javax.swing.JScrollPane();
            JScrollPaneLicense.setName("JScrollPaneLicense");
            getJScrollPaneLicense().setViewportView(getJTextAreaLicense());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JScrollPaneLicense;
}
/**
 * Insert the method's description here.
 * Creation date: (3.6.2002 15:17:43)
 * @return javax.swing.JTextArea
 */
private javax.swing.JTextArea getJTextAreaLicense() {
	if (JTextAreaLicense == null) {
		try {
			JTextAreaLicense = new javax.swing.JTextArea();
			JTextAreaLicense.setName("JTextAreaLicense");
			JTextAreaLicense.setLineWrap(true);
			JTextAreaLicense.setWrapStyleWord(true);
			JTextAreaLicense.setFont(new java.awt.Font("monospaced", 0, 12));
			JTextAreaLicense.setBounds(0, 0, 160, 120);
			JTextAreaLicense.setMargin(new java.awt.Insets(1, 3, 1, 3));
			JTextAreaLicense.setEditable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	return JTextAreaLicense;
}
	/**
	 * @see com.cosylab.gui.components.about.AboutTab#getPanel()
	 */
	public JPanel getPanel() {
		
		
		return this;
	}

}
