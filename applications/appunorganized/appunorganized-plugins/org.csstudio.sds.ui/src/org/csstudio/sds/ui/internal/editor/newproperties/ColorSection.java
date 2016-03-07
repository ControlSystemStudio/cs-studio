package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.internal.model.ColorProperty;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.editor.newproperties.colorservice.NamedColor;
import org.csstudio.sds.util.ColorAndFontUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Section for {@link ColorProperty}.
 *
 * @author Sven Wende
 *
 */
public class ColorSection extends AbstractTextSection<ColorProperty, String> implements ISelectionChangedListener {

    static ImageRegistry imageRegistry = new ImageRegistry();

    private Label colorPreview;

    public ColorSection(final String propertyId) {
        super(propertyId);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected String getConvertedValue(final String text) {
        return text;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doCreateControls(final Composite parent, final TabbedPropertySheetPage tabbedPropertySheetPage) {
        super.doCreateControls(parent, tabbedPropertySheetPage);
        FormData fd;

        Font font = SdsUiPlugin.getDefault().getColorAndFontService().getFont("Courier, 10");
        getTextControl().setFont(font);

        // .. preview icon
        colorPreview = getWidgetFactory().createLabel(parent, "");

        // .. button to open the color dialog
        Hyperlink chooseLink = getWidgetFactory().createHyperlink(parent, "Choose ...", SWT.NONE);
        chooseLink.setUnderlined(false);

        chooseLink.addHyperlinkListener(new HyperlinkAdapter(){
            @Override
            public void linkActivated(final HyperlinkEvent e) {
                ColorDialog d = new ColorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
                if ((mainWidgetProperty != null) && (mainWidgetProperty.getPropertyValue() != null)) {
                    String propertyValue = mainWidgetProperty.getPropertyValue();
                    RGB oldRgb = SdsUiPlugin.getDefault().getColorAndFontService()
                            .getColor(propertyValue).getRGB();
                    d.setRGB(oldRgb);
                }
                RGB rgb = d.open();
                if (rgb != null) {
                    applyPropertyChange(ColorAndFontUtil.toHex(rgb.red, rgb.green, rgb.blue));
                }
            }
        });

        // .. layout
        fd = new FormData();
        fd.right = new FormAttachment(50,0);
        chooseLink.setLayoutData(fd);

        fd = new FormData();
        fd.right = new FormAttachment(chooseLink, -5);
        colorPreview.setLayoutData(fd);

        fd = new FormData();
        fd.left = new FormAttachment(0,0);
        fd.right = new FormAttachment(colorPreview,-5);
        getTextControl().setLayoutData(fd);

    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doRefreshControls(final ColorProperty widgetProperty) {
        // .. refresh colored preview icon
        if (widgetProperty != null) {
            String hexOrId = widgetProperty.getPropertyValue();
            setCurrentText(hexOrId);

            RGB rgb = SdsUiPlugin.getDefault().getColorAndFontService().getColor(hexOrId).getRGB();

            if (rgb != null) {
                colorPreview.setImage(getIcon(rgb, 20, 20));
            } else {
                colorPreview.setImage(getNoColorIcon(20, 20));
            }
        }
    }

    /**
     * Creates a small icon using the specified color.
     *
     * @param rgb
     *            the color
     * @return an icon
     */
    private Image getIcon(final RGB rgb, final int width, final int height) {
        assert rgb != null : "rgb!=null"; //$NON-NLS-1$

        String id = "SDS.COLORPROPERTY.ICON_" + rgb.red + "_" + rgb.green + "_" + rgb.blue;

        if (imageRegistry.get(id) == null) {
            Color color = CustomMediaFactory.getInstance().getColor(rgb);

            // create new graphics context, to draw on
            Image image = new Image(Display.getCurrent(), width, height);
            GC gc = new GC(image);

            // draw transparent background
            Color bg = CustomMediaFactory.getInstance().getColor(255, 255, 255);
            gc.setBackground(bg);
            gc.fillRectangle(0, 0, width, height);
            // draw icon
            gc.setBackground(color);
            Rectangle r = new Rectangle(0, 0, width - 1, height - 1);
            gc.fillRectangle(r);
            gc.setBackground(CustomMediaFactory.getInstance().getColor(0, 0, 0));
            gc.drawRectangle(r);
            gc.dispose();

            // setup tranparency
            image.getImageData().transparentPixel = image.getImageData().palette.getPixel(new RGB(255, 255, 255));

            imageRegistry.put(id, image);
        }

        return imageRegistry.get(id);
    }

    /**
     * Creates a small icon using the specified color.
     *
     * @param rgb
     *            the color
     * @return an icon
     */
    private Image getNoColorIcon(final int width, final int height) {
        String id = "SDS.COLORPROPERTY.ICON_NO_COLOR";

        if (imageRegistry.get(id) == null) {
            // create new graphics context, to draw on
            Image image = new Image(Display.getCurrent(), width, height);
            GC gc = new GC(image);

            // draw transparent background
            Color bg = CustomMediaFactory.getInstance().getColor(255, 255, 255);
            gc.setBackground(bg);
            gc.fillRectangle(0, 0, width, height);
            // draw lines
            gc.drawLine(0, 0, 20, 20);
            gc.dispose();

            // setup tranparency
            image.getImageData().transparentPixel = image.getImageData().palette.getPixel(new RGB(255, 255, 255));

            imageRegistry.put(id, image);
        }

        return imageRegistry.get(id);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void selectionChanged(final SelectionChangedEvent event) {
        final NamedColor namedColor = (NamedColor) ((IStructuredSelection) event.getSelection()).getFirstElement();
        applyPropertyChange(namedColor.getHex());
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected List<IContentProposal> getContentProposals(final ColorProperty property, final AbstractWidgetModel selectedWidget,
            final List<AbstractWidgetModel> selectedWidgets) {
        List<NamedColor> colors = SdsUiPlugin.getDefault().getColorAndFontService().listAvailableColors();

        List<IContentProposal> proposals = new ArrayList<IContentProposal>();
        for (NamedColor c : colors) {
            proposals.add(new NamedColorContentProposal(c));
        }
        return proposals;
    }

    /**
     * Proposal for named colors.
     *
     * @author Sven Wende
     *
     */
    private static final class NamedColorContentProposal implements IContentProposal {
        private final NamedColor namedColor;

        public NamedColorContentProposal(final NamedColor namedColor) {
            assert namedColor != null;
            this.namedColor = namedColor;
        }

        @Override
        public String getContent() {
            return "${" + namedColor.getName() + "}";
        }

        @Override
        public int getCursorPosition() {
            return 0;
        }

        @Override
        public String getDescription() {
            return namedColor.getDescription();
        }

        @Override
        public String getLabel() {
            return namedColor.getName();
        }

    }

}