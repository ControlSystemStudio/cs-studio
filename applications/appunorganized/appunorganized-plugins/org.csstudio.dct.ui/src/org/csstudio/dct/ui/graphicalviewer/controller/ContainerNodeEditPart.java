package org.csstudio.dct.ui.graphicalviewer.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.ui.graphicalviewer.GraphicalRepresentationUtil;
import org.csstudio.dct.ui.graphicalviewer.model.AbstractContainerNode;
import org.csstudio.dct.ui.graphicalviewer.model.DctGraphicalModel;
import org.csstudio.dct.ui.graphicalviewer.model.RecordNode;
import org.csstudio.dct.ui.graphicalviewer.view.ContainerNodeFigure;
import org.csstudio.dct.ui.graphicalviewer.view.RecordFigure;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.EllipseAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

/**
 * Controller for {@link AbstractContainerNode}s.
 *
 * @author Sven Wende
 *
 */
public class ContainerNodeEditPart extends AbstractGraphicalEditPart implements NodeEditPart, PropertyChangeListener {

    /**
     *{@inheritDoc}
     */
    @Override
    protected void addChildVisual(EditPart childEditPart, int index) {
        IFigure child = ((GraphicalEditPart) childEditPart).getFigure();

        ContainerNodeFigure figure = (ContainerNodeFigure) getFigure();

        IFigure contentPane = null;

        if (childEditPart instanceof RecordNodeEditPart) {
            // record nodes are inserted in different positions that depend on
            // existing connections to gain a graphical representation with less
            // crossing connection lines
            RecordNode recordBox = (RecordNode) childEditPart.getModel();

            if (recordBox.getSourceConnections().size() > 0) {
                if (recordBox.getTargetConnections().size() > 0) {
                    // .. both
                    contentPane = figure.getMiddle();
                } else {
                    // .. only outgoing
                    contentPane = figure.getMiddle();
                }
            } else if (recordBox.getTargetConnections().size() > 0) {
                // .. only incoming
                contentPane = figure.getRight();
            } else {
                // .. none
                contentPane = figure.getLeft();
            }
        } else if (childEditPart instanceof InstanceNodeEditPart) {
            contentPane = figure.getInstanceArea();
        } else if (childEditPart instanceof ContainerNodeEditPart) {
            contentPane = figure.getInstanceArea();
        }

        contentPane.add(child);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.NODE_ROLE, null);
        installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, null);
        installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
        installEditPolicy(EditPolicy.LAYOUT_ROLE,null);
//        installEditPolicy(EditPolicy.LAYOUT_ROLE, new XYLayoutEditPolicy() {
//
//            @Override
//            protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
//                return null;
//            }
//
//            @Override
//            protected Command getCreateCommand(CreateRequest request) {
//                return null;
//            }
//
//        });
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void activate() {
        super.activate();
        getCastedModel().addPropertyChangeListener(this);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void deactivate() {
        getCastedModel().removePropertyChangeListener(this);
        super.deactivate();

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public IFigure getContentPane() {
        return ((ContainerNodeFigure) getFigure()).getRecordArea();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
        return createConnectionAnchor();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public ConnectionAnchor getSourceConnectionAnchor(Request request) {
        return createConnectionAnchor();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
        return createConnectionAnchor();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public ConnectionAnchor getTargetConnectionAnchor(Request request) {
        return createConnectionAnchor();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected List getModelChildren() {
        return getCastedModel().getNodes();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected IFigure createFigure() {
        ContainerNodeFigure figure = new ContainerNodeFigure(getCastedModel().getCaption());

        figure.addMouseListener(new MouseListener() {

            @Override
            public void mouseDoubleClicked(MouseEvent me) {
                AbstractContainerNode node = getCastedModel();
                IContainer container = (IContainer) node.getElement();
                IProject project = container.getProject();
                DctGraphicalModel model= GraphicalRepresentationUtil.createGraphicalModel(project, container);
                GraphicalViewer viewer = GraphicalRepresentationUtil.openShell(100, 100, 500, 500, "xdd");
                viewer.setContents(model);
            }

            @Override
            public void mousePressed(MouseEvent me) {
            }

            @Override
            public void mouseReleased(MouseEvent me) {
            }

        });
        return figure;
    }


    private ConnectionAnchor createConnectionAnchor() {
        RecordFigure f = (RecordFigure) getFigure();
        return new EllipseAnchor(f);
    }

    private AbstractContainerNode getCastedModel() {
        return (AbstractContainerNode) getModel();
    }
}
