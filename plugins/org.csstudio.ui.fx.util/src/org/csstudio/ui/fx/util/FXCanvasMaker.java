package org.csstudio.ui.fx.util;

import java.util.function.Supplier;

import org.eclipse.fx.ui.workbench3.FXViewPart;
import org.eclipse.swt.widgets.Composite;

import javafx.scene.Scene;

/**
 *
 * <code>FXCanvasMaker</code> is an auxiliary class, which allows to add FX components to an SWT container.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public abstract class FXCanvasMaker extends FXViewPart {

//    private final Supplier<Scene> sceneSupplier;
//
//    public FXCanvasMaker(Composite parent, Supplier<Scene> sceneSupplier) {
//        this.sceneSupplier = sceneSupplier;
//        createPartControl(parent);
//    }
//
//    @Override
//    protected Scene createFxScene() {
//        return sceneSupplier.get();
//    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.fx.ui.workbench3.FXViewPart#setFxFocus()
     */
    @Override
    protected void setFxFocus() {
    }
}
