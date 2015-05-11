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
package org.csstudio.dct.ui.graphicalviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.ProjectFactory;
import org.csstudio.dct.model.visitors.SearchConnectionsVisitor;
import org.csstudio.dct.model.visitors.SearchConnectionsVisitor.ConnectionDescriptor;
import org.csstudio.dct.ui.graphicalviewer.model.AbstractContainerNode;
import org.csstudio.dct.ui.graphicalviewer.model.AbstractNode;
import org.csstudio.dct.ui.graphicalviewer.model.Connection;
import org.csstudio.dct.ui.graphicalviewer.model.DctGraphicalModel;
import org.csstudio.dct.ui.graphicalviewer.model.InstanceNode;
import org.csstudio.dct.ui.graphicalviewer.model.PrototypeNode;
import org.csstudio.dct.ui.graphicalviewer.model.RecordNode;
import org.csstudio.dct.ui.graphicalviewer.model.SearchNodeVisitor;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * A view implementation that uses a GEF graphical viewer to display SDS
 * displays.
 *
 * Note: This view is enabled for multiple use.
 *
 * @author Sven Wende
 */
public final class GraphicalDctView extends ViewPart {
    public static final String PRIMARY_ID = "org.csstudio.dct.ui.graphicalviewer.GraphicalDctView";

    /**
     * The graphical viewer.
     */
    private GraphicalViewer _graphicalViewer;

    private DctGraphicalModel model;

    public GraphicalDctView() {
        model = new DctGraphicalModel(ProjectFactory.createNewDCTProject());
    }

    public void setInstance(IProject project,IInstance instance) {
        model = new DctGraphicalModel(project);

        AbstractContainerNode box = new InstanceNode(instance);
        model.addNode(box);

        for (IInstance in : instance.getInstances()) {
            box.addNode(createBox(in));
        }

        for (IRecord record : instance.getRecords()) {
            RecordNode r = new RecordNode(record);
            box.addNode(r);
        }

        // .. connections
        SearchConnectionsVisitor visitor = new SearchConnectionsVisitor();
        Set<ConnectionDescriptor> connectionDescriptors = visitor.search(project);

        if(!connectionDescriptors.isEmpty()) {
            SearchNodeVisitor nodeVisitor = new SearchNodeVisitor();

            for(ConnectionDescriptor cd : connectionDescriptors) {
                AbstractNode srcNode = nodeVisitor.find(cd.getSource().getId(), model);
                AbstractNode tgtNode = nodeVisitor.find(cd.getTarget().getId(), model);

                if(srcNode!=null && tgtNode!=null) {
                    Connection connection = new Connection(srcNode, tgtNode);
                    connection.setCaption(cd.getDetails());
                }
            }
        }


        _graphicalViewer.setContents(model);
    }

    public void setPrototype(IProject project, IPrototype prototype) {
        model = new DctGraphicalModel(project);

        AbstractContainerNode box = new PrototypeNode(prototype);
        model.addNode(box);

        for (IInstance instance : prototype.getInstances()) {
            box.addNode(createBox(instance));
        }

        for (IRecord record : prototype.getRecords()) {
            RecordNode r = new RecordNode(record);
            box.addNode(r);
        }

        // .. connections
        SearchConnectionsVisitor visitor = new SearchConnectionsVisitor();
        Set<ConnectionDescriptor> connectionDescriptors = visitor.search(project);

        if(!connectionDescriptors.isEmpty()) {
            SearchNodeVisitor nodeVisitor = new SearchNodeVisitor();

            for(ConnectionDescriptor cd : connectionDescriptors) {
                AbstractNode srcNode = nodeVisitor.find(cd.getSource().getId(), model);
                AbstractNode tgtNode = nodeVisitor.find(cd.getTarget().getId(), model);

                if(srcNode!=null && tgtNode!=null) {
                    Connection connection = new Connection(srcNode, tgtNode);
                    connection.setCaption(cd.getDetails());
                }
            }
        }


        _graphicalViewer.setContents(model);
    }

    private AbstractContainerNode createBox(IInstance instance) {
        AbstractContainerNode box = new InstanceNode(instance);

        for (IInstance instanceLevel2 : instance.getInstances()) {
            box.addNode(createBox(instanceLevel2));
        }

        for (IRecord record : instance.getRecords()) {
            RecordNode r = new RecordNode(record);
            box.addNode(r);
        }

        return box;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(final Composite parent) {
        _graphicalViewer = createGraphicalViewer(parent);
        configureGraphicalViewer(_graphicalViewer);

        _graphicalViewer.setContents(model);
    }

    private GraphicalViewer createGraphicalViewer(final Composite parent) {

        final ScrollingGraphicalViewer viewer = new ScrollingGraphicalViewer();
        viewer.createControl(parent);

        viewer.setEditPartFactory(new DctEditPartFactory());

        final ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart();
        viewer.setRootEditPart(root);

        EditDomain editDomain = new EditDomain();

        final SelectionTool tool = new SelectionTool();
        tool.setUnloadWhenFinished(false);
        editDomain.setDefaultTool(tool);
        editDomain.addViewer(viewer);

        return viewer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
    }

    /**
     * Configures the specified graphical viewer.
     *
     * @param viewer
     *            the graphical viewer
     */
    private void configureGraphicalViewer(final GraphicalViewer viewer) {
        assert viewer != null;

        final ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart();
        viewer.setRootEditPart(root);

        final ZoomManager zm = root.getZoomManager();

        final List<String> zoomLevels = new ArrayList<String>(3);
        zoomLevels.add(ZoomManager.FIT_ALL);
        zoomLevels.add(ZoomManager.FIT_WIDTH);
        zoomLevels.add(ZoomManager.FIT_HEIGHT);
        zm.setZoomLevelContributions(zoomLevels);

        /* scroll-wheel zoom */
        viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1), MouseWheelZoomHandler.SINGLETON);

        viewer.setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, false);
        viewer.setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, false);
        viewer.setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED, false);

    }

    public GraphicalViewer getGraphicalViewer() {
        return _graphicalViewer;
    }

}
