package org.csstudio.utility.toolbox.framework.builder;

import java.util.Map;

import org.csstudio.utility.toolbox.common.Constant;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.property.Property.PropertyNameHint;
import org.csstudio.utility.toolbox.framework.property.SearchTermType;
import org.csstudio.utility.toolbox.framework.proposal.TextValueProposalProvider;
import org.csstudio.utility.toolbox.func.Option;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class TextBuilder extends AbstractControlWithLabelBuilder<TextBuilder> {

	private final boolean isSearchMode;
	private final Binder<?> binder;

	private boolean textArea = false;
	private boolean isJoinedForSeearch = false;
	private boolean enableEditingOnNew = false;
	private boolean useBigDecimalConverter = false;
	private boolean limitInputToDigits = false;
	
	public TextBuilder(Composite composite, String property, Map<Property, Widget> properties,
				GenericEditorInput<?> editorInput, Binder<?> binder, SearchTermType type, boolean isSearchMode) {
		super(composite, property, properties, editorInput, type);
		this.isSearchMode = isSearchMode;
		this.binder = binder;
	}

	public TextBuilder(Composite composite, Property property, Map<Property, Widget> properties,
				GenericEditorInput<?> editorInput, Binder<?> binder, SearchTermType type, boolean isSearchMode) {
		super(composite, property, properties, editorInput, type);
		this.isSearchMode = isSearchMode;
		this.binder = binder;
	}

	public TextBuilder multiLine() {
		this.textArea = true;
		return this;
	}

	public TextBuilder isJoinedForSearch() {
		this.isJoinedForSeearch = true;
		return this;
	}
		
	public TextBuilder useBigDecimalConverter() {
		this.useBigDecimalConverter = true;
		return this;
	}

	public TextBuilder limitInputToDigits() {
		this.limitInputToDigits = true;
		return this;
	}

	public TextBuilder enableEditingOnNew() {
		this.enableEditingOnNew = true;
		return this;
	}

	private void assignDataBinding(Text text) {

		ControlDecoration controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		controlDecoration.setMarginWidth(4);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
					FieldDecorationRegistry.DEC_ERROR);
		controlDecoration.setImage(fieldDecoration.getImage());
		controlDecoration.hide();

		text.setData(BuilderConstant.USE_BIG_DECIMAL_CONVERTER, Boolean.valueOf(useBigDecimalConverter));
		
		text.setData(BuilderConstant.NO_BINDING, Boolean.valueOf(isNoBinding()));

		binder.bindPropertyToText(getProperty(), text, controlDecoration, useBigDecimalConverter);

		text.setData(BuilderConstant.DECORATOR, controlDecoration);

	}

	private int calculateStyle() {
		int styleToSet = SWT.BORDER;
		if (textArea) {
			styleToSet = styleToSet | SWT.MULTI;
		} else {
			styleToSet = styleToSet | SWT.SINGLE;
		}
		if (getStyle() == SWT.NONE) {
			if (isReadOnly()) {
				styleToSet = styleToSet | SWT.READ_ONLY;				
			}
			if (isNoBinding()) {
				return styleToSet;
			}
			if (getEditorInput().isReadOnlyPropertyField(getProperty())
						&& ((!getEditorInput().isNewData() || (getEditorInput().isNewData() && (!enableEditingOnNew))))) {
				styleToSet = styleToSet | SWT.READ_ONLY;
			}
		} else {
			styleToSet = styleToSet | getStyle();
		}
		return styleToSet;
	}

	private static class IntegerVerifyListener implements VerifyListener {
		public void verifyText(VerifyEvent e) {
			e.doit = e.text.matches("\\d*");
		}
	}

	public Text build() {

		if (getLabel() != null) {
			buildLabel();
		}

		int style = calculateStyle();
		Text text = new Text(getComposite(), style);

		if (isReadOnly() || isReadOnlyStyle(style)) {
			text.setBackground(AbstractControlWithLabelBuilder.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		}

		text.setLayoutData(getLayoutData());

		// If binding wanted, set maximum size of allowed input if specified
		if (!isNoBinding()) {
			Option<Integer> sizeLimit = getEditorInput().getSizeLimit(getProperty());
			if (sizeLimit.hasValue()) {
				text.setTextLimit(sizeLimit.get());
			}
		}

		// Indicate speical treatment of this property. Relevant for building the sql query.
		if (isJoinedForSeearch) {
			getProperty().setHint(PropertyNameHint.SubQueryOnly);
		}

		getProperties().put(getProperty(), text);

		if ((!isSearchMode) && (!isNoBinding())) {
			assignDataBinding(text);
		}

		if (limitInputToDigits) {
			text.addVerifyListener(new IntegerVerifyListener());
		}

		if ((getData() != null) && (getProperty().getType() != SearchTermType.DATE)) {
			addTextProposalAdapter(text);
			text.setBackground(Constant.TEXT_PROPOSAL_INDICATOR_COLOR);
		}
		
		if (!isEditable()) {
			text.setEditable(false);
		}
		
		if (getMessage() != null) {
			text.setMessage(getMessage());
		}
		
		if (isSearchExact()) {
			getProperty().setType(SearchTermType.STRING_SEARCH_EXACT);
		}
		
		return text;
	}
		
	private void addTextProposalAdapter(Text text) {
	try {
		KeyStroke ks = KeyStroke.getInstance("Ctrl+SPACE");
		TextValueProposalProvider proposalProvider = new TextValueProposalProvider(getData());
		ContentProposalAdapter adapter = new ContentProposalAdapter(text, new TextContentAdapter(),
					proposalProvider, ks, null);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		text.setData(AbstractControlWithLabelBuilder.CONTENT_PROPOSAL_PROVIDER, proposalProvider);
	} catch (ParseException e) {
		throw new IllegalStateException(e);
	}
}
}