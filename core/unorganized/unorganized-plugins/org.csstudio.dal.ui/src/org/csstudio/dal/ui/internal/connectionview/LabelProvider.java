/**
 *
 */
package org.csstudio.dal.ui.internal.connectionview;

import java.util.Arrays;

import org.csstudio.dal.ui.Activator;
import org.csstudio.dal.ui.util.CustomMediaFactory;
import org.csstudio.dal.ui.util.ImageUtil;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IConnector;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

final class LabelProvider extends ColumnLabelProvider {

	/**
	 * FIXME: Momentan ist update() nur wegen eines Workarrounds überschrieben.
	 * Umstellen des Labelproviders auf TableColumnViewerLabelProvider.class,
	 * sobald diese public ist. {@inheritDoc}
	 */
	@Override
	public void update(final ViewerCell cell) {
		final Object element = cell.getElement();
		final int index = cell.getColumnIndex();
		cell.setText(getText(element, index));
		final Image image = getImage(element, index);
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
		final IConnector connectorStatistic = (IConnector) element;

		String result = "";

		final Object v = connectorStatistic.getLatestValue();
		switch (columnIndex) {
		case 0:
			result = connectorStatistic.getProcessVariableAddress() != null ? connectorStatistic
					.getProcessVariableAddress().getProperty()
					: "";
			break;
		case 1:
			result = connectorStatistic.getLatestConnectionState() != null ? connectorStatistic
					.getLatestConnectionState().name()
					: "";
			break;
		case 2:
			result = v != null ? (v instanceof Object[] ? Arrays
					.toString((Object[]) v) : v.toString()) : "";

			break;
		default:
			break;
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public String getToolTipText(final Object element) {
		final IConnector connectorStatistic = (IConnector) element;

		final StringBuffer tooltip = new StringBuffer();
		final IProcessVariableAddress pv = connectorStatistic
				.getProcessVariableAddress();
		tooltip.append("full name: " + pv.toString());

		tooltip.append("control system: "
				+ (pv.getControlSystem() != null ? pv.getControlSystem()
						.toString() : "-"));
		tooltip.append("\r\n");
		tooltip.append("channel: "
				+ (pv.getProperty() != null ? pv.getProperty() : "-"));
		tooltip.append("\r\n");
		tooltip.append("characteristic: "
				+ (pv.getCharacteristic() != null ? pv.getCharacteristic()
						: "-"));
		tooltip.append("\r\n");
		tooltip
				.append("type hint: "
						+ (pv.getValueTypeHint() != null ? pv
								.getValueTypeHint() : "-"));
		tooltip.append("\r\n");
		tooltip
				.append("value type: "
						+ (connectorStatistic.getValueType() != null ? connectorStatistic
								.getValueType().toString()
								: "?"));
		tooltip.append("\r\n");
		tooltip
				.append("latest value: "
						+ (connectorStatistic.getLatestValue() != null ? connectorStatistic
								.getLatestValue()
								: "-"));
		tooltip.append("\r\n");
		tooltip
				.append("latest error: "
						+ (connectorStatistic.getLatestError() != null ? connectorStatistic
								.getLatestError()
								: "-"));
		tooltip.append("\r\n");
		tooltip.append("listeners: " + connectorStatistic.getListenerCount());

		return tooltip.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public Point getToolTipShift(final Object object) {
		return new Point(5, 5);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public int getToolTipDisplayDelayTime(final Object object) {
		return 100;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
		final int style = SWT.NORMAL;
		return CustomMediaFactory.getInstance().getDefaultFont(style);
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
		final RGB rgb = new RGB(0, 0, 0);

//		IConnector connectorStatistic = (IConnector) element;
//
//		ConnectionState connectionState = connectorStatistic
//				.getLatestConnectionState();
//
//		if (connectionState == null
//				|| connectionState != ConnectionState.CONNECTED) {
//			rgb = new RGB(200, 0, 0);
//		}
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
		final IConnector connector = (IConnector) element;

		switch (index) {
		case 0:
			final ControlSystemEnum controlSystem = connector.getProcessVariableAddress().getControlSystem();

			String icon = controlSystem.getIconPrefix();

			if (connector.isDisposable()) {
				icon+= "-dispose";
			}
			else if (connector.getLatestConnectionState() == ConnectionState.CONNECTED) {
				icon+= "-connected";
			} else {
				icon+= "-disconnected";
			}

			icon+=".png";

			result = ImageUtil.getInstance().getImage(
					Activator.PLUGIN_ID, icon);
			break;
		default:
			break;
		}

		return result;
	}
}