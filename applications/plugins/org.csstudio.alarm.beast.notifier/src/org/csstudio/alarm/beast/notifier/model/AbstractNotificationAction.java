package org.csstudio.alarm.beast.notifier.model;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.notifier.ActionID;
import org.csstudio.alarm.beast.notifier.Activator;
import org.csstudio.alarm.beast.notifier.AlarmNotifier;
import org.csstudio.alarm.beast.notifier.EActionPriority;
import org.csstudio.alarm.beast.notifier.EActionStatus;
import org.csstudio.alarm.beast.notifier.ItemInfo;
import org.csstudio.alarm.beast.notifier.PVAlarmHandler;
import org.csstudio.alarm.beast.notifier.PVSnapshot;
import org.csstudio.alarm.beast.notifier.actions.NotificationActionListener;

/**
 * Implements common behavior for automated actions.
 * All actions must extend this class.
 * @author Fred Arnaud (Sopra Group)
 *
 */
@SuppressWarnings("nls")
public abstract class AbstractNotificationAction implements INotificationAction {

	protected List<NotificationActionListener> listeners;
	protected AlarmNotifier notifier;
	protected ActionID ID;
	protected IActionValidator validator;

	protected Thread wrappingThread;

	/** Map underlying PVs with their respective alarm  */
	protected Map<String, PVAlarmHandler> pvs;

	/** Information from {@link AlarmTreeItem} providing the automated action */
	protected ItemInfo item;

	/** If <code>true</code>, this action is from {@link AlarmTreePV} */
	protected boolean isPV = true;

	/** The delay for the action. */
	protected int delay;
	protected EActionStatus status = EActionStatus.OK;
	protected EActionPriority priority= EActionPriority.OK;

	/** {@inheritDoc} */
	@Override
    public void init(AlarmNotifier notifier, ActionID id, ItemInfo item,
			int delay, String details) {
		this.listeners = new ArrayList<NotificationActionListener>();
		this.notifier = notifier;
		addListener(notifier);
		this.ID = id;
		this.item = item;
		this.delay = delay;
		this.isPV = item.isPV();
		pvs = new HashMap<String, PVAlarmHandler>();
		if (item.isImportant()) {
			this.priority = EActionPriority.IMPORTANT;
		}
		Activator.getLogger().log(Level.FINE, "Triggered by {0}", item.getPath());
	}

	/** {@inheritDoc} */
	@Override
    public void init(AlarmNotifier notifier, ActionID id, ItemInfo item,
			int delay, String details, IActionValidator validator) {
		init(notifier, id, item, delay, details);
		this.validator = validator;
	}

	/** {@inheritDoc} */
    @Override
	public void addListener(NotificationActionListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	/** {@inheritDoc} */
    @Override
	public void start() {
		wrappingThread = new Thread(this);
		wrappingThread.start();
		while(!wrappingThread.isAlive()) {
			try { Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
	}


	/** Standard behavior of an automated action
	 * 1. Sleep during the delay
	 * 2. Update status with possible new alarms information
	 * 3. Check status
	 * 4. Fill information
	 * 5. Execute action
	 * 6. Notify listeners
	 */
	@Override
	public void run() {
		try {
			// Wait until automated action delay is over.
			Thread.sleep(delay * 1000);
			refreshStatus();
			if (status.equals(EActionStatus.OK)) {
				execute();
			}
		} catch (InterruptedException e) {
			if (status.equals(EActionStatus.FORCED)) {
				Activator.getLogger().log(Level.INFO,
						toString() + ": execution FORCED");
				refreshStatus();
				if (status.equals(EActionStatus.FORCED)) {
					execute();
				}
			} else {
				Activator.getLogger().log(Level.INFO,
						toString() + ": INTERRUPTED: Alarm recovered.");
			}
		} finally {
			actionCompleted();
		}
	}

	private void refreshStatus() {
		// If canceled or stopped, we do nothing
		if (status.equals(EActionStatus.CANCELED)
				|| status.equals(EActionStatus.STOPPED))
			return;
		// Clean PVs
		synchronized (pvs) {
			Map<String, PVAlarmHandler> pvsClone =
					new HashMap<String, PVAlarmHandler>(pvs);
			for (Entry<String, PVAlarmHandler> entry : pvsClone.entrySet())
				if (entry.getValue().validate() == false)
					pvs.remove(entry.getKey());
			if (pvs.size() == 0)
				status = EActionStatus.CANCELED;
		}
	}

	/** {@inheritDoc} */
    @Override
	public boolean isSleeping() {
		return wrappingThread.getState().equals(State.TIMED_WAITING);
	}

	/** {@inheritDoc} */
    @Override
	public void actionCompleted() {
		if (listeners != null) {
			synchronized (listeners) {
				for (NotificationActionListener listener : listeners) {
					listener.actionCompleted(this);
				}
			}
		}
	}

	/** {@inheritDoc} */
    @Override
	public void updateAlarms(PVSnapshot pv) {
		// Update alarm handler
		synchronized (pvs) {
			PVAlarmHandler alarmHandler = pvs.get(pv.getName());
			if (alarmHandler == null) {
				alarmHandler = new PVAlarmHandler();
				pvs.put(pv.getName(), alarmHandler);
			}
			alarmHandler.update(pv);
		}
		// Update action priority
		if (pv.isImportant()) {
			this.priority = EActionPriority.IMPORTANT;
		} else if (!this.priority.equals(EActionPriority.IMPORTANT)) {
			switch (pv.getCurrentSeverity()) {
			case OK: this.priority = EActionPriority.OK; break;
			case MINOR: this.priority = EActionPriority.MINOR; break;
			case MAJOR: this.priority = EActionPriority.MAJOR; break;
			case INVALID: this.priority = EActionPriority.MAJOR; break;
			}
		}
		// If PV already in alarm & state come back to NoAlarm => cancel
		refreshStatus();
		if (status.equals(EActionStatus.CANCELED)
				|| status.equals(EActionStatus.STOPPED))
			cancel();
	}

	/** {@inheritDoc} */
    @Override
	public void updateStatus(EActionStatus status) {
		this.status = status;
	}

	/** {@inheritDoc} */
    @Override
	public void cancel() {
		status = EActionStatus.CANCELED;
		if (wrappingThread != null)
		    wrappingThread.interrupt();
	}

	/** {@inheritDoc} */
    @Override
	public void forceExec() {
		status = EActionStatus.FORCED;
		if (wrappingThread != null)
		    wrappingThread.interrupt();
	}

    @Override
	public ActionID getID() {
		return ID;
	}
    @Override
	public ItemInfo getItem() {
		return item;
	}
    @Override
	public EActionStatus getActionStatus() {
		return status;
	}
    @Override
	public EActionPriority getActionPriority() {
		return priority;
	}
    @Override
	public boolean isPV() {
		return isPV;
	}

    @Override
	public String toString() {
		return item.getName() + ": " + ID.getAaTitle();
	}
}
