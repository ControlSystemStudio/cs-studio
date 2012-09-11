package org.csstudio.utility.toolbox.framework.builder;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.Some;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LabelBuilder {

	private final Composite composite;
	private GenericEditorInput<?> editorInput;

	private String text = "";
	private String layoutData = "";
	private boolean bold = false;
	private int fontSize = 0;
	private String addProperty = null;

	public LabelBuilder(Composite composite, GenericEditorInput<?> editorInput) {
		this.composite = composite;
		this.editorInput = editorInput;
	}

	public LabelBuilder titleStyle() {
		layoutData = "wrap, gapbottom 8";
		fontSize = 15;
		return this;
	}

	public LabelBuilder text(String text) {
		this.text = text;
		return this;
	}

	public LabelBuilder hint(String layoutData) {
		this.layoutData = layoutData;
		return this;
	}

	public LabelBuilder bold() {
		this.bold = true;
		return this;
	}

	public LabelBuilder add(String property) {
		this.addProperty = property;
		return this;
	}

	public LabelBuilder fontSize(int fontSize) {
		this.fontSize = fontSize;
		return this;
	}

	public Label build() {

		final Label label = new Label(composite, SWT.NONE);

		if (addProperty != null) {
			editorInput.processGenericData(new Func1Void<Some<Object>>() {
				@Override
				public void apply(Some<Object> data) {
					if (data.hasValue()) {
						try {
							String addData = BeanUtils.getSimpleProperty(data.get(), addProperty);
							if ((addData != null) && (!addData.contains("Null"))) {
								label.setText(text + " (" + addData + ")");
							} else {
								label.setText(text);								
							}
						} catch (Exception e) {
							label.setText(text);
						}
					}
				}
			});
		} else {
			label.setText(StringUtils.trimToEmpty(text));
		}

		if (bold || fontSize > 0) {
			FontData[] fD = label.getFont().getFontData();
			if (bold) {
				fD[0].setStyle(SWT.BOLD);
			}
			if (fontSize > 0) {
				fD[0].setHeight(fontSize);
			}
			label.setFont(new Font(AbstractControlWithLabelBuilder.getDisplay(), fD[0]));
		}

		if (!layoutData.isEmpty()) {
			label.setLayoutData(layoutData);
		}

		return label;
	}

}
