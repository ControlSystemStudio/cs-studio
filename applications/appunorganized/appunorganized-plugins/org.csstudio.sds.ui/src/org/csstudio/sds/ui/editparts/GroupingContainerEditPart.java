/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.csstudio.sds.ui.editparts;

import org.csstudio.sds.model.GroupingContainerModel;
import org.csstudio.sds.ui.figures.GroupingContainerFigure;
import org.csstudio.sds.util.RotationUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Holds a circuit, which is a container capable of holding other
 * LogicEditParts.
 */
public final class GroupingContainerEditPart extends AbstractContainerEditPart {

    /**
     * The previously set angle.
     */
    private double _previousRotationAngle = 0.0;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        // Transparent background
        IWidgetPropertyChangeHandler transparentHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                GroupingContainerFigure container = (GroupingContainerFigure) refreshableFigure;
                container.setTransparent((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(GroupingContainerModel.PROP_TRANSPARENT,
                transparentHandler);

        // Rotation
        IWidgetPropertyChangeHandler rotationHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                rotateChildren((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(GroupingContainerModel.PROP_ROTATION,
                rotationHandler);
    }

    /**
     * Rotates all children.
     *
     * @param angle
     *            the angle to rotate
     */
    protected void rotateChildren(final double angle) {
        double trueAngle = angle - _previousRotationAngle;
        _previousRotationAngle = angle;
        for (Object obj : this.getChildren()) {
            if (obj instanceof AbstractBaseEditPart) {
                AbstractBaseEditPart editPart = (AbstractBaseEditPart) obj;

                Rectangle childBounds = editPart.getFigure().getBounds();
                Point point = childBounds.getCenter();
                Rectangle groupBounds = this.getFigure().getBounds();
                Point center = groupBounds.getCenter();
                Point rotationPoint = new Point(center.x - groupBounds.x,
                        center.y - groupBounds.y);

                Point rotatedPoint = RotationUtil.rotate(point, trueAngle,
                        rotationPoint);

                editPart.getCastedModel().setLocation(
                        rotatedPoint.x - childBounds.width / 2,
                        rotatedPoint.y - childBounds.height / 2);

                if (editPart.getCastedModel().isRotatable()) {
                    // rotate children if it is rotatable
                    editPart.getCastedModel().setRotationAngle(angle);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IFigure getContentPane() {
        return ((GroupingContainerFigure) getFigure()).getContentsPane();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        GroupingContainerFigure groupingContainerFigure = new GroupingContainerFigure();
        GroupingContainerModel model = (GroupingContainerModel) this
                .getCastedModel();
        groupingContainerFigure.setTransparent(model.getTransparent());
        return groupingContainerFigure;
    }

    @Override
    public void setSelected(int value) {
        super.setSelected(value);
    }

    @Override
    protected boolean determineChildrenSelectability() {
        return isSelected() || isAnyChildSelected();
    }
}
