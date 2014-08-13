package org.csstudio.utility.pvmanager.fa;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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

public class FAChannelHandler extends MultiplexedChannelHandler<FALiveDataRequest, ArchiveVDisplayType> {
	
	private String url;
    private static ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
	FALiveDataRequest faChannel; 
	private static final Logger log = Logger.getLogger(FAChannelHandler.class.getName());
    private ScheduledFuture<?> taskFuture;
	private final Runnable task = new Runnable() {
		 
        @Override
        public void run() {
            // Protect the timer thread for possible problems.
            try {
                List<ArchiveVDisplayType> newValues = faChannel.fetchNewValues();

                for (ArchiveVDisplayType newValue : newValues) {
                    processMessage(newValue);
                }
            } catch (Exception ex) {
                log.log(Level.WARNING, "Problem fetching data from FastArchiver", ex);
            }
        }
    };

	public FAChannelHandler(String channelName, String url) {
		super(channelName);
		this.url = url;
		HashMap<String, int[]> mapping = null;
		try {
			FAInfoRequest faInfo = new FAInfoRequest(url);
			 mapping = faInfo.fetchMapping();
		} catch (FADataNotAvailableException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		//int bpm = mapping.get(channelName)[0];
		// TODO Create Connection to archive with stream or archived data
		
		return;
	}

	/** {@inheritDoc} */
	@Override
	protected void connect() {
		try {
			faChannel =  new FALiveDataRequest(url);
		} catch (FADataNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		taskFuture = exec.scheduleWithFixedDelay(task, 0, 10, TimeUnit.MILLISECONDS);
        processConnection(faChannel); //TODO: What is this ConnectionPayload?		
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
        throw new UnsupportedOperationException("Can't write to Fast Archiver channel.");
	}
	
	@Override
    protected DataSourceTypeAdapter<FALiveDataRequest, ArchiveVDisplayType> findTypeAdapter(ValueCache<?> cache, FALiveDataRequest connection) {
		return new FADataSourceTypeAdapter();
	}





}
