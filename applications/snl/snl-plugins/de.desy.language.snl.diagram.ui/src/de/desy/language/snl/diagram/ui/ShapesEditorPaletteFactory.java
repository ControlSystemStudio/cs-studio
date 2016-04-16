package de.desy.language.snl.diagram.ui;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;

import de.desy.language.snl.diagram.model.WhenConnection;
import de.desy.language.snl.diagram.model.StateModel;
import de.desy.language.snl.diagram.model.StateSetModel;

/**
 * Utility class that can create a GEF Palette.
 *
 * @see #createPalette()
 */
final class ShapesEditorPaletteFactory {

    /** Create the "Shapes" drawer. */
    private static PaletteContainer createShapesDrawer() {
        final PaletteDrawer componentsDrawer = new PaletteDrawer("Shapes");

        CombinedTemplateCreationEntry component = new CombinedTemplateCreationEntry(
                "State", "Create a State", StateModel.class, new SimpleFactory(
                        StateModel.class), ShapesPlugin
                        .getImageDescriptor("icons/ellipse16.gif"),
                ShapesPlugin.getImageDescriptor("icons/ellipse24.gif"));
        componentsDrawer.add(component);

        component = new CombinedTemplateCreationEntry("StateSet",
                "Create a StateSet", StateSetModel.class, new SimpleFactory(
                        StateSetModel.class), ShapesPlugin
                        .getImageDescriptor("icons/rectangle16.gif"),
                ShapesPlugin.getImageDescriptor("icons/rectangle24.gif"));
        componentsDrawer.add(component);

        return componentsDrawer;
    }

    /**
     * Creates the PaletteRoot and adds all palette elements. Use this factory
     * method to create a new palette for your graphical editor.
     *
     * @return a new PaletteRoot
     */
    static PaletteRoot createPalette() {
        final PaletteRoot palette = new PaletteRoot();
        palette.add(createToolsGroup(palette));
        palette.add(createShapesDrawer());
        return palette;
    }

    /** Create the "Tools" group. */
    private static PaletteContainer createToolsGroup(final PaletteRoot palette) {
        final PaletteGroup toolGroup = new PaletteGroup("Tools");

        // Add a selection tool to the group
        ToolEntry tool = new PanningSelectionToolEntry();
        toolGroup.add(tool);
        palette.setDefaultEntry(tool);

        // Add a marquee tool to the group
        toolGroup.add(new MarqueeToolEntry());

        // Add a (unnamed) separator to the group
        toolGroup.add(new PaletteSeparator());

        // Add (solid-line) connection tool
        tool = new ConnectionCreationToolEntry("Solid connection",
                "Create a solid-line connection", new CreationFactory() {
                    @Override
                    public Object getNewObject() {
                        return null;
                    }

                    // see ShapeEditPart#createEditPolicies()
                    // this is abused to transmit the desired line style
                    @Override
                    public Object getObjectType() {
                        return WhenConnection.SOLID_CONNECTION;
                    }
                }, ShapesPlugin.getImageDescriptor("icons/connection_s16.gif"),
                ShapesPlugin.getImageDescriptor("icons/connection_s24.gif"));
        toolGroup.add(tool);

        return toolGroup;
    }

    /** Utility class. */
    private ShapesEditorPaletteFactory() {
        // Utility class
    }

}