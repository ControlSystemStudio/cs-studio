package org.csstudio.diag.diles.palette;

import org.csstudio.diag.diles.model.ModelFactory;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.jface.resource.ImageDescriptor;

public class DilesPalette {
	public static final String XOR_TEMPLATE = "XOR_TEMP",
			AND_TEMPLATE = "AND_TEMP", OR_TEMPLATE = "OR_TEMP",
			HARDWARE_TRUE_TEMPLATE = "TRUE_TEMP",
			FALSE_TEMPLATE = "FALSE_TEMP", FLIPFLOP_TEMPLATE = "FLIPFLOP_TEMP",
			NOT_TEMPLATE = "NOT_TEMP", TDETIMER_TEMPLATE = "TDETIMER_TEMP",
			TDDTIMER_TEMPLATE = "TDDTIMER_TEMP",
			HARDWARE_OUT_TEMPLATE = "OUT_TEMPLATE",
			COMMAND_TRUE_TEMPLATE = "COMMAND_TRUE_TEMP",
			STATUS_TEMPLATE = "STATUS_TEMP",
			COMPARATOR_TEMPLATE = "COMPARATOR_TEMP",
			ANALOG_INPUT_TEMPLATE = "ANALOG_INPUT_TEMP";

	public static PaletteRoot getPaletteRoot() {
		PaletteRoot root = new PaletteRoot();

		PaletteGroup toolGroup = new PaletteGroup("Tools");

		ToolEntry tool = new SelectionToolEntry();
		toolGroup.add(tool);
		root.setDefaultEntry(tool);

		tool = new MarqueeToolEntry();
		toolGroup.add(tool);

		tool = new ConnectionCreationToolEntry("Connection",
				"Used to connect multiple components", null, ImageDescriptor
						.createFromFile(DilesPalette.class, "icons/conn.gif"),
				ImageDescriptor.getMissingImageDescriptor());
		toolGroup.add(tool);

		root.add(toolGroup);

		PaletteSeparator sep = new PaletteSeparator();
		root.add(sep);

		PaletteGroup templateGroup = new PaletteGroup("Inputs & Outputs");

		CombinedTemplateCreationEntry entry = new CombinedTemplateCreationEntry(
				"Input (HW)", "Hardware input", new ModelFactory(
						HARDWARE_TRUE_TEMPLATE), ImageDescriptor
						.createFromFile(DilesPalette.class, "icons/true.png"),
				ImageDescriptor.getMissingImageDescriptor());
		templateGroup.add(entry);

		entry = new CombinedTemplateCreationEntry("Output (HW)",
				"Hardware output", new ModelFactory(HARDWARE_OUT_TEMPLATE),
				ImageDescriptor.createFromFile(DilesPalette.class,
						"icons/out.png"), ImageDescriptor
						.getMissingImageDescriptor());
		templateGroup.add(entry);

		entry = new CombinedTemplateCreationEntry("Command", "Command",
				new ModelFactory(COMMAND_TRUE_TEMPLATE), ImageDescriptor
						.createFromFile(DilesPalette.class, "icons/true.png"),
				ImageDescriptor.getMissingImageDescriptor());
		templateGroup.add(entry);

		entry = new CombinedTemplateCreationEntry("Status", "Status",
				new ModelFactory(STATUS_TEMPLATE), ImageDescriptor
						.createFromFile(DilesPalette.class, "icons/out.png"),
				ImageDescriptor.getMissingImageDescriptor());
		templateGroup.add(entry);

		entry = new CombinedTemplateCreationEntry("Analog Input",
				"AnalogInput", new ModelFactory(ANALOG_INPUT_TEMPLATE),
				ImageDescriptor.createFromFile(DilesPalette.class, "icons/analog.png"), ImageDescriptor
						.getMissingImageDescriptor());
		templateGroup.add(entry);

		entry = new CombinedTemplateCreationEntry("Comparator", "Comparator",
				new ModelFactory(COMPARATOR_TEMPLATE), ImageDescriptor
						.createFromFile(DilesPalette.class, "icons/comparator.png"), ImageDescriptor
						.getMissingImageDescriptor());
		templateGroup.add(entry);

		root.add(templateGroup);

		sep = new PaletteSeparator();
		root.add(sep);

		templateGroup = new PaletteGroup("Templates");

		entry = new CombinedTemplateCreationEntry("Not",
				"Outputs the opposite of the input", NOT_TEMPLATE,
				new ModelFactory(NOT_TEMPLATE), ImageDescriptor.createFromFile(
						DilesPalette.class, "icons/not.png"), ImageDescriptor
						.getMissingImageDescriptor());
		templateGroup.add(entry);

		entry = new CombinedTemplateCreationEntry("And",
				"Returns TRUE only if both are TRUE", AND_TEMPLATE,
				new ModelFactory(AND_TEMPLATE), ImageDescriptor.createFromFile(
						DilesPalette.class, "icons/and.png"), ImageDescriptor
						.getMissingImageDescriptor());
		templateGroup.add(entry);

		entry = new CombinedTemplateCreationEntry("Or",
				"Returns TRUE if at least one is TRUE", OR_TEMPLATE,
				new ModelFactory(OR_TEMPLATE), ImageDescriptor.createFromFile(
						DilesPalette.class, "icons/or.png"), ImageDescriptor
						.getMissingImageDescriptor());
		templateGroup.add(entry);

		entry = new CombinedTemplateCreationEntry("Xor",
				"Returns TRUE only if one is TRUE", XOR_TEMPLATE,
				new ModelFactory(XOR_TEMPLATE), ImageDescriptor.createFromFile(
						DilesPalette.class, "icons/xor.png"), ImageDescriptor
						.getMissingImageDescriptor());
		templateGroup.add(entry);

		entry = new CombinedTemplateCreationEntry("Flip Flop", "Flip Flop",
				new ModelFactory(FLIPFLOP_TEMPLATE), ImageDescriptor
						.createFromFile(DilesPalette.class,
								"icons/flipflop.png"), ImageDescriptor
						.getMissingImageDescriptor());
		templateGroup.add(entry);

		entry = new CombinedTemplateCreationEntry("TDE Timer",
				"Time Delay after Energization", new ModelFactory(
						TDETIMER_TEMPLATE), ImageDescriptor.createFromFile(
						DilesPalette.class, "icons/tde.png"), ImageDescriptor
						.getMissingImageDescriptor());
		templateGroup.add(entry);

		entry = new CombinedTemplateCreationEntry("TDD Timer",
				"Time Delay after De-energization", new ModelFactory(
						TDDTIMER_TEMPLATE), ImageDescriptor.createFromFile(
						DilesPalette.class, "icons/tdd.png"), ImageDescriptor
						.getMissingImageDescriptor());
		templateGroup.add(entry);

		root.add(templateGroup);

		return root;
	}
}