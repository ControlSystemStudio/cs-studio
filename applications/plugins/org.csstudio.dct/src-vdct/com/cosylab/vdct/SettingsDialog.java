/*
 * SettingsDialog2.java
 *
 * Created on August 13, 2004, 9:57 AM
 */

package com.cosylab.vdct;


import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MouseInputAdapter;

import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.graphics.objects.Group;
import com.cosylab.vdct.util.DoubleClickProxy;
import com.cosylab.vdct.util.UniversalFileFilter;

/**
 *
 * @author  ilist
 */
public class SettingsDialog extends javax.swing.JDialog {

    /** Creates new form SettingsDialog */
    public SettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initModel();
        loadSettings();
    }

    /**
     *
     */
    private void initModel() {
        jSliderDoubleClickSmudge.setModel(new DefaultBoundedRangeModel(){
            public int getMaximum() {
                return 10;
            }

            public int getValue() {
                return DoubleClickProxy.getAwt_multiclick_smudge();
            }

            public void setValue(int newValue) {
                DoubleClickProxy.setAwt_multiclick_smudge(newValue);
            }
        });

        jSliderDoubleClickSpeed.setModel(new DefaultBoundedRangeModel(){
                public int getMaximum() {
                    return 1000;
                }

                public int getValue() {
                    return DoubleClickProxy.getAwt_multiclick_time();
                }

                public void setValue(int newValue) {
                    DoubleClickProxy.setAwt_multiclick_time(newValue);
                }
        });

        DoubleClickProxy proxy = new DoubleClickProxy(new MouseInputAdapter() {
            public void mouseClicked(MouseEvent e) {
                jLabelTest.setText(new Integer(e.getClickCount()).toString());
            }
        });
        jPanelTesting.addMouseListener(proxy);
        jPanelTesting.addMouseMotionListener(proxy);

        jTextFieldGroupingSeparator.addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent keyEvent) {
                        if ((keyEvent.getKeyChar()!=java.awt.event.KeyEvent.VK_BACK_SPACE && jTextFieldGroupingSeparator.getText().length()>=1) &&
                            ((jTextFieldGroupingSeparator.getSelectedText()==null) || (jTextFieldGroupingSeparator.getSelectedText().length()==0)))
                            keyEvent.setKeyChar('\0');
                    }
        });

        jCheckBoxEnableGrouping.setModel(new JToggleButton.ToggleButtonModel(){
                    public void setSelected(boolean b) {
                        super.setSelected(b);

                        jLabelGroupingSeparator.setEnabled(b);
                        jTextFieldGroupingSeparator.setEnabled(b);
                    }
        });

        jCheckBoxDisplayNavigator.setModel(new JToggleButton.ToggleButtonModel(){
            public void setSelected(boolean b) {
                super.setSelected(b);
                jLabel13.setEnabled(b);
                jLabel14.setEnabled(b);
                jSpinnerNavigatorWidth.setEnabled(b);
                jSpinnerNavigatorHeight.setEnabled(b);
            }
    });

        jTextFieldLogo.getDocument().addDocumentListener(new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        // we won't ever get this with a PlainDocument
                    }

                    public void insertUpdate(DocumentEvent e) {
                        update(e);
                    }

                    public void removeUpdate(DocumentEvent e) {
                        update(e);
                    }

                    private void update(DocumentEvent e) {
                        File f = new File(jTextFieldLogo.getText());
                        boolean ok = f.canRead();
                        jLabelWarning.setVisible(!ok);

                        //System.out.println("<html><img src=\"file://"+f.getPath()+"\"></img>");
                        jPanelLegend.repaint();
                        /*if (ok) jLabelImage.setText("<html><img src=\"file://"+f.getPath()+"\"></img>");
                        else jLabelImage.setText("");*/
                    }
                });

        JPanel mypanel = new JPanel() {
            Image img = null;
            String currImage = "";
            int logoWidth, logoHeight;

            private Image getImage() {
                if (img == null || !currImage.equals(jTextFieldLogo.getText())) {
                                        img = Toolkit.getDefaultToolkit().getImage(jTextFieldLogo.getText());
                                        MediaTracker mediaTracker = new MediaTracker(this);
                                        mediaTracker.addImage(img, 0);
                                        try
                                        {
                                            mediaTracker.waitForID(0);
                                        }
                                        catch (InterruptedException ie)
                                        {
                                        }

                                        logoWidth = img.getWidth(null); logoHeight = img.getHeight(null);
                                        int maxLogo =Math.max(logoWidth,logoHeight);
                                        if (maxLogo > 200) {
                                            logoHeight = logoHeight * 200 / maxLogo;
                                            logoWidth = logoWidth * 200 / maxLogo;
                                        }
                }
                return img;
            }

            public void paint(Graphics g) {
                    g.drawImage(getImage(), 0, 0, logoWidth, logoHeight, null);
            }
        };
        mypanel.setMinimumSize(new java.awt.Dimension(128, 128));
        mypanel.setPreferredSize(new java.awt.Dimension(128, 128));
        jPanel7.add(mypanel);
    }

    /**
     *
     */
    private void loadSettings() {
        Settings s = Settings.getInstance();
        jSpinnerRecordNameLength.setValue(new Integer(s.getRecordLength()));

        jCheckBoxEnableGrouping.setSelected(s.getGrouping());
        if (Constants.GROUP_SEPARATOR=='\0')
                jTextFieldGroupingSeparator.setText("");
            else
                jTextFieldGroupingSeparator.setText(String.valueOf((char)Constants.GROUP_SEPARATOR));


        jCheckBoxGlobalMacros.setSelected(s.getGlobalMacros());
        jCheckBoxCapFast.setSelected(s.getHierarhicalNames());

        jSpinnerWidth.setValue(new Integer(s.getCanvasWidth()));
        jSpinnerHeight.setValue(new Integer(s.getCanvasHeight()));

        // double click is global
        DoubleClickProxy.update();
        jCheckBoxSilhouetteMoving.setSelected(s.getFastMove());

        jCheckBoxDefaultVisiblity.setSelected(s.isDefaultVisibility());
        jCheckBoxLinksVisibility.setSelected(s.isHideLinks());

        jCheckBoxWireCrossingAvoidance.setSelected(s.isWireCrossingAvoidance());

        //legend
        jTextFieldLogo.setText(s.getLegendLogo());
        switch (s.getLegendVisibility()) {
            case 0: jRadioButton1.setSelected(true); break;
            case 1: jRadioButton2.setSelected(true); break;
            case 2: jRadioButton3.setSelected(true); break;
        }
        switch (s.getLegendPosition()) {
            case 1: jToggleButtonTL.setSelected(true); break;
            case 2: jToggleButtonTR.setSelected(true); break;
            case 3: jToggleButtonBL.setSelected(true); break;
            case 4: jToggleButtonBR.setSelected(true); break;
        }
        jCheckBoxDisplayNavigator.setSelected(s.isLegendNavigatorVisibility());
        jSpinnerNavigatorWidth.setValue(new Integer(s.getLegendNavigatorWidth()));
        jSpinnerNavigatorHeight.setValue(new Integer(s.getLegendNavigatorHeight()));
    }

    private void saveSettings() {
        Settings s = Settings.getInstance();

        s.setRecordLength(((Number)jSpinnerRecordNameLength.getValue()).intValue());

        s.setGrouping(jCheckBoxEnableGrouping.isSelected());
        if (jTextFieldGroupingSeparator.getText().length()>0)
            s.setGroupSeparator(jTextFieldGroupingSeparator.getText().charAt(0));
        else
            s.setGroupSeparator('\0');

        s.setGlobalMacros(jCheckBoxGlobalMacros.isSelected());
        s.setHierarhicalNames(jCheckBoxCapFast.isSelected());

        s.setCanvasWidth(((Number)jSpinnerWidth.getValue()).intValue());
        s.setCanvasHeight(((Number)jSpinnerHeight.getValue()).intValue());
        DrawingSurface.getInstance().reset();

        // double click is global
        s.setDoubleClickSpeed(jSliderDoubleClickSpeed.getValue());
        s.setDoubleClickSmudge(jSliderDoubleClickSmudge.getValue());
        DoubleClickProxy.update();

        s.setFastMove(jCheckBoxSilhouetteMoving.isSelected());

        s.setDefaultVisibility(jCheckBoxDefaultVisiblity.isSelected());
        s.setHideLinks(jCheckBoxLinksVisibility.isSelected());
        Group.getRoot().updateFields();

        s.setWireCrossingAvoidance(jCheckBoxWireCrossingAvoidance.isSelected());

        // legend
        s.setLegendLogo(jTextFieldLogo.getText());
        s.setLegendVisibility(Integer.parseInt(buttonGroupVisibility.getSelection().getActionCommand()));
        s.setLegendPosition(Integer.parseInt(buttonGroupLocation.getSelection().getActionCommand()));
        s.setLegendNavigatorVisibility(jCheckBoxDisplayNavigator.isSelected());
        s.setLegendNavigatorWidth(((Number)jSpinnerNavigatorWidth.getValue()).intValue());
        s.setLegendNavigatorHeight(((Number)jSpinnerNavigatorHeight.getValue()).intValue());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupVisibility = new javax.swing.ButtonGroup();
        buttonGroupLocation = new javax.swing.ButtonGroup();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jTabbedPanel = new javax.swing.JTabbedPane();
        jPanelDatabase = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jSpinnerRecordNameLength = new javax.swing.JSpinner();
        jPanel3 = new javax.swing.JPanel();
        jCheckBoxEnableGrouping = new javax.swing.JCheckBox();
        jLabelGroupingSeparator = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldGroupingSeparator = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jCheckBoxGlobalMacros = new javax.swing.JCheckBox();
        jCheckBoxCapFast = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jSpinnerHeight = new javax.swing.JSpinner();
        jSpinnerWidth = new javax.swing.JSpinner();
        jPanelVisual = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jSliderDoubleClickSpeed = new javax.swing.JSlider();
        jLabel9 = new javax.swing.JLabel();
        jSliderDoubleClickSmudge = new javax.swing.JSlider();
        jLabel10 = new javax.swing.JLabel();
        jPanelTesting = new javax.swing.JPanel();
        jLabelTest = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jCheckBoxSilhouetteMoving = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        jCheckBoxDefaultVisiblity = new javax.swing.JCheckBox();
        jCheckBoxLinksVisibility = new javax.swing.JCheckBox();
        jPanel11 = new javax.swing.JPanel();
        jCheckBoxWireCrossingAvoidance = new javax.swing.JCheckBox();
        jPanelPrint = new javax.swing.JPanel();
        jPanelLegend = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jToggleButtonTL = new javax.swing.JToggleButton();
        jToggleButtonTR = new javax.swing.JToggleButton();
        jToggleButtonBL = new javax.swing.JToggleButton();
        jToggleButtonBR = new javax.swing.JToggleButton();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldLogo = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabelImage = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jCheckBoxDisplayNavigator = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        jSpinnerNavigatorHeight = new javax.swing.JSpinner();
        jSpinnerNavigatorWidth = new javax.swing.JSpinner();
        jLabel13 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jButtonBrowse = new javax.swing.JButton();
        jLabelWarning = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Settings Dialog");
        jButtonOk.setMnemonic('O');
        jButtonOk.setText("OK");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });

        jPanelButtons.add(jButtonOk);

        jButtonCancel.setMnemonic('C');
        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jPanelButtons.add(jButtonCancel);

        getContentPane().add(jPanelButtons, java.awt.BorderLayout.SOUTH);

        jTabbedPanel.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPanel.setAutoscrolls(true);
        jPanelDatabase.setLayout(new javax.swing.BoxLayout(jPanelDatabase, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(new javax.swing.border.TitledBorder("Record Name"));
        jLabel5.setDisplayedMnemonic('R');
        jLabel5.setLabelFor(jSpinnerRecordNameLength);
        jLabel5.setText("Record name length limit: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        jPanel1.add(jLabel5, gridBagConstraints);

        jSpinnerRecordNameLength.setMinimumSize(new java.awt.Dimension(60, 20));
        jSpinnerRecordNameLength.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 7, 7);
        jPanel1.add(jSpinnerRecordNameLength, gridBagConstraints);

        jPanelDatabase.add(jPanel1);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel3.setBorder(new javax.swing.border.TitledBorder("Grouping"));
        jCheckBoxEnableGrouping.setMnemonic('G');
        jCheckBoxEnableGrouping.setText("Enable Grouping");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        jPanel3.add(jCheckBoxEnableGrouping, gridBagConstraints);

        jLabelGroupingSeparator.setDisplayedMnemonic('s');
        jLabelGroupingSeparator.setLabelFor(jTextFieldGroupingSeparator);
        jLabelGroupingSeparator.setText("Grouping separator:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 8, 8);
        jPanel3.add(jLabelGroupingSeparator, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 11));
        jLabel7.setForeground(java.awt.Color.red);
        jLabel7.setText("Warning: Changing separator char will not reflect changes on preexisting names!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 8, 8);
        jPanel3.add(jLabel7, gridBagConstraints);

        jTextFieldGroupingSeparator.setColumns(3);
        jTextFieldGroupingSeparator.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldGroupingSeparator.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        jTextFieldGroupingSeparator.setMinimumSize(new java.awt.Dimension(35, 17));
        jTextFieldGroupingSeparator.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 8, 9);
        jPanel3.add(jTextFieldGroupingSeparator, gridBagConstraints);

        jPanelDatabase.add(jPanel3);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel4.setBorder(new javax.swing.border.TitledBorder("Generating Flat Database"));
        jCheckBoxGlobalMacros.setMnemonic('m');
        jCheckBoxGlobalMacros.setText("Enable global macros evaluation");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        jPanel4.add(jCheckBoxGlobalMacros, gridBagConstraints);

        jCheckBoxCapFast.setMnemonic('P');
        jCheckBoxCapFast.setText("Produce hierarhical names like CapFast");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 8, 8);
        jPanel4.add(jCheckBoxCapFast, gridBagConstraints);

        jPanelDatabase.add(jPanel4);

        jPanel9.setLayout(new java.awt.GridBagLayout());

        jPanel9.setBorder(new javax.swing.border.TitledBorder("Drawing surface size"));
        jLabel6.setText("Width:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        jPanel9.add(jLabel6, gridBagConstraints);

        jLabel11.setText("Height:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 8, 8);
        jPanel9.add(jLabel11, gridBagConstraints);

        jSpinnerHeight.setMinimumSize(new java.awt.Dimension(60, 20));
        jSpinnerHeight.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 8, 8);
        jPanel9.add(jSpinnerHeight, gridBagConstraints);

        jSpinnerWidth.setMinimumSize(new java.awt.Dimension(60, 20));
        jSpinnerWidth.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 8, 8);
        jPanel9.add(jSpinnerWidth, gridBagConstraints);

        jPanelDatabase.add(jPanel9);

        jTabbedPanel.addTab("Database", jPanelDatabase);

        jPanelVisual.setLayout(new javax.swing.BoxLayout(jPanelVisual, javax.swing.BoxLayout.Y_AXIS));

        jPanel5.setLayout(new java.awt.GridBagLayout());

        jPanel5.setBorder(new javax.swing.border.TitledBorder("Double Click"));
        jLabel8.setDisplayedMnemonic('s');
        jLabel8.setLabelFor(jSliderDoubleClickSpeed);
        jLabel8.setText("Double click speed:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 8);
        jPanel5.add(jLabel8, gridBagConstraints);

        jSliderDoubleClickSpeed.setMajorTickSpacing(100);
        jSliderDoubleClickSpeed.setMaximum(1000);
        jSliderDoubleClickSpeed.setPaintTicks(true);
        jSliderDoubleClickSpeed.setSnapToTicks(true);
        jSliderDoubleClickSpeed.setValue(0);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 8);
        jPanel5.add(jSliderDoubleClickSpeed, gridBagConstraints);

        jLabel9.setDisplayedMnemonic('m');
        jLabel9.setLabelFor(jSliderDoubleClickSmudge);
        jLabel9.setText("Double click smudge:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 8, 0, 8);
        jPanel5.add(jLabel9, gridBagConstraints);

        jSliderDoubleClickSmudge.setMajorTickSpacing(1);
        jSliderDoubleClickSmudge.setMaximum(10);
        jSliderDoubleClickSmudge.setPaintTicks(true);
        jSliderDoubleClickSmudge.setSnapToTicks(true);
        jSliderDoubleClickSmudge.setValue(0);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        jPanel5.add(jSliderDoubleClickSmudge, gridBagConstraints);

        jLabel10.setText("Test:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 8);
        jPanel5.add(jLabel10, gridBagConstraints);

        jPanelTesting.setLayout(new java.awt.BorderLayout());

        jPanelTesting.setBackground(java.awt.Color.white);
        jPanelTesting.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        jPanelTesting.setMinimumSize(new java.awt.Dimension(64, 64));
        jPanelTesting.setPreferredSize(new java.awt.Dimension(64, 64));
        jLabelTest.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTest.setText("1");
        jPanelTesting.add(jLabelTest, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 7, 7);
        jPanel5.add(jPanelTesting, gridBagConstraints);

        jPanelVisual.add(jPanel5);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        jPanel6.setBorder(new javax.swing.border.TitledBorder("Speed"));
        jCheckBoxSilhouetteMoving.setMnemonic('m');
        jCheckBoxSilhouetteMoving.setText("Silhouette when moving a record");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        jPanel6.add(jCheckBoxSilhouetteMoving, gridBagConstraints);

        jPanelVisual.add(jPanel6);

        jPanel8.setLayout(new java.awt.GridBagLayout());

        jPanel8.setBorder(new javax.swing.border.TitledBorder("Field Visibility"));
        jCheckBoxDefaultVisiblity.setSelected(true);
        jCheckBoxDefaultVisiblity.setText("Show value of fields when it is not default");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        jPanel8.add(jCheckBoxDefaultVisiblity, gridBagConstraints);

        jCheckBoxLinksVisibility.setText("Hide value of valid links");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 8, 8);
        jPanel8.add(jCheckBoxLinksVisibility, gridBagConstraints);

        jPanelVisual.add(jPanel8);

        jPanel11.setLayout(new java.awt.GridBagLayout());

        jPanel11.setBorder(new javax.swing.border.TitledBorder("Wires"));
        jCheckBoxWireCrossingAvoidance.setSelected(true);
        jCheckBoxWireCrossingAvoidance.setText("Crossing avoidance");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        jPanel11.add(jCheckBoxWireCrossingAvoidance, gridBagConstraints);

        jPanelVisual.add(jPanel11);

        jTabbedPanel.addTab("Visual", jPanelVisual);

        jPanelPrint.setLayout(new javax.swing.BoxLayout(jPanelPrint, javax.swing.BoxLayout.Y_AXIS));

        jPanelLegend.setLayout(new java.awt.GridBagLayout());

        jPanelLegend.setBorder(new javax.swing.border.TitledBorder("Legend Settings"));
        jLabel1.setDisplayedMnemonic('L');
        jLabel1.setText("Lab's logo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 4, 8);
        jPanelLegend.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Print legend:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 4, 8);
        jPanelLegend.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Legend location:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 4, 8);
        jPanelLegend.add(jLabel3, gridBagConstraints);

        jRadioButton1.setMnemonic('n');
        jRadioButton1.setText("never");
        buttonGroupVisibility.add(jRadioButton1);
        jRadioButton1.setActionCommand("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 8);
        jPanelLegend.add(jRadioButton1, gridBagConstraints);

        jRadioButton2.setMnemonic('o');
        jRadioButton2.setSelected(true);
        jRadioButton2.setText("only on one page");
        buttonGroupVisibility.add(jRadioButton2);
        jRadioButton2.setActionCommand("1");
        jRadioButton2.setDisplayedMnemonicIndex(8);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 8);
        jPanelLegend.add(jRadioButton2, gridBagConstraints);

        jRadioButton3.setMnemonic('e');
        jRadioButton3.setText("on every page");
        buttonGroupVisibility.add(jRadioButton3);
        jRadioButton3.setActionCommand("2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 8);
        jPanelLegend.add(jRadioButton3, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridLayout(2, 2));

        buttonGroupLocation.add(jToggleButtonTL);
        jToggleButtonTL.setActionCommand("1");
        jToggleButtonTL.setMaximumSize(new java.awt.Dimension(32, 32));
        jToggleButtonTL.setMinimumSize(new java.awt.Dimension(32, 32));
        jToggleButtonTL.setPreferredSize(new java.awt.Dimension(32, 32));
        jPanel2.add(jToggleButtonTL);

        buttonGroupLocation.add(jToggleButtonTR);
        jToggleButtonTR.setActionCommand("2");
        jToggleButtonTR.setMaximumSize(new java.awt.Dimension(32, 32));
        jToggleButtonTR.setMinimumSize(new java.awt.Dimension(32, 32));
        jToggleButtonTR.setPreferredSize(new java.awt.Dimension(32, 32));
        jPanel2.add(jToggleButtonTR);

        buttonGroupLocation.add(jToggleButtonBL);
        jToggleButtonBL.setActionCommand("3");
        jToggleButtonBL.setMaximumSize(new java.awt.Dimension(32, 32));
        jToggleButtonBL.setMinimumSize(new java.awt.Dimension(32, 32));
        jToggleButtonBL.setPreferredSize(new java.awt.Dimension(32, 32));
        jPanel2.add(jToggleButtonBL);

        jToggleButtonBR.setSelected(true);
        buttonGroupLocation.add(jToggleButtonBR);
        jToggleButtonBR.setActionCommand("4");
        jToggleButtonBR.setMaximumSize(new java.awt.Dimension(32, 32));
        jToggleButtonBR.setMinimumSize(new java.awt.Dimension(32, 32));
        jToggleButtonBR.setPreferredSize(new java.awt.Dimension(32, 32));
        jPanel2.add(jToggleButtonBR);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 20, 8, 8);
        jPanelLegend.add(jPanel2, gridBagConstraints);

        jLabel4.setText("Preview logo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 4, 8);
        jPanelLegend.add(jLabel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 8, 8);
        jPanelLegend.add(jTextFieldLogo, gridBagConstraints);

        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(new javax.swing.border.EtchedBorder());
        jPanel7.setMinimumSize(new java.awt.Dimension(128, 128));
        jPanel7.setPreferredSize(new java.awt.Dimension(128, 128));
        jPanel7.add(jLabelImage);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        jPanelLegend.add(jPanel7, gridBagConstraints);

        jLabel12.setText("Navigator:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 4, 8);
        jPanelLegend.add(jLabel12, gridBagConstraints);

        jCheckBoxDisplayNavigator.setSelected(true);
        jCheckBoxDisplayNavigator.setText("Display navigator");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 12);
        jPanelLegend.add(jCheckBoxDisplayNavigator, gridBagConstraints);

        jLabel14.setText("Height:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 7, 12);
        jPanelLegend.add(jLabel14, gridBagConstraints);

        jSpinnerNavigatorHeight.setMinimumSize(new java.awt.Dimension(60, 20));
        jSpinnerNavigatorHeight.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 8);
        jPanelLegend.add(jSpinnerNavigatorHeight, gridBagConstraints);

        jSpinnerNavigatorWidth.setMinimumSize(new java.awt.Dimension(60, 20));
        jSpinnerNavigatorWidth.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        jPanelLegend.add(jSpinnerNavigatorWidth, gridBagConstraints);

        jLabel13.setText("Width:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 12);
        jPanelLegend.add(jLabel13, gridBagConstraints);

        jPanel10.setLayout(new java.awt.GridBagLayout());

        jButtonBrowse.setMnemonic('B');
        jButtonBrowse.setText("Browse...");
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.0010;
        jPanel10.add(jButtonBrowse, gridBagConstraints);

        jLabelWarning.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabelWarning.setForeground(new java.awt.Color(255, 0, 0));
        jLabelWarning.setText("Warning: file doesn't exist");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel10.add(jLabelWarning, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 8);
        jPanelLegend.add(jPanel10, gridBagConstraints);

        jPanelPrint.add(jPanelLegend);

        jTabbedPanel.addTab("Print", jPanelPrint);

        getContentPane().add(jTabbedPanel, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
        JFileChooser chooser = VisualDCT.getInstance().getfileChooser();

        UniversalFileFilter filter = new UniversalFileFilter(new String[]{"jpg","gif","png"}, "Image files");
        chooser.resetChoosableFileFilters();
        chooser.addChoosableFileFilter(filter);

        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int retval = chooser.showOpenDialog(this);
        if(retval == JFileChooser.APPROVE_OPTION) {
            File theFile = chooser.getSelectedFile();
            jTextFieldLogo.setText(theFile.getPath());
        }
    }//GEN-LAST:event_jButtonBrowseActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        DoubleClickProxy.update();
        dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        saveSettings();
        dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new SettingsDialog(new javax.swing.JFrame(), true).setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupLocation;
    private javax.swing.ButtonGroup buttonGroupVisibility;
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JCheckBox jCheckBoxCapFast;
    private javax.swing.JCheckBox jCheckBoxDefaultVisiblity;
    private javax.swing.JCheckBox jCheckBoxDisplayNavigator;
    private javax.swing.JCheckBox jCheckBoxEnableGrouping;
    private javax.swing.JCheckBox jCheckBoxGlobalMacros;
    private javax.swing.JCheckBox jCheckBoxLinksVisibility;
    private javax.swing.JCheckBox jCheckBoxSilhouetteMoving;
    private javax.swing.JCheckBox jCheckBoxWireCrossingAvoidance;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelGroupingSeparator;
    private javax.swing.JLabel jLabelImage;
    private javax.swing.JLabel jLabelTest;
    private javax.swing.JLabel jLabelWarning;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelDatabase;
    private javax.swing.JPanel jPanelLegend;
    private javax.swing.JPanel jPanelPrint;
    private javax.swing.JPanel jPanelTesting;
    private javax.swing.JPanel jPanelVisual;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JSlider jSliderDoubleClickSmudge;
    private javax.swing.JSlider jSliderDoubleClickSpeed;
    private javax.swing.JSpinner jSpinnerHeight;
    private javax.swing.JSpinner jSpinnerNavigatorHeight;
    private javax.swing.JSpinner jSpinnerNavigatorWidth;
    private javax.swing.JSpinner jSpinnerRecordNameLength;
    private javax.swing.JSpinner jSpinnerWidth;
    private javax.swing.JTabbedPane jTabbedPanel;
    private javax.swing.JTextField jTextFieldGroupingSeparator;
    private javax.swing.JTextField jTextFieldLogo;
    private javax.swing.JToggleButton jToggleButtonBL;
    private javax.swing.JToggleButton jToggleButtonBR;
    private javax.swing.JToggleButton jToggleButtonTL;
    private javax.swing.JToggleButton jToggleButtonTR;
    // End of variables declaration//GEN-END:variables

}
