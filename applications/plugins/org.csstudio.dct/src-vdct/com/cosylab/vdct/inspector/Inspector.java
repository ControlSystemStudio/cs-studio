package com.cosylab.vdct.inspector;

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

import java.util.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.*;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.graphics.ViewState;
import com.cosylab.vdct.graphics.objects.VisibleObject;

/**
 * Insert the type's description here.
 * Creation date: (6.1.2001 22:41:16)
 * @author Matej Sekoranja
 */
public class Inspector extends JDialog implements InspectableObjectsListener, InspectorInterface, WindowListener, ChangeListener {
    private JLabel ivjCommentLabel = null;
    private CommentTextArea ivjCommentTextArea = null;
    private JCheckBox ivjFrozeCheckBox = null;
    private JLabel ivjHelpLabel = null;
    private JPanel ivjJDialogContentPane = null;
    private JTable ivjScrollPaneTable = null;
    private JScrollPane ivjTableScrollPane = null;
    private JScrollPane ivjCommentScrollPane = null;
    private JComboBox ivjObjectComboBox = null;
    private JButton centerOnScreen;

    // inspector components
    private InspectorTableModel tableModel;
    private Inspectable inspectedObject = null;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private Vector objs;
    private Inspectable toRemove = null;
    private JTabbedPane ivjViewTabbedPane = null;

    private ArrayList currentTabs = null;

    private static final String defaultString = "Default";

    private int mode = 0;

class IvjEventHandler implements java.awt.event.ItemListener {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
            if (e.getSource() == Inspector.this.getObjectComboBox())
                connEtoC1(e);
        };
    };
/**
 * Inspector constructor comment.
 */
public Inspector() {
    super();
    initialize();
}
/**
 * Inspector constructor comment.
 * @param owner java.awt.Frame
 */
public Inspector(java.awt.Frame owner) {
    super(owner);
    initialize();
}
/**
 * Inspector constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public Inspector(java.awt.Frame owner, boolean modal) {
    super(owner, modal);
    initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 22:31:51)
 */
private void cleanObjectList(Inspectable newObj) {
    if ((toRemove!=null) && (newObj!=toRemove)) {
        getObjectComboBox().removeItem(toRemove);
        toRemove = null;
    }
}
/**
 * Insert the method's description here.
 * Creation date: (12.1.2001 21:47:19)
 * @param validate boolean
 */
