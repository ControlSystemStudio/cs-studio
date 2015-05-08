package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetProperty;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Base class for sections which use a text field for editing. Subclasses can
 * easily equip the text field with context sensitive auto completion texts if
 * needed.
 *
 * @author Sven Wende
 *
 * @param <E>
 *            the type of {@link WidgetProperty} that will be edited using this
 *            section
 *
 * @param <V>
 *            the type of values that can be set to the widget property
 *
 */
public abstract class AbstractTextSection<E extends WidgetProperty, V> extends
        AbstractBaseSection<E> {
    private Text textControl;

    public AbstractTextSection(final String propertyId) {
        super(propertyId);
    }

    /**
     * Returns the text control.
     *
     * @return the text control
     */
    protected Text getTextControl() {
        return textControl;
    }

    /**
     * Returns the SWT styles for the text control. Default is
     * {@link SWT#SINGLE} and {@link SWT#NO_SCROLL}. Subclasses may override.
     *
     * @return the SWT styles for the text control
     */
    protected int getTextControlStyle() {
        return SWT.SINGLE | SWT.NO_SCROLL;
    }

    /**
     * Returns the minimum width for the text control. Default is
     * {@link AbstractBaseSection#STANDARD_WIDGET_WIDTH}. Subclasses may
     * override.
     *
     * @return the minimum width for the text control
     */
    protected int getMinimumWidth() {
        return STANDARD_WIDGET_WIDTH;
    }

    /**
     * Sets the current text.
     *
     * @param text
     *            the text
     */
    protected void setCurrentText(final String text) {
        textControl.setText(text);
    }

    /**
     * Returns the current text.
     */
    protected String getCurrentText() {
        return textControl.getText();
    }

    public int getTextHeight() {
        return super.getMinimumHeight();
    }

    /**
     * Template method. Subclassed need to return a converted value which can be
     * applied to the widget property.
     *
     * @param text
     *            the current text
     *
     * @return a domain object which can be set as value for the underlying
     *         widget property
     */
    protected abstract V getConvertedValue(String text);

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doCreateControls(final Composite parent,
            final TabbedPropertySheetPage tabbedPropertySheetPage) {
        parent.setLayout(new FormLayout());

        // .. the text control
        textControl = getWidgetFactory().createText(parent, null,
                getTextControlStyle());
        FormData fd = new FormData();
        fd.left = new FormAttachment(0, 0);
        fd.right = new FormAttachment(50, 0);
        fd.height = getTextHeight();
        textControl.setLayoutData(fd);

        // .. key listeners (stop editing on ESCAPE and apply value on ENTER and
        // TAB)
        textControl.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                switch (e.keyCode) {
                    case SWT.ESC:
                        cancelEditing();
                        break;
                    case SWT.TAB:
                        e.doit = false;
                        applyPropertyChange(getConvertedValue(getCurrentText()));
                        setFocusToNextSibling(parent);
                        break;
                    case SWT.CR:
                    case SWT.KEYPAD_CR:
                        if (e.stateMask != SWT.MOD1) {
                            e.doit = false;
                            applyPropertyChange(getConvertedValue(getCurrentText()));
                            setFocusToNextSibling(parent);
                        }
                        break;
                }
            }
        });

        // .. highlight text control, when it has the focus
        final Color color = textControl.getBackground();

        textControl.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {
                applyPropertyChange(getConvertedValue(getCurrentText()));
                textControl.setBackground(color);
            }

            @Override
            public void focusGained(final FocusEvent e) {
                textControl.setBackground(COLOR_CONTROL_ACTIVE);
            }
        });

        // .. configure the content proposal adapter
        ContentProposalAdapter adapter = new ContentProposalAdapter(
                textControl, new TextContentAdapter(),
                new IContentProposalProvider() {
                    public IContentProposal[] getProposals(final String contents,
                            final int position) {
                        List<IContentProposal> proposals = getContentProposals(
                                mainWidgetProperty, selectedWidget,
                                selectedWidgets);
                        return proposals.toArray(new IContentProposal[proposals
                                .size()]);
                    }
                }, getContentProposalActivationKeystroke(),
                getContentProposalActivationCharacters());
        adapter.setPropagateKeys(true);
        adapter.setProposalAcceptanceStyle(getProposalAcceptanceStyle());
        adapter.setPopupSize(new Point(400, 300));

    }

    /**
     * Returns the characters which activate the content proposal popup menu.
     * Default is "$" Subclasses may override.
     *
     * @return the characters which activate the content proposal popup menu
     */
    protected char[] getContentProposalActivationCharacters() {
        return new char[] { '$' };
    }

    /**
     * Returns the keystroke which activates the content proposal popup menu.
     * Default is CTRL+Space Subclasses may override.
     *
     * @return the keystroke which activates the content proposal popup menu
     */
    protected KeyStroke getContentProposalActivationKeystroke() {
        KeyStroke keyStroke;
        try {
            keyStroke = KeyStroke.getInstance("Ctrl+Space");
        } catch (ParseException e1) {
            keyStroke = null;
        }

        return keyStroke;
    }

    /**
     * Returns the insertion behavior of the {@link ContentProposalAdapter}.
     * One of {@link ContentProposalAdapter#PROPOSAL_IGNORE},
     * {@link ContentProposalAdapter#PROPOSAL_INSERT} or
     * {@link ContentProposalAdapter#PROPOSAL_REPLACE};
     *
     * @return The style for the insertion behavior
     */
    protected int getProposalAcceptanceStyle() {
        return ContentProposalAdapter.PROPOSAL_REPLACE;
    }

    /**
     * Template method. Subclasses may return content proposals for the current
     * input elements.
     *
     * @param property
     *            the property
     * @param selectedWidget
     *            the primary selected widget
     * @param selectedWidgets
     *            all selected widgets
     * @return
     */
    protected abstract List<IContentProposal> getContentProposals(E property,
            AbstractWidgetModel selectedWidget,
            List<AbstractWidgetModel> selectedWidgets);

    /**
     * Move focus to the next neighbor.
     *
     * @param c
     *            the current control
     * @param next
     * @return
     */
    private boolean setFocusToNextSibling(final Control c) {
        Composite parent = c.getParent();
        Control[] children = parent.getTabList();
        for (int i = 0; i < children.length; i++) {
            Control child = children[i];
            if (child == c) {
                for (int j = i + 1; j < children.length; j++) {
                    Control nc = children[j];
                    if (nc.setFocus()) {
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
