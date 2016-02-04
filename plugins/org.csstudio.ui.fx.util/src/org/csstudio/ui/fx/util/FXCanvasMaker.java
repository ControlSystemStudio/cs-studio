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
 * <code>FXCanvasMaker</code> is an auxiliary class, which allows to add FX components to an SWT container.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class FXCanvasMaker extends FXViewPart {

    private static class FXParentComposite extends Composite {

        FXParentComposite(Composite parent) {
            super(parent, SWT.NONE);
            setLayoutData(new GridData(GridData.FILL_BOTH));
            setLayout(new FillLayout());
        }
    }

    private final Function<Composite, Scene> sceneSupplier;
    private final Composite parent;

    /**
     * Constructs a new FXCanvasMaker.
     *
     * @param parent the parent of the fx canvas
     * @param sceneSupplier the function that receives the given parent and returns the scene to display by this canvas
     */
    public FXCanvasMaker(Composite parent, Function<Composite, Scene> sceneSupplier) {
        this.sceneSupplier = sceneSupplier;
        this.parent = parent;
        createPartControl(new FXParentComposite(parent));
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
