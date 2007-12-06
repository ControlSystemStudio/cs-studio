package org.csstudio.platform.internal.simpledal.local;

import java.util.Random;

public class RandomDoubleGenerator extends AbstractDataGenerator<Double> {
	private double _min;
	private double _max;

	public RandomDoubleGenerator(LocalChannel localChannel, int defaultPeriod,
			String[] options) {
		super(localChannel, defaultPeriod, options);
	}


	@Override
	protected void init(String[] options) {
		try {
			_min = Double.parseDouble(options[0]);
		} catch (NumberFormatException nfe) {
			_min = 0;
		}

		try {
			_max = Double.parseDouble(options[1]);
		} catch (NumberFormatException nfe) {
			_max = 1;
		}

		try {
			int period = Integer.parseInt(options[2]);
			setPeriod(period);
		} catch (NumberFormatException nfe) {
			// ignore
		}

		if (_min > _max) {
			double tmp = _min;
			_min = _max;
			_max = tmp;
		}
	}

	@Override
	protected Double generateNextValue() {
		Random rd = new Random(System.currentTimeMillis());

		double d = rd.nextDouble();

		double result = _min + ((_max - _min) * d);

		return result;
	}

}
