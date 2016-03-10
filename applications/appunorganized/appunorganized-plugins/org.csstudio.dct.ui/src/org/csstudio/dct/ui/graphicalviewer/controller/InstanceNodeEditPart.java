package org.csstudio.dct.ui.graphicalviewer.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.ui.graphicalviewer.GraphicalDctView;
import org.csstudio.dct.ui.graphicalviewer.model.InstanceNode;
import org.csstudio.dct.ui.graphicalviewer.view.InstanceBoxFigure;
import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Controller for an {@link InstanceNode}.
 *
 * @author Sven Wende
 *
 */
public class InstanceNodeEditPart extends AbstractGraphicalEditPart implements NodeEditPart, PropertyChangeListener {

    private ChopboxAnchor anchorCenter;

    /**
     *{@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
    }

    /**
     * @see org.eclipse.gef.EditPart#activate()
     */
    @Override
    public void activate() {
        super.activate();
        List l = getTargetConnections();
        for (int i = 0; i < l.size(); i++) {
            ((EditPart) l.get(i)).activate();
        }
        getCastedModel().addPropertyChangeListener(this);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivate() {
        getCastedModel().removePropertyChangeListener(this);
        // deactivate target connections (check super call for source
        // connections)
        List l = getTargetConnections();
        for (int i = 0; i < l.size(); i++) {
            ((EditPart) l.get(i)).deactivate();
        }
        super.deactivate();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure createFigure() {
        InstanceBoxFigure figure = new InstanceBoxFigure(getCastedModel().getCaption());
        anchorCenter = new ChopboxAnchor(figure);

        figure.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                final IPrototype prototype = getCastedModel().getElement().getPrototype();

                String secondaryId = "" + System.currentTimeMillis();
                final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try {
                    GraphicalDctView view = (GraphicalDctView) page.showView(GraphicalDctView.PRIMARY_ID, secondaryId,
                            IWorkbenchPage.VIEW_ACTIVATE);

                    view.setPrototype(prototype.getProject(), prototype);
                } catch (final PartInitException e) {
                    e.printStackTrace();
                }
            }
        });

        return figure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connectionEditpart) {
        return anchorCenter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractConnectionAnchor getSourceConnectionAnchor(Request request) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connectionEditpart) {
        return anchorCenter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractConnectionAnchor getTargetConnectionAnchor(Request request) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelSourceConnections() {
        return getCastedModel().getSourceConnections();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getModelTargetConnections() {
        return getCastedModel().getTargetConnections();
    }

    private InstanceNode getCastedModel() {
        return (InstanceNode) getModel();
    }
}