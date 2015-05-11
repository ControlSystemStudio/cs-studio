package org.csstudio.sds.ui.internal.editor.newproperties;

import org.csstudio.sds.internal.model.OptionProperty;
import org.csstudio.sds.model.IOption;
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
 * Section implementation of {@link OptionProperty}.
 *
 * @author Sven Wende
 *
 */
public final class OptionSection extends AbstractBaseSection<OptionProperty> {

    private ComboViewer optionViewer;
    private ISelectionChangedListener _changeListener;

    public OptionSection(final String propertyId) {
        super(propertyId);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doCreateControls(final Composite parent, final TabbedPropertySheetPage tabbedPropertySheetPage) {
        parent.setLayout(new FormLayout());

        CCombo combo = getWidgetFactory().createCCombo(parent, SWT.BORDER | SWT.READ_ONLY);
        FormData fd = new FormData();
        fd.left = new FormAttachment(0,0);
        fd.right = new FormAttachment(50,0);
        combo.setLayoutData(fd);

        optionViewer = new ComboViewer(combo);
        optionViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(final Object element) {
                return ((IOption) element).getIdentifier();
            }
        });

        optionViewer.setContentProvider(new ArrayContentProvider());
          // .. listen to changes
        _changeListener = new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                IOption[] options = (IOption[]) optionViewer.getInput();
                Object tmp = ((IStructuredSelection) optionViewer.getSelection()).getFirstElement();
                for (IOption option : options) {
                    if (option.equals(tmp)) {
                        applyPropertyChange(option);
                        return;
                    }
                }
            }
        };
        optionViewer.addSelectionChangedListener(_changeListener);

    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doRefreshControls(final OptionProperty widgetProperty) {
        String value = widgetProperty.getPropertyValue();
        IOption[] options = widgetProperty.getOptions();
        optionViewer.setInput(options);

        IOption sel = null;

        for (IOption option : options) {
            if (value.equals(option.getIdentifier())) {
                sel = option;
            }
        }
        optionViewer.removeSelectionChangedListener(_changeListener);
        optionViewer.setSelection(sel != null ? new StructuredSelection(sel) : null);
        optionViewer.addSelectionChangedListener(_changeListener);

    }

}
