package org.csstudio.sds.ui.internal.editor.newproperties;

import org.csstudio.sds.internal.model.ArrayOptionProperty;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Section implementation for {@link ArrayOptionProperty}.
 *
 * @author Sven Wende
 *
 */
public class ArrayOptionSection extends AbstractBaseSection<ArrayOptionProperty> {

    private ComboViewer optionViewer;
    private ISelectionChangedListener changeListener;

    public ArrayOptionSection(String propertyId) {
        super(propertyId);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doCreateControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
        parent.setLayout(new FormLayout());

        // .. create a combo and the corresponding viewer
        CCombo combo = getWidgetFactory().createCCombo(parent, SWT.BORDER | SWT.READ_ONLY);

        FormData fd = new FormData();
        fd.left = new FormAttachment(0,0);
        fd.right = new FormAttachment(50,0);
        combo.setLayoutData(fd);

        optionViewer = new ComboViewer(combo);
        optionViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return element.toString();
            }
        });

        optionViewer.setContentProvider(new ArrayContentProvider());

        // .. listen to changes
        changeListener = new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                String[] options = (String[]) optionViewer.getInput();
                Object tmp = ((IStructuredSelection) optionViewer.getSelection()).getFirstElement();
                for (int i = 0; i < options.length; i++) {
                    if (options[i].equals(tmp)) {
                        // .. the selection index is the needed value, not the
                        // String itself!
                        applyPropertyChange(i);
                        return;
                    }
                }
            }
        };
        optionViewer.addSelectionChangedListener(changeListener);
    }

    @Override
    protected void doRefreshControls(ArrayOptionProperty widgetProperty) {
        if (!optionViewer.getControl().isDisposed()) {
            if (widgetProperty != null) {
                int index = widgetProperty.getPropertyValue();
                String[] options = widgetProperty.getOptions();
                optionViewer.setInput(options);

                optionViewer.removeSelectionChangedListener(changeListener);
                optionViewer.setSelection(new StructuredSelection(options[index]));
                optionViewer.addSelectionChangedListener(changeListener);
            }
        }
    }

}
