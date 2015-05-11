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

import javax.swing.Icon;
import javax.swing.JPanel;

/**
 * @author
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ProgramTabPanel extends AboutTabPanel {

    private javax.swing.JPanel JPanelLogo = null;
    private javax.swing.JLabel JLabelLogo = null;

    private javax.swing.JPanel JPanelName = null;
    private javax.swing.JLabel JLabelProgramNameProperty = null;
    private javax.swing.JLabel JLabelProgramVersionProperty = null;
    private javax.swing.JLabel JLabelProgramBuildProperty = null;
    private javax.swing.JLabel JLabelProgramBuildDateProperty = null;

       private javax.swing.JPanel JPanelURLs = null;
       private javax.swing.JLabel JLabelProgramURL = null;
       private javax.swing.JLabel JLabelProgramURLProperty = null;
       private javax.swing.JLabel JLabelProgramURLDoc = null;
       private javax.swing.JLabel JLabelProgramURLDocProperty = null;

    private javax.swing.JPanel JPanelCopyright = null;
    private javax.swing.JLabel JLabelCopyright1 = null;
    private javax.swing.JLabel JLabelCopyright2 = null;
    private javax.swing.JLabel JLabelCopyright3 = null;
    private javax.swing.JLabel JLabelCopyright4 = null;
    private javax.swing.JLabel JLabelCopyright5 = null;

/**
 * Insert the method's description here.
 * Creation date: (8.7.2002 0:33:28)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelCopyright1() {
    if (JLabelCopyright1 == null) {
        try {
            JLabelCopyright1 = new javax.swing.JLabel();
            JLabelCopyright1.setName("JLabelCopyright1");
            JLabelCopyright1.setFont(new java.awt.Font("dialog", 1, 10));
            JLabelCopyright1.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            JLabelCopyright1.setText("Copyright © 2001 - 2005. All rights reserved.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JLabelCopyright1;
}

public String getTitle(){
    return ((ProgramTabModel)model).getProductName();
}

/**
 * Insert the method's description here.
 * Creation date: (8.7.2002 0:33:28)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelCopyright2() {
    if (JLabelCopyright2 == null) {
        try {
            JLabelCopyright2 = new javax.swing.JLabel();
            JLabelCopyright2.setName("JLabelCopyright2");
            JLabelCopyright2.setFont(new java.awt.Font("dialog", 1, 10));
            JLabelCopyright2.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            JLabelCopyright2.setText("Vendor not entered");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JLabelCopyright2;
}
/**
 * Insert the method's description here.
 * Creation date: (8.7.2002 0:33:28)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelCopyright3() {
    if (JLabelCopyright3 == null) {
        try {
            JLabelCopyright3 = new javax.swing.JLabel();
            JLabelCopyright3.setName("JLabelCopyright3");
            JLabelCopyright3.setFont(new java.awt.Font("dialog", 1, 10));
            JLabelCopyright3.setText("Vendor e-mail not entered");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JLabelCopyright3;
}
/**
 * Insert the method's description here.
 * Creation date: (8.7.2002 0:33:28)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelCopyright4() {
    if (JLabelCopyright4 == null) {
        try {
            JLabelCopyright4 = new javax.swing.JLabel();
            JLabelCopyright4.setName("JLabelCopyright4");
            JLabelCopyright4.setFont(new java.awt.Font("dialog", 1, 10));
            JLabelCopyright4.setText("Vendor URL not entered");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JLabelCopyright4;
}
/**
 * Insert the method's description here.
 * Creation date: (8.7.2002 0:33:28)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelCopyright5() {
    if (JLabelCopyright5 == null) {
        try {
            JLabelCopyright5 = new javax.swing.JLabel();
            JLabelCopyright5.setName("JLabelCopyright5");
            JLabelCopyright5.setFont(new java.awt.Font("dialog", 1, 10));
            JLabelCopyright5.setText("Use and duplication of this product or its parts is subject to the license.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JLabelCopyright5;
}
/**
 * Insert the method's description here.
 * Creation date: (7.7.2002 23:04:59)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelLogo() {
    if (JLabelLogo == null) {
        try {
            JLabelLogo = new javax.swing.JLabel();
            JLabelLogo.setName("JLabelLogo");
            JLabelLogo.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JLabelLogo;
}
/**
 * Insert the method's description here.
 * Creation date: (7.7.2002 23:29:55)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelProgramBuildDateProperty() {
    if (JLabelProgramBuildDateProperty == null) {
        try {
            JLabelProgramBuildDateProperty = new javax.swing.JLabel();
            JLabelProgramBuildDateProperty.setName("JLabelProgramBuildDateProperty");
            JLabelProgramBuildDateProperty.setText("Build Date");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JLabelProgramBuildDateProperty;
}
/**
 * Insert the method's description here.
 * Creation date: (7.7.2002 23:29:55)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelProgramBuildProperty() {
    if (JLabelProgramBuildProperty == null) {
        try {
            JLabelProgramBuildProperty = new javax.swing.JLabel();
            JLabelProgramBuildProperty.setName("JLabelProgramBuildProperty");
            JLabelProgramBuildProperty.setText("Build");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JLabelProgramBuildProperty;
}
/**
 * Insert the method's description here.
 * Creation date: (7.7.2002 23:29:55)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelProgramNameProperty() {
    if (JLabelProgramNameProperty == null) {
        try {
            JLabelProgramNameProperty = new javax.swing.JLabel();
            JLabelProgramNameProperty.setName("JLabelProgramNameProperty");
            JLabelProgramNameProperty.setFont(new java.awt.Font("dialog", 1, 18));
            JLabelProgramNameProperty.setText("Product Name");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JLabelProgramNameProperty;
}
/**
 * Insert the method's description here.
 * Creation date: (8.7.2002 0:19:00)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelProgramURL() {
    if (JLabelProgramURL == null) {
        try {
            JLabelProgramURL = new javax.swing.JLabel();
            JLabelProgramURL.setName("JLabelProgramURL");
            JLabelProgramURL.setText("Project web page :");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JLabelProgramURL;
}
/**
 * Insert the method's description here.
 * Creation date: (8.7.2002 0:19:00)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelProgramURLDoc() {
    if (JLabelProgramURLDoc == null) {
        try {
            JLabelProgramURLDoc = new javax.swing.JLabel();
            JLabelProgramURLDoc.setName("JLabelProgramURLDoc");
            JLabelProgramURLDoc.setText("Documentation web page :");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JLabelProgramURLDoc;
}
/**
 * Insert the method's description here.
 * Creation date: (8.7.2002 0:19:00)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelProgramURLDocProperty() {
    if (JLabelProgramURLDocProperty == null) {
        try {
            JLabelProgramURLDocProperty = new javax.swing.JLabel();
            JLabelProgramURLDocProperty.setName("JLabelProgramURLDocProperty");
            JLabelProgramURLDocProperty.setText("url");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JLabelProgramURLDocProperty;
}
/**
 * Insert the method's description here.
 * Creation date: (8.7.2002 0:19:00)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelProgramURLProperty() {
    if (JLabelProgramURLProperty == null) {
        try {
            JLabelProgramURLProperty = new javax.swing.JLabel();
            JLabelProgramURLProperty.setName("JLabelProgramURLProperty");
            JLabelProgramURLProperty.setText("url");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JLabelProgramURLProperty;
}
/**
 * Insert the method's description here.
 * Creation date: (7.7.2002 23:29:55)
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelProgramVersionProperty() {
    if (JLabelProgramVersionProperty == null) {
        try {
            JLabelProgramVersionProperty = new javax.swing.JLabel();
            JLabelProgramVersionProperty.setName("JLabelProgramVersionProperty");
            JLabelProgramVersionProperty.setFont(new java.awt.Font("dialog", 1, 18));
            JLabelProgramVersionProperty.setText("version");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JLabelProgramVersionProperty;
}
/**
 * Insert the method's description here.
 * Creation date: (7.7.2002 22:56:40)
 * @return javax.swing.JLabel
 */
