package org.csstudio.diag.diles.model;

import org.csstudio.diag.diles.palette.DilesPalette;
import org.eclipse.gef.requests.CreationFactory;

public class ModelFactory implements CreationFactory {
	/**
	 * Use DilesEditor.getChart() instead.
	 * 
	 * @return
	 */
	@Deprecated
	public static Chart getChart() {
		return new Chart();
	}

	private String template;

	public ModelFactory(Object str) {
		template = (String) str;
	}

	public Object getNewObject() {
		if (DilesPalette.XOR_TEMPLATE.equals(template))
			return new Xor();
		if (DilesPalette.AND_TEMPLATE.equals(template))
			return new And();
		if (DilesPalette.OR_TEMPLATE.equals(template))
			return new Or();
		if (DilesPalette.HARDWARE_TRUE_TEMPLATE.equals(template))
			return new HardwareTrueFalse();
		if (DilesPalette.FALSE_TEMPLATE.equals(template)) {
			HardwareTrueFalse t = new HardwareTrueFalse();
			t.setResult(false);
			return t;
		}
		if (DilesPalette.FLIPFLOP_TEMPLATE.equals(template)) {
			return new FlipFlop();
		}
		if (DilesPalette.NOT_TEMPLATE.equals(template)) {
			return new Not();
		}
		if (DilesPalette.TDETIMER_TEMPLATE.equals(template)) {
			return new TDETimer();
		}
		if (DilesPalette.TDDTIMER_TEMPLATE.equals(template)) {
			return new TDDTimer();
		}
		if (DilesPalette.HARDWARE_OUT_TEMPLATE.equals(template)) {
			return new HardwareOut();
		}
		if (DilesPalette.COMMAND_TRUE_TEMPLATE.equals(template)) {
			return new CommandTrueFalse();
		}
		if (DilesPalette.STATUS_TEMPLATE.equals(template)) {
			return new Status();
		}
		if (DilesPalette.COMPARATOR_TEMPLATE.equals(template)) {
			return new Comparator();
		}
		if (DilesPalette.ANALOG_INPUT_TEMPLATE.equals(template)) {
			return new AnalogInput();
		}
		return null;
	}

	public Object getObjectType() {
		return template;
	}

	public Path getPath() {
		return new Path();
	}
}