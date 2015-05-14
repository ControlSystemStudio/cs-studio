package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.eventhandling.BehaviorDescriptor;
import org.csstudio.sds.internal.eventhandling.IBehaviorDescription;
import org.csstudio.sds.internal.eventhandling.IBehaviorService;
import org.csstudio.sds.internal.model.BehaviorProperty;
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
 * Section for {@link BehaviorProperty}.
 *
 * @author Sven Wende
 *
 */
public class BehaviorSection extends AbstractBaseSection<BehaviorProperty> {

    private ComboViewer optionViewer;
    private ISelectionChangedListener changeListener;

    public BehaviorSection(String propertyId) {
        super(propertyId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCreateControls(Composite parent,
            TabbedPropertySheetPage tabbedPropertySheetPage) {
        parent.setLayout(new FormLayout());

        // .. create a combo and the corresponding viewer
        CCombo combo = getWidgetFactory().createCCombo(parent,
                SWT.BORDER | SWT.READ_ONLY);
        FormData fd = new FormData();
        fd.left = new FormAttachment(0, 0);
        fd.right = new FormAttachment(50, 0);
        combo.setLayoutData(fd);

        optionViewer = new ComboViewer(combo);
        optionViewer.setLabelProvider(new BehaviorDescriptionLabelProvider());
        optionViewer.setContentProvider(new ArrayContentProvider());

        // .. listen to changes
        changeListener = new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IBehaviorDescription behaviorDescription = (IBehaviorDescription) ((IStructuredSelection) optionViewer
                        .getSelection()).getFirstElement();

                if (behaviorDescription != null) {
                    applyPropertyChange(behaviorDescription.getBehaviorId());
                } else {
                    applyPropertyChange("");
                }
            }
        };
        optionViewer.addSelectionChangedListener(changeListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRefreshControls(BehaviorProperty widgetProperty) {
        // .. update the combobox
        if (optionViewer.getContentProvider() != null) {
            List<BehaviorDescriptor> behaviorDescriptors = getBehaviorDescriptors();

            optionViewer.setInput(behaviorDescriptors);

            // .. select the current behavior
            BehaviorDescriptor selectedBehavior = behaviorDescriptors.get(0);

            String currentBehaviorId = widgetProperty.getPropertyValue();
            if (currentBehaviorId != null) {
                for (BehaviorDescriptor d : behaviorDescriptors) {
                    if (currentBehaviorId.equals(d.getBehaviorId())) {
                        selectedBehavior = d;
                        break;
                    }
                }
            }

            assert selectedBehavior != null;

            optionViewer.removeSelectionChangedListener(changeListener);
            optionViewer
                    .setSelection(new StructuredSelection(selectedBehavior));
            optionViewer.addSelectionChangedListener(changeListener);

        }
    }

    @SuppressWarnings("unchecked")
    private List<BehaviorDescriptor> getBehaviorDescriptors() {
        List<BehaviorDescriptor> result = new ArrayList<BehaviorDescriptor>();

        // .. add empty
        BehaviorDescriptor noneDescriptor = new BehaviorDescriptor("none", "*",
                "None", Collections.EMPTY_SET, null);
        result.add(noneDescriptor);

        // .. add real behaviors
        if (selectedWidget != null) {
            IBehaviorService service = SdsPlugin.getDefault()
                    .getBehaviourService();
            result.addAll(service.getBehaviors(selectedWidget.getTypeID()));
        }
        return result;
    }

    /**
     * Label provider for {@link IBehaviorDescription}.
     *
     * @author Sven Wende
     *
     */
    private static final class BehaviorDescriptionLabelProvider extends
            LabelProvider {
        @Override
        public String getText(Object element) {
            BehaviorDescriptor d = (BehaviorDescriptor) element;
            return d.getDescription();
        }
    }

}
