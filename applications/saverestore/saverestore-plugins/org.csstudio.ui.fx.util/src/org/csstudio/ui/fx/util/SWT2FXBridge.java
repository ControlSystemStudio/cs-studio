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

import java.util.function.Function;

import org.eclipse.fx.ui.workbench3.FXViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import javafx.scene.Scene;

/**
 *
 * <code>SWT2FXBridge</code> is an auxiliary class, which allows to add FX components to an SWT container.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
final class SWT2FXBridge {

    /**
     * Constructs a new bridge between the parent and the scene provided by the sceneSupplier.
     *
     * @param parent the parent of the fx canvas
     * @param sceneSupplier the function that receives a parent and returns the scene to add to the parent
     */
    static void createFXBridge(Composite parent, Function<Composite, Scene> sceneSupplier) {
        new FXCanvas(parent, sceneSupplier);
    }

    private static class FXParentComposite extends Composite {

        FXParentComposite(Composite parent) {
            super(parent, SWT.NONE);
            setLayoutData(new GridData(GridData.FILL_BOTH));
            setLayout(new FillLayout());
        }
    }

    private static class FXCanvas extends FXViewPart {
        private final Function<Composite, Scene> sceneSupplier;
        private final Composite parent;

        /**
         * Constructs a new FXCanvas.
         *
         * @param parent the parent of the fx canvas
         * @param sceneSupplier the function that receives a parent and returns the scene to display by this canvas
         */
        private FXCanvas(Composite parent, Function<Composite, Scene> sceneSupplier) {
            this.sceneSupplier = sceneSupplier;
            this.parent = new FXParentComposite(parent);
            createPartControl(this.parent);
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.fx.ui.workbench3.FXViewPart#createFxScene()
         */
        @Override
        protected Scene createFxScene() {
            return sceneSupplier.apply(parent);
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.fx.ui.workbench3.FXViewPart#setFxFocus()
         */
        @Override
        protected void setFxFocus() {
        }
    }
}
