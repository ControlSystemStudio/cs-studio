package org.csstudio.sds.ui.internal.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.persistence.PersistenceUtil;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

/**
 * Transfer type implementation for widget models. This implementation uses the
 * normal persistence mechanisms to convert a list of widget models to their xml
 * representation. This xml is then converted to a byte array.
 * 
 * @author swende
 * 
 */
public final class WidgetModelTransfer extends ByteArrayTransfer {

	/**
	 * Type name for this transfer type.
	 */
	private static final String TYPENAME = "sds_widgets_list"; //$NON-NLS-1$

	/**
	 * Type ID for this transfer type.
	 */
	private static final int TYPEID = registerType(TYPENAME);

	/**
	 * The singleton instance.
	 */
	private static WidgetModelTransfer _instance;

	/**
	 * Private constructor (singleton pattern).
	 * 
	 */
	private WidgetModelTransfer() {

	}

	/**
	 * Returns the singleton instance.
	 * 
	 * @return the singleton instance
	 */
	public static WidgetModelTransfer getInstance() {
		if (_instance == null) {
			_instance = new WidgetModelTransfer();
		}
		return _instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void javaToNative(final Object object,
			final TransferData transferData) {
		if (!isSupportedType(transferData) || !(checkInput(object))) {
			DND.error(DND.ERROR_INVALID_DATA);
		}

		List<AbstractWidgetModel> widgets = (List<AbstractWidgetModel>) object;

		// create a temporary display model
		DisplayModel tmpModel = new DisplayModel();
		tmpModel.disableParentChecks();

		for (AbstractWidgetModel widget : widgets) {
			tmpModel.addWidget(widget);
		}

		// convert the temporary display model to a byte array
		InputStream is = PersistenceUtil.createStream(tmpModel);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(bos);

		int nextByte;
		try {
			while ((nextByte = is.read()) > -1) {
				writer.write(nextByte);
			}
		} catch (IOException e) {
			CentralLogger.getInstance().debug(this, e);
		}

		try {
			writer.flush();
		} catch (IOException e) {
			CentralLogger.getInstance().debug(this, e);
		}

		byte[] bytes = bos.toByteArray();

		// clean up
		try {
			is.close();
			bos.close();
			writer.close();
		} catch (IOException e) {
			CentralLogger.getInstance().debug(this, e);
		}

		// store the byte array
		super.javaToNative(bytes, transferData);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object nativeToJava(final TransferData transferData) {
		if (isSupportedType(transferData)) {

			byte[] bytes = (byte[]) super.nativeToJava(transferData);

			DisplayModel displayModel = new DisplayModel();
			displayModel.disableParentChecks();
			PersistenceUtil.syncFillModel(displayModel,
					new ByteArrayInputStream(bytes));

			List<AbstractWidgetModel> widgets = displayModel.getWidgets();

			return widgets;
		}

		return null;
	}

	/**
	 * Checks the provided input, which must be a non-empty list that contains
	 * only objects of type {@link AbstractWidgetModel}.
	 * 
	 * @param input
	 *            the input to check
	 * @return true, if the input object is valid, false otherwise
	 */
	private boolean checkInput(final Object input) {
		boolean result = true;

		if (input instanceof List) {
			List list = (List) input;

			if (list.size() > 0) {
				for (Object o : list) {
					if (!(o instanceof AbstractWidgetModel)) {
						result = false;
					}
				}
			} else {
				result = false;
			}
		} else {
			result = false;
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPENAME };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

}
