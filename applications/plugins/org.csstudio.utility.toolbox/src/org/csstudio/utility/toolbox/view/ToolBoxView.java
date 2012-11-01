package org.csstudio.utility.toolbox.view;

import net.miginfocom.swt.MigLayout;

import org.csstudio.utility.toolbox.actions.OpenArticleDescriptionEditorAction;
import org.csstudio.utility.toolbox.actions.OpenArticleDescriptionSearchAction;
import org.csstudio.utility.toolbox.actions.OpenArticleSearchAction;
import org.csstudio.utility.toolbox.actions.OpenFirmaEditorAction;
import org.csstudio.utility.toolbox.actions.OpenFirmaSearchAction;
import org.csstudio.utility.toolbox.actions.OpenOrderEditorAction;
import org.csstudio.utility.toolbox.actions.OpenOrderSearchAction;
import org.csstudio.utility.toolbox.actions.OpenStoreArticleEditorAction;
import org.csstudio.utility.toolbox.actions.OpenStoreArticleSearchAction;
import org.csstudio.utility.toolbox.actions.OpenStoreEditorAction;
import org.csstudio.utility.toolbox.actions.OpenStoreSearchAction;
import org.csstudio.utility.toolbox.framework.TestHelper;
import org.csstudio.utility.toolbox.framework.WidgetFactory;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.listener.SimpleSelectionListener;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import com.google.inject.Inject;

public class ToolBoxView extends ViewPart implements TestHelper<BindingEntity> {

	public static final String ID = "org.csstudio.utility.toolbox.view.ToolBoxView";

	@Inject
	private OpenOrderEditorAction openOrderEditorAction;

	@Inject
	private OpenOrderSearchAction openOrderSearchAction;

	@Inject
	private OpenFirmaEditorAction openFirmaEditorAction;

	@Inject
	private OpenFirmaSearchAction openFirmaSearchAction;

	@Inject
	private OpenStoreEditorAction openStoreEditorAction;

	@Inject
	private OpenStoreSearchAction openStoreSearchAction;

	@Inject
	private OpenStoreArticleEditorAction openStoreArticleEditorAction;

	@Inject
	private OpenStoreArticleSearchAction openStoreAarticleSearchAction;

	@Inject
	private OpenArticleDescriptionEditorAction openArticleDescriptionEditorAction;

	@Inject
	private OpenArticleDescriptionSearchAction openArticleDescriptionSearchAction;

	@Inject
	private OpenArticleSearchAction openArticleSearchAction;

	@Inject
	private WidgetFactory<BindingEntity> wf;

	@Override
	public void createPartControl(Composite parent) {
		createToolbar();
		createUi(parent);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	private void createToolbar() {
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolbarManager = actionBars.getToolBarManager();

		toolbarManager.add(openOrderEditorAction);

	}

	private void createUi(Composite parent) {
		MigLayout ml = new MigLayout("ins 0", "[grow,fill]", "[grow,fill]");
		parent.setLayout(ml);

		createExpander(parent);
	}

	private void createExpander(Composite parent) {

		ExpandBar bar = new ExpandBar(parent, SWT.V_SCROLL);

		Composite company = createEntry(bar, "Create Company", "Search Company", new Property("CreateCompany"),
					new Property("SearchCompany"), new SimpleSelectionListener() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							openFirmaEditorAction.run();
						}
					}, new SimpleSelectionListener() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							openFirmaSearchAction.run();
						}
					});

		Composite order = createEntry(bar, "Create Order", "Search Order", new Property("CreateOrder"), new Property(
					"SearchOrder"), new SimpleSelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openOrderEditorAction.run();
			}
		}, new SimpleSelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openOrderSearchAction.run();
			}
		});

		Composite articleComposite = new Composite(bar, SWT.NONE);
		MigLayout ml = new MigLayout("ins 0, wrap1 ", "[fill,grow]", "[fill,grow]");
		articleComposite.setLayout(ml);

		createEntry(articleComposite, "Create Description", "Search Description", new Property(
					"CreateArticleDescription"), new Property("SearchArticleDescription"),
					new SimpleSelectionListener() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							openArticleDescriptionEditorAction.run();
						}
					}, new SimpleSelectionListener() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							openArticleDescriptionSearchAction.run();
						}
					});

		createEntry(articleComposite, "Search Article", new Property("SearchArticle"), new SimpleSelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openArticleSearchAction.run();
			}
		});

		Composite stockComposite = new Composite(bar, SWT.NONE);
		MigLayout mlStock = new MigLayout("ins 0, wrap1 ", "[fill,grow]", "[fill,grow]");
		stockComposite.setLayout(mlStock);

		createEntry(stockComposite, "Create Stock", "Search Stock", new Property("CreateStock"), new Property(
					"SearchStock"), new SimpleSelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openStoreEditorAction.run();
			}
		}, new SimpleSelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openStoreSearchAction.run();
			}
		});

		createEntry(stockComposite, "Create Stock Article", "Search Stock Article", new Property("CreateStockArticle"), new Property(
					"SearchStockArticle"), new SimpleSelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openStoreArticleEditorAction.run();
			}
		}, new SimpleSelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openStoreAarticleSearchAction.run();
			}
		});

		wf.createExpandItem(bar, "Stock", stockComposite, new Property("ExpandItemStock"));
		wf.createExpandItem(bar, "Article", articleComposite, new Property("ExpandItemArticle"));
		wf.createExpandItem(bar, "Company", company, new Property("ExpandItemCompany"));
		wf.createExpandItem(bar, "Order", order, new Property("ExpandItemOrder"));

	}

	public Composite createEntry(Composite bar, String searchTitle, Property searchName,
				SimpleSelectionListener listenerSearch) {

		Composite composite = new Composite(bar, SWT.NONE);
		MigLayout ml = new MigLayout("ins 0, wrap1 ", "[fill,grow]", "[fill,grow]");
		composite.setLayout(ml);

		wf.button(composite, searchName.getName()).hint("h 30").text(searchTitle).listener(listenerSearch).build();

		return composite;
	}

	public Composite createEntry(Composite bar, String createTitle, String searchTitle, Property createName,
				Property searchName, SimpleSelectionListener listenerCreate, SimpleSelectionListener listenerSearch) {

		Composite composite = new Composite(bar, SWT.NONE);
		MigLayout ml = new MigLayout("ins 0, wrap1 ", "[fill,grow]", "[fill,grow]");
		composite.setLayout(ml);

		wf.button(composite, createName.getName()).hint("h 30").text(createTitle).listener(listenerCreate).build();
		wf.button(composite, searchName.getName()).hint("h 30").text(searchTitle).listener(listenerSearch).build();

		return composite;
	}

	@Override
	public WidgetFactory<BindingEntity> getWidgetFactory() {
		return wf;
	}

}
