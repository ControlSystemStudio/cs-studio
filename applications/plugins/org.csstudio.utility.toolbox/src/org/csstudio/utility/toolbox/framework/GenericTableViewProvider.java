package org.csstudio.utility.toolbox.framework;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.func.Func2;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;

public final class GenericTableViewProvider<E> {

	private static final class StructuredContentProvider<E> implements IStructuredContentProvider {

		private List<E> data;

		private StructuredContentProvider(List<E> data) {
			this.data = data;
		}

		public Object[] getElements(Object imput) {
			return data.toArray();
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			//
		}

	}

	private static final class GenericLabelProvider extends StyledCellLabelProvider {

		private List<Property> properties;
		private Func2<Boolean, ViewerCell, String> cellUpdateCallback;

		private GenericLabelProvider(List<Property> properties, Func2<Boolean, ViewerCell, String> cellUpdateCallback) {
			this.properties = properties;
			this.cellUpdateCallback = cellUpdateCallback;
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		@Override
		public void update(ViewerCell cell) {
			Property property = properties.get(cell.getColumnIndex());
			try {
				String value = BeanUtils.getProperty(cell.getElement(), property.getName());
				if (!(cellUpdateCallback.apply(cell, value))) {
					cell.setText(value);
				}
				super.update(cell);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			super.update(cell);
		}

	}

	public IStructuredContentProvider createStructuredContentProvider(List<E> data) {
		return new StructuredContentProvider<E>(data);
	}

	public StyledCellLabelProvider createTableLableProvider(List<Property> properties,
				Func2<Boolean, ViewerCell, String> cellUpdateCallback) {
		return new GenericLabelProvider(properties, cellUpdateCallback);
	}

}
