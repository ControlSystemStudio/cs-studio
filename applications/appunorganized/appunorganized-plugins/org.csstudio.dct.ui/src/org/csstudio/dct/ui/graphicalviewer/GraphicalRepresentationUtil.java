package org.csstudio.dct.ui.graphicalviewer;

import java.util.Set;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.visitors.SearchConnectionsVisitor;
import org.csstudio.dct.model.visitors.SearchConnectionsVisitor.ConnectionDescriptor;
import org.csstudio.dct.ui.Activator;
import org.csstudio.dct.ui.graphicalviewer.model.AbstractContainerNode;
import org.csstudio.dct.ui.graphicalviewer.model.AbstractNode;
import org.csstudio.dct.ui.graphicalviewer.model.Connection;
import org.csstudio.dct.ui.graphicalviewer.model.DctGraphicalModel;
import org.csstudio.dct.ui.graphicalviewer.model.InstanceNode;
import org.csstudio.dct.ui.graphicalviewer.model.PrototypeNode;
import org.csstudio.dct.ui.graphicalviewer.model.RecordNode;
import org.csstudio.dct.ui.graphicalviewer.model.SearchNodeVisitor;
import org.csstudio.domain.common.LayoutUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class GraphicalRepresentationUtil {
    /**
     * {@inheritDoc}
     */
    public static GraphicalViewer openShell(final int x, final int y, final int width, final int height, final String title) {
        // create a shell
        Shell shell = new Shell();

        shell.setText(title);
        shell.setLocation(x, y);
        shell.setLayout(LayoutUtil.createGridLayout(1, 0, 0, 0, 0, 0, 0));
        shell.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/dct.gif"));

        // create a parent composite that fills the whole shell
        GridData gd = new GridData();
        gd.verticalAlignment = 1;
        gd.horizontalAlignment = 1;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        gd.widthHint = width + 17;
        gd.heightHint = height + 17;
        Composite parent = new Composite(shell, SWT.NONE);
        parent.setLayout(LayoutUtil.createGridLayout(1, 0, 0, 0, 0, 0, 0));
        parent.setLayoutData(LayoutUtil.createGridDataForFillingCell(width + 17, height + 17 + 20));

        // create a composite for the graphical viewer
        Composite c = new Composite(parent, SWT.None);
        c.setLayout(new FillLayout());
        c.setLayoutData(LayoutUtil.createGridDataForFillingCell());

        // configure a graphical viewer
        final GraphicalViewer graphicalViewer = createGraphicalViewer(c);

        shell.pack();

        // add dispose listener
        shell.addDisposeListener(new DisposeListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetDisposed(final DisposeEvent e) {
                // FIXME
            }
        });

        // open the shell
        shell.open();

        return graphicalViewer;
    }

    public static DctGraphicalModel createGraphicalModel(IProject project, IPrototype prototype) {
        DctGraphicalModel model = new DctGraphicalModel(project);

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

        if (!connectionDescriptors.isEmpty()) {
            SearchNodeVisitor nodeVisitor = new SearchNodeVisitor();

            for (ConnectionDescriptor cd : connectionDescriptors) {
                AbstractNode srcNode = nodeVisitor.find(cd.getSource().getId(), model);
                AbstractNode tgtNode = nodeVisitor.find(cd.getTarget().getId(), model);

                if (srcNode != null && tgtNode != null) {
                    Connection connection = new Connection(srcNode, tgtNode);
                    connection.setCaption(cd.getDetails());
                }
            }
        }

        return model;
    }

    public static DctGraphicalModel createGraphicalModel(IProject project, IInstance instance) {
        DctGraphicalModel model = new DctGraphicalModel(project);

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

        if (!connectionDescriptors.isEmpty()) {
            SearchNodeVisitor nodeVisitor = new SearchNodeVisitor();

            for (ConnectionDescriptor cd : connectionDescriptors) {
                AbstractNode srcNode = nodeVisitor.find(cd.getSource().getId(), model);
                AbstractNode tgtNode = nodeVisitor.find(cd.getTarget().getId(), model);

                if (srcNode != null && tgtNode != null) {
                    Connection connection = new Connection(srcNode, tgtNode);
                    connection.setCaption(cd.getDetails());
                }
            }
        }

        return model;
    }

    private static AbstractContainerNode createBox(IInstance instance) {
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

    private static GraphicalViewer createGraphicalViewer(final Composite parent) {

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

    public static DctGraphicalModel createGraphicalModel(IProject project, IContainer container) {
        if (container instanceof IPrototype) {
            return createGraphicalModel(project, (IPrototype) container);
        } else if (container instanceof IInstance) {
            return createGraphicalModel(project, (IInstance) container);
        } else {
            return null;
        }
    }
}
