package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelQuery;
import gov.bnl.channelfinder.api.ChannelQuery.Result;
import gov.bnl.channelfinder.api.ChannelQueryListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.channel.widgets.AbstractChannelQueryResultWidget;
import org.csstudio.channel.widgets.ChannelQueryInputBar;
import org.csstudio.channel.widgets.ConfigurableWidget;
import org.csstudio.channel.widgets.PopupMenuUtil;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

public abstract class AbstractChannelQueryView<T extends AbstractChannelQueryResultWidget>
		extends ViewPart {

	/** Memento */
	private IMemento memento = null;

	private ChannelQueryInputBar inputBar;
	private Button configureButton;
	private T channelQueryWidget;
	private String viewShortName = getClass().getSimpleName();

	private final ChannelQueryListener channelQueryListener = new ChannelQueryListener() {

		@Override
		public void queryExecuted(final Result result) {
			SWTUtil.swtThread().execute(new Runnable() {

				@Override
				public void run() {
					if (configureButton != null)
						configureButton.setEnabled(result.channels != null
								&& !result.channels.isEmpty());
				}
			});
		}
	};

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}

	@Override
	public void init(final IViewSite site, final IMemento memento)
			throws PartInitException {
		super.init(site, memento);
		// Save the memento
		this.memento = memento;
	}

	@Override
	public void saveState(final IMemento memento) {
		super.saveState(memento);
		saveWidgetState(channelQueryWidget, memento);
	}

	public abstract void saveWidgetState(T widget, IMemento memento);

	public abstract void loadWidgetState(T widget, IMemento memento);

	public void setChannelQuery(ChannelQuery channelQuery) {
		inputBar.setChannelQuery(channelQuery);
		ChannelQuery oldQuery = channelQueryWidget.getChannelQuery();
		if (oldQuery != null) {
			oldQuery.removeChannelQueryListener(channelQueryListener);
		}
		if (channelQuery != null) {
			channelQuery.execute(channelQueryListener);
		}
		channelQueryWidget.setChannelQuery(channelQuery);
	}

	protected abstract T createChannelQueryWidget(Composite parent, int style);

	protected ConfigurableWidget toConfigurableWidget(T widget) {
		if (widget instanceof ConfigurableWidget) {
			return (ConfigurableWidget) channelQueryWidget;
		} else {
			return null;
		}
	}

	protected ISelectionProvider toISelectionProvider(T widget) {
		if (widget instanceof ISelectionProvider) {
			return (ISelectionProvider) channelQueryWidget;
		} else {
			return null;
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());
		channelQueryWidget = createChannelQueryWidget(parent, SWT.NONE);

		Label lblPvName = new Label(parent, SWT.NONE);
		FormData fd_lblPvName = new FormData();
		fd_lblPvName.top = new FormAttachment(0, 8);
		fd_lblPvName.left = new FormAttachment(0, 5);
		lblPvName.setLayoutData(fd_lblPvName);
		lblPvName.setText("Query:");

		inputBar = new ChannelQueryInputBar(parent, SWT.NONE, Activator
				.getDefault().getDialogSettings(), viewShortName + "query");
		FormData fd_inputBar = new FormData();
		fd_inputBar.left = new FormAttachment(lblPvName, 6);
		fd_inputBar.top = new FormAttachment(0, 5);
		inputBar.setLayoutData(fd_inputBar);
		inputBar.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("channelQuery".equals(event.getPropertyName())) {
					setChannelQuery((ChannelQuery) event.getNewValue());
				}
			}
		});

		if (toConfigurableWidget(channelQueryWidget) != null) {
			configureButton = new Button(parent, SWT.NONE);
			configureButton.setImage(ResourceManager.getPluginImage(
					"org.csstudio.channel.widgets", "icons/gear-16.png"));
			fd_inputBar.right = new FormAttachment(configureButton, -6);
			FormData fd_configureButton = new FormData();
			fd_configureButton.top = new FormAttachment(0, 5);
			fd_configureButton.right = new FormAttachment(100, -5);
			configureButton.setLayoutData(fd_configureButton);
			configureButton.setText("Configure");
			configureButton.setEnabled(false);
			configureButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					toConfigurableWidget(channelQueryWidget)
							.openConfigurationDialog();
				}
			});
		} else {
			fd_inputBar.right = new FormAttachment(100, -5);
		}

		FormData fd_waterfallComposite = new FormData();
		fd_waterfallComposite.bottom = new FormAttachment(100, -5);
		fd_waterfallComposite.left = new FormAttachment(0, 5);
		fd_waterfallComposite.right = new FormAttachment(100, -5);
		fd_waterfallComposite.top = new FormAttachment(inputBar, 4);
		channelQueryWidget.setLayoutData(fd_waterfallComposite);

		loadWidgetState(channelQueryWidget, memento);
		inputBar.setChannelQuery(channelQueryWidget.getChannelQuery());

		if (toISelectionProvider(channelQueryWidget) != null) {
			PopupMenuUtil.installPopupForView(channelQueryWidget, getSite(),
					toISelectionProvider(channelQueryWidget));
		}
		PopupMenuUtil.installPopupForView(inputBar, getSite(), inputBar);

	}
}
