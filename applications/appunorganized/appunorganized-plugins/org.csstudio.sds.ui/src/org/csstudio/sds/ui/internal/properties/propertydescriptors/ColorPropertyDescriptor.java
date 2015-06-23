/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.sds.ui.internal.properties.propertydescriptors;

import org.csstudio.sds.model.PropertyTypesEnum;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.properties.RGBCellEditor;
import org.csstudio.sds.ui.properties.PropertyDescriptor;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Descriptor for a property that has a color value which should be edited with
 * a color cell editor.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 * Example:
 *
 * <pre>
 * IPropertyDescriptor pd = new ColorPropertyDescriptor(&quot;fg&quot;, &quot;Foreground Color&quot;);
 * </pre>
 *
 * </p>
 *
 * @author Sven Wende
 */
public final class ColorPropertyDescriptor extends PropertyDescriptor {

    static ImageRegistry imageRegistry = new ImageRegistry();

    /**
     * Creates an property descriptor with the given id and display name.
     *
     * @param id
     *            the id of the property
     * @param displayName
     *            the name to display for the property
     */
    public ColorPropertyDescriptor(final Object id, final String displayName, PropertyTypesEnum type) {
        super(id, displayName, type);

        setLabelProvider(new RgbLabelProvider());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CellEditor createPropertyEditor(final Composite parent) {
        //CellEditor editor = new ColorCellEditor(parent);
        RGBCellEditor editor = new RGBCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }


        return new TextCellEditor(parent);
    }

    /**
     * A label provider for RGB value, which displays a small colored icon and
     * the RGB value as String as well.
     *
     * @author swende
     *
     */
    private final class RgbLabelProvider extends LabelProvider {

        /**
         * {@inheritDoc}
         */
        @Override
        public Image getImage(final Object element) {
            Color color = SdsUiPlugin.getDefault().getColorAndFontService().getColor((String)element);
            RGB rgb = color.getRGB();
            String id = "SDS.COLORPROPERTY.ICON_"+rgb.red+"_"+rgb.green+"_"+rgb.blue;

            if(imageRegistry.get(id)==null) {
                imageRegistry.put(id, createIcon(rgb));
            } else {
                return imageRegistry.get(id);
            }

            return imageRegistry.get(id);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getText(final Object element) {
            return element.toString();
        }

        /**
         * Creates a small icon using the specified color.
         *
         * @param rgb
         *            the color
         * @return an icon
         */
        private Image createIcon(final RGB rgb) {
            assert rgb != null : "rgb!=null"; //$NON-NLS-1$

            Color color = CustomMediaFactory.getInstance().getColor(rgb);

            // create new graphics context, to draw on
            Image image = new Image(Display.getCurrent(), 16, 16);
            GC gc = new GC(image);

            // draw transparent background
            Color bg = CustomMediaFactory.getInstance().getColor(255, 255, 255);
            gc.setBackground(bg);
            gc.fillRectangle(0, 0, 16, 16);
            // draw icon
            gc.setBackground(color);
            Rectangle r = new Rectangle(1, 4, 14, 9);
            gc.fillRectangle(r);
            gc
                    .setBackground(CustomMediaFactory.getInstance().getColor(0,
                            0, 0));
            gc.drawRectangle(r);
            gc.dispose();

            // setup tranparency
            image.getImageData().transparentPixel = image.getImageData().palette.getPixel(new RGB(255,
                    255, 255));

            return image;
        }
    }
}
