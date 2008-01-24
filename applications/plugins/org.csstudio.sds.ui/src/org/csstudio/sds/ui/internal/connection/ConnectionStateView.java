package org.csstudio.sds.ui.internal.connection;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.internal.connection.ActiveConnectorsState;
import org.csstudio.sds.internal.connection.ConnectionService;
import org.csstudio.sds.internal.connection.ConnectionUtil;
import org.csstudio.sds.internal.connection.Connector;
import org.csstudio.sds.internal.connection.IConnectionServiceStateListener;
import org.csstudio.sds.internal.connection.IConnectorStateListener;
import org.csstudio.sds.internal.connection.dal.SystemConnector;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.context.ConnectionState;

/**
 * A view that shows the status of all currently connected channels.
 * 
 * @author Sven Wende
 * 
 */
public final class ConnectionStateView extends ViewPart {
	/**
	 * The view´s ID.
	 */
	public static final String VIEW_ID = "org.csstudio.sds.ui.internal.connection.ConnectionStateView";

	/**
	 * A hashmap, which contains listeners that have already been added to
	 * connectors.
	 */
	private HashMap<Connector, IConnectorStateListener> _connectorStateListeners;

	/**
	 * Constructor.
	 */
	public ConnectionStateView() {
		super();
		_connectorStateListeners = new HashMap<Connector, IConnectorStateListener>();
	}

	/**
	 * The Treeviewer used for displaying the connection status.
	 */
	private TreeViewer _tv;

	/**
	 * A UI job used to refresh the view repeatedly.
	 */
	private UIJob _updateJob;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		// create treeviewer
		_tv = new TreeViewer(parent);
		_tv.setContentProvider(new BaseWorkbenchContentProvider() {
			private ConnectionService _connectionService;

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Object[] getElements(final Object element) {
				return ((ConnectionService) element).getStatesByChannel()
						.keySet().toArray();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Object[] getChildren(final Object element) {
				if (element instanceof IProcessVariableAddress) {
					ActiveConnectorsState state = _connectionService
							.getStatesByChannel().get(element);

					List<SystemConnector> connectors = state
							.getConnectors((IProcessVariableAddress) element);

					for (final SystemConnector c : connectors) {
						if (!_connectorStateListeners.containsKey(c)) {
							IConnectorStateListener listener = new IConnectorStateListener() {
								private long _lastRefresh = 0;

								/**
								 * {@inheritDoc}
								 */
								public void connectorStateChanged(
										final Connector connector) {
									if ((System.currentTimeMillis() - _lastRefresh) > 1000) {
										_lastRefresh = System
												.currentTimeMillis();
										ConnectionUtil.getInstance().syncExec(
												new Runnable() {
													public void run() {
														_tv.update(c, null);
													}

												});
									}

								}
							};

//							c.addConnectorStateListener(listener);
//							_connectorStateListeners.put(c, listener);
						}

					}
					return connectors.toArray();
				}
				return new Object[0];
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void inputChanged(final Viewer viewer,
					final Object oldInput, final Object newInput) {
				_connectionService = (ConnectionService) newInput;
			}

		});

		// add some columns to the tree
		Tree tree = _tv.getTree();
		tree.setHeaderVisible(true);
		TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
		column1.setText("Channel");
		column1.setWidth(330);
		TreeColumn column2 = new TreeColumn(tree, SWT.CENTER);
		column2.setText("State");
		column2.setWidth(80);
		TreeColumn column3 = new TreeColumn(tree, SWT.RIGHT);
		column3.setText("Latest Value");
		column3.setWidth(80);
		TreeColumn column4 = new TreeColumn(tree, SWT.RIGHT);
		column4.setText("Latest Value Timestamp");
		column4.setWidth(130);
		TreeColumn column5 = new TreeColumn(tree, SWT.RIGHT);
		column5.setText("Condition");
		column5.setWidth(130);

		// setup label and input providers
		ILabelDecorator decorator = PlatformUI.getWorkbench()
				.getDecoratorManager().getLabelDecorator();
		_tv.setLabelProvider(new TableDecoratingLabelProvider(
				new StateViewLabelProvider(), decorator));

		final ConnectionService connectionService = ConnectionService.getInstance();

		// set input
		_tv.setInput(connectionService);

		// listen to state changes of the connection service
		connectionService
				.addConnectionServiceStateListener(new IConnectionServiceStateListener() {
					/**
					 * {@inheritDoc}
					 */
					public void connectionServiceStateChanged(
							final ConnectionService service) {

						ConnectionUtil.getInstance().syncExec(new Runnable() {
							public void run() {
								// _tv.setInput(connectionService);
								_tv.refresh();
							}

						});

					}

				});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		_updateJob.cancel();
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void setFocus() {
	}

	/**
	 * A label provider for the Treeviewer used in this view.
	 * 
	 * @author swende
	 * 
	 */
	class StateViewLabelProvider extends WorkbenchLabelProvider implements
			ITableLabelProvider {

		/**
		 * {@inheritDoc}
		 */
		public Image getColumnImage(final Object element, final int columnIndex) {
			if (columnIndex == 0) {
				return getImage(element);
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getColumnText(final Object element, final int columnIndex) {
			String result = "";

			if (element instanceof Connector) {
				Connector connector = (Connector) element;

				switch (columnIndex) {
				case 0:
					result = connector.getProcessVariable().getProperty();
					break;
				case 1:
					ConnectionState state = connector.getState()
							.getConnectionState();
					result = state != null ? state.name() : "";
					break;
				case 2:
					Object value = connector.getState().getLatestValue();
					result = value != null ? value.toString() : "";
					break;
				case 3:
					long timeStamp = connector.getState()
							.getLatestValueTimestamp();
					DateFormat df = DateFormat.getTimeInstance();
					result = df.format(new Date(timeStamp));
					break;
				case 4:
					DynamicValueCondition condition = connector.getState()
							.getDynamicValueCondition();
					result = condition != null ? condition.getStates()
							.toString() : "";
					break;
				default:
					result = "";
					break;
				}
			} else if (element instanceof IProcessVariableAddress) {
				switch (columnIndex) {
				case 0:
					result = ((IProcessVariableAddress) element).getProperty();
					break;
				default:
					result = "";
					break;
				}
			} else {
				switch (columnIndex) {
				case 0:
					result = getText(element);
					break;
				default:
					result = "";
					break;
				}
			}
			return result;
		}

	}

}
