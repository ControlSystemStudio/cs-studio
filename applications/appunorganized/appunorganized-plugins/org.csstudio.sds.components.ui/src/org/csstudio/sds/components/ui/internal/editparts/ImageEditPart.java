/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.sds.components.ui.internal.editparts;


import org.csstudio.sds.components.model.ImageModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableImageFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * EditPart controller for the image widget.
 *
 * @author jbercic, Xihui Chen
 *
 */
public final class ImageEditPart extends AbstractWidgetEditPart {



    /**
     * Returns the casted model. This is just for convenience.
     *
     * @return the casted {@link ImageModel}
     */
    protected ImageModel getCastedModel() {
        return (ImageModel) getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        ImageModel model = getCastedModel();
        // create AND initialize the view properly
        final RefreshableImageFigure figure = new RefreshableImageFigure();

        figure.setFilePath(model.getFilename());
        figure.setTopCrop(model.getTopCrop());
        figure.setBottomCrop(model.getBottomCrop());
        figure.setLeftCrop(model.getLeftCrop());
        figure.setRightCrop(model.getRightCrop());
        figure.setStretch(model.getStretch());
        figure.setAutoSize(model.isAutoSize());
        figure.setStopAnimation(model.isStopAnimation());
        return figure;
    }

    @Override
    public void activate() {
        super.activate();
        if(((ImageModel)getModel()).isVisible() && !((ImageModel)getModel()).isStopAnimation())
            ((RefreshableImageFigure) getFigure()).startAnimation();
    }
    /**
     * Register change handlers for the four crop properties.
     */
    protected void registerCropPropertyHandlers() {
        // top
        IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableImageFigure imageFigure = (RefreshableImageFigure) figure;
                imageFigure.setTopCrop((Integer)newValue);
                autoSizeWidget(imageFigure);
                return true;
            }
        };
        setPropertyChangeHandler(ImageModel.PROP_TOPCROP, handle);

        // bottom
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableImageFigure imageFigure = (RefreshableImageFigure) figure;
                imageFigure.setBottomCrop((Integer)newValue);
                autoSizeWidget(imageFigure);
                return true;
            }
        };
        setPropertyChangeHandler(ImageModel.PROP_BOTTOMCROP, handle);

        // left
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableImageFigure imageFigure = (RefreshableImageFigure) figure;
                imageFigure.setLeftCrop((Integer)newValue);
                autoSizeWidget(imageFigure);
                return true;
            }
        };
        setPropertyChangeHandler(ImageModel.PROP_LEFTCROP, handle);

        // right
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableImageFigure imageFigure = (RefreshableImageFigure) figure;
                imageFigure.setRightCrop((Integer)newValue);
                autoSizeWidget(imageFigure);
                return true;
            }
        };
        setPropertyChangeHandler(ImageModel.PROP_RIGHTCROP, handle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        // changes to the filename property
        IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableImageFigure imageFigure = (RefreshableImageFigure) figure;
                imageFigure.setFilePath((IPath)newValue);
                autoSizeWidget(imageFigure);
                return true;
            }


        };
        setPropertyChangeHandler(ImageModel.PROP_FILENAME, handle);

        // changes to the stretch property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableImageFigure imageFigure = (RefreshableImageFigure) figure;
                imageFigure.setStretch((Boolean)newValue);
                autoSizeWidget(imageFigure);
                return true;
            }
        };
        setPropertyChangeHandler(ImageModel.PROP_STRETCH, handle);

        // changes to the autosize property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableImageFigure imageFigure = (RefreshableImageFigure) figure;
                imageFigure.setAutoSize((Boolean)newValue);
                ImageModel model = (ImageModel)getModel();
                Dimension d = imageFigure.getAutoSizedDimension();
                if((Boolean) newValue && !model.getStretch() && d != null)
                    model.setSize(d.width, d.height);
                return true;
            }
        };
        setPropertyChangeHandler(ImageModel.PROP_AUTOSIZE, handle);


        // changes to the stop animation property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableImageFigure imageFigure = (RefreshableImageFigure) figure;
                imageFigure.setStopAnimation((Boolean)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(ImageModel.PROP_STOP_ANIMATION, handle);

        // changes to the border width property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableImageFigure imageFigure = (RefreshableImageFigure) figure;
                imageFigure.resizeImage();
                autoSizeWidget(imageFigure);
                return true;
            }
        };
        setPropertyChangeHandler(ImageModel.PROP_BORDER_WIDTH, handle);
        setPropertyChangeHandler(ImageModel.PROP_BORDER_STYLE, handle);

        //size change handlers - so we can stretch accordingly
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableImageFigure imageFigure = (RefreshableImageFigure) figure;
                imageFigure.resizeImage();
                autoSizeWidget(imageFigure);
                return true;
            }
        };
        setPropertyChangeHandler(ImageModel.PROP_HEIGHT, handle);
        setPropertyChangeHandler(ImageModel.PROP_WIDTH, handle);

        registerCropPropertyHandlers();
    }



    @Override
    public void deactivate() {
        super.deactivate();
        ((RefreshableImageFigure) getFigure()).stopAnimation();
        ((RefreshableImageFigure) getFigure()).dispose();
    }

    private void autoSizeWidget(RefreshableImageFigure imageFigure) {
        ImageModel model = (ImageModel)getModel();
        imageFigure.setAutoSize(model.isAutoSize());
        Dimension d = imageFigure.getAutoSizedDimension();
        if(model.isAutoSize() && !model.getStretch() && d != null)
            model.setSize(d.width, d.height);
    }

}
