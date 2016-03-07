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
 package org.csstudio.sds.ui.internal.properties.view;

import org.csstudio.sds.model.WidgetModelFactoryService;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IContributedContentsView;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

/**
 * Main class for the Property Sheet View.
 * <p>
 * This standard view has id <code>"org.eclipse.ui.views.PropertySheet"</code>.
 * </p>
 * <p>
 * Note that property <it>sheets</it> and property sheet pages are not the same
 * thing as property <it>dialogs</it> and their property pages (the property
 * pages extension point is for contributing property pages to property
 * dialogs). Within the property sheet view, all pages are
 * <code>IPropertySheetPage</code>s.
 * </p>
 * <p>
 * Property sheet pages are discovered by the property sheet view automatically
 * when a part is first activated. The property sheet view asks the active part
 * for its property sheet page; this is done by invoking
 * <code>getAdapter(IPropertySheetPage.class)</code> on the part. If the part
 * returns a page, the property sheet view then creates the controls for that
 * property sheet page (using <code>createControl</code>), and adds the page
 * to the property sheet view. Whenever this part becomes active, its
 * corresponding property sheet page is shown in the property sheet view (which
 * may or may not be visible at the time). A part's property sheet page is
 * discarded when the part closes. The property sheet view has a default page
 * (an instance of <code>PropertySheetPage</code>) which services all parts
 * without a property sheet page of their own.
 * </p>
 * <p>
 * The workbench will automatically instantiates this class when a Property
 * Sheet view is needed for a workbench window. This class is not intended to be
 * instantiated or subclassed by clients.
 * </p>
 *
 * @see IPropertySheetPage
 * @see PropertySheetPage
 *
 * @author Sven Wende
 */
public final class PropertySheet extends PageBookView implements
        ISelectionListener {
    /**
     * The view id.
     */
    public static final String VIEW_ID = "org.csstudio.sds.ui.internal.properties.view.PropertySheet";

    /**
     * No longer used but preserved to avoid api change.
     */
    public static final String HELP_CONTEXT_PROPERTY_SHEET_VIEW = IPropertiesHelpContextIds.PROPERTY_SHEET_VIEW;

    /**
     * The initial selection when the property sheet opens.
     */
    private ISelection _bootstrapSelection;

    /**
     * Creates a property sheet view.
     */
    public PropertySheet() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IPage createDefaultPage(final PageBook book) {
        PropertySheetPage page = new PropertySheetPage();
        initPage(page);
        page.createControl(book);
        return page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(final Composite parent) {
        super.createPartControl(parent);
        getSite().getPage().getWorkbenchWindow().getWorkbench().getHelpSystem()
                .setHelp(getPageBook(),
                        IPropertiesHelpContextIds.PROPERTY_SHEET_VIEW);

        parent.getParent().layout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        // run super.
        super.dispose();

        // remove ourselves as a selection listener
        getSite().getPage().removeSelectionListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PageRec doCreatePage(final IWorkbenchPart part) {
        // Try to get a custom property sheet page.
        IPropertySheetPage page = (IPropertySheetPage) part
                .getAdapter(IPropertySheetPage.class);
        if (page == null) {
            // Look for a declaratively-contributed adapter.
            // See bug 86362 [PropertiesView] Can not access AdapterFactory,
            // when plugin is not loaded.
            page = (IPropertySheetPage) Platform.getAdapterManager()
                    .loadAdapter(part, IPropertySheetPage.class.getName());
        }
        if (page != null) {
            if (page instanceof IPageBookViewPage) {
                initPage((IPageBookViewPage) page);
            }
            page.createControl(getPageBook());
            return new PageRec(part, page);
        }

        // Use the default page
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doDestroyPage(final IWorkbenchPart part, final PageRec rec) {
        IPropertySheetPage page = (IPropertySheetPage) rec.page;
        page.dispose();
        rec.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IWorkbenchPart getBootstrapPart() {
        IWorkbenchPage page = getSite().getPage();
        if (page != null) {
            _bootstrapSelection = page.getSelection();
            return page.getActivePart();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final IViewSite site) throws PartInitException {
        site.getPage().addSelectionListener(this);
        super.init(site);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isImportant(final IWorkbenchPart part) {
        return part instanceof DisplayEditor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partActivated(final IWorkbenchPart part) {
        IContributedContentsView view = (IContributedContentsView) part
                .getAdapter(IContributedContentsView.class);
        IWorkbenchPart source = null;
        if (view != null) {
            source = view.getContributingPart();
        }
        if (source != null) {
            super.partActivated(source);
        } else {
            super.partActivated(part);
        }

        // When the view is first opened, pass the selection to the page
        if (_bootstrapSelection != null) {
            IPropertySheetPage page = (IPropertySheetPage) getCurrentPage();
            if (page != null) {
                page.selectionChanged(part, _bootstrapSelection);
            }
            this.setContentDescription(this.createContentDescription(_bootstrapSelection));
            _bootstrapSelection = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectionChanged(final IWorkbenchPart part, final ISelection sel) {
        // we ignore our own selection or null selection
        if (part == this || sel == null) {
            return;
        }

        // pass the selection to the page
        IPropertySheetPage page = (IPropertySheetPage) getCurrentPage();
        if (page != null) {
            page.selectionChanged(part, sel);
        }
        this.setContentDescription(this.createContentDescription(sel));
    }

    /**
     * Creates and returns the content description for this View.
     * @param sel
     *             The current selection
     * @return String
     *             The content description
     */
    private String createContentDescription(final ISelection sel) {
        String description = "Type: ";

        if(sel instanceof IStructuredSelection) {
            Object[] objects = ((IStructuredSelection) sel).toArray();

            if (objects.length>1) {
                description = description +"Multiple ("+objects.length+") Widgets selected";
            } else if(objects.length==1){
                if (objects[0] instanceof AbstractWidgetEditPart) {
                    description = description + WidgetModelFactoryService.getInstance().getName(
                            ((AbstractWidgetEditPart)objects[0]).getWidgetModel().getTypeID() );
                } else {
                    description = description + "Display";
                }
            }
        }

        return description;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Object getViewAdapter(final Class key) {
        if (ISaveablePart.class.equals(key)) {
            return getSaveablePart();
        }
        return super.getViewAdapter(key);
    }

    /**
     * Returns an <code>ISaveablePart</code> that delegates to the source part
     * for the current page if it implements <code>ISaveablePart</code>, or
     * <code>null</code> otherwise.
     *
     * @return an <code>ISaveablePart</code> or <code>null</code>
     * @since 3.2
     */
    protected ISaveablePart getSaveablePart() {
        IWorkbenchPart part = getCurrentContributingPart();
        if (part instanceof ISaveablePart) {
            return (ISaveablePart) part;
        }
        return null;
    }
}
