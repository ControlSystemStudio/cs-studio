package org.csstudio.opibuilder.properties;

import java.util.List;

import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

public class MatrixProperty extends AbstractWidgetProperty {
	
	/**
	 * XML ELEMENT name for a row.
	 */
	public static final String XML_ELEMENT_ROW= "row"; //$NON-NLS-1$
	
	/**
	 * XML ELEMENT name for a column.
	 */
	public static final String XML_ELEMENT_COLUMN= "col"; //$NON-NLS-1$
	
	public MatrixProperty(String prop_id, String description,
			WidgetPropertyCategory category, double[][] defaultValue) {
		super(prop_id, description, category, defaultValue);
	}

	@Override
	public Object checkValue(Object value) {
		if (value == null)
			return null;
		double[][] acceptableValue = null;
		if (value instanceof double[][]) {
			acceptableValue = (double[][]) value;
		}
		return acceptableValue;
	}
	
	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		if (PropertySSHelper.getIMPL() == null)
			return null;
		PropertyDescriptor propertyDescriptor = PropertySSHelper.getIMPL()
				.getMatrixPropertyDescriptor(prop_id, description);
		return propertyDescriptor;
	}

	@Override
	public void writeToXML(Element propElement) {
		double[][] data = (double[][]) propertyValue;
		for (double row[] : data) {
			Element rowElement = new Element(XML_ELEMENT_ROW);
			for (double e : row) {
				Element colElement = new Element(XML_ELEMENT_COLUMN);
				colElement.setText(Double.toString(e));
				rowElement.addContent(colElement);
			}
			propElement.addContent(rowElement);
		}
	}

	@Override
	public double[][] readValueFromXML(Element propElement) throws Exception {
		List<?> rowChildren = propElement.getChildren();
		if (rowChildren.size() == 0)
			return null;
		double[][] result = new double[rowChildren.size()][((Element) rowChildren
				.get(0)).getChildren().size()];
		int i = 0, j = 0;
		for (Object oe : rowChildren) {
			Element re = (Element) oe;
			if (re.getName().equals(XML_ELEMENT_ROW)) {
				j = 0;
				for (Object oc : re.getChildren()) {
					result[i][j++] = Double.parseDouble(((Element) oc)
							.getText());
				}
				i++;
			}
		}
		return result;
	}

}
