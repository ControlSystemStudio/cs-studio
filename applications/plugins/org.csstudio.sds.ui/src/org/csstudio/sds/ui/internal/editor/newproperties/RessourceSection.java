package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.List;

import org.csstudio.sds.internal.model.ResourceProperty;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.ui.util.dialogs.ResourceSelectionDialog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.csstudio.sds.ui.dialogs.SdsResourceSelectionDialog;

public class RessourceSection extends AbstractTextSection<ResourceProperty, IPath> {

	public RessourceSection(String propertyId) {
		super(propertyId);
	}

	@Override
	protected List<IContentProposal> getContentProposals(ResourceProperty property, AbstractWidgetModel selectedWidget,
			List<AbstractWidgetModel> selectedWidgets) {
		return null;
	}

	@Override
	protected IPath getConvertedValue(String text) {
		IPath result = new Path(text);
		return result;
	}

	@Override
	protected void doRefreshControls(ResourceProperty widgetProperty) {
		if (widgetProperty != null && widgetProperty.getPropertyValue() != null
				&& !widgetProperty.getPropertyValue().toString().equals(getTextControl().getText())) {
			getTextControl().setText(widgetProperty.getPropertyValue().toString());
		}
	}

	@Override
	protected void doCreateControls(final Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.doCreateControls(parent, tabbedPropertySheetPage);

		// .. change position of the text control
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(50, -85);
		getTextControl().setLayoutData(fd);

		// .. create button
		Hyperlink link = getWidgetFactory().createHyperlink(parent, "Choose ...", SWT.NONE);
		link.setUnderlined(false);

		fd = new FormData();
		fd.left = new FormAttachment(getTextControl(), 5);
		fd.right = new FormAttachment(50, 0);
		link.setLayoutData(fd);

		link.addHyperlinkListener(new HyperlinkAdapter() {

			public void linkActivated(HyperlinkEvent e) {
				ResourceProperty property = getMainWidgetProperty();
				String[] fileExtensions = new String[] { "*.*" };
				IPath path = new Path("");

				if (property != null) {
					fileExtensions = property.getFileExtensions();
					path = property.getPropertyValue();
				}
				// Special dialog for SDS display selection
				if(fileExtensions.length == 1 && fileExtensions[0].equalsIgnoreCase("css-sds")) {
					SdsResourceSelectionDialog sdsDialog = new SdsResourceSelectionDialog(parent.getShell());
					if(Window.OK == sdsDialog.open()) {
						path = sdsDialog.getSelectedPath();
						applyPropertyChange(path);
					}
				}
				else {
					ResourceSelectionDialog dialog = new ResourceSelectionDialog(parent.getShell(), "Select a resource", fileExtensions);
					dialog.setSelectedResource(path);
					if (Window.OK == dialog.open()) {
						if (dialog.getSelectedResource() != null) {
							path = dialog.getSelectedResource();
							applyPropertyChange(path);
						}
					}
				}
			}

		});
	}

}