private void closeCellEditors(boolean validate) {
    TableCellEditor tce = null;
    if (getScrollPaneTable().isEditing()) {
        tce = getScrollPaneTable().getCellEditor();
    }
    if (tce!=null) {
        if (validate) {
            tce.stopCellEditing();
            getScrollPaneTable().editingStopped(null);
        } else {
            tce.cancelCellEditing();
            getScrollPaneTable().editingCanceled(null);
        }
    }
}
/**
 * connEtoC1:  (ObjectComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> Inspector.objectChanged()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ItemEvent arg1) {
    try {
        // user code begin {1}
        // user code end
        this.objectChanged();
        // user code begin {2}
        // user code end
    } catch (java.lang.Throwable ivjExc) {
        // user code begin {3}
        // user code end
        handleException(ivjExc);
    }
}
/**
 * Return the CommentLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCommentLabel() {
    if (ivjCommentLabel == null) {
        try {
            ivjCommentLabel = new javax.swing.JLabel();
            ivjCommentLabel.setName("CommentLabel");
            ivjCommentLabel.setText("Comment");
            ivjCommentLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            ivjCommentLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjCommentLabel;
}
/**
 * Return the CommentTextArea property value.
 * @return com.cosylab.vdct.inspector.CommentTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private CommentTextArea getCommentTextArea() {
    if (ivjCommentTextArea == null) {
        try {
            ivjCommentTextArea = new com.cosylab.vdct.inspector.CommentTextArea();
            ivjCommentTextArea.setName("CommentTextArea");
            //ivjCommentTextArea.setBorder(new javax.swing.border.EtchedBorder());
            // user code begin {1}
            if (ivjCommentTextArea.getFont()!=null)
                ivjCommentTextArea.setFont(new java.awt.Font("monospaced", ivjCommentTextArea.getFont().getStyle(), ivjCommentTextArea.getFont().getSize()));
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjCommentTextArea;
}
/**
 * Return the TableScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getCommentScrollPane() {
    if (ivjCommentScrollPane == null) {
        try {
            ivjCommentScrollPane = new javax.swing.JScrollPane();
            ivjCommentScrollPane.setName("TableScrollPane");
            ivjCommentScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            ivjCommentScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            getCommentScrollPane().setViewportView(getCommentTextArea());
            // user code begin {1}
            //ivjCommentTextArea.setMaximumSize(new java.awt.Dimension(10, 120));
            //ivjCommentTextArea.setMinimumSize(new java.awt.Dimension(10, 40));
            //ivjCommentTextArea.setPreferredSize(new java.awt.Dimension(10, 60));
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjCommentScrollPane;
}
/**
 * Return the FrozeCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getFrozeCheckBox() {
    if (ivjFrozeCheckBox == null) {
        try {
            ivjFrozeCheckBox = new javax.swing.JCheckBox();
            ivjFrozeCheckBox.setName("FrozeCheckBox");
            ivjFrozeCheckBox.setFont(new java.awt.Font("Arial", 1, 10));
            ivjFrozeCheckBox.setText("Frozen");
            ivjFrozeCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            ivjFrozeCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjFrozeCheckBox;
}
/**
 * Return the HelpLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getHelpLabel() {
    if (ivjHelpLabel == null) {
        try {
            ivjHelpLabel = new javax.swing.JLabel();
            ivjHelpLabel.setName("HelpLabel");
            ivjHelpLabel.setText("(help)");
            ivjHelpLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            ivjHelpLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjHelpLabel;
}

/**
 * TabbedPane listener
 */
public void stateChanged(ChangeEvent e)
{
    // we have direct mapping
    setMode(getViewTabbedPane().getSelectedIndex());
    // close all editors and refresh
    closeCellEditors(true);
    tableModel.setDataObject(tableModel.getDataObject());
}

/**
 * Return the ViewTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getViewTabbedPane() {
    if (ivjViewTabbedPane == null) {
        try {
            ivjViewTabbedPane = new javax.swing.JTabbedPane();
            ivjViewTabbedPane.setName("ViewTabbedPane");
            ivjViewTabbedPane.setTabPlacement(javax.swing.JTabbedPane.TOP);
            // user code begin {1}
            ivjViewTabbedPane.addTab(defaultString, null);
        //    ivjViewTabbedPane.addTab("Group", null);
        //    ivjViewTabbedPane.addTab("Alphabetical", null);
        //    ivjViewTabbedPane.addTab("DBD Order", null);

            //show only tabbs
            getViewTabbedPane().setMinimumSize(new Dimension(1, 24));
            getViewTabbedPane().setMaximumSize(new Dimension(1000, 24));
            getViewTabbedPane().setPreferredSize(new Dimension(250, 24));

            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjViewTabbedPane;
}

/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 21:43:40)
 * @return com.cosylab.vdct.inspector.Inspectable
 */
public Inspectable getInspectedObject() {
    return (Inspectable)getObjectComboBox().getSelectedItem();
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

            java.awt.GridBagConstraints constraintsObjectComboBox = new java.awt.GridBagConstraints();
            constraintsObjectComboBox.gridx = 0; constraintsObjectComboBox.gridy = 0;
            constraintsObjectComboBox.gridwidth = 2;
            constraintsObjectComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsObjectComboBox.weightx = 1.0;
            constraintsObjectComboBox.insets = new java.awt.Insets(8, 4, 4, 4);
            getJDialogContentPane().add(getObjectComboBox(), constraintsObjectComboBox);

            java.awt.GridBagConstraints constraintsViewTabbedPane = new java.awt.GridBagConstraints();
            constraintsViewTabbedPane.gridx = 0; constraintsViewTabbedPane.gridy = 1;
            constraintsViewTabbedPane.gridwidth = 2;
            constraintsViewTabbedPane.fill = java.awt.GridBagConstraints.BOTH;
            constraintsViewTabbedPane.weightx = 1.0;
            constraintsViewTabbedPane.weighty = 0.0;
            constraintsViewTabbedPane.insets = new java.awt.Insets(4, 4, 0, 4);
            getJDialogContentPane().add(getViewTabbedPane(), constraintsViewTabbedPane);

            java.awt.GridBagConstraints constraintsTableScrollPane = new java.awt.GridBagConstraints();
            constraintsTableScrollPane.gridx = 0; constraintsTableScrollPane.gridy = 2;
            constraintsTableScrollPane.gridwidth = 2;
            constraintsTableScrollPane.fill = java.awt.GridBagConstraints.BOTH;
            constraintsTableScrollPane.weightx = 1.0;
            constraintsTableScrollPane.weighty = 7.0;
            constraintsTableScrollPane.insets = new java.awt.Insets(0, 4, 4, 4);
            getJDialogContentPane().add(getTableScrollPane(), constraintsTableScrollPane);

            java.awt.GridBagConstraints constraintsCommentLabel = new java.awt.GridBagConstraints();
            constraintsCommentLabel.gridx = 0; constraintsCommentLabel.gridy = 3;
            constraintsCommentLabel.gridwidth = 2;
            constraintsCommentLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsCommentLabel.weightx = 1.0;
            constraintsCommentLabel.insets = new java.awt.Insets(8, 4, 4, 4);
            getJDialogContentPane().add(getCommentLabel(), constraintsCommentLabel);

            java.awt.GridBagConstraints constraintsCommentTextArea = new java.awt.GridBagConstraints();
            constraintsCommentTextArea.gridx = 0; constraintsCommentTextArea.gridy = 4;
            constraintsCommentTextArea.gridwidth = 2;
            constraintsCommentTextArea.fill = java.awt.GridBagConstraints.BOTH;
            constraintsCommentTextArea.weightx = 1.0;
            constraintsCommentTextArea.weighty = 1.0;
            constraintsCommentTextArea.insets = new java.awt.Insets(4, 4, 4, 4);
            getJDialogContentPane().add(getCommentScrollPane(), constraintsCommentTextArea);

            java.awt.GridBagConstraints constraintsHelpLabel = new java.awt.GridBagConstraints();
            constraintsHelpLabel.gridx = 0; constraintsHelpLabel.gridy = 5;
            constraintsHelpLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsHelpLabel.weightx = 1.0;
            constraintsHelpLabel.insets = new java.awt.Insets(8, 8, 4, 4);
            getJDialogContentPane().add(getHelpLabel(), constraintsHelpLabel);

            java.awt.GridBagConstraints constraintsFrozeCheckBox = new java.awt.GridBagConstraints();
            constraintsFrozeCheckBox.gridx = 1; constraintsFrozeCheckBox.gridy = 5;
            constraintsFrozeCheckBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsFrozeCheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
            getJDialogContentPane().add(getFrozeCheckBox(), constraintsFrozeCheckBox);

            getJDialogContentPane().add(getCenterOnScreenButton(), new GridBagConstraints(
                    0,6,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(4,4,4,4),0,0));

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

private JButton getCenterOnScreenButton() {
    if (centerOnScreen == null) {
        try {
            centerOnScreen = new JButton("Center object");
            centerOnScreen.setName("CenterButton");
            centerOnScreen.setFont(new Font("dialog", 0, 12));
            centerOnScreen.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (inspectedObject != null) {
                        if (inspectedObject instanceof VisibleObject) {
                            DrawingSurface.getInstance().centerObject((VisibleObject) inspectedObject);
                        }
                    }
                }

            });
        } catch (Throwable e) {
            handleException(e);
        }
    }

    return centerOnScreen;
}

