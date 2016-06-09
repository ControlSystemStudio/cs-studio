/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.ui.fx.util;

import org.eclipse.fx.ui.workbench3.FXViewPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.EditorPart;

import javafx.scene.Node;
import javafx.scene.Scene;

/**
 *
 * <code>FXEditorPart</code> is the base class for all Java FX based editors. This is a workaround which does not
 * require user to add the jfxswt.jar to the classpath. It delegates the constructions of the swt-fx bridge to the
 * {@link FXViewPart}, which is provided by the e(fx)clipse.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public abstract class FXEditorPart extends EditorPart {

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
        FXViewPart part = new FXViewPart() {
            @Override
            protected void setFxFocus() {
                FXEditorPart.this.setFxFocus();
            }

            @Override
            protected Scene createFxScene() {
                return FXEditorPart.this.createFxScene();
            }
        };
        part.createPartControl(parent);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        setFxFocus();
    }

    /**
     * Set the focus on the FX {@link Node} when this editor receives focus.
     */
    protected abstract void setFxFocus();

    /**
     * Construct the main scene that will be placed on the canvas of this editor.
     *
     * @return the scene
     */
    protected abstract Scene createFxScene();

}
