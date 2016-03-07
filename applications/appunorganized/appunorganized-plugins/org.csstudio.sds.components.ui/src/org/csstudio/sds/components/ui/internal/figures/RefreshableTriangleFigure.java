/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.CrossedOutAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.RhombusAdapter;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A Triangle figure.
 * @author Kai Meyer
 *
 */
public final class RefreshableTriangleFigure extends RectangleFigure implements IAdaptable{
    /**
     * The fill grade (0 - 100%).
     */
    private double _fillGrade = 50;

    /**
     * The rotation angle (0 - 360).
     */
    private double _rotationAngle = 0;

    /**
     * The transparent state of the background.
     */
    private boolean _transparent = false;

    //private InternalTriangle _internalFigure;

    private final PolylineDecoration _polylineDecoration;

    /**
     * A border adapter, which covers all border handlings.
     */
    private IBorderEquippedWidget _borderAdapter;

    private CrossedOutAdapter _crossedOutAdapter;

    private RhombusAdapter _rhombusAdapter;


    public RefreshableTriangleFigure() {
//        _internalFigure = new InternalTriangle();
//        this.add(_internalFigure, BorderLayout.CENTER);

        Rectangle figureBounds = getBounds().getCopy();
        figureBounds.crop(this.getInsets());

        _polylineDecoration = new PolylineDecoration();
        PointList points = new PointList();
        points.addPoint(-1, -1);
        points.addPoint(1, 0);
        points.addPoint(-1, 1);
        //points.addPoint(-1, 1);
        points.addPoint(-1, -1);
        _polylineDecoration.setTemplate(points);
        //_polylineDecoration.setTemplate(PolylineDecoration.TRIANGLE_TIP);
        _polylineDecoration.setRotation(Math.toRadians(_rotationAngle));
        this.add(_polylineDecoration, BorderLayout.CENTER);
        addFigureListener(new FigureListener() {
            public void figureMoved(final IFigure source) {
                refreshConstraints();
            }
        });
        refreshConstraints();
    }

    private void refreshConstraints() {
        System.out.println("RefreshableTriangleFigure.refreshConstraints()");
        Rectangle figureBounds = getBounds().getCopy();
        figureBounds.crop(this.getInsets());
        System.out.println("   "+figureBounds);
        Point point = new Point(figureBounds.x+figureBounds.width/2, figureBounds.y+figureBounds.height/2);
        System.out.println("   Location: "+point);
        _polylineDecoration.setLocation(point);
        //_polylineDecoration.setSize(figureBounds.width, figureBounds.height);
        Point refPoint = new Point(figureBounds.x+figureBounds.width/2, figureBounds.y+figureBounds.height/2);
        System.out.println("   ReferencePoint: "+refPoint);
        _polylineDecoration.setRotation(Math.toRadians(_rotationAngle));
        _polylineDecoration.setReferencePoint(refPoint);
        _polylineDecoration.setScale(20, 20);

        Point newPoint = refPoint.getCopy();
        Point pt = Point.SINGLETON;
        pt.setLocation(newPoint);
        pt.negate();
        System.out.println("   Negated RefPoint: "+pt);
        pt.translate(point);
        System.out.println("   Translated RefPoint: "+pt);
    }

    @Override
    protected boolean useLocalCoordinates() {
        return false;
    }

    @Override
    public void paintFigure(final Graphics graphics) {
    }

    /**
     * Sets the fill grade.
     *
     * @param fill
     *            the fill grade.
     */
    public void setFillLevel(final double fill) {
        _fillGrade = fill;
    }

    /**
     * Gets the fill grade.
     *
     * @return the fill grade
     */
    public double getFill() {
        return _fillGrade;
    }

    /**
     * Sets the rotation angle.
     *
     * @param rotationAngle
     *            the rotation angle.
     */
    public void setRotationAngle(final double rotationAngle) {
        _rotationAngle = rotationAngle;
        _polylineDecoration.setRotation(Math.toRadians(_rotationAngle));
    }

    /**
     * Gets the rotation angle.
     *
     * @return the rotation angle
     */
    public double getRotationAngle() {
        return _rotationAngle;
    }

    /**
     * Sets the transparent state of the background.
     *
     * @param transparent
     *            the transparent state.
     */
    public void setTransparent(final boolean transparent) {
        _transparent = transparent;
    }

    /**
     * Gets the transparent state of the background.
     *
     * @return the transparent state of the background
     */
    public boolean getTransparent() {
        return _transparent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        if (adapter == IBorderEquippedWidget.class) {
            if(_borderAdapter==null) {
                _borderAdapter = new BorderAdapter(this);
            }
            return _borderAdapter;
        } else if (adapter == ICrossedFigure.class) {
            if (_crossedOutAdapter == null) {
                _crossedOutAdapter = new CrossedOutAdapter(this);
            }
            return _crossedOutAdapter;
        } else if (adapter == IRhombusEquippedWidget.class) {
            if (_rhombusAdapter == null) {
                _rhombusAdapter = new RhombusAdapter(this);
            }
            return _rhombusAdapter;
        }

        return null;
    }

    private final class InternalTriangle extends RectangleFigure {
        /**
         * {@inheritDoc}
         */
        @Override
        public void paintFigure(final Graphics graphics) {
            fillShape(graphics);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected synchronized void fillShape(final Graphics graphics) {
            Rectangle figureBounds = getBounds().getCopy();
            figureBounds.crop(this.getInsets());
            graphics.setBackgroundColor(getForegroundColor());
            int x = figureBounds.x+figureBounds.width/2;
            int y = figureBounds.y+figureBounds.height/2;
            graphics.translate(x, y);
            graphics.rotate(Float.parseFloat(String.valueOf(_rotationAngle)));
            PointList points = new PointList();
            points.addPoint(-figureBounds.width/2, -figureBounds.height/2);
            points.addPoint(figureBounds.width/2, 0);
            points.addPoint(-figureBounds.width/2, figureBounds.height/2);
            if (!_transparent) {
                graphics.setBackgroundColor(getBackgroundColor());
                graphics.fillPolygon(points);
            }
            int newW = (int) Math.round(figureBounds.width * (getFill() / 100));
            Rectangle fillRectangle = new Rectangle(-figureBounds.width/2,-figureBounds.height/2,newW,figureBounds.height);
            graphics.setClip(fillRectangle);
            graphics.setBackgroundColor(getForegroundColor());
            graphics.fillPolygon(points);
        }
    }

}