/**
 * Return the ObjectComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getObjectComboBox() {
    if (ivjObjectComboBox == null) {
        try {
            ivjObjectComboBox = new javax.swing.JComboBox();
            ivjObjectComboBox.setName("ObjectComboBox");
            ivjObjectComboBox.setFont(new java.awt.Font("dialog", 0, 12));
            // user code begin {1}
            ivjObjectComboBox.setRenderer(new InspectorListCellRenderer());
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjObjectComboBox;
}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getScrollPaneTable() {
    if (ivjScrollPaneTable == null) {
        try {
            ivjScrollPaneTable = new javax.swing.JTable();
            ivjScrollPaneTable.setName("ScrollPaneTable");
            getTableScrollPane().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
            // this caused visibility icon problems when scrooling
            //getTableScrollPane().getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
            ivjScrollPaneTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
            ivjScrollPaneTable.setBackground(new java.awt.Color(204,204,204));
            ivjScrollPaneTable.setShowVerticalLines(true);
            ivjScrollPaneTable.setGridColor(java.awt.Color.black);
            ivjScrollPaneTable.setBounds(0, 0, 200, 200);
            ivjScrollPaneTable.setRowHeight(17);

            // enable clipboard actions
            new InspectorTableClipboardAdapter(ivjScrollPaneTable);
            ivjScrollPaneTable.setRowSelectionAllowed(true);
            // note: selection is possible only on name column
            ivjScrollPaneTable.setColumnSelectionAllowed(false);

            // user code begin {1}
            tableModel = new InspectorTableModel(this);
            ivjScrollPaneTable.setModel(tableModel);
            ivjScrollPaneTable.setTableHeader(null);
            ivjScrollPaneTable.setDefaultRenderer(String.class, new InspectorTableCellRenderer(ivjScrollPaneTable, tableModel));
            ivjScrollPaneTable.setDefaultEditor(String.class, new InspectorCellEditor(tableModel));

            ivjScrollPaneTable.addMouseListener(new MouseAdapter() {
                public void mouseReleased(MouseEvent evt) {
                        java.awt.Point pnt = evt.getPoint();
                        int popupAtRow = ivjScrollPaneTable.rowAtPoint(pnt);
                        int popupAtCol = -1;
                        if ((popupAtRow != -1) &&
                            ((popupAtCol=ivjScrollPaneTable.columnAtPoint(pnt)) != -1)) {
                                mouseEvent(evt, popupAtRow, popupAtCol);
                            }
                    }
            });

            // lock field name column width
            int width = 20;
            TableColumn col = ivjScrollPaneTable.getColumnModel().getColumn(0);
            col.setMinWidth(width);
            col.setMaxWidth(width);
            col.setPreferredWidth(width);
            // user code end

            // lock field name column width
//            int width = 117;
//            TableColumn col = ivjScrollPaneTable.getColumnModel().getColumn(0);
            width = 117;
            col = ivjScrollPaneTable.getColumnModel().getColumn(1);
            col.setMinWidth(width);
            col.setMaxWidth(width);
            col.setPreferredWidth(width);
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjScrollPaneTable;
}
/**
 * Return the TableScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getTableScrollPane() {
    if (ivjTableScrollPane == null) {
        try {
            ivjTableScrollPane = new javax.swing.JScrollPane();
            ivjTableScrollPane.setName("TableScrollPane");
            ivjTableScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            ivjTableScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            getTableScrollPane().setViewportView(getScrollPaneTable());
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjTableScrollPane;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {
    Console.getInstance().println("--------- UNCAUGHT EXCEPTION ---------");
    Console.getInstance().println(exception);
    exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
    // user code begin {1}
    getViewTabbedPane().addChangeListener(this);
    // user code end
    getObjectComboBox().addItemListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
    try {
        // user code begin {1}
        // user code end
        setName("Inspector");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(300, 550);
        setTitle("Inspector");
        setContentPane(getJDialogContentPane());
        initConnections();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
    // user code begin {2}

    updateObjectList();
    addWindowListener(this);
    getHelpLabel().setText("");
    // user code end
}
/**
 * Insert the method's description here.
 * Creation date: (17.4.2001 17:36:09)
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
public void inspectableObjectAdded(Inspectable object) {
    //System.out.println("Added: "+object.getName());
    if (!objs.contains(object)) {
        objs.addElement(object);
        getObjectComboBox().addItem(object);
    }
}
/**
 * Insert the method's description here.
 * Creation date: (17.4.2001 17:36:09)
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
public void inspectableObjectRemoved(Inspectable object) {
    //System.out.println("Removed: "+object.getName());
    if (objs.contains(object)) {
        if (object==inspectedObject)
            inspectObject(null, false);
        objs.removeElement(object);
        getObjectComboBox().removeItem(object);
    }
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 17:50:20)
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
public void inspectObject(Inspectable object) {
    inspectObject(object, true);

}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 17:50:20)
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
public void inspectObject(Inspectable object, boolean raise) {

    closeCellEditors(true);
    if (inspectedObject==object) return;
    inspectedObject=object;

    if (inspectedObject==null) {
        getHelpLabel().setText("No object selected");
        getCommentTextArea().setProperty(null);
        tableModel.setDataObject(null);
        setTitle("Inspector");
        if (raise) setVisible(true);
        return;
    }

    boolean isOutsider = !objs.contains(object);
    if (isOutsider)
        getObjectComboBox().addItem(object);

    // !!! object must be in list !!!
    getObjectComboBox().setSelectedItem(object);

    if (isOutsider)
        toRemove = object;

    //com.cosylab.vdct.Console.getInstance().println("Inspecting: "+object);

    setTitle("Inspector - "+inspectedObject.getName());
    tableModel.setDataObject(inspectedObject);

    getCommentTextArea().setProperty(inspectedObject.getCommentProperty());
    getCommentTextArea().setCaretPosition(0);

    // setup tabs
    initializeTabs(object.getModeNames());

    if (raise) setVisible(true);

    if (!updatingList)
    {
        if (object instanceof VisibleObject) {
            ViewState.getInstance().setAsHilited((VisibleObject) object);
            DrawingSurface.getInstance().repaint(true);
        }
    }
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 21:49:13)
 * @return boolean
 */
