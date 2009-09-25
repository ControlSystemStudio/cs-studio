package org.csstudio.diag.diles.editpart;

import org.csstudio.diag.diles.model.AnalogInput;
import org.csstudio.diag.diles.model.And;
import org.csstudio.diag.diles.model.Chart;
import org.csstudio.diag.diles.model.CommandTrueFalse;
import org.csstudio.diag.diles.model.Comparator;
import org.csstudio.diag.diles.model.FlipFlop;
import org.csstudio.diag.diles.model.HardwareOut;
import org.csstudio.diag.diles.model.HardwareTrueFalse;
import org.csstudio.diag.diles.model.Not;
import org.csstudio.diag.diles.model.Or;
import org.csstudio.diag.diles.model.Path;
import org.csstudio.diag.diles.model.Status;
import org.csstudio.diag.diles.model.TDDTimer;
import org.csstudio.diag.diles.model.TDETimer;
import org.csstudio.diag.diles.model.Xor;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

public class PartFactory implements EditPartFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart,
	 * java.lang.Object)
	 */
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		if (model instanceof And)
			part = new AndPart();
		else if (model instanceof Or)
			part = new OrPart();
		else if (model instanceof Xor)
			part = new XorPart();
		else if (model instanceof Path)
			part = new PathPart();
		else if (model instanceof Chart)
			part = new ChartPart();
		else if (model instanceof HardwareTrueFalse)
			part = new HardwareTrueFalsePart();
		else if (model instanceof FlipFlop)
			part = new FlipFlopPart();
		else if (model instanceof Not)
			part = new NotPart();
		else if (model instanceof TDETimer)
			part = new TDETimerPart();
		else if (model instanceof TDDTimer)
			part = new TDDTimerPart();
		else if (model instanceof HardwareOut)
			part = new HardwareOutPart();
		else if (model instanceof Status)
			part = new StatusPart();
		else if (model instanceof CommandTrueFalse)
			part = new CommandTrueFalsePart();
		else if (model instanceof Comparator)
			part = new ComparatorPart();
		else if (model instanceof AnalogInput)
			part = new AnalogInputPart();
		part.setModel(model);
		return part;
	}
}