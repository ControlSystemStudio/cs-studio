package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csstudio.sds.internal.model.TooltipProperty;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetProperty;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.swt.SWT;

/**
 * Section implementation for {@link TooltipProperty}.
 *
 * @author Sven Wende
 *
 */
public class TooltipSection extends AbstractTextSection<TooltipProperty, String> {

    public TooltipSection(String propertyId) {
        super(propertyId);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int getTextHeight() {
        return STANDARD_WIDGET_HEIGHT * 6;
    }

    @Override
    protected int getTextControlStyle() {
        return SWT.MULTI | SWT.V_SCROLL;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected String getConvertedValue(String text) {
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getProposalAcceptanceStyle() {
        return ContentProposalAdapter.PROPOSAL_INSERT;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doRefreshControls(TooltipProperty widgetProperty) {
        if (widgetProperty != null && !widgetProperty.getPropertyValue().toString().equals(getTextControl().getText())) {
            getTextControl().setText(widgetProperty.getPropertyValue().toString());
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected List<IContentProposal> getContentProposals(TooltipProperty property, AbstractWidgetModel selectedWidget,
            List<AbstractWidgetModel> selectedWidgets) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();

        // .. collect property proposals
        if (selectedWidgets != null && selectedWidgets.size() > 0) {
            Set<String> props = new HashSet<String>();

            // .. we only display proposals for properties that are currently visible on all selected widgets
            props.addAll(selectedWidgets.get(0).getVisiblePropertyIds());

            for (AbstractWidgetModel widget : selectedWidgets) {
                props.retainAll(widget.getVisiblePropertyIds());
            }

            for (String propertyId : props) {
                proposals.add(new WidgetPropertContentProposal(selectedWidgets.get(0).getPropertyInternal(propertyId)));
            }

        }

        return proposals;
    }

    /**
     * Describes a proposal.
     *
     * @author Sven Wende
     */
    private static final class WidgetPropertContentProposal implements IContentProposal {
        private WidgetProperty property;

        private WidgetPropertContentProposal(WidgetProperty property) {
            assert property != null;
            this.property = property;
        }

        public String getContent() {
            return "${" + property.getId() + "}";
        }

        public int getCursorPosition() {
            return 0;
        }

        public String getDescription() {
            return property.getDescription();
        }

        public String getLabel() {
            return property.getDescription();
        }

    }

}
