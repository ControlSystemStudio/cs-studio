/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.sim;

/**
 * A function set that corresponds to all methods in Simulation DataSource.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class SimDSFunctionSet extends DSFunctionSet {

	public static String name = "sim";

	/**
	 * Creates a new set.
	 */
	public SimDSFunctionSet() {
		super(initSet());
	}

	private static DSFunctionSetDescription initSet() {
		DSFunctionSetDescription setDescription = new DSFunctionSetDescription(
				name, "Simulation DataSource");
		DSFunction function = null;
		DSFunction polymorphicFunction = null;

		// delayedConnectionChannel
		function = new DSFunction("delayedConnectionChannel", null, true, false);
		function.addArgument("delayInSeconds", Double.class);
		function.addArgument("value", Object.class);
		function.setDescription("A channel that connects after an initial delay. This is useful to test timeout behavior of applications. The value can be a number, a string, an array of numbers or an array of strings.");
		setDescription.addFunction(function);

		// intermittentChannel
		function = new DSFunction("intermittentChannel", null, true, false);
		function.addArgument("delayInSeconds", Double.class);
		function.addArgument("value", Object.class);
		function.setDescription("A channel that keeps connecting and disonnecting. This is useful to test disconnect behavior of applications. The value can be a number, a string, an array of numbers or an array of strings.");
		setDescription.addFunction(function);

		// const
		polymorphicFunction = new DSFunction("const", null, true, false);
		polymorphicFunction.addArgument("number", Double.class);
		polymorphicFunction
				.setDescription("A constant which can be a number, a string, a number array or a string array.");
		setDescription.addFunction(polymorphicFunction);

		function = new DSFunction("const", null, true, false);
		function.addArgument("string", String.class);
		polymorphicFunction.addPolymorphicFunction(function);

		function = new DSFunction("const", null, true, true);
		function.addArgument("args", Double.class);
		polymorphicFunction.addPolymorphicFunction(function);

		function = new DSFunction("const", null, true, true);
		function.addArgument("args", String.class);
		polymorphicFunction.addPolymorphicFunction(function);

		// flipflop
		polymorphicFunction = new DSFunction("flipflop", null, true, false);
		polymorphicFunction.addArgument("timeStep", Double.class);
		polymorphicFunction
				.setDescription("A boolean value that changes state every timeStep.\nThe default is equivalent to sim://flipflop(0.5).");
		setDescription.addFunction(polymorphicFunction);

		function = new DSFunction("flipflop", null, true, false);
		function.setTooltip("is equivalent to sim://flipflop(0.5)");
		polymorphicFunction.addPolymorphicFunction(function);

		// gaussianNoise
		polymorphicFunction = new DSFunction("gaussianNoise", null, true, false);
		polymorphicFunction.addArgument("average", Double.class);
		polymorphicFunction.addArgument("stdDev", Double.class);
		polymorphicFunction.addArgument("timeStep", Double.class);
		polymorphicFunction
				.setDescription("Random numbers gaussian distributed around average with stdDev width, generated every timeStep.\nThe default is equivalent to sim://gaussianNoise(0, 1, 0.1).");
		setDescription.addFunction(polymorphicFunction);

		function = new DSFunction("gaussianNoise", null, true, false);
		function.setTooltip("is equivalent to sim://gaussianNoise(0, 1, 0.1)");
		polymorphicFunction.addPolymorphicFunction(function);

		// gaussianWaveform
		polymorphicFunction = new DSFunction("gaussianWaveform", null, true,
				false);
		polymorphicFunction.addArgument("periodInSeconds", Double.class);
		polymorphicFunction.addArgument("stdDev", Double.class);
		polymorphicFunction.addArgument("nSamples", Double.class);
		polymorphicFunction.addArgument("updateRateInSeconds", Double.class);
		polymorphicFunction
				.setDescription("A waveform with nSamples shaped like a gaussian that cycles around in time every periodInSeconds, generated every updateRateInSeconds.\nThe default is equivalent to sim://gaussianWaveform(1, 100, 100, 0.1).");
		setDescription.addFunction(polymorphicFunction);

		function = new DSFunction("gaussianWaveform", null, true, false);
		function.setTooltip("is equivalent to sim://gaussianWaveform(1, 100, 100, 0.1)");
		polymorphicFunction.addPolymorphicFunction(function);

		// sineWaveform
		polymorphicFunction = new DSFunction("sineWaveform", null, true, false);
		polymorphicFunction.addArgument("periodInSeconds", Double.class);
		polymorphicFunction.addArgument("wavelengthInSamples", Double.class);
		polymorphicFunction.addArgument("nSamples", Double.class);
		polymorphicFunction.addArgument("updateRateInSeconds", Double.class);
		polymorphicFunction
				.setDescription("A waveform with nSamples shaped like a sine wave of width wavelengthInSamples that cycles around in time every periodInSeconds, generated every updateRateInSeconds.\nThe default is equivalent to sim://sineWaveform(1, 100, 100, 0.1).");
		setDescription.addFunction(polymorphicFunction);

		function = new DSFunction("sineWaveform", null, true, false);
		function.setTooltip("is equivalent to sim://sineWaveform(1, 100, 100, 0.1)");
		polymorphicFunction.addPolymorphicFunction(function);

		// squareWaveform
		polymorphicFunction = new DSFunction("squareWaveform", null, true,
				false);
		polymorphicFunction.addArgument("periodInSeconds", Double.class);
		polymorphicFunction.addArgument("wavelengthInSamples", Double.class);
		polymorphicFunction.addArgument("nSamples", Double.class);
		polymorphicFunction.addArgument("updateRateInSeconds", Double.class);
		polymorphicFunction
				.setDescription("A waveform with nSamples shaped like a square wave of width wavelengthInSamples that cycles around in time every periodInSeconds, generated every updateRateInSeconds.\nThe default is equivalent to sim://squareWaveform(1, 100, 100, 0.1).");
		setDescription.addFunction(polymorphicFunction);

		function = new DSFunction("squareWaveform", null, true, false);
		function.setTooltip("is equivalent to sim://squareWaveform(1, 100, 100, 0.1)");
		polymorphicFunction.addPolymorphicFunction(function);

		// triangleWaveform
		polymorphicFunction = new DSFunction("triangleWaveform", null, true,
				false);
		polymorphicFunction.addArgument("periodInSeconds", Double.class);
		polymorphicFunction.addArgument("wavelengthInSamples", Double.class);
		polymorphicFunction.addArgument("nSamples", Double.class);
		polymorphicFunction.addArgument("updateRateInSeconds", Double.class);
		polymorphicFunction
				.setDescription("A waveform with nSamples shaped like a triangle wave of width wavelengthInSamples that cycles around in time every periodInSeconds, generated every updateRateInSeconds.\nThe default is equivalent to sim://triangleWaveform(1, 100, 100, 0.1).");
		setDescription.addFunction(polymorphicFunction);

		function = new DSFunction("triangleWaveform", null, true, false);
		function.setTooltip("is equivalent to sim://triangleWaveform(1, 100, 100, 0.1)");
		polymorphicFunction.addPolymorphicFunction(function);

		// sawtoothWaveform
		polymorphicFunction = new DSFunction("sawtoothWaveform", null, true,
				false);
		polymorphicFunction.addArgument("periodInSeconds", Double.class);
		polymorphicFunction.addArgument("wavelengthInSamples", Double.class);
		polymorphicFunction.addArgument("nSamples", Double.class);
		polymorphicFunction.addArgument("updateRateInSeconds", Double.class);
		polymorphicFunction
				.setDescription("A waveform with nSamples shaped like a sawtooth wave of width wavelengthInSamples that cycles around in time every periodInSeconds, generated every updateRateInSeconds.\nThe default is equivalent to sim://sawtoothWaveform(1, 100, 100, 0.1).");
		setDescription.addFunction(polymorphicFunction);

		function = new DSFunction("sawtoothWaveform", null, true, false);
		function.setTooltip("is equivalent to sim://sawtoothWaveform(1, 100, 100, 0.1)");
		polymorphicFunction.addPolymorphicFunction(function);

		// noise
		polymorphicFunction = new DSFunction("noise", null, true, false);
		polymorphicFunction.addArgument("minValue", Double.class);
		polymorphicFunction.addArgument("maxValue", Double.class);
		polymorphicFunction.addArgument("timeStep", Double.class);
		polymorphicFunction
				.setDescription("Random numbers uniformly distributed between the minValue and the maxValue, generated every timeStep.\nThe default is equivalent to sim://noise(-5, 5, 1).");
		setDescription.addFunction(polymorphicFunction);

		function = new DSFunction("noise", null, true, false);
		function.setTooltip("is equivalent to sim://noise(-5, 5, 1)");
		polymorphicFunction.addPolymorphicFunction(function);

		// noiseWaveform
		polymorphicFunction = new DSFunction("noiseWaveform", null, true, false);
		polymorphicFunction.addArgument("minValue", Double.class);
		polymorphicFunction.addArgument("maxValue", Double.class);
		polymorphicFunction.addArgument("nSamples", Double.class);
		polymorphicFunction.addArgument("timeStep", Double.class);
		polymorphicFunction
				.setDescription("A waveform filled with nSampes random numbers uniformly distributed between the minValue and the maxValue, generated every timeStep.\nThe default is equivalent to sim://noiseWaveform(-5, 5, 100, 1).");
		setDescription.addFunction(polymorphicFunction);

		function = new DSFunction("noiseWaveform", null, true, false);
		function.addArgument("minValue", Double.class);
		function.addArgument("maxValue", Double.class);
		function.addArgument("timeStep", Double.class);
		polymorphicFunction.addPolymorphicFunction(function);

		function = new DSFunction("noiseWaveform", null, true, false);
		function.setTooltip("is equivalent to sim://noiseWaveform(-5, 5, 100, 1)");
		polymorphicFunction.addPolymorphicFunction(function);

		// ramp
		polymorphicFunction = new DSFunction("ramp", null, true, false);
		polymorphicFunction.addArgument("minValue", Double.class);
		polymorphicFunction.addArgument("maxValue", Double.class);
		polymorphicFunction.addArgument("step", Double.class);
		polymorphicFunction.addArgument("timeStep", Double.class);
		polymorphicFunction
				.setDescription("A ramp (sawtooth wave) between the minValue and maxValue, increasing by the step amount every timeStep. If step is negative, the wave starts at maxValue and decreases.\nThe default is equivalent to sim://ramp(-5, 5, 1, 1).");
		setDescription.addFunction(polymorphicFunction);

		function = new DSFunction("ramp", null, true, false);
		function.addArgument("minValue", Double.class);
		function.addArgument("maxValue", Double.class);
		function.addArgument("timeStep", Double.class);
		polymorphicFunction.addPolymorphicFunction(function);

		function = new DSFunction("ramp", null, true, false);
		function.setTooltip("is equivalent to sim://ramp(-5, 5, 1, 1)");
		polymorphicFunction.addPolymorphicFunction(function);

		// sine
		polymorphicFunction = new DSFunction("sine", null, true, false);
		polymorphicFunction.addArgument("minValue", Double.class);
		polymorphicFunction.addArgument("maxValue", Double.class);
		polymorphicFunction.addArgument("samplePerCycle", Double.class);
		polymorphicFunction.addArgument("timeStep", Double.class);
		polymorphicFunction
				.setDescription("A sine wave between the minValue and maxValue divided into samplePerCycle samples in each period, updating every timeStep.\nThe default is equivalent to sim://sine(-5, 5, 10, 1).");
		setDescription.addFunction(polymorphicFunction);

		function = new DSFunction("sine", null, true, false);
		function.addArgument("minValue", Double.class);
		function.addArgument("maxValue", Double.class);
		function.addArgument("timeStep", Double.class);
		polymorphicFunction.addPolymorphicFunction(function);

		function = new DSFunction("sine", null, true, false);
		function.setTooltip("is equivalent to sim://sine(-5, 5, 10, 1)");
		polymorphicFunction.addPolymorphicFunction(function);

		// strings
		polymorphicFunction = new DSFunction("strings", null, true, false);
		polymorphicFunction.addArgument("timeStep", Double.class);
		polymorphicFunction
				.setDescription("A string generated every timeStamp.\nThe default is equivalent to sim://strings(0.1).");
		setDescription.addFunction(polymorphicFunction);

		function = new DSFunction("strings", null, true, false);
		function.setTooltip("is equivalent to sim://strings(0.1)");
		polymorphicFunction.addPolymorphicFunction(function);

		// table
		polymorphicFunction = new DSFunction("table", null, true, false);
		polymorphicFunction.addArgument("timeStep", Double.class);
		polymorphicFunction
				.setDescription("Generates a table.\nThe default is equivalent to sim://table(0.1).");
		setDescription.addFunction(polymorphicFunction);

		function = new DSFunction("table", null, true, false);
		function.setTooltip("is equivalent to sim://table(0.1)");
		polymorphicFunction.addPolymorphicFunction(function);

		return setDescription;
	}
}
