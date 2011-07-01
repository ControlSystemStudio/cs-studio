
package org.csstudio.nams.configurator.views;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.csstudio.nams.configurator.Messages;
import org.csstudio.nams.configurator.actions.BeanToEditorId;
import org.csstudio.nams.configurator.actions.DeleteConfugurationBeanAction;
import org.csstudio.nams.configurator.actions.DuplicateConfigurationBeanAction;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.composite.FilterableBeanList;
import org.csstudio.nams.configurator.editor.AbstractEditor;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.csstudio.nams.configurator.service.AbstractConfigurationBeanServiceListener;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.csstudio.nams.configurator.service.ConfigurationBeanServiceImpl;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.preferenceservice.declaration.HoldsAPreferenceId;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService.PreferenceChangeListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public abstract class AbstractNamsView extends ViewPart {

	/**
	 * Gibt an, in welchem Modus die View ist, also u.a. welches Stack der View
	 * (Medlung) gezeigt wird.
	 * 
	 * @author mz
	 * 
	 */
	private enum ViewModes {
		NOT_INITIALIZED, NORMAL;
	}

	static boolean isInitialized = false;

	protected static ConfigurationBeanService _configurationBeanService;
	private static PreferenceService _preferenceService;
	private static ConfigurationServiceFactory _configurationServiceFactory;
	private static Logger _logger;
	private static Semaphore semaphore = new Semaphore(1);

	public static boolean isInitialized() {
		return AbstractNamsView.isInitialized;
	}

	public static void staticInject(
			final ConfigurationServiceFactory configurationServiceFactory) {
		AbstractNamsView._configurationServiceFactory = configurationServiceFactory;
	}

	public static void staticInject(final Logger logger) {
		AbstractNamsView._logger = logger;
	}

	public static void staticInject(final PreferenceService preferenceService) {
		AbstractNamsView._preferenceService = preferenceService;
	}

	protected static ConfigurationBeanService getConfigurationBeanService() {
		return AbstractNamsView._configurationBeanService;
	}

	private FilterableBeanList filterableBeanList;

	private StackLayout viewsRootLayout;

	private final Map<ViewModes, Composite> viewStackContents = new HashMap<ViewModes, Composite>();
	private Composite viewsRoot;

	public AbstractNamsView() {
	    // Nothing to do
	}

	@Override
	public void createPartControl(final Composite rootComposite) {
		this.viewsRoot = new Composite(rootComposite, SWT.NONE);
		this.viewsRootLayout = new StackLayout();
		this.viewsRoot.setLayout(this.viewsRootLayout);

		final Composite normalViewElements = new Composite(this.viewsRoot,
				SWT.TOP);
		normalViewElements.setLayout(new GridLayout(1, true));

		final Composite error = new Composite(this.viewsRoot, SWT.TOP);
		error.setLayout(new GridLayout(1, true));
		final Label errorLabel = new Label(error, SWT.WRAP);
		errorLabel
				.setText(Messages.AbstractNamsView_db_error);

		this.viewStackContents.put(ViewModes.NORMAL, normalViewElements);
		this.viewStackContents.put(ViewModes.NOT_INITIALIZED, error);

		this.performInitializeAndSetCorrespondingViewMode();

		this.filterableBeanList = new FilterableBeanList(normalViewElements,
				SWT.None) {
			@Override
			protected IConfigurationBean[] getTableInput() {
				if (AbstractNamsView.isInitialized()) {
					return AbstractNamsView.this.getTableContent();
				}
				return new IConfigurationBean[0];
			}
		};

		final MenuManager menuManager = new MenuManager();
		final TableViewer table = this.filterableBeanList.getTable();
		final Menu menu = menuManager.createContextMenu(table.getTable());
		table.getTable().setMenu(menu);
		this.getSite().registerContextMenu(menuManager, table);
		this.getSite().setSelectionProvider(table);

		menuManager.add(new Action() {
			@Override
			public String getText() {
				return Messages.AbstractNamsView_new;
			}

			@Override
			public void run() {
				ConfigurationEditorInput editorInput;
				try {
					editorInput = new ConfigurationEditorInput(
							AbstractNamsView.this.getBeanClass().newInstance());

					final IWorkbenchPage activePage = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					final String editorId = BeanToEditorId.getEnumForClass(
							AbstractNamsView.this.getBeanClass()).getEditorId();

					activePage.openEditor(editorInput, editorId);
				} catch (final InstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (final IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (final PartInitException e) {
					e.printStackTrace();
				}
			}
		});

		this.initDragAndDrop(this.filterableBeanList);

		AbstractNamsView._preferenceService
				.addPreferenceChangeListenerFor(
						new PreferenceServiceDatabaseKeys[] {
								PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION,
								PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER,
								PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD,
								PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_TYPE },
						new PreferenceChangeListener() {
							public <T extends Enum<?> & HoldsAPreferenceId> void preferenceUpdated(
									final T id, final Object oldValue,
									final Object newValue) {
								AbstractNamsView.isInitialized = false;
								AbstractNamsView.this
										.performInitializeAndSetCorrespondingViewMode();
							}
						});

		final IActionBars actionBar = this.getViewSite().getActionBars();
		actionBar.getToolBarManager().add(new Action() {
			@Override
			public int getStyle() {
				return SWT.BORDER | SWT.ICON_WORKING;
			}

			@Override
			public String getText() {
				return Messages.AbstractNamsView_reload;
			}

			@Override
			public String getToolTipText() {
				return Messages.AbstractNamsView_reload_toolTipText;
			}

			@Override
			public void run() {
				if (MessageDialog.openQuestion(AbstractNamsView.this
						.getViewSite().getShell(),
						Messages.AbstractNamsView_reload_question_title,
						Messages.AbstractNamsView_reload_question_text1
								+ Messages.AbstractNamsView_reload_question_text2)) {
					AbstractNamsView._logger
							.logDebugMessage(this,
									"Reload of entire configuration requested by user..."); //$NON-NLS-1$
					AbstractNamsView._configurationBeanService.refreshData();
					AbstractNamsView._logger.logInfoMessage(this,
							"Reload of entire configuration done."); //$NON-NLS-1$
				}
			}

		});
	}

	@Override
	public void setFocus() {
		this.filterableBeanList.getTable().getTable().setFocus();
	}

	protected abstract Class<? extends IConfigurationBean> getBeanClass();

	protected abstract IConfigurationBean[] getTableContent();

	protected void initDragAndDrop(final FilterableBeanList filterableBeanList) {

	}

	synchronized private void initialize() throws Throwable {
		try {
			AbstractNamsView.semaphore.acquire(1);
			if (!AbstractNamsView.isInitialized()) {
				final String P_CONFIG_DATABASE_CONNECTION = AbstractNamsView._preferenceService
						.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION);
				final String P_CONFIG_DATABASE_TYPE_asString = AbstractNamsView._preferenceService
						.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_TYPE);
				final String P_CONFIG_DATABASE_USER = AbstractNamsView._preferenceService
						.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER);
				final String P_CONFIG_DATABASE_PASSWORD = AbstractNamsView._preferenceService
						.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD);

				if (((P_CONFIG_DATABASE_CONNECTION == null) || (P_CONFIG_DATABASE_CONNECTION
						.length() == 0))
						|| ((P_CONFIG_DATABASE_TYPE_asString == null) || (P_CONFIG_DATABASE_TYPE_asString
								.length() == 0))
						|| ((P_CONFIG_DATABASE_USER == null) || (P_CONFIG_DATABASE_USER
								.length() == 0))
						|| (P_CONFIG_DATABASE_PASSWORD == null)) {
					throw new RuntimeException("Missing database setting!"); //$NON-NLS-1$
				}

				final DatabaseType P_CONFIG_DATABASE_TYPE = DatabaseType
						.valueOf(P_CONFIG_DATABASE_TYPE_asString);

				final LocalStoreConfigurationService localStoreConfigurationService = AbstractNamsView._configurationServiceFactory
						.getConfigurationService(P_CONFIG_DATABASE_CONNECTION,
								P_CONFIG_DATABASE_TYPE, P_CONFIG_DATABASE_USER,
								P_CONFIG_DATABASE_PASSWORD);

				AbstractNamsView._logger.logDebugMessage(this,
						"DB connected with P_CONFIG_DATABASE_CONNECTION: " //$NON-NLS-1$
								+ P_CONFIG_DATABASE_CONNECTION);
				AbstractNamsView._logger.logDebugMessage(this,
						"DB connected with P_CONFIG_DATABASE_TYPE: " //$NON-NLS-1$
								+ P_CONFIG_DATABASE_TYPE);
				AbstractNamsView._logger.logDebugMessage(this,
						"DB connected with P_CONFIG_DATABASE_USER: " //$NON-NLS-1$
								+ P_CONFIG_DATABASE_USER);
				AbstractNamsView._logger
						.logDebugMessage(
								this,
								"DB P_CONFIG_DATABASE_PASSWORD is: " //$NON-NLS-1$
										+ ((P_CONFIG_DATABASE_PASSWORD != null)
												&& (P_CONFIG_DATABASE_PASSWORD
														.length() > 0) ? "available" //$NON-NLS-1$
												: "missing")); //$NON-NLS-1$

				if (AbstractNamsView._configurationBeanService == null) {
					AbstractNamsView._configurationBeanService = new ConfigurationBeanServiceImpl();
				}
				((ConfigurationBeanServiceImpl) AbstractNamsView._configurationBeanService)
						.setNewConfigurationStore(localStoreConfigurationService);

				// prepare views
				SyncronizeView.staticInject(AbstractNamsView._configurationBeanService);
				
				// prepare editors
				AbstractEditor
						.staticInject(AbstractNamsView._configurationBeanService);

				// prepare actions
				DeleteConfugurationBeanAction
						.staticInject(AbstractNamsView._configurationBeanService);
				DuplicateConfigurationBeanAction
						.staticInject(AbstractNamsView._configurationBeanService);

				AbstractNamsView._configurationBeanService.refreshData();
			}
			AbstractNamsView.semaphore.release(1);

			AbstractNamsView._configurationBeanService
					.addConfigurationBeanServiceListener(new AbstractConfigurationBeanServiceListener() {
						@Override
						public void onBeanDeleted(final IConfigurationBean bean) {
							if (AbstractNamsView.this.filterableBeanList != null) {
								AbstractNamsView.this.filterableBeanList
										.updateView();
							}
						}

						// TODO updateView() is in most cases overkill
						@Override
						public void onBeanInsert(final IConfigurationBean bean) {
							if (AbstractNamsView.this.filterableBeanList != null) {
								AbstractNamsView.this.filterableBeanList
										.updateView();
							}
						}

						@Override
						public void onBeanUpdate(final IConfigurationBean bean) {
							if (AbstractNamsView.this.filterableBeanList != null) {
								AbstractNamsView.this.filterableBeanList
										.updateView();
							}
						}

						public void onConfigurationReload() {
							if (AbstractNamsView.this.filterableBeanList != null) {
								AbstractNamsView._logger.logDebugMessage(this,
										"Refreshing list for " //$NON-NLS-1$
												+ AbstractNamsView.this
														.getClass()
														.getSimpleName()
												+ "..."); //$NON-NLS-1$
								AbstractNamsView.this.filterableBeanList
										.updateView();
							}
						}
					});

			AbstractNamsView.isInitialized = true;
		} catch (final Throwable t) {
			AbstractNamsView.isInitialized = false;

			AbstractNamsView.semaphore.release();

			throw t;
		}
	}

	private void performInitializeAndSetCorrespondingViewMode() {
		AbstractNamsView._logger.logInfoMessage(this,
				"perfoming initialization..."); //$NON-NLS-1$
		try {
			this.initialize();
			AbstractNamsView._logger.logDebugMessage(this,
					"init done, update ui..."); //$NON-NLS-1$
		} catch (final Throwable e) {
			AbstractNamsView._logger.logFatalMessage(this,
					"Failed to initialize bean service!", e); //$NON-NLS-1$
			assert AbstractNamsView.isInitialized == false;
		}
		if (AbstractNamsView.isInitialized()) {
			AbstractNamsView._logger.logDebugMessage(this,
					"ui goes to normal view..."); //$NON-NLS-1$
			final Composite composite = this.viewStackContents
					.get(ViewModes.NORMAL);
			assert composite != null;
			this.viewsRootLayout.topControl = composite;
			if (this.filterableBeanList != null) {
				this.filterableBeanList.updateView();
			}
		} else {
			AbstractNamsView._logger.logDebugMessage(this,
					"ui goes to error view..."); //$NON-NLS-1$
			final Composite composite = this.viewStackContents
					.get(ViewModes.NOT_INITIALIZED);
			assert composite != null;
			this.viewsRootLayout.topControl = composite;
		}

		this.viewsRoot.layout();
	}
}
