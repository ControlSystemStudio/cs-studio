/**
 *
 */
package org.csstudio.unitconversion.ui;

import gov.bnl.unitconversion.ConversionAlgorithm;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 *
 * A Simple Widget to Display the conversionAlgortithms assocaited with a device
 *
 *
 * @author shroffk
 *
 */
public class ConversionAlgorithmTable extends Composite {

    private static class ContentProvider implements IStructuredContentProvider {

    @SuppressWarnings("unchecked")
    public Object[] getElements(Object inputElement) {
        return (((Map<String, ConversionAlgorithm>) inputElement)
            .entrySet()).toArray();
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
    }

    private Table conversionAlgorithmTable;

    private Map<String, ConversionAlgorithm> conversionAlgorithms = Collections
        .emptyMap();

    private TableViewer tableViewer;

    public ConversionAlgorithmTable(Composite parent, int style) {
    super(parent, style);
    setLayout(new FormLayout());

    Composite composite = new Composite(this, SWT.NONE);
    FormData fd_composite = new FormData();
    fd_composite.bottom = new FormAttachment(100);
    fd_composite.right = new FormAttachment(100);
    fd_composite.top = new FormAttachment(0);
    fd_composite.left = new FormAttachment(0);
    composite.setLayoutData(fd_composite);
    TableColumnLayout tcl_composite = new TableColumnLayout();
    composite.setLayout(tcl_composite);

    tableViewer = new TableViewer(composite, SWT.BORDER
        | SWT.FULL_SELECTION);
    conversionAlgorithmTable = tableViewer.getTable();
    conversionAlgorithmTable.setHeaderVisible(true);
    conversionAlgorithmTable.setLinesVisible(true);

    TableViewerColumn tableViewerName = new TableViewerColumn(tableViewer,
        SWT.NONE);
    tableViewerName.setLabelProvider(new ColumnLabelProvider() {
        public Image getImage(Object element) {
        return null;
        }

        @SuppressWarnings("unchecked")
        public String getText(Object element) {
        if (element == null) {
            return "";
        } else {
            return ((Entry<String, ConversionAlgorithm>) element)
                .getKey();
        }
        }
    });
    TableColumn tblclmnName = tableViewerName.getColumn();
    tcl_composite.setColumnData(tblclmnName, new ColumnWeightData(25));
    tblclmnName.setText("Name");

    TableViewerColumn tableViewerFunction = new TableViewerColumn(
        tableViewer, SWT.NONE);
    tableViewerFunction.setLabelProvider(new ColumnLabelProvider() {
        public Image getImage(Object element) {
        return null;
        }

        @SuppressWarnings("unchecked")
        public String getText(Object element) {
        if (element == null) {
            return "";
        } else {
            return ((Entry<String, ConversionAlgorithm>) element)
                .getValue().getFunction();
        }
        }
    });
    TableColumn tblclmnFunction = tableViewerFunction.getColumn();
    tcl_composite.setColumnData(tblclmnFunction, new ColumnWeightData(50,
        true));
    tblclmnFunction.setText("Function");

    TableViewerColumn tableViewerColumnInitialUnit = new TableViewerColumn(
        tableViewer, SWT.NONE);
    tableViewerColumnInitialUnit
        .setLabelProvider(new ColumnLabelProvider() {
            public Image getImage(Object element) {
            return null;
            }

            public String getText(Object element) {
            if (element == null) {
                return "";
            } else {
                return ((Entry<String, ConversionAlgorithm>) element)
                    .getValue().getInitialUnit();
            }
            }
        });
    TableColumn tblclmnInitialUnit = tableViewerColumnInitialUnit
        .getColumn();
    tcl_composite.setColumnData(tblclmnInitialUnit, new ColumnWeightData(
        15, ColumnWeightData.MINIMUM_WIDTH, true));
    tblclmnInitialUnit.setText("Initial Unit");

    TableViewerColumn tableViewerColumnFinalUnit = new TableViewerColumn(
        tableViewer, SWT.NONE);
    tableViewerColumnFinalUnit.setLabelProvider(new ColumnLabelProvider() {
        public Image getImage(Object element) {
        return null;
        }

        public String getText(Object element) {
        if (element == null) {
            return "";
        } else {
            return ((Entry<String, ConversionAlgorithm>) element)
                .getValue().getresultUnit();
        }
        }
    });
    TableColumn tblclmnFinalUnit = tableViewerColumnFinalUnit.getColumn();
    tcl_composite.setColumnData(tblclmnFinalUnit, new ColumnWeightData(15,
        ColumnWeightData.MINIMUM_WIDTH, true));
    tblclmnFinalUnit.setText("Final Unit");

    TableViewerColumn tableViewerAuxInfo = new TableViewerColumn(
        tableViewer, SWT.NONE);
    tableViewerAuxInfo.setLabelProvider(new ColumnLabelProvider() {
        public Image getImage(Object element) {
        return null;
        }

        @SuppressWarnings("unchecked")
        public String getText(Object element) {
        if (element == null) {
            return "";
        } else {
            return String
                .valueOf(((Entry<String, ConversionAlgorithm>) element)
                    .getValue().getAuxInfo());
        }
        }
    });
    TableColumn tblclmnAuxInfo = tableViewerAuxInfo.getColumn();
    tcl_composite.setColumnData(tblclmnAuxInfo, new ColumnWeightData(15,
        true));
    tblclmnAuxInfo.setText("Aux Info");

    tableViewer.setContentProvider(new ContentProvider());
    }

    /**
     * @return the conversionAlgorithms
     */
    public Map<String, ConversionAlgorithm> getConversionAlgorithms() {
    return conversionAlgorithms;
    }

    /**
     * @param conversionAlgorithms the conversionAlgorithms to set
     */
    public void setConversionAlgorithms(Map<String, ConversionAlgorithm> conversionAlgorithms) {
    this.conversionAlgorithms = conversionAlgorithms;
    // TODO use property change listener (shroffk)
    tableViewer.setInput(this.conversionAlgorithms);
    }
}
