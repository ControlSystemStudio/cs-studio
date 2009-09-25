package org.csstudio.diag.diles.figures;

import org.eclipse.draw2d.IFigure;

public class FigureFactory {
	public static AnalogInputFigure createAnalogInputFigure() {
		return new AnalogInputFigure();
	}

	public static IFigure createAndFigure() {
		return new AndFigure();
	}

	public static ChartFigure createChartFigure() {
		return new ChartFigure();
	}

	public static CommandTrueFalseFigure createCommandTrueFalseFigure() {
		return new CommandTrueFalseFigure();
	}

	public static ComparatorFigure createComparatorFigure() {
		return new ComparatorFigure();
	}

	public static FlipFlopFigure createFlipFlopFigure() {
		return new FlipFlopFigure();
	}

	public static NotFigure createNotFigure() {
		return new NotFigure();
	}

	public static IFigure createOrFigure() {
		return new OrFigure();
	}

	public static HardwareOutFigure createOutFigure() {
		return new HardwareOutFigure();
	}

	public static PathFigure createPathFigure() {
		return new PathFigure();
	}

	public static StatusFigure createStatusFigure() {
		return new StatusFigure();
	}

	public static TDDTimerFigure createTDDTimerFigure() {
		return new TDDTimerFigure();
	}

	public static TDETimerFigure createTDETimerFigure() {
		return new TDETimerFigure();
	}

	public static HardwareTrueFalseFigure createTrueFigure() {
		return new HardwareTrueFalseFigure();
	}

	public static IFigure createXorFigure() {
		return new XorFigure();
	}
}