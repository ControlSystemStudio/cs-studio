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

import javax.swing.*;

/**
 * Insert the type's description here.
 * Creation date: (26.1.2001 15:10:46)
 * @author Matej Sekoranja
 */
public class CommentTextArea extends JTextArea implements java.awt.event.FocusListener {
    InspectableProperty property = null;
/**
 * CommentTextArea constructor comment.
 */
public CommentTextArea() {
    addFocusListener(this);
}
    /**
     * Invoked when a component gains the keyboard focus.
     */
public void focusGained(java.awt.event.FocusEvent e) {
    InspectorManager.getInstance().getActiveInspector().setHelp(property.getHelp());
}
    /**
     * Invoked when a component loses the keyboard focus.
     */
public void focusLost(java.awt.event.FocusEvent e) {
    property.setValue(getText());
    InspectorManager.getInstance().getActiveInspector().setHelp("");
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:11:22)
 * @param newProperty com.cosylab.vdct.inspector.InspectableProperty
 */
public void setProperty(InspectableProperty newProperty) {
    if ((newProperty==null) && (property!=null)) {
        property.setValue(getText());
        setText("");
        setEnabled(false);
    }

    property=newProperty;
    if (property==null) return;

    if (!isEnabled())
        setEnabled(true);
    if (isEditable()!=property.isEditable())
        setEditable(property.isEditable());

    setText(property.getValue());
}
}
