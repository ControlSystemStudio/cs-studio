package org.csstudio.sds.ui.internal.connection;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * LabelProvider class that supports Decoration of TableViewer and TreeViewer
 * with TreeColumns.
 * 
 * @author Sven Wende
 * 
 */
final class TableDecoratingLabelProvider extends DecoratingLabelProvider
		implements ITableLabelProvider {

	/**
	 * A table lable _provider.
	 */
	private ITableLabelProvider _provider;

	/**
	 * A label _decorator.
	 */
	private ILabelDecorator _decorator;

	/**
	 * Constructor.
	 * 
	 * @param provider
	 *            a table lable provider
	 * @param decorator
	 *            a decorator
	 */
	public TableDecoratingLabelProvider(final ILabelProvider provider,
			final ILabelDecorator decorator) {
		super(provider, decorator);
		_provider = (ITableLabelProvider) provider;
		_decorator = decorator;
	}

	/**
	 * {@inheritDoc}
	 */
	public Image getColumnImage(final Object element, final int columnIndex) {
		Image image = _provider.getColumnImage(element, columnIndex);
		if (_decorator != null) {
			Image decorated = _decorator.decorateImage(image, element);
			if (decorated != null) {
				return decorated;
			}
		}
		return image;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getColumnText(final Object element, final int columnIndex) {
		String text = _provider.getColumnText(element, columnIndex);
		if (_decorator != null) {
			String decorated = _decorator.decorateText(text, element);
			if (decorated != null) {
				return decorated;
			}
		}
		return text;
	}
}
