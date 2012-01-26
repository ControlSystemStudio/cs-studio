package org.csstudio.config.ioconfig.view;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.model.AbstractNodeSharedImpl;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @since 20.06.2007
 */
class ProfiBusViewLabelProvider extends ColumnLabelProvider {

    private static final Color PROGRAMMABLE_MARKER_COLOR = CustomMediaFactory.getInstance()
    .getColor(255, 140, 0);
    private static final Font PROGRAMMABLE_MARKER_FONT = CustomMediaFactory.getInstance()
    .getFont("Tahoma", 8, SWT.ITALIC);

    @Override
    @CheckForNull
    public Color getBackground(@Nullable final Object element) {
        if (haveProgrammableModule(element)) {
            return PROGRAMMABLE_MARKER_COLOR;
        }
        return null;
    }

    @Override
    @CheckForNull
    public Font getFont(@Nullable final Object element) {
        if (haveProgrammableModule(element)) {
            return PROGRAMMABLE_MARKER_FONT;
        }
        return null;
    }

    @Override
    @CheckForNull
    public Image getImage(@Nullable final Object obj) {
        if (obj instanceof AbstractNodeSharedImpl) {
            final AbstractNodeSharedImpl<?,?> node = (AbstractNodeSharedImpl<?,?>) obj;
            return ConfigHelper.getImageFromNode(node);
        }
        return null;
    }

    @Override
    @CheckForNull
    public String getText(@Nonnull final Object element) {
        String text = super.getText(element);
        final String[] split = text.split("(\r(\n)?)");
        if (split.length > 1) {
            text = split[0];
        }
        if (haveProgrammableModule(element)) {
            return text + " [prog]";
        }
        return text;
    }

    @Override
    @CheckForNull
    public String getToolTipText(@Nullable final Object element) {
        if (haveProgrammableModule(element)) {
            return "Is a programmable Module!";
        }
        return null;
    }

    private boolean haveProgrammableModule(@Nullable final Object element) {
        /*
         * TODO: (hrickens) Das finden von Projekt Document Datein führt teilweise dazu das sich
         * CSS Aufhängt! if (element instanceof Slave) { Slave node = (Slave) element;
         * Set<Document> documents = node.getDocuments(); while (documents.iterator().hasNext())
         * { Document doc = (Document) documents.iterator().next(); if (doc.getSubject() != null
         * && doc.getSubject().startsWith("Projekt:")) { return true; } } }
         */
                return false;
    }

}
