package org.csstudio.sds.ui.internal.connectionview;

import org.csstudio.dal.simple.SimpleDALBroker;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.ui.runmode.IOpenDisplayListener;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class ConnectionView extends ViewPart {

    private TreeViewer connectionTree;
    private IOpenDisplayListener openDisplayListener;

    public ConnectionView() {
    }

    @Override
    public void createPartControl(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout(1, false));

        connectionTree = new TreeViewer(main);
        connectionTree.getTree().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        connectionTree.setLabelProvider(createLabelProvider());
        connectionTree.setContentProvider(createContentProvider());

        openDisplayListener = new IOpenDisplayListener() {
            @Override
            public void openDisplayChanged() {
                final DisplayModel[] allActivDisplayModels = RunModeService.getInstance().getAllActivDisplayModels();
                connectionTree.getTree().getDisplay().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        connectionTree.setInput(allActivDisplayModels);
                    }
                });
            }
        };
        RunModeService.getInstance().addOpenDisplayListener(openDisplayListener);
    }

    private ITreeContentProvider createContentProvider() {
        return new ITreeContentProvider() {

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }

            @Override
            public void dispose() {
            }

            @Override
            public boolean hasChildren(Object element) {
                if (element instanceof DisplayModel) {
                    DisplayModel current = (DisplayModel) element;
                    SimpleDALBroker broker = current.getRuntimeContext().getBroker();
                    int mapSize = broker.getPropertiesMapSize();
                    return mapSize > 0;
                }
                return false;
            }

            @Override
            public Object getParent(Object element) {
                return null;
            }

            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof DisplayModel[]) {
                    return (DisplayModel[])inputElement;
                }
                return null;
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                if (parentElement instanceof DisplayModel) {
                    DisplayModel current = (DisplayModel) parentElement;
                    SimpleDALBroker broker = current.getRuntimeContext().getBroker();
                    System.out
                            .println("ConnectionView.createContentProvider().new ITreeContentProvider() {...}.getChildren() " + broker);
                    int size = broker.getPropertiesMapSize();
                    ConnectionDescriptor[] descriptors = new ConnectionDescriptor[size];
                    for (int i=0; i< size; i++) {
                        descriptors[i] = new ConnectionDescriptor("Channel" + i);
                    }
                    return descriptors;
                }
                return null;
            }
        };
    }

    private LabelProvider createLabelProvider() {
        return new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof DisplayModel) {
                    return ((DisplayModel) element).getRuntimeContext().getDisplayFilePath().toString();
                } else if (element instanceof ConnectionDescriptor) {
                    return ((ConnectionDescriptor) element).getDescription();
                }
                return super.getText(element);
            }
        };
    }

    @Override
    public void setFocus() {
        connectionTree.getTree().setFocus();
    }

    @Override
    public void dispose() {
        RunModeService.getInstance().removeOpenDisplayListener(openDisplayListener);
        super.dispose();
    }

}
