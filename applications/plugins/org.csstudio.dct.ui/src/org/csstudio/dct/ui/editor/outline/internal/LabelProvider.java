/**
 * 
 */
package org.csstudio.dct.ui.editor.outline.internal;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

@SuppressWarnings("unchecked")
public final class LabelProvider extends ColumnLabelProvider {

	/**
	 * FIXME: Momentan ist update() nur wegen eines Workarrounds überschrieben.
	 * Umstellen des Labelproviders auf TableColumnViewerLabelProvider.class,
	 * sobald diese public ist. {@inheritDoc}
	 */
	@Override
	public void update(final ViewerCell cell) {
		Object element = cell.getElement();
		int index = cell.getColumnIndex();
		cell.setText(getText(element, index));
		Image image = getImage(element, index);
		cell.setImage(image);
		cell.setBackground(getBackground(element));
		cell.setForeground(getForeground(element, index));
		cell.setFont(getFont(element, index));
	}

	/**
	 * Returns the text to display.
	 * 
	 * @param element
	 *            the current element
	 * @param columnIndex
	 *            the current column index
	 * @return The text to display in the viewer
	 */
	private String getText(final Object element, final int columnIndex) {
		return element.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getToolTipText(final Object element) {
		return element.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public Point getToolTipShift(final Object object) {
		return new Point(5, 5);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getToolTipDisplayDelayTime(final Object object) {
		return 100;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getToolTipTimeDisplayed(final Object object) {
		return 10000;
	}

	/**
	 * Returns the font, which is used to display the channel informations.
	 * 
	 * @param element
	 *            The current element
	 * @param column
	 *            The current column index
	 * @return The font
	 */
	private Font getFont(final Object element, final int column) {
		return CustomMediaFactory.getInstance().getDefaultFont(SWT.NORMAL);
	}

	/**
	 * returns the foreground color for a cell.
	 * 
	 * @param element
	 *            The current element
	 * @param column
	 *            The current column index
	 * @return The foreground color
	 */
	private Color getForeground(final Object element, final int column) {
		RGB rgb = new RGB(0, 0, 0);

		return CustomMediaFactory.getInstance().getColor(rgb);
	}

	/**
	 * Returns the Image for a cell.
	 * 
	 * @param element
	 *            The current element
	 * @param index
	 *            The current column index
	 * @return The Image for the cell
	 */
	private Image getImage(final Object element, final int index) {
		Image result = null;
		return result;
	}
}