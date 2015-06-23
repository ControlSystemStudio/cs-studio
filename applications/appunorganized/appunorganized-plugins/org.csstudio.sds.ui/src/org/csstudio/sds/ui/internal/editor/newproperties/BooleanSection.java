package org.csstudio.sds.ui.internal.editor.newproperties;

import org.csstudio.sds.internal.model.BooleanProperty;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Section for {@link BooleanProperty}.
 *
 * @author Sven Wende
 *
 */
public class BooleanSection extends AbstractBaseSection<BooleanProperty> {

    private Button checkbox;
    private SelectionListener _selectionListener;

    public BooleanSection(String propertyId) {
        super(propertyId);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doCreateControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
        GridLayoutFactory.swtDefaults().numColumns(1).applyTo(parent);

        // .. create the checkbox
        checkbox = getWidgetFactory().createButton(parent, null, SWT.CHECK);
        GridDataFactory.fillDefaults().applyTo(checkbox);

        // .. listen to changes
        _selectionListener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                applyPropertyChange(checkbox.getSelection());
            }
        };

        checkbox.addSelectionListener(_selectionListener);
    }

    @Override
    protected void doRefreshControls(BooleanProperty widgetProperty) {
        if (widgetProperty != null && !checkbox.isDisposed()) {
            boolean selected = widgetProperty.getPropertyValue();
            checkbox.removeSelectionListener(_selectionListener);
            checkbox.setSelection(selected);
            checkbox.addSelectionListener(_selectionListener);
        }
    }

}
