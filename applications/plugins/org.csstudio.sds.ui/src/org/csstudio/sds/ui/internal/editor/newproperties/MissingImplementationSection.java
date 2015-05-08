package org.csstudio.sds.ui.internal.editor.newproperties;

import org.csstudio.sds.model.WidgetProperty;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Section implementation which acts as fallback when no other section
 * implementation is available for the current selection.
 *
 * @author Sven Wende
 *
 */
public class MissingImplementationSection extends AbstractBaseSection<WidgetProperty> {

    private Label label;

    public MissingImplementationSection(String propertyId) {
        super(propertyId);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doCreateControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
        GridLayoutFactory.swtDefaults().numColumns(1).applyTo(parent);

        label = getWidgetFactory().createLabel(parent, null);
        GridDataFactory.swtDefaults().hint(300, SWT.DEFAULT).applyTo(label);
        GridDataFactory.swtDefaults();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doRefreshControls(WidgetProperty widgetProperty) {
        if (widgetProperty != null) {
            label.setText(widgetProperty.getPropertyValue() + " (no editor for " + widgetProperty.getPropertyType().name() + ")");
        }
    }

}
