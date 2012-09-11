package org.csstudio.utility.toolbox.framework.builder;

import java.util.Map;

import org.csstudio.utility.toolbox.framework.property.Property;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

public class TableViewerBuilder {

	private final Composite composite;
	private final Property property;
	private final Map<Property, Widget> properties;
	private final Map<Property, TableViewer> tableViewers;

	private boolean enableEditingSupport = false;

	public TableViewerBuilder(Composite composite, String property,  Map<Property, Widget> properties, Map<Property, TableViewer> tableViewers) {
		this.composite = composite;
		this.property = new Property(property);
		this.properties = properties;
		this.tableViewers = tableViewers;
	}

	public TableViewerBuilder enableEditingSupport() {
		enableEditingSupport = true;
		return this;
	}

	private static class EditorActivationStrategy extends ColumnViewerEditorActivationStrategy {
		EditorActivationStrategy(TableViewer tableViewer) {
			super(tableViewer);
		}
		protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
			return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && (event.keyCode == SWT.CR))
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
		}	
	}
	
	private void activateEditingSupport(TableViewer tableViewer) {
		final TableViewerFocusCellManager focusCellMgr = new TableViewerFocusCellManager(tableViewer,
					new FocusCellOwnerDrawHighlighter(tableViewer));
		final ColumnViewerEditorActivationStrategy actSupport = new EditorActivationStrategy(tableViewer);
		TableViewerEditor.create(tableViewer, focusCellMgr, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL
					| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL
					| ColumnViewerEditor.KEYBOARD_ACTIVATION);
	}

	public TableViewer build() {
		TableViewer tableViewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.FULL_SELECTION | SWT.BORDER);

		ControlDecoration controlDecoration = new ControlDecoration(tableViewer.getControl(), SWT.LEFT | SWT.TOP);
		controlDecoration.setMarginWidth(4);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
					FieldDecorationRegistry.DEC_ERROR);
		controlDecoration.setImage(fieldDecoration.getImage());
		controlDecoration.hide();

		tableViewer.getControl().setData(BuilderConstant.DECORATOR, controlDecoration);
		
		tableViewers.put(property,  tableViewer);		
		properties.put(property, tableViewer.getControl());

		if (enableEditingSupport) {
			activateEditingSupport(tableViewer);
		}

		return tableViewer;
	}
	
}