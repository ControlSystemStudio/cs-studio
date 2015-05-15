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

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

/**
 * Insert the type's description here.
 * Creation date: (7.1.2001 11:03:22)
 * @author Matej Sekoranja
 */
public class InspectorTableCellRenderer extends DefaultTableCellRenderer {
    private Color bgColor = null;
    private Color fgColor = null;
    private Color selectionbgColor = null;
    private Color selectionfgColor = null;
    private Color separatorbgColor = Color.black;
    private Color separatorfgColor = Color.white;
    private Color invalidColor = Color.red;
    private Color undefinedVisibility = new Color(128, 128, 128);
    private InspectorTableModel tableModel;

    private ImageIcon blankIcon = null;
    private ImageIcon eyeIcon = null;
    private ImageIcon noeyeIcon = null;

/**
 * InspectorTableCellRenderer constructor comment.
 */
public InspectorTableCellRenderer(JTable table, InspectorTableModel tableModel) {
    super();
    this.tableModel=tableModel;
    bgColor = table.getBackground();
    fgColor = table.getForeground();
    selectionbgColor = table.getSelectionBackground();
    selectionfgColor = table.getSelectionForeground();
    separatorbgColor = table.getGridColor();
    separatorfgColor = Color.white;
    setFont(table.getFont());
    setBorder(noFocusBorder);

    try
    {
        blankIcon = new ImageIcon(getClass().getResource("/images/blank.gif"));
        eyeIcon = new ImageIcon(getClass().getResource("/images/eye.gif"));
        noeyeIcon = new ImageIcon(getClass().getResource("/images/noeye.gif"));
    }
    catch (Exception e)
    {
        System.out.println("Failed to load icons!");
        System.out.println(e);
        System.out.println();
    }
}

/**
 * Insert the method's description here.
 * Creation date: (7.1.2001 11:08:56)
 * @return java.awt.Component
 * @param table javax.swing.JTable
 * @param value java.lang.Object
 * @param isSelected boolean
 * @param hasFocus boolean
 * @param row int
 * @param column int
 */
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    String str = null;
    if (value!=null)
        str = value.toString();
    InspectableProperty property = tableModel.getPropertyAt(row);

    setIcon(null);

    if (tableModel.getPropertyAt(row).isSepatator()) {
        super.setHorizontalAlignment(JLabel.CENTER);
        super.setBackground(separatorbgColor);
        super.setForeground(separatorfgColor);
        if (column==0)
        {
            switch (property.getVisibility())
            {
                case InspectableProperty.ALWAYS_VISIBLE:
                    setIcon(eyeIcon);
                    break;
                case InspectableProperty.NEVER_VISIBLE:
                    setIcon(noeyeIcon);
                    break;
            }
        }
    }
    else {
        super.setHorizontalAlignment(JLabel.LEFT);

        if (isSelected)
            super.setBackground(selectionbgColor);
        else
            super.setBackground(bgColor);

        if (column==0)
        {
            switch (property.getVisibility())
            {
                case InspectableProperty.NON_DEFAULT_VISIBLE:
                    setIcon(blankIcon);
                    break;
                case InspectableProperty.ALWAYS_VISIBLE:
                    setIcon(eyeIcon);
                    break;
                case InspectableProperty.NEVER_VISIBLE:
                    setIcon(noeyeIcon);
                    break;
                default:
                    super.setBackground(undefinedVisibility);
                    setIcon(blankIcon);
            }
        }

        if (column==2 && !property.isValid())
            super.setForeground(invalidColor);
        else if (isSelected)
            super.setForeground(selectionfgColor);
        else
            super.setForeground(fgColor);
    }
    setValue(str);

    setToolTipText(property.getToolTipText());

    return this;
}
}
