package org.csstudio.opibuilder.actions;

import java.util.List;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

public class OPIWidgetsTransfer extends ByteArrayTransfer {
	
	private static OPIWidgetsTransfer instance;
	
	private static final String TYPE_NAME = "OPIWidgetsTransfer:"
			+ System.currentTimeMillis();
	
	private static final int TYPEID = registerType(TYPE_NAME);
	
	
	public static OPIWidgetsTransfer getInstance() {
		if(instance == null)
			instance = new OPIWidgetsTransfer();
		return instance;
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] {TYPEID};
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] {TYPE_NAME};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		if (!isSupportedType(transferData) || !(checkInput(object))) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		
		List<AbstractWidgetModel> widgets = (List<AbstractWidgetModel>)object;
		
		DisplayModel tempModel = new DisplayModel();
		
		for(AbstractWidgetModel widget : widgets){
			tempModel.addChild(widget);
		}
		
		String xml = XMLUtil.WidgetToXMLString(tempModel, false);
		
		super.javaToNative(xml.getBytes(), transferData);
	}
	
	@Override
	protected Object nativeToJava(TransferData transferData) {
		if(!isSupportedType(transferData))
			return null;
		byte[] bytes = (byte[])super.nativeToJava(transferData);
		if(bytes == null)
			return null;
		try {
			DisplayModel displayModel = (DisplayModel) XMLUtil.XMLStringToWidget(new String(bytes));
			List<AbstractWidgetModel> widgets = displayModel.getChildren();
			return widgets;
		} catch (Exception e) {
			CentralLogger.getInstance().error(this, "Failed to transfer XML to widget", e);
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

		if (input instanceof List<?>) {
			List<?> list = (List<?>) input;

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

}