private javax.swing.JPanel getJPanelCopyright() {
    if (JPanelCopyright == null) {
        try {
            JPanelCopyright = new javax.swing.JPanel();
            JPanelCopyright.setName("JPanelCopyright");
            JPanelCopyright.setLayout(new java.awt.GridBagLayout());

            java.awt.GridBagConstraints constraintsJLabelCopyright1 = new java.awt.GridBagConstraints();
            constraintsJLabelCopyright1.gridx = 0; constraintsJLabelCopyright1.gridy = 0;
            constraintsJLabelCopyright1.gridwidth = 4;
            constraintsJLabelCopyright1.insets = new java.awt.Insets(4, 4, 0, 4);
            getJPanelCopyright().add(getJLabelCopyright1(), constraintsJLabelCopyright1);

            java.awt.GridBagConstraints constraintsJLabelCopyright2 = new java.awt.GridBagConstraints();
            constraintsJLabelCopyright2.gridx = 0; constraintsJLabelCopyright2.gridy = 1;
            constraintsJLabelCopyright2.gridwidth = 3;
            constraintsJLabelCopyright2.insets = new java.awt.Insets(4, 4, 0, 4);
            getJPanelCopyright().add(getJLabelCopyright2(), constraintsJLabelCopyright2);

            java.awt.GridBagConstraints constraintsJLabelCopyright3 = new java.awt.GridBagConstraints();
            constraintsJLabelCopyright3.gridx = 0; constraintsJLabelCopyright3.gridy = 2;
            constraintsJLabelCopyright3.gridwidth = 3;
            constraintsJLabelCopyright3.insets = new java.awt.Insets(8, 4, 0, 4);
            getJPanelCopyright().add(getJLabelCopyright3(), constraintsJLabelCopyright3);

            java.awt.GridBagConstraints constraintsJLabelCopyright4 = new java.awt.GridBagConstraints();
            constraintsJLabelCopyright4.gridx = 0; constraintsJLabelCopyright4.gridy = 3;
            constraintsJLabelCopyright4.gridwidth = 5;
            constraintsJLabelCopyright4.insets = new java.awt.Insets(4, 4, 4, 4);
            getJPanelCopyright().add(getJLabelCopyright4(), constraintsJLabelCopyright4);

            java.awt.GridBagConstraints constraintsJLabelCopyright5 = new java.awt.GridBagConstraints();
            constraintsJLabelCopyright5.gridx = 0; constraintsJLabelCopyright5.gridy = 4;
            constraintsJLabelCopyright5.insets = new java.awt.Insets(4, 4, 4, 0);
            getJPanelCopyright().add(getJLabelCopyright5(), constraintsJLabelCopyright5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JPanelCopyright;
}
/**
 * Insert the method's description here.
 * Creation date: (7.7.2002 23:04:33)
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanelLogo() {
        if (JPanelLogo == null) {
        try {
            JPanelLogo = new javax.swing.JPanel();
            JPanelLogo.setName("JPanelLogo");
            JPanelLogo.setLayout(new java.awt.GridBagLayout());
            //JPanelLogo.setBackground(java.awt.Color.white);

            java.awt.GridBagConstraints constraintsJLabelLogo = new java.awt.GridBagConstraints();
            constraintsJLabelLogo.gridx = 0; constraintsJLabelLogo.gridy = 0;
            constraintsJLabelLogo.insets = new java.awt.Insets(4, 4, 4, 4);
            getJPanelLogo().add(getJLabelLogo(), constraintsJLabelLogo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JPanelLogo;
}
/**
 * Insert the method's description here.
 * Creation date: (7.7.2002 22:56:40)
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanelName() {
    if (JPanelName == null) {
        try {
            JPanelName = new javax.swing.JPanel();
            JPanelName.setName("JPanelName");
            JPanelName.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            JPanelName.setLayout(new java.awt.GridBagLayout());

            java.awt.GridBagConstraints constraintsJLabelProgramNameProperty = new java.awt.GridBagConstraints();
            constraintsJLabelProgramNameProperty.gridx = 0; constraintsJLabelProgramNameProperty.gridy = 0;
            constraintsJLabelProgramNameProperty.gridwidth = 2;
            constraintsJLabelProgramNameProperty.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJLabelProgramNameProperty.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelProgramNameProperty.insets = new java.awt.Insets(0, 6, 4, 4);
            getJPanelName().add(getJLabelProgramNameProperty(), constraintsJLabelProgramNameProperty);

            java.awt.GridBagConstraints constraintsJLabelProgramVersionProperty = new java.awt.GridBagConstraints();
            constraintsJLabelProgramVersionProperty.gridx = 2; constraintsJLabelProgramVersionProperty.gridy = 0;
            constraintsJLabelProgramVersionProperty.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJLabelProgramVersionProperty.weightx = 1.0;
            constraintsJLabelProgramVersionProperty.insets = new java.awt.Insets(0, 4, 4, 4);
            getJPanelName().add(getJLabelProgramVersionProperty(), constraintsJLabelProgramVersionProperty);

            java.awt.GridBagConstraints constraintsJLabelProgramBuildProperty = new java.awt.GridBagConstraints();
            constraintsJLabelProgramBuildProperty.gridx = 0; constraintsJLabelProgramBuildProperty.gridy = 1;
            constraintsJLabelProgramBuildProperty.insets = new java.awt.Insets(4, 6, 4, 0);
            getJPanelName().add(getJLabelProgramBuildProperty(), constraintsJLabelProgramBuildProperty);

            java.awt.GridBagConstraints constraintsJLabelProgramBuildDateProperty = new java.awt.GridBagConstraints();
            constraintsJLabelProgramBuildDateProperty.gridx = 1; constraintsJLabelProgramBuildDateProperty.gridy = 1;
            constraintsJLabelProgramBuildDateProperty.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelProgramBuildDateProperty.insets = new java.awt.Insets(4, 4, 4, 4);
            getJPanelName().add(getJLabelProgramBuildDateProperty(), constraintsJLabelProgramBuildDateProperty);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JPanelName;
}
/**
 * Insert the method's description here.
 * Creation date: (7.7.2002 22:56:40)
 * @return javax.swing.JLabel
 */
private javax.swing.JPanel getJPanelURLs() {
    if (JPanelURLs == null) {
        try {
            JPanelURLs = new javax.swing.JPanel();
            JPanelURLs.setName("JPanelURLs");
            JPanelURLs.setLayout(new java.awt.GridBagLayout());

            java.awt.GridBagConstraints constraintsJLabelProgramURLDoc = new java.awt.GridBagConstraints();
            constraintsJLabelProgramURLDoc.gridx = 0; constraintsJLabelProgramURLDoc.gridy = 1;
            constraintsJLabelProgramURLDoc.anchor = java.awt.GridBagConstraints.EAST;
            constraintsJLabelProgramURLDoc.insets = new java.awt.Insets(6, 12, 6, 6);
            getJPanelURLs().add(getJLabelProgramURLDoc(), constraintsJLabelProgramURLDoc);

            java.awt.GridBagConstraints constraintsJLabelProgramURLDocProperty = new java.awt.GridBagConstraints();
            constraintsJLabelProgramURLDocProperty.gridx = 1; constraintsJLabelProgramURLDocProperty.gridy = 1;
            constraintsJLabelProgramURLDocProperty.insets = new java.awt.Insets(6, 4, 6, 12);
            getJPanelURLs().add(getJLabelProgramURLDocProperty(), constraintsJLabelProgramURLDocProperty);

            java.awt.GridBagConstraints constraintsJLabelProgramURL = new java.awt.GridBagConstraints();
            constraintsJLabelProgramURL.gridx = 0; constraintsJLabelProgramURL.gridy = 0;
            constraintsJLabelProgramURL.anchor = java.awt.GridBagConstraints.EAST;
            constraintsJLabelProgramURL.insets = new java.awt.Insets(6, 12, 6, 6);
            getJPanelURLs().add(getJLabelProgramURL(), constraintsJLabelProgramURL);

            java.awt.GridBagConstraints constraintsJLabelProgramURLProperty = new java.awt.GridBagConstraints();
            constraintsJLabelProgramURLProperty.gridx = 1; constraintsJLabelProgramURLProperty.gridy = 0;
            constraintsJLabelProgramURLProperty.insets = new java.awt.Insets(6, 4, 6, 12);
            getJPanelURLs().add(getJLabelProgramURLProperty(), constraintsJLabelProgramURLProperty);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return JPanelURLs;
}
    /**
     * Constructor for ProgramTabPanel.
     * @param tabModel
     */
    public ProgramTabPanel(ProgramTabModel tabModel) {
        super(tabModel);
    }

    /**
     * @see com.cosylab.gui.components.about.AboutTabPanel#initializePanel()
     */
    protected void initializePanel() {

    try {
        setName("Program");
        setLayout(new java.awt.GridBagLayout());

        java.awt.GridBagConstraints constraintsJPanelLogo = new java.awt.GridBagConstraints();
        constraintsJPanelLogo.gridx = 1; constraintsJPanelLogo.gridy = 0;
        constraintsJPanelLogo.fill = java.awt.GridBagConstraints.BOTH;
        constraintsJPanelLogo.weightx = 1.0;
        constraintsJPanelLogo.weighty = 0;
        constraintsJPanelLogo.insets = new java.awt.Insets(4, 4, 4, 4);
        this.add(getJPanelLogo(), constraintsJPanelLogo);

        java.awt.GridBagConstraints constraintsJPanelName = new java.awt.GridBagConstraints();
        constraintsJPanelName.gridx = 0; constraintsJPanelName.gridy = 1;
        constraintsJPanelName.gridwidth = 2;
        constraintsJPanelName.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraintsJPanelName.anchor = java.awt.GridBagConstraints.NORTH;
        constraintsJPanelName.weightx = 1.0;
        constraintsJPanelName.insets = new java.awt.Insets(4, 4, 4, 4);
        this.add(getJPanelName(), constraintsJPanelName);

        java.awt.GridBagConstraints constraintsJPanelURLs = new java.awt.GridBagConstraints();
        constraintsJPanelURLs.gridx = 1; constraintsJPanelURLs.gridy = 2;
        constraintsJPanelURLs.fill = java.awt.GridBagConstraints.BOTH;
        constraintsJPanelURLs.weightx = 1.0;
        constraintsJPanelURLs.weighty = 1.0;
        constraintsJPanelURLs.insets = new java.awt.Insets(4, 4, 4, 4);
        this.add(getJPanelURLs(), constraintsJPanelURLs);

        java.awt.GridBagConstraints constraintsJPanelCopyright = new java.awt.GridBagConstraints();
        constraintsJPanelCopyright.gridx = 0; constraintsJPanelCopyright.gridy = 3;
        constraintsJPanelCopyright.gridwidth = 2;
        constraintsJPanelCopyright.fill = java.awt.GridBagConstraints.BOTH;
        constraintsJPanelCopyright.weightx = 1.0;
        constraintsJPanelCopyright.weighty = 1.0;
        constraintsJPanelCopyright.insets = new java.awt.Insets(4, 4, 4, 4);
        this.add(getJPanelCopyright(), constraintsJPanelCopyright);



    } catch (Exception e) {
            e.printStackTrace();
    }

    }

    /**
     * @see com.cosylab.gui.components.about.AboutTabPanel#processData()
     */
    protected void processData() {

    ProgramTabModel ptm = (ProgramTabModel)model;

    Icon icon = ptm.getProductLogoImage();
    if (icon == null) {
        remove(getJPanelLogo());
    } else {
        getJLabelLogo().setIcon(icon);
        getJPanelLogo().setBackground(java.awt.Color.white);
    }



    getJLabelProgramNameProperty().setText(ptm.getProductName());
    getJLabelProgramVersionProperty().setText(ptm.getProductVersion());

    if (isProgramPropertyEmpty(ptm.getProductBuild())) {
        getJPanelName().remove(getJLabelProgramBuildProperty());
    } else {
        getJLabelProgramBuildProperty().setText(ptm.getProductBuild());
    }
    if (isProgramPropertyEmpty(ptm.getProductBuildDate())) {
        getJPanelName().remove(getJLabelProgramBuildDateProperty());
    } else {
        getJLabelProgramBuildDateProperty().setText(ptm.getProductBuildDate());
    }


    if (ptm.getProductURL() == null) getJPanelURLs().remove(getJLabelProgramURL());
    else getJLabelProgramURLProperty().setText(ptm.getProductURL().toString());
    if (ptm.getProductDocsURL() == null) getJPanelURLs().remove(getJLabelProgramURLDoc());
    else getJLabelProgramURLDocProperty().setText(ptm.getProductDocsURL().toString());

    getJLabelCopyright3().setText(ptm.getVendorEmail());
    if (ptm.getVendorURL() != null) getJLabelCopyright4().setText(ptm.getVendorURL().toString());
    getJLabelCopyright2().setText(ptm.getVendor());

    }

    private boolean isProgramPropertyEmpty(String programProperty) {

    if (programProperty == null || programProperty.equals("")) return true;
    else return false;
}

    /**
     * @see com.cosylab.gui.components.about.AboutTab#getPanel()
     */
    public JPanel getPanel() {
        return this;
    }

}
