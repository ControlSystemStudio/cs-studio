package org.csstudio.utility.toolbox.framework.builder;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.property.SearchTermType;
import org.csstudio.utility.toolbox.framework.proposal.DateProposalProvider;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class DateBuilder {

	private final Composite composite;
	private final Property property;
	private final Map<Property, Widget> properties;
	private final GenericEditorInput<?> editorInput;
	private final boolean isSearchMode;
	private final Binder<?> binder;
	
	private String label;
	private String layoutData = "";
	private String layoutDataLabel = "";
	private SimpleDateFormat sd;

	public DateBuilder(Composite composite, String property, Map<Property, Widget> properties,
				GenericEditorInput<?> editorInput, Binder<?> binder, boolean isSearchMode, SimpleDateFormat sd) {
		this.composite = composite;
		this.property = new Property(property);
		this.property.setType(SearchTermType.DATE);
		this.properties = properties;
		this.editorInput = editorInput;
		this.binder = binder;
		this.isSearchMode = isSearchMode;
		this.sd = sd;
	}
	
	public DateBuilder hint(String layoutData) {
		this.layoutData = layoutData;
		return this;
	}

	public DateBuilder label(String label) {
		this.label = label;
		return this;
	}

	public DateBuilder label(String label, String layoutDataLabel) {
		this.label = label;
		this.layoutDataLabel = layoutDataLabel;
		return this;
	}

	public Text build() {

		TextBuilder tb = new TextBuilder(composite, property, properties, editorInput, binder, SearchTermType.DATE, isSearchMode);
			
		if (label != null) {
			tb.label(label, layoutDataLabel);
		}

		Text text =  tb.hint(layoutData).build();
	
		addDateProposalAdapter(text);
		
		return text;
	}
	
	private void addDateProposalAdapter(Text text) {
		try {
			KeyStroke ks = KeyStroke.getInstance("Ctrl+SPACE");
			ContentProposalAdapter adapter = new ContentProposalAdapter(text, new TextContentAdapter(),
						new DateProposalProvider(sd), ks, null);
			adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		} catch (ParseException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
