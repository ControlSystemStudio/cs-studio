package org.csstudio.dct.ui.workbenchintegration;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;

/**
 * Base class for workbench adapters for elements of the DCT model.
 *
 * @author Sven Wende
 *
 * @param <E>
 *            the element type
 */
@SuppressWarnings("unchecked")
public abstract class BaseWorkbenchAdapter<E> implements IWorkbenchAdapter, IWorkbenchAdapter2 {
    /**
     * {@inheritDoc}
     */
    @Override
    public final RGB getBackground(Object element) {
        return doGetBackground((E) element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final FontData getFont(Object element) {
        return doGetFontData((E) element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final RGB getForeground(Object element) {
        return doGetForeground((E) element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object[] getChildren(Object element) {
        return doGetChildren((E) element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ImageDescriptor getImageDescriptor(Object element) {
        ImageDescriptor result = null;

        String iconPath = doGetIcon((E) element);

        if (iconPath != null) {
            result = CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(org.csstudio.dct.ui.Activator.PLUGIN_ID, iconPath);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getLabel(Object element) {
        return doGetLabel((E) element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object getParent(Object element) {
        return doGetParent((E) element);
    }

    /**
     * Provide a background color.
     *
     * @param element
     *            the element
     *
     * @return a background color
     */
    protected RGB doGetBackground(E element) {
        return new RGB(255, 255, 255);
    }

    /**
     * Provide a font.
     *
     * @param element
     *            the element
     *
     * @return a font
     */
    protected FontData doGetFontData(E element) {
        return null;
    }

    /**
     * Provide a background color.
     *
     * @param element
     *            the element
     *
     * @return a background color
     */
    protected RGB doGetForeground(E element) {
        return new RGB(0, 0, 0);
    }

    /**
     * Provide the children.
     *
     * @param element
     *            the element
     * @return the children
     */
    protected Object[] doGetChildren(E element) {
        return new Object[0];
    }

    /**
     * Provide an image. the element
     *
     * @param object the element
     *
     * @return an image
     */
    protected String doGetIcon(E object) {
        return null;
    }

    /**
     * Provide a label.
     *
     * @param object
     *            the element
     * @return a label
     */
    protected String doGetLabel(E object) {
        return object.toString();
    }

    /**
     * Provide the parent.
     *
     * @param object
     *            the element
     * @return the parent
     */
    protected Object doGetParent(E object) {
        return null;
    }
}
