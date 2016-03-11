package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolderMember;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.ui.Activator;
import org.csstudio.dct.ui.editor.tables.ConvenienceTableWrapper;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.domain.common.LayoutUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all editing forms. The class already prepares input element
 * that are common to all model parts and takes care of refreshes.
 *
 * @author Sven Wende
 *
 * @param <E>
 *            the type of element that is edited with a form
 */
public abstract class AbstractForm<E extends IElement> implements CommandStackListener {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractForm.class);

    private final class ElementJumpLinkListener implements Listener {
        @Override
        public void handleEvent(Event event) {
            UUID id = null;

            try {
                id = UUID.fromString(event.text);

                if (id != null) {
                    editor.selectItemInOutline(id);
                }
            } catch (Exception e) {
                LOG.warn("Warning", e);
            }

        }
    }

    private Composite mainComposite;
    private E input;
    private Label headlineLabel;
    private ConvenienceTableWrapper commonTable;
    private Link breadcrumbLink;
    private final DctEditor editor;

    /**
     * Constructor.
     *
     * @param editor
     *            a DCT editor instance
     */
    public AbstractForm(DctEditor editor) {
        assert editor != null;
        assert editor.getCommandStack() != null;
        this.editor = editor;
        editor.getCommandStack().addCommandStackListener(this);
    }

    /**
     * Returns the editor instance.
     *
     * @return the editor instance
     */
    public DctEditor getEditor() {
        return editor;
    }

    /**
     * Returns the underlying DCT project.
     *
     * @return the DCT project
     */
    public Project getProject() {
        return editor.getProject();
    }

    /**
     * Creates the controls for this editing form.
     *
     * @param parent
     *            the parent composite
     */
    public final void createControl(Composite parent) {
        // .. main composite
        mainComposite = new Composite(parent, SWT.None);
        GridLayout layout = LayoutUtil.createGridLayout(1, 5, 5, 5);
        mainComposite.setLayout(layout);

        // .. headline label
        headlineLabel = new Label(mainComposite, SWT.NONE);
        headlineLabel.setFont(CustomMediaFactory.getInstance().getFont("Arial", 16, SWT.BOLD));
        headlineLabel.setText("");
        headlineLabel.setLayoutData(LayoutUtil.createGridDataForHorizontalFillingCell(25));

        // .. breadcrumb
        breadcrumbLink = new Link(mainComposite, SWT.WRAP);
        breadcrumbLink.setLayoutData(LayoutUtil.createGridDataForHorizontalFillingCell(20));
        breadcrumbLink.setFont(CustomMediaFactory.getInstance().getFont("Courier", 10, SWT.NORMAL));
        breadcrumbLink.addListener(SWT.Selection, new ElementJumpLinkListener());

        // .. create expand bar
        ExpandBar expandBar = new ExpandBar(mainComposite, SWT.V_SCROLL);
        expandBar.setLayoutData(LayoutUtil.createGridDataForFillingCell());
        expandBar.setSpacing(8);

        // create overview
        Composite composite = new Composite(expandBar, SWT.NONE);
        composite.setLayout(LayoutUtil.createGridLayout(1, 5, 8, 8));

        commonTable = WidgetUtil.create3ColumnTable(composite, getCommandStack());
        commonTable.getViewer().getControl().setLayoutData(LayoutUtil.createGridDataForHorizontalFillingCell(100));

        ExpandItem expandItem = new ExpandItem(expandBar, SWT.NONE, 0);
        expandItem.setText("Common Settings");
        expandItem.setHeight(170);
        expandItem.setControl(composite);
        expandItem.setExpanded(true);
        expandItem.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/tab_common.png"));

        // .. let subclasses add their own widgets
        doCreateControl(expandBar, getCommandStack());
    }

    /**
     * Returns the command stack.
     *
     * @return the command stack
     */
    public CommandStack getCommandStack() {
        return editor.getCommandStack();
    }

    /**
     * Returns the main composite.
     *
     * @return the main composite
     */
    public final Composite getMainComposite() {
        return mainComposite;
    }

    /**
     * Sets the input object for this editing form.
     *
     * @param in
     *            the current input element for the form
     */
    @SuppressWarnings("unchecked")
    public final void setInput(Object in) {
        this.input = (E) in;

        if (input != null) {
            // .. refresh headline label
            headlineLabel.setText(doGetFormLabel(input));

            // .. refresh the breadcrumb
            String additionalLinks = doGetAdditionalBreadcrumbLinks(input);
            breadcrumbLink.setText(doCreateBreadcrumbLink(input) + (additionalLinks != null ? "     [" + additionalLinks + "]" : ""));

            // prepare input for overview table
            List<ITableRow> rows = new ArrayList<ITableRow>();
            rows.add(new BeanPropertyTableRowAdapter("Identifier", input, "id", true));
            rows.add(new NameTableRowAdapter(input));
            doAddCommonRows(rows, input);
            commonTable.setInput(rows);

            // call subclasses
            doSetInput(input);
        }

    }

    /**
     * Returns the input object for this editing form.
     *
     * @return the input object
     */
    public final E getInput() {
        return input;
    }

    /**
     * Refreshes this form.
     */
    public final void refresh() {
        if (input != null) {
            setInput(input);
            LOG.info("refresh + {}", input.getId());
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public final void commandStackChanged(EventObject event) {
        refresh();
    }

    /**
     * Template method. Subclasses return a label text for the form title here.
     *
     * @param input
     *            TODO
     *
     * @return a label text for the form title
     */
    protected abstract String doGetFormLabel(E input);

    /**
     * Template method. Subclasses can add table rows for the "common settings"
     * table here.
     *
     * @param rows
     *            a list with table rows for the "common settings"
     * @param input
     *            the current input element for the form
     */
    protected abstract void doAddCommonRows(List<ITableRow> rows, E input);

    /**
     * Subclasses my provide a text with links to model elements, which will
     * appear in the breadcrumb underneath the headline. The text can contain
     * links in the following format
     *
     * <code>
     *         <a href="${elementId}">link</a>
     * </code>
     *
     * @param input
     *            the current input element for the form
     * @return a text with link that will appear underneath the headline
     */
    protected abstract String doGetAdditionalBreadcrumbLinks(E input);

    private String doCreateBreadcrumbLink(IElement input) {
        Stack<IElement> stack = new Stack<IElement>();

        IElement parent = getParentElement(input);

        while (parent != null) {
            stack.push(parent);
            parent = getParentElement(parent);
        }

        StringBuffer sb = new StringBuffer();

        while (!stack.isEmpty()) {
            IElement e = stack.pop();
            sb.append("<a href=\"" + e.getId().toString() + "\">" + AliasResolutionUtil.getPropertyViaHierarchy(e, "name") + "</a> > ");
        }

        sb.append(AliasResolutionUtil.getPropertyViaHierarchy(input, "name"));

        return sb.toString();
    }

    private IElement getParentElement(IElement e) {
        IElement result = null;

        if (e instanceof IRecord) {
            result = ((IRecord) e).getContainer();
        } else {
            if (e instanceof IFolderMember) {
                result = ((IFolderMember) e).getParentFolder();
            }

            if (result == null && e instanceof IContainer) {
                result = ((IContainer) e).getContainer();
            }
        }

        return result;
    }

    /**
     * Templates method. Used by subclasses to prepare their widgets.
     *
     * @param bar
     *            the expand bar
     *
     * @param commandStack
     *            the command stack
     */
    protected abstract void doCreateControl(ExpandBar bar, CommandStack commandStack);

    /**
     * Template method that is called, when the input for this form changes.
     * Subclasses should refresh their widgets when this method is called.
     *
     * @param input
     *            the current input object
     */
    protected abstract void doSetInput(E input);

}
