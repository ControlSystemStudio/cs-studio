package org.csstudio.nams.configurator.views;

import java.util.HashMap;
import java.util.Map;

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
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public abstract class AbstractNamsView extends ViewPart {

	protected static ConfigurationBeanService configurationBeanService;
	private FilterableBeanList filterableBeanList;
	private StackLayout viewsRootLayout;
	private static PreferenceService preferenceService;
	private static ConfigurationServiceFactory configurationServiceFactory;
	private static Logger logger;

	public boolean isInitialized() {
		return configurationBeanService != null;
	}

	private void initialize() throws Throwable {
		try {
			if (!isInitialized()) {
				LocalStoreConfigurationService localStoreConfigurationService = configurationServiceFactory
						.getConfigurationService(

								preferenceService
										.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_CONNECTION),
								DatabaseType.Oracle10g,
								preferenceService
										.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_USER),
								preferenceService
										.getString(PreferenceServiceDatabaseKeys.P_CONFIG_DATABASE_PASSWORD));
				configurationBeanService = new ConfigurationBeanServiceImpl(
						localStoreConfigurationService);
				// prepare editors
				AbstractEditor.staticInject(configurationBeanService);

				// prepare actions TODO Dieses sollten die Views selber tun.
				DeleteConfugurationBeanAction
						.staticInject(configurationBeanService);
				DuplicateConfigurationBeanAction
						.staticInject(configurationBeanService);
			} 
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
						});

			

		} catch (Throwable t) {
			configurationBeanService = null;
			throw t;
		}
	}

	private void performInitializeAndSetCorrespondingViewMode() {
		try {
			initialize();
		} catch (Throwable e) {
			logger.logFatalMessage(this, "Failed to initialize bean service!",
					e);
			assert configurationBeanService == null;
		}
		if (isInitialized()) {
			Composite composite = viewStackContents.get(ViewModes.NORMAL);
			assert composite != null;
			viewsRootLayout.topControl = composite;
			if (filterableBeanList != null) {
				filterableBeanList.updateView();
			}
		} else {
			Composite composite = viewStackContents
					.get(ViewModes.NOT_INITIALIZED);
			assert composite != null;
			viewsRootLayout.topControl = composite;
		}
	}

	public AbstractNamsView() {
		// try {
		// initialize();
		// } catch (Throwable e) {
		// // Ignore.
		// }
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

	@Override
	public void createPartControl(Composite rootComposite) {
		Composite rootParent = new Composite(rootComposite, SWT.NONE);
		viewsRootLayout = new StackLayout();
		rootParent.setLayout(viewsRootLayout);

		Composite normalViewElements = new Composite(rootParent, SWT.TOP);
		normalViewElements.setLayout(new GridLayout(1, true));

		Composite error = new Composite(rootParent, SWT.TOP);
		error.setLayout(new GridLayout(1, true));
		new Label(error, SWT.ICON_ERROR).setText("Error!");

		viewStackContents.put(ViewModes.NORMAL, normalViewElements);
		viewStackContents.put(ViewModes.NOT_INITIALIZED, error);

		performInitializeAndSetCorrespondingViewMode();

		filterableBeanList = new FilterableBeanList(normalViewElements,
				SWT.None) {
			@Override
			protected IConfigurationBean[] getTableInput() {
				// if (isInitialized()) {
				return getTableContent();
				// }
				// return new IConfigurationBean[0];
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
