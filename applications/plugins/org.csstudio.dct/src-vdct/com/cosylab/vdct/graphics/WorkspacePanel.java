package com.cosylab.vdct.graphics;

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

import java.awt.event.*;
import java.awt.Graphics;


import com.cosylab.vdct.events.*;
import com.cosylab.vdct.events.commands.*;

/**
 * Insert the type's description here.
 * Creation date: (10.12.2000 12:31:58)
 * @author Matej Sekoranja
 */

public class WorkspacePanel extends PanelDecorator implements ComponentListener {
/**
 * VisualAge support
 * Creation date: (10.12.2000 12:34:33)
 */
public WorkspacePanel() {
    initialize();
}
/**
 * WorkspacePanel constructor comment.
 * @param component com.cosylab.vdct.graphics.VisualComponent
 */
public WorkspacePanel(VisualComponent component) {
    super(component);
}
    /**
     * Invoked when the component has been made invisible.
     */
public void componentHidden(ComponentEvent e) {}
    /**
     * Invoked when the component's position changes.
     */
public void componentMoved(ComponentEvent e) {}
    /**
     * Invoked when the component's size changes.
     */
public void componentResized(ComponentEvent e) {
    getComponent().resize(0, 0, getWidth(), getHeight());
}
    /**
     * Invoked when the component has been made visible.
     */
public void componentShown(ComponentEvent e) {
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 15:44:25)
 */
protected void initialize() {
    addComponentListener(this);
    MouseEventManager.getInstance().registerSubscreiber("WorkspacePanel", this);
    CommandManager.getInstance().addCommand("NullCommand", new NullCommand(this));
}

/**
 * Insert the method's description here.
 * Creation date: (10.12.2000 14:19:55)
 * @param g java.awt.Graphics
 */

protected void paintComponent(Graphics g) {
    getComponent().draw(g);
}
}
