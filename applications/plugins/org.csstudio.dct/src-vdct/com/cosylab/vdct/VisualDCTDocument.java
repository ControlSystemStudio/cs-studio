package com.cosylab.vdct;

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

import java.net.URL;

/**
 * Insert the type's description here.
 * Creation date: (13.5.2001 20:29:29)
 * @author
 */
public class VisualDCTDocument extends javax.swing.JDialog {
    private javax.swing.JEditorPane ivjEditorPane = null;
    private javax.swing.JPanel ivjJDialogContentPane = null;
/**
 * VisualDCTDocument constructor comment.
 */
public VisualDCTDocument() {
    super();
    initialize();
}
/**
 * VisualDCTDocument constructor comment.
 * @param owner java.awt.Frame
 */
public VisualDCTDocument(java.awt.Frame owner, URL document) {
    super(owner);
    initialize();

    if (document==null)
    {
        setTitle("VisualDCT - [Invalid URL]");
        Console.getInstance().println("Failed to load document.");
    }

    setTitle("VisualDCT - "+document.getFile());
    try {
        getEditorPane().setPage(document);
    }
    catch (Exception e)
    {
        Console.getInstance().println("Failed to load: "+document.toString());
        e.printStackTrace();
    }
}
/**
 * Return the EditorPane property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JEditorPane getEditorPane() {
    if (ivjEditorPane == null) {
        try {
            ivjEditorPane = new javax.swing.JEditorPane();
            ivjEditorPane.setName("EditorPane");
            ivjEditorPane.setEditable(false);
            ivjEditorPane.setMargin(new java.awt.Insets(10, 10, 10, 10));
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjEditorPane;
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
            ivjJDialogContentPane.setLayout(new java.awt.BorderLayout());
            getJDialogContentPane().add(getEditorPane(), "Center");
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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
    try {
        // user code begin {1}
        // user code end
        setName("VisualDCTDocument");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(681, 622);
        setContentPane(getJDialogContentPane());
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
        VisualDCTDocument aVisualDCTDocument;
        aVisualDCTDocument = new VisualDCTDocument();
        aVisualDCTDocument.setModal(true);
        aVisualDCTDocument.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            };
        });
        aVisualDCTDocument.setVisible(true);
        java.awt.Insets insets = aVisualDCTDocument.getInsets();
        aVisualDCTDocument.setSize(aVisualDCTDocument.getWidth() + insets.left + insets.right, aVisualDCTDocument.getHeight() + insets.top + insets.bottom);
        aVisualDCTDocument.setVisible(true);
    } catch (Throwable exception) {
        System.err.println("Exception occurred in main() of javax.swing.JDialog");
        exception.printStackTrace(System.out);
    }
}
}
