package org.csstudio.opibuilder.visualparts;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertySheet;

/**
	 * The {@link EditingSupport} for the value columns of the property table.
	 * 
	 * @author Xihui Chen
	 * 
	 */
public class PropertiesEditingSupport extends EditingSupport {

		/**
		 * The {@link Table} where this {@link EditingSupport} is embedded.
		 */
		private final Table table;
		

		/**
		 * Constructor.
		 * 
		 * @param viewer
		 *            The {@link ColumnViewer} for this
		 *            {@link EditingSupport}.
		 * @param table
		 *            The {@link Table}
		 */
		public PropertiesEditingSupport(final ColumnViewer viewer,
				final Table table) {
			super(viewer);
			this.table = table;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean canEdit(final Object element) {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected CellEditor getCellEditor(final Object element) {
			AbstractWidgetProperty property;
			if((property = getSelectedProperty()) != null){
				return property.getPropertyDescriptor().createPropertyEditor(table);				
			}			
			return null;
		}

		private AbstractWidgetProperty getSelectedProperty(){
			IStructuredSelection selection = (IStructuredSelection) this
					.getViewer().getSelection();
			if(selection.getFirstElement() instanceof AbstractWidgetProperty){
				AbstractWidgetProperty property = (AbstractWidgetProperty) selection
						.getFirstElement();
				return property;
			}
			return null;
		}
		
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Object getValue(final Object element) {
			if (element instanceof AbstractWidgetProperty) {			
					return ((AbstractWidgetProperty)element).getPropertyValue();
				}
			
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void setValue(final Object element, final Object value) {
			if (element instanceof AbstractWidgetProperty) {
				AbstractWidgetProperty prop = (AbstractWidgetProperty) element;
				if (prop != null) {
					prop.setPropertyValue(value);	
					getViewer().refresh();
				}
			}
		}
	}