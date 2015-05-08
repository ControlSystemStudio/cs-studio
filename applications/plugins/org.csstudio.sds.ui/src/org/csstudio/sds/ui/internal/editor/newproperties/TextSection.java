package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.csstudio.auth.security.RegisteredAuthorizationId;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.sds.internal.model.StringProperty;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.jface.fieldassist.IContentProposal;

/**
 * Section implementation for {@link StringProperty}.
 *
 * @author Sven Wende
 *
 */
public final class TextSection extends AbstractTextSection<StringProperty, String> {

    public TextSection(String propertyId) {
        super(propertyId);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected String getConvertedValue(String text) {
        return text;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doRefreshControls(StringProperty widgetProperty) {
        if (widgetProperty != null && widgetProperty.getPropertyValue() != null
                && !widgetProperty.getPropertyValue().toString().equals(getTextControl().getText())) {
            getTextControl().setText(widgetProperty.getPropertyValue().toString());
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected List<IContentProposal> getContentProposals(StringProperty property, AbstractWidgetModel selectedWidget,
            List<AbstractWidgetModel> selectedWidgets) {
        if (property.getId().equals(AbstractWidgetModel.PROP_PERMISSSION_ID)) {
            Collection<RegisteredAuthorizationId> authorizationIds = SecurityFacade.getInstance().getRegisteredAuthorizationIds();
            List<IContentProposal> result = new ArrayList<IContentProposal>();
            for (RegisteredAuthorizationId id : authorizationIds) {
                result.add(new PermissionIdContentProposal(id.getId(), id.getDescription()));
            }
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    private class PermissionIdContentProposal implements IContentProposal {

        private final String _content;
        private final String _description;

        public PermissionIdContentProposal(String content, String description) {
            assert content != null : "content != null";
            assert content.trim().length() > 0 : "content.trim().length() > 0";
            _content = content;
            _description = description;
        }

        public String getContent() {
            return _content;
        }

        public int getCursorPosition() {
            return 0;
        }

        public String getDescription() {
            return _description;
        }

        public String getLabel() {
            return _content;
        }

    }

}
