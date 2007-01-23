package org.csstudio.sds.components.epics;

import org.csstudio.sds.components.model.RectangleElement;
import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.model.AliasDescriptor;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;
import org.csstudio.sds.model.initializers.IElementModelInitializer;
import org.csstudio.sds.model.logic.DirectConnectionRule;
import org.csstudio.sds.model.logic.ParameterDescriptor;

/**
 * @author Stefan Hofer
 * @version $Revision$
 *
 */
public class EpicsRectangleInitializer implements IElementModelInitializer {

	/**
	 * {@inheritDoc}
	 */
	public void initialize(AbstractElementModel model,
			AbstractControlSystemSchema schema) {
		
		assert model instanceof RectangleElement : "Precondition violated: model instanceof RectangleElement"; //$NON-NLS-1$
	
		model.setPropertyValue(AbstractElementModel.PROP_BACKGROUND_COLOR, schema.getColorProperty(AbstractElementModel.PROP_BACKGROUND_COLOR));
		model.setPropertyValue(AbstractElementModel.PROP_FOREGROUND_COLOR, schema.getColorProperty(AbstractElementModel.PROP_FOREGROUND_COLOR));
		
		AliasDescriptor aliasDescriptor = new AliasDescriptor("record", "", "Enter the record name into the 'value' column.");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		model.addAliasDescriptor(aliasDescriptor);
		
		final DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor(DirectConnectionRule.TYPE_ID);
		dynamicsDescriptor.addInputParameterBinding(new ParameterDescriptor("$record$_calc", Double.class)); //$NON-NLS-1$
		model.setDynamicsDescriptor(RectangleElement.PROP_FILL_PERCENTAGE, dynamicsDescriptor);
	}

}
