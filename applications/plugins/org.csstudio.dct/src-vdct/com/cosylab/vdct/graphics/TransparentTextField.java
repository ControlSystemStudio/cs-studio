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

/**
 * Insert the type's description here.
 * Creation date: (2.5.2001 8:25:44)
 * @author
 */
public class TransparentTextField extends javax.swing.JTextField {
    private com.cosylab.vdct.graphics.objects.VisibleObject owner = null;
    private int offsetX;
    private int offsetY;
/**
 * TransparentTextField constructor comment.
 */
public TransparentTextField() {
    super();
    initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:47:36)
 * @param owner com.cosylab.vdct.graphics.objects.VisibleObject
 */
public TransparentTextField(com.cosylab.vdct.graphics.objects.VisibleObject owner, int offsetX, int offsetY)
{
    super();
    setOwner(owner);
    initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:51:02)
 * @return int
 */
public int getOffsetX() {
    return offsetX;
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:51:02)
 * @return int
 */
public int getOffsetY() {
    return offsetY;
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:47:11)
 * @return com.cosylab.vdct.graphics.objects.VisibleObject
 */
public com.cosylab.vdct.graphics.objects.VisibleObject getOwner() {
    return owner;
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:48:17)
 */
public int getX() {
    ViewState view = ViewState.getInstance();
    return (int)((owner.getX()+offsetX)*view.getScale()-view.getRx());
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:48:53)
 * @return int
 */
public int getY() {
    ViewState view = ViewState.getInstance();
    return (int)((owner.getY()+offsetY)*view.getScale()-view.getRy());
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {
    System.out.println("--------- UNCAUGHT EXCEPTION ---------");
    com.cosylab.vdct.Console.getInstance().println(exception);
    exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
    try {
        // user code begin {1}
        // user code end
        setName("TransparentTextField");
        setOpaque(false);
        setSize(100, 20);
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
        javax.swing.JFrame frame = new javax.swing.JFrame();
        TransparentTextField aTransparentTextField;
        aTransparentTextField = new TransparentTextField();
        frame.setContentPane(aTransparentTextField);
        frame.setSize(aTransparentTextField.getSize());
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            };
        });
        frame.setVisible(true);
        java.awt.Insets insets = frame.getInsets();
        frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
        frame.setVisible(true);
    } catch (Throwable exception) {
        System.err.println("Exception occurred in main() of com.cosylab.vdct.graphics.TransparentTextField");
        exception.printStackTrace(System.out);
    }
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:51:02)
 * @param newOffsetX int
 */
public void setOffsetX(int newOffsetX) {
    offsetX = newOffsetX;
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:51:02)
 * @param newOffsetY int
 */
public void setOffsetY(int newOffsetY) {
    offsetY = newOffsetY;
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 9:47:11)
 * @param newOwner com.cosylab.vdct.graphics.objects.VisibleObject
 */
public void setOwner(com.cosylab.vdct.graphics.objects.VisibleObject newOwner) {
    owner = newOwner;
}
}
