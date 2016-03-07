package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.internal.model.FontProperty;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.editor.newproperties.colorservice.NamedFont;
import org.csstudio.sds.util.ColorAndFontUtil;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Section for {@link FontProperty}.
 *
 * @author Sven Wende
 *
 */
public class FontSection extends AbstractTextSection<FontProperty, String> implements ISelectionChangedListener {
    private String latestFont;
    static ImageRegistry imageRegistry = new ImageRegistry();
    private FontDialog fontDialog;

    public FontSection(String propertyId) {
        super(propertyId);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected String getConvertedValue(String text) {
        return text;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doCreateControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
        super.doCreateControls(parent, tabbedPropertySheetPage);

        fontDialog = new FontDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

        FormData fd;

        // .. button to open the color dialog
        Hyperlink link = getWidgetFactory().createHyperlink(parent, "Choose ...", SWT.NONE);
        link.setUnderlined(false);
        fd = new FormData();
        fd.right = new FormAttachment(50,0);
        link.setLayoutData(fd);

        link.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            public void linkActivated(HyperlinkEvent e) {
                FontData fontdata = fontDialog.open();

                if (fontdata != null) {
                    boolean italic = ((fontdata.getStyle() & SWT.ITALIC) == SWT.ITALIC);
                    boolean bold = ((fontdata.getStyle() & SWT.BOLD) == SWT.BOLD);
                    String f = ColorAndFontUtil.toFontString(fontdata.getName(), fontdata.getHeight(), bold, italic);
                    applyPropertyChange(f);
                }
            }
        });


        // .. change position of the text control
        fd = new FormData();
        fd.left = new FormAttachment(0,0);
        fd.right = new FormAttachment(link, -5);
        getTextControl().setLayoutData(fd);

    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doRefreshControls(FontProperty widgetProperty) {
        // .. refresh colored preview icon
        if (widgetProperty != null) {
            latestFont = widgetProperty.getPropertyValue();

            if (latestFont != null) {
                setCurrentText(latestFont);
                Font font = SdsUiPlugin.getDefault().getColorAndFontService().getFont(latestFont);

                if (font != null) {
                    fontDialog.setFontList(new FontData[] { font.getFontData()[0] });
                }
            }
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        final NamedFont namedColor = (NamedFont) ((IStructuredSelection) event.getSelection()).getFirstElement();
        applyPropertyChange(namedColor.toFontString());
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected List<IContentProposal> getContentProposals(FontProperty property, AbstractWidgetModel selectedWidget,
            List<AbstractWidgetModel> selectedWidgets) {
        List<IContentProposal> proposals = new ArrayList<IContentProposal>();

        List<NamedFont> fonts = SdsUiPlugin.getDefault().getColorAndFontService().listAvailableFonts();

        for (NamedFont f : fonts) {
            proposals.add(new NamedFontContentProposal(f));
        }
        return proposals;
    }

    /**
     * Content proposal for named fonts.
     *
     * @author Sven Wende
     *
     */
    private static final class NamedFontContentProposal implements IContentProposal {
        private NamedFont namedFont;

        public NamedFontContentProposal(NamedFont namedColor) {
            assert namedColor != null;
            this.namedFont = namedColor;
        }

        @Override
        public String getContent() {
            return "${" + namedFont.getName() + "}";
        }

        @Override
        public int getCursorPosition() {
            return 0;
        }

        @Override
        public String getDescription() {
            return namedFont.getDescription();
        }

        @Override
        public String getLabel() {
            return namedFont.getName();
        }

    }

}