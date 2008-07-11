package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.actions.BeanToEditorId;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.composite.FilterableBeanList;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.csstudio.nams.configurator.service.AbstractConfigurationBeanServiceListener;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public abstract class AbstractNamsView extends ViewPart {

	protected static ConfigurationBeanService configurationBeanService;
	private FilterableBeanList filterableBeanList;

	public AbstractNamsView() {
		configurationBeanService.addConfigurationBeanServiceListener(new AbstractConfigurationBeanServiceListener() {
			//TODO updateView() is in most cases overkill
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
	}

	@Override
	public void createPartControl(Composite parent) {
		filterableBeanList = new FilterableBeanList(parent, SWT.None) {
			@Override
			protected IConfigurationBean[] getTableInput() {
				return getTableContent();
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
					editorInput = new ConfigurationEditorInput(getBeanClass().newInstance());

					IWorkbenchPage activePage = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					String editorId = BeanToEditorId.getEnumForClass(getBeanClass())
							.getEditorId();

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

	
	
	protected void initDragAndDrop(FilterableBeanList filterableBeanList){
		
	}

	@Override
	public void setFocus() {
		filterableBeanList.getTable().getTable().setFocus();
	}
	protected abstract IConfigurationBean[] getTableContent();

	public static void staticInject(ConfigurationBeanService beanService) {
		configurationBeanService = beanService;
	}
	
	protected abstract Class<? extends IConfigurationBean> getBeanClass();
}