public boolean isFrozen() {
    return getFrozeCheckBox().isSelected();
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
    try {
        Inspector aInspector;
        aInspector = new Inspector();
        aInspector.setModal(true);
        aInspector.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            };
        });
        aInspector.setVisible(true);
        java.awt.Insets insets = aInspector.getInsets();
        aInspector.setSize(aInspector.getWidth() + insets.left + insets.right, aInspector.getHeight() + insets.top + insets.bottom);
        aInspector.setVisible(true);
    } catch (Throwable exception) {
        System.err.println("Exception occurred in main() of javax.swing.JDialog");
        exception.printStackTrace(System.out);
    }
}
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 16:46:45)
 */
private void objectChanged() {
    Inspectable selected = (Inspectable)getObjectComboBox().getSelectedItem();
    cleanObjectList(selected);
    inspectObject(selected, false);
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 17:50:20)
 */
public void reinitialize() {}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 17:50:20)
 */
public void updateObject() {
    Inspectable ins = inspectedObject;
    inspectedObject=null;
    inspectObject(ins);
    // name/type might change, do the repaint to force the change
    getObjectComboBox().repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:18:44)
 * @param help java.lang.String
 */
public void setHelp(java.lang.String help) {
    getHelpLabel().setText(help);
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 16:24:25)
 * @param state boolean
 */
public void setVisible(boolean state) {
    super.setVisible(state);
    if (state) {
        toFront();
        requestFocus();
    }
}
/**
 * Insert the method's description here.
 * Creation date: (5.5.2001 15:13:41)
 */
public void updateComment() {
    if (inspectedObject!=null)
        getCommentTextArea().setText(inspectedObject.getCommentProperty().getValue());
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 17:50:20)
 */
private boolean updatingList = false;
public void updateObjectList() {
    try
    {
        updatingList = true;

        JComboBox combo = getObjectComboBox();
        if (combo.getItemCount()>0) combo.removeAllItems();        // !!!
        objs = com.cosylab.vdct.DataProvider.getInstance().getInspectable();
        Enumeration e = objs.elements();
        while (e.hasMoreElements()) {
            combo.addItem(e.nextElement());
        }
        toRemove = null;
    }
    finally
    {
        updatingList = false;
    }
}
/**
 * Insert the method's description here.
 * Creation date: (5.5.2001 15:13:41)
 * @param property com.cosylab.vdct.inspector.InspectableProperty
 */
