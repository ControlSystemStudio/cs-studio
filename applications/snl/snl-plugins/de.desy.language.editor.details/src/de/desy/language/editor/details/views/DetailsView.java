package de.desy.language.editor.details.views;


import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

import de.desy.language.editor.core.measurement.IMeasurementProvider;
import de.desy.language.editor.core.measurement.IUpdateListener;
import de.desy.language.editor.core.measurement.KeyValuePair;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class DetailsView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "de.desy.language.editor.details.views.DetailsView";

    private TableViewer _measurementDataViewer;
    private IMeasurementProvider _measurementProvider;
    private Label _resourceLabel;
    private boolean _partsCreated = false;

    private RefreshDetailViewListener _partListener;


    /*
     * The content provider class is responsible for
     * providing objects to the view. It can wrap
     * existing objects in adapters or simply return
     * objects as-is. These objects may be sensitive
     * to the current input of the view, or ignore
     * it and always show the same content
     * (like Task List, for example).
     */

    private final class RefreshDetailViewListener implements IPartListener2 {
        @Override
        public void partVisible(IWorkbenchPartReference partRef) {
        }

        @Override
        public void partOpened(IWorkbenchPartReference partRef) {
        }

        @Override
        public void partInputChanged(IWorkbenchPartReference partRef) {
        }

        @Override
        public void partHidden(IWorkbenchPartReference partRef) {
        }

        @Override
        public void partDeactivated(IWorkbenchPartReference partRef) {
        }

        @Override
        public void partClosed(IWorkbenchPartReference partRef) {
        }

        @Override
        public void partBroughtToTop(IWorkbenchPartReference partRef) {
            partChanged(partRef);
        }

        @Override
        public void partActivated(IWorkbenchPartReference partRef) {
            partChanged(partRef);
        }

        private void partChanged(IWorkbenchPartReference partRef) {
            IWorkbenchPart part = partRef.getPart(false);
            IMeasurementProvider measureMentProvider = (IMeasurementProvider) part.getAdapter(IMeasurementProvider.class);
            if (measureMentProvider != null) {
                update(measureMentProvider);
            }
        }
    }
    class DataLabelProvider extends LabelProvider implements ITableLabelProvider {
        @Override
        public String getColumnText(Object obj, int index) {
            if (obj instanceof KeyValuePair) {
                if (index == 0) {
                    String text = ((KeyValuePair) obj).getKey();
                    return text;
                } else {
                    String text = String.valueOf(((KeyValuePair) obj).getValue());
                    return text;
                }
            }
            return getText(obj);
        }
        @Override
        public Image getColumnImage(Object obj, int index) {
            return getImage(obj);
        }
    }
    class NameSorter extends ViewerSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            if (e1 instanceof KeyValuePair && e2 instanceof KeyValuePair) {
                KeyValuePair p1 = (KeyValuePair) e1;
                KeyValuePair p2 = (KeyValuePair) e2;
                return p1.getKey().compareTo(p2.getKey());
            }
            return super.compare(viewer, e1, e2);
        }
    }

    /**
     * The constructor.
     */
    public DetailsView() {
    }

    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout(1, false));
        _resourceLabel = new Label(main, SWT.NONE);
        _resourceLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        _measurementDataViewer = new TableViewer(main, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        TableViewerColumn column = new TableViewerColumn(_measurementDataViewer, SWT.NONE);
        column.getColumn().setText("Description");
        column.getColumn().setWidth(200);
        column = new TableViewerColumn(_measurementDataViewer, SWT.NONE);
        column.getColumn().setText("Value");
        column.getColumn().setWidth(100);

        _measurementDataViewer.setContentProvider(new ArrayContentProvider());
        _measurementDataViewer.setLabelProvider(new DataLabelProvider());
        _measurementDataViewer.setSorter(new NameSorter());
        _measurementDataViewer.getTable().setLinesVisible(true);
        _measurementDataViewer.getTable().setHeaderVisible(true);

        GridData griddata = new GridData(SWT.FILL, SWT.FILL, true, true);
        _measurementDataViewer.getTable().setLayoutData(griddata);

        _partListener = new RefreshDetailViewListener();
        getSite().getWorkbenchWindow().getPartService().addPartListener(_partListener);
        _partsCreated = true;
    }

    private void update(IMeasurementProvider provider) {
        if (_partsCreated) {
            _measurementProvider = provider;
            refreshContent();
             _measurementProvider.addUpdateListener(new IUpdateListener() {
                 @Override
                public void update() {
                    refreshContent();
                }
            });
        }
    }

    private void refreshContent() {
        _resourceLabel.setText(_measurementProvider
                .getRessourceIdentifier());
         if (_measurementProvider.getMeasuredData() != null) {
             _measurementDataViewer.setInput(_measurementProvider.getMeasuredData());
         }
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        _measurementDataViewer.getControl().setFocus();
    }

    @Override
    public void dispose() {
        getSite().getWorkbenchWindow().getPartService().removePartListener(_partListener);
        super.dispose();
    }
}