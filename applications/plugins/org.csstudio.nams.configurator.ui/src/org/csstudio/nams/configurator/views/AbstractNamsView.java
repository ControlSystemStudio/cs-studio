package org.csstudio.nams.configurator.views;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

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

	protected static ConfigurationBeanService configurationBeanService;

	protected static ConfigurationBeanService getConfigurationBeanService() {
		return configurationBeanService;
	}

	private FilterableBeanList filterableBeanList;
	private StackLayout viewsRootLayout;
	private static PreferenceService preferenceService;
	private static ConfigurationServiceFactory configurationServiceFactory;
	private static Logger logger;

	boolean isInitialized = false;

	public boolean isInitialized() {
		return isInitialized;
	}

	private static Semaphore semaphore = new Semaphore(1);

	synchronized private void initialize() throws Throwable {
		try {
			semaphore.acquire(1);
			if (!isInitialized()) {
				String P_CONFIG_DATABASE_CONNECTION = preferenceService
						.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION);
				String P_CONFIG_DATABASE_TYPE_asString = preferenceService
						.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_TYPE);
				String P_CONFIG_DATABASE_USER = preferenceService
						.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER);
				String P_CONFIG_DATABASE_PASSWORD = preferenceService
						.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD);

				if ((P_CONFIG_DATABASE_CONNECTION == null || P_CONFIG_DATABASE_CONNECTION
						.length() == 0)
						|| (P_CONFIG_DATABASE_TYPE_asString == null || P_CONFIG_DATABASE_TYPE_asString
								.length() == 0)
						|| (P_CONFIG_DATABASE_USER == null || P_CONFIG_DATABASE_USER
								.length() == 0)
						|| (P_CONFIG_DATABASE_PASSWORD == null || P_CONFIG_DATABASE_PASSWORD
								.length() == 0)) {
					throw new RuntimeException("Missing database setting!");
				}

				DatabaseType P_CONFIG_DATABASE_TYPE = DatabaseType
						.valueOf(P_CONFIG_DATABASE_TYPE_asString);

				LocalStoreConfigurationService localStoreConfigurationService = configurationServiceFactory
						.getConfigurationService(P_CONFIG_DATABASE_CONNECTION,
								P_CONFIG_DATABASE_TYPE, P_CONFIG_DATABASE_USER,
								P_CONFIG_DATABASE_PASSWORD);

				logger.logDebugMessage(this,
						"DB connected with P_CONFIG_DATABASE_CONNECTION: "
								+ P_CONFIG_DATABASE_CONNECTION);
				logger.logDebugMessage(this,
						"DB connected with P_CONFIG_DATABASE_TYPE: "
								+ P_CONFIG_DATABASE_TYPE);
				logger.logDebugMessage(this,
						"DB connected with P_CONFIG_DATABASE_USER: "
								+ P_CONFIG_DATABASE_USER);
				logger
						.logDebugMessage(
								this,
								"DB P_CONFIG_DATABASE_PASSWORD is: "
										+ (P_CONFIG_DATABASE_PASSWORD != null
												&& P_CONFIG_DATABASE_PASSWORD
														.length() > 0 ? "available"
												: "missing"));

				if (configurationBeanService == null) {
					configurationBeanService = new ConfigurationBeanServiceImpl();
				}
				((ConfigurationBeanServiceImpl) configurationBeanService)
						.setNewConfigurationStore(localStoreConfigurationService);

				// prepare editors
				AbstractEditor.staticInject(configurationBeanService);

				// prepare actions
				DeleteConfugurationBeanAction
						.staticInject(configurationBeanService);
				DuplicateConfigurationBeanAction
						.staticInject(configurationBeanService);
				
				configurationBeanService.refreshData();
			}
			semaphore.release(1);

			configurationBeanService
					.addConfigurationBeanServiceListener(new AbstractConfigurationBeanServiceListener() {
						// TODO updateView() is in most cases overkill
						@Override
						public void onBeanInsert(IConfigurationBean bean) {
							if (filterableBeanList != null) {
								filterableBeanList.updateView();
							}
						}

						@Override
						public void onBeanUpdate(IConfigurationBean bean) {
							if (filterableBeanList != null) {
								filterableBeanList.updateView();
							}
						}

						@Override
						public void onBeanDeleted(IConfigurationBean bean) {
							if (filterableBeanList != null) {
								filterableBeanList.updateView();
							}
						}

						public void onConfigurationReload() {
							if (filterableBeanList != null) {
								logger
								.logDebugMessage(this,
										"Refreshing list for "+AbstractNamsView.this.getClass().getSimpleName()+"...");
								filterableBeanList.updateView();
							}
						}
					});

			

			isInitialized = true;
		} catch (Throwable t) {
			isInitialized = false;

			semaphore.release();

			throw t;
		}
	}

	private void performInitializeAndSetCorrespondingViewMode() {
		logger.logInfoMessage(this, "perfoming initialization...");
		try {
			initialize();
			logger.logDebugMessage(this, "init done, update ui...");
		} catch (Throwable e) {
			logger.logFatalMessage(this, "Failed to initialize bean service!",
					e);
			assert isInitialized == false;
		}
		if (isInitialized()) {
			logger.logDebugMessage(this, "ui goes to normal view...");
			Composite composite = viewStackContents.get(ViewModes.NORMAL);
			assert composite != null;
			viewsRootLayout.topControl = composite;
			if (filterableBeanList != null) {
				filterableBeanList.updateView();
			}
		} else {
			logger.logDebugMessage(this, "ui goes to error view...");
			Composite composite = viewStackContents
					.get(ViewModes.NOT_INITIALIZED);
			assert composite != null;
			viewsRootLayout.topControl = composite;
		}

		viewsRoot.layout();
	}

	public AbstractNamsView() {

	}

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

	private Map<ViewModes, Composite> viewStackContents = new HashMap<ViewModes, Composite>();
	private Composite viewsRoot;

	@Override
	public void createPartControl(Composite rootComposite) {
		viewsRoot = new Composite(rootComposite, SWT.NONE);
		viewsRootLayout = new StackLayout();
		viewsRoot.setLayout(viewsRootLayout);

		Composite normalViewElements = new Composite(viewsRoot, SWT.TOP);
		normalViewElements.setLayout(new GridLayout(1, true));

		Composite error = new Composite(viewsRoot, SWT.TOP);
		error.setLayout(new GridLayout(1, true));
		Label errorLabel = new Label(error, SWT.WRAP);
		errorLabel
				.setText("Konnte keine Verbindung zur Datenbank herstellen.\nBitte überprüfen Sie die Einstellungen unter:\nCSS-Application/Configuration/New AMS.");

		viewStackContents.put(ViewModes.NORMAL, normalViewElements);
		viewStackContents.put(ViewModes.NOT_INITIALIZED, error);

		performInitializeAndSetCorrespondingViewMode();

		filterableBeanList = new FilterableBeanList(normalViewElements,
				SWT.None) {
			@Override
			protected IConfigurationBean[] getTableInput() {
				if (isInitialized()) {
					return getTableContent();
				}
				return new IConfigurationBean[0];
			}
		};

		MenuManager menuManager = new MenuManager();
		TableViewer table = filterableBeanList.getTable();
		Menu menu = menuManager.createContextMenu(table.getTable());
		table.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, table);
		getSite().setSelectionProvider(table);

		menuManager.add(new Action() {
			@Override
			public void run() {
				ConfigurationEditorInput editorInput;
				try {
					editorInput = new ConfigurationEditorInput(getBeanClass()
							.newInstance());

					IWorkbenchPage activePage = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					String editorId = BeanToEditorId.getEnumForClass(
							getBeanClass()).getEditorId();

					activePage.openEditor(editorInput, editorId);
				} catch (InstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}

			@Override
			public String getText() {
				return "Neu";
			}
		});

		initDragAndDrop(filterableBeanList);

		preferenceService
				.addPreferenceChangeListenerFor(
						new PreferenceServiceDatabaseKeys[] {
								PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION,
								PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER,
								PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD,
								PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_TYPE },
						new PreferenceChangeListener() {
							public <T extends Enum<?> & HoldsAPreferenceId> void preferenceUpdated(
									T id, Object oldValue, Object newValue) {
								isInitialized = false;
								performInitializeAndSetCorrespondingViewMode();
							}
						});
		
		IActionBars actionBar = getViewSite().getActionBars();
		actionBar.getToolBarManager().add(new Action() {
			@Override
			public void run() {
				if (MessageDialog
						.openQuestion(
								getViewSite().getShell(),
								"Reload entire configuration?",
								"Do you realy like to reload the entire configuration?" +
								"\n\nUnsaved changes may get lost.")) {
					logger
							.logDebugMessage(this,
									"Reload of entire configuration requested by user...");
					configurationBeanService.refreshData();
					logger
					.logDebugMessage(this,
					"Reload of entire configuration done.");
				}
			}

			@Override
			public String getText() {
				return "Reload";
			}

			@Override
			public String getToolTipText() {
				return "Reloads the entire configuration - unsaved changes may be discarded!";
			}

			@Override
			public int getStyle() {
				return SWT.BORDER | SWT.ICON_WORKING;
			}

		});
	}

	protected void initDragAndDrop(FilterableBeanList filterableBeanList) {

	}

	@Override
	public void setFocus() {
		filterableBeanList.getTable().getTable().setFocus();
	}

	protected abstract IConfigurationBean[] getTableContent();

	protected abstract Class<? extends IConfigurationBean> getBeanClass();

	public static void staticInject(PreferenceService preferenceService) {
		AbstractNamsView.preferenceService = preferenceService;
	}

	public static void staticInject(
			ConfigurationServiceFactory configurationServiceFactory) {
		AbstractNamsView.configurationServiceFactory = configurationServiceFactory;
	}

	public static void staticInject(Logger logger) {
		AbstractNamsView.logger = logger;
	}
}