public void updateProperty(InspectableProperty property) {
    tableModel.updateProperty(property);
}
    /**
     * Invoked when the window is set to be the user's
     * active window, which means the window (or one of its
     * subcomponents) will receive keyboard events.
     */
public void windowActivated(java.awt.event.WindowEvent e) {
    InspectorManager.getInstance().fucusGained(this);
    if (inspectedObject != null) {
        if (inspectedObject instanceof VisibleObject) {
            ViewState.getInstance().setAsHilited((VisibleObject) inspectedObject);
            DrawingSurface.getInstance().repaint(true);
        }
    }
}
    /**
     * Invoked when a window has been closed as the result
     * of calling dispose on the window.
     */
public void windowClosed(java.awt.event.WindowEvent e) {}
    /**
     * Invoked when the user attempts to close the window
     * from the window's system menu.  If the program does not
     * explicitly hide or dispose the window while processing
     * this event, the window close operation will be cancelled.
     */
public void windowClosing(java.awt.event.WindowEvent e) {}
    /**
     * Invoked when a window is no longer the user's active
     * window, which means that keyboard events will no longer
     * be delivered to the window or its subcomponents.
     */
public void windowDeactivated(java.awt.event.WindowEvent e) {
    if (inspectedObject != null) {
        if (inspectedObject instanceof VisibleObject) {
            ViewState.getInstance().setAsHilited(null);
            DrawingSurface.getInstance().repaint(true);
        }
    }
}
    /**
     * Invoked when a window is changed from a minimized
     * to a normal state.
     */
public void windowDeiconified(java.awt.event.WindowEvent e) {}
    /**
     * Invoked when a window is changed from a normal to a
     * minimized state. For many platforms, a minimized window
     * is displayed as the icon specified in the window's
     * iconImage property.
     * @see Frame#setIconImage
     */
public void windowIconified(java.awt.event.WindowEvent e) {}
    /**
     * Invoked the first time a window is made visible.
     */
public void windowOpened(java.awt.event.WindowEvent e) {}

/**
 * Returns the mode.
 * @return int
 */
public int getMode()
{
    return mode;
}

/**
 * Sets the mode.
 * @param mode The mode to set
 */
public void setMode(int mode)
{
    this.mode = mode;
}

/**
 */
private void mouseEvent(MouseEvent event, int row, int col)
{
    boolean popupTrigger = event.getButton() == MouseEvent.BUTTON3;
    // event.isPopupTrigger() does not work on my instalation of Linux (RH7.2, GNOME 1.4)

    InspectableProperty property = (InspectableProperty)tableModel.getPropertyAt(row);

    // change visibility
    if (col==0)
    {
        if (property!=null && property instanceof ChangableVisibility)
        {
            int visibility = property.getVisibility();
            visibility = (visibility+1) % 3;                        /// !!! who said there are only three modes?!
            ((ChangableVisibility)property).setVisibility(visibility);

            // this does now work permanently (try to scroll), but after table update it works?
            getScrollPaneTable().tableChanged(new TableModelEvent(tableModel, row, row, col));
        }
    }
    else if (popupTrigger)
    {
        if (property!=null)
            property.popupEvent(getScrollPaneTable(), event.getX(), event.getY());
    }

}

/**
 */
private void initializeTabs(ArrayList tabs)
{
    if (tabs!=currentTabs)            // is object is using flyweight pattern, this is a good optimization
    {
        currentTabs = tabs;

        getViewTabbedPane().removeAll();
        if (currentTabs==null)
            getViewTabbedPane().addTab(defaultString, null);
        else
        {
            Iterator i = currentTabs.iterator();
            while (i.hasNext())
                getViewTabbedPane().addTab(i.next().toString(), null);
        }
        // TBD: set the last mode of the object (now it will always switch to the first one) !!!
    }
}

}

