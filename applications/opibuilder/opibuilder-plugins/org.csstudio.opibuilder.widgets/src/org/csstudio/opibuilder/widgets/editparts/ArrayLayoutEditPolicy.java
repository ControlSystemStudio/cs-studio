/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.opibuilder.widgets.editparts;

import java.util.List;

import org.csstudio.opibuilder.commands.AddWidgetCommand;
import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.csstudio.opibuilder.commands.WidgetCreateCommand;
import org.csstudio.opibuilder.commands.WidgetSetConstraintCommand;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editpolicies.WidgetXYLayoutEditPolicy;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.ArrayModel;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

/**
 * The EditPolicy for array widget. It can only be used for
 * {@link ArrayEditPart}
 *
 * @author Xihui Chen
 *
 */
public class ArrayLayoutEditPolicy extends WidgetXYLayoutEditPolicy {

    @Override
    protected Command createChangeConstraintCommand(
            ChangeBoundsRequest request, EditPart child, Object constraint) {
        if (request.getType().equals(REQ_MOVE_CHILDREN)
                || request.getType().equals(REQ_ALIGN_CHILDREN)) {
            return null;
        }
        return super.createChangeConstraintCommand(request, child, constraint);

    }

    @Override
    protected Command createAddCommand(ChangeBoundsRequest request,
            EditPart child, Object constraint) {
        if (!(child instanceof AbstractBaseEditPart)
                || !(constraint instanceof Rectangle))
            return super.createAddCommand(request, child, constraint);

        AbstractContainerModel container = (AbstractContainerModel) getHost()
                .getModel();
        if (!container.getChildren().isEmpty())
            return null;
        AbstractWidgetModel widget = (AbstractWidgetModel) child.getModel();
        CompoundCommand result = new CompoundCommand("Add widget to array");
        addUpdateContainerCommands(container, widget.getSize(), result);
        result.add(new AddWidgetCommand(container, widget,
                (Rectangle) constraint));

        return result;
    }

    protected void addUpdateContainerCommands(AbstractContainerModel container,
            Dimension widgetSize, CompoundCommand result) {
        int elementsCount = getHostArrayEditPart().getArrayFigure()
                .calcVisibleElementsCount(widgetSize);
        Dimension proposedContainerSize = getHostArrayEditPart()
                .getArrayFigure().calcWidgetSizeForElements(elementsCount,
                        widgetSize);
        result.add(new WidgetSetConstraintCommand(container, null,
                new Rectangle(container.getLocation(), proposedContainerSize)));
        result.add(new SetWidgetPropertyCommand(container,
                ArrayModel.PROP_VISIBLE_ELEMENTS_COUNT, elementsCount));
    }

    public ArrayEditPart getHostArrayEditPart() {
        return (ArrayEditPart) getHost();
    }

    @Override
    protected Command createWidgetCreateCommand(CreateRequest request) {
        AbstractContainerModel container = (AbstractContainerModel) getHost()
                .getModel();
        if (!container.getChildren().isEmpty())
            return null;
        CompoundCommand result = new CompoundCommand("Create widget in array");
        Dimension size = ((Rectangle) getConstraintFor(request)).getSize();
        AbstractWidgetModel widget = (AbstractWidgetModel) request
                .getNewObject();
        if (size == null || size.width < 1 || size.height < 1)
            size = widget.getSize();
        addUpdateContainerCommands(container, size, result);
        WidgetCreateCommand widgetCreateCommand = new WidgetCreateCommand(
                widget, container, (Rectangle) getConstraintFor(request),
                false, true);
        result.add(widgetCreateCommand);
        return result;
    }




    /**
     *The behavior of resizing children in an array will be determined by its editpart.
     *
     * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#
     * getResizeChildrenCommand(org.eclipse.gef.requests.ChangeBoundsRequest)
     */
    @Override
    protected Command getResizeChildrenCommand(ChangeBoundsRequest request) {
        if (request.getType().equals(REQ_MOVE_CHILDREN)
                || request.getType().equals(REQ_ALIGN_CHILDREN)) {
            return null;
        }
        CompoundCommand resize = new CompoundCommand();
        Command c;
        List<?> children = getHostArrayEditPart().getChildren();
        GraphicalEditPart child = (GraphicalEditPart) request.getEditParts()
                .get(0);
        Object contraint = translateToModelConstraint(getConstraintForResize(
                request, child));
        c = createChangeConstraintCommand(request, (EditPart) children.get(0),
                contraint);
        resize.add(c);

        return resize.unwrap();
    }

    /* Override super method because array widget only allows adding one child.
     * (non-Javadoc)
     * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#getAddCommand(org.eclipse.gef.Request)
     */
    @Override
    protected Command getAddCommand(Request generic) {
        ChangeBoundsRequest request = (ChangeBoundsRequest) generic;
        List<?> editParts = request.getEditParts();
        CompoundCommand command = new CompoundCommand();
        command.setDebugLabel("Add in ConstrainedLayoutEditPolicy");//$NON-NLS-1$
        GraphicalEditPart child;
        if(editParts.size()>0){
            child = (GraphicalEditPart) editParts.get(0);
            command.add(createAddCommand(
                    request,
                    child,
                    translateToModelConstraint(getConstraintFor(request, child))));
        }
        return command.unwrap();
    }



}
