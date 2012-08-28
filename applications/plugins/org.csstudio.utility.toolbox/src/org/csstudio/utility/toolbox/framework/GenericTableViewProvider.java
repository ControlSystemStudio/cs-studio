package org.csstudio.utility.toolbox.framework;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

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

	private static final class GenericLabelProvider implements ITableLabelProvider {

		private List<Property> properties;

		private GenericLabelProvider(List<Property> properties) {
			this.properties = properties;
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
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Property property = properties.get(columnIndex);
			try {
				return BeanUtils.getProperty(element, property.getName());
			} catch (Exception e) {
				throw new IllegalStateException(e);	
			}
		}
	}

	public IStructuredContentProvider createStructuredContentProvider(List<E> data) {
		return new StructuredContentProvider<E>(data);
	}

	public ITableLabelProvider createTableLableProvider(List<Property> properties) {
		return new GenericLabelProvider(properties);
	}

}
