package org.csstudio.utility.pvmanager.fa;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.archive.reader.fastarchiver.archive_requests.FAInfoRequest;
import org.csstudio.archive.reader.fastarchiver.archive_requests.FALiveDataRequest;
import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;
import org.csstudio.archive.vtype.ArchiveVDisplayType;
import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.DataSourceTypeAdapter;
import org.epics.pvmanager.MultiplexedChannelHandler;
import org.epics.pvmanager.ValueCache;

/**
 * Extends a multiplexedChannelHandler, using values from the DLS Fast Archiver
 * and creating a FADataSourceTypeAdapter to handle these.
 * 
 * @author Friederike Johlinger
 *
 */
public class FAChannelHandler extends
		MultiplexedChannelHandler<FALiveDataRequest, ArchiveVDisplayType> {

	private String url;

	private static ScheduledExecutorService exec = Executors
			.newSingleThreadScheduledExecutor();
	FALiveDataRequest faChannel;
	private static final Logger log = Logger.getLogger(FAChannelHandler.class
			.getName());
	private ScheduledFuture<?> taskFuture;
	private final Runnable task = new Runnable() {

		@Override
		public void run() {
			// Protect the timer thread for possible problems.
			try {
				ArchiveVDisplayType[] newValues = faChannel.fetchNewValues(100);
				for (ArchiveVDisplayType newValue : newValues) {
					processMessage(newValue);
					System.out.println(newValue.getTimestamp().toDate()
							.toString());
					System.out.println(newValue.getTimestamp().getNanoSec());
				}
			} catch (FADataNotAvailableException ex) {
				// No new values available, no problem
			} catch (Exception ex) {
				log.log(Level.WARNING,
						"Problem fetching data from FastArchiver", ex);
			}
		}
	};

	/**
	 * Creates a new FAChannelHandler to handle connections with the DLS Fast
	 * Archiver
	 * 
	 * @param channelName
	 *            PV name
	 * @param url
	 *            containing the host and port of the archive
	 * @throws IOException
	 *             if no connection can be made with the archive
	 * @throws FADataNotAvailableException
	 *             if the URL doesn't have the right format
	 */
	public FAChannelHandler(String channelName, String url) throws IOException,
			FADataNotAvailableException {
		super(channelName);
		this.url = url;
	}

	/** {@inheritDoc} */
	@Override
	protected void connect() {
		try {
			faChannel = new FALiveDataRequest(url, getChannelName(), new FAInfoRequest(url).fetchMapping());
		} catch (FADataNotAvailableException | IOException e) {
			throw new RuntimeException(
					"Connection to Fast Archiver not possible, ");
		}
		taskFuture = exec.scheduleWithFixedDelay(task, 0, 100,
				TimeUnit.MILLISECONDS);
		processConnection(faChannel);
	}

	/** {@inheritDoc} */
	@Override
	protected void disconnect() {
		taskFuture.cancel(false);
		taskFuture = null;
		faChannel.close();
		processConnection(null);
	}

	/** {@inheritDoc} */
	@Override
	protected void write(Object newValue, ChannelWriteCallback callback) {
		throw new UnsupportedOperationException(
				"Can't write to Fast Archiver channel.");
	}

	/**
	 * {@inheritDoc} Returns a FADataSourceTypeAdapter
	 */
	@Override
	protected DataSourceTypeAdapter<FALiveDataRequest, ArchiveVDisplayType> findTypeAdapter(
			ValueCache<?> cache, FALiveDataRequest connection) {
		return new FADataSourceTypeAdapter();
	}

}
