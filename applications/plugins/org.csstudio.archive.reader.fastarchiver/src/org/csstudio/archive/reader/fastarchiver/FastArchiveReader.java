package org.csstudio.archive.reader.fastarchiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.csstudio.apputil.text.RegExHelper;
import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.fastarchiver.fast_archive_requests.FastArchiverArchivedDataRequest;
import org.csstudio.archive.reader.fastarchiver.fast_archive_requests.FastArchiverInfoRequest;
import org.epics.util.time.Timestamp;


//most methods need to be implemented
public class FastArchiveReader implements ArchiveReader{
		
		private final String url;
		private final int version = 1;
		private final String description;
		private FastArchiverInfoRequest faInfoRequest;
		private FastArchiverArchivedDataRequest faDataRequest;
		
		/**
		 * Connect to the Fast Archiver
		 * @param url String should start with fads
		 */
		public FastArchiveReader (String url){
			this.url = url;
			description = createDescription();
			faInfoRequest = new FastArchiverInfoRequest(url);
			faDataRequest = new FastArchiverArchivedDataRequest(url);
			
		}

		/* FROM ARCHIVEREADER */
		@Override
		public String getServerName() {
			return "Fast Archiver";
		}

		@Override
		public String getURL() {
			return url;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public int getVersion() {
			return version;
		}

		/** {@inheritDoc}*/
		// rather superfluous for the fast archiver
		@Override
		public ArchiveInfo[] getArchiveInfos() {
			return faInfoRequest.getArchiveInfos();
		}

		/** {@inheritDoc}*/
		@Override
		public String[] getNamesByPattern(int key, String glob_pattern)
				throws Exception {
			return getNamesByRegExp(key, RegExHelper.fullRegexFromGlob(glob_pattern));
		}

		/** {@inheritDoc}*/
		// ignores key, not very neat, works
		@Override
		public String[] getNamesByRegExp(int key, String reg_exp) throws Exception {
			String [] allNames = faInfoRequest.getAllNames();
			// find matching names
			List<String> matches = new ArrayList<String>();
			for (int i = 0; i < allNames.length; i++){
				if (allNames[i].toLowerCase().matches(reg_exp.toLowerCase())) matches.add(allNames[i]);
			}
			String[] matchingNames = new String[matches.size()];
			int i = 0;
			for (String name: matches){
				matchingNames[i] = name;
				i++;
			}
			System.out.println(Arrays.toString(matchingNames));
			return matchingNames;
			
		}

		/** {@inheritDoc}*/
		@Override
		public ValueIterator getRawValues(int key, String name, Timestamp start,
				Timestamp end) throws UnknownChannelException, Exception {
			// TODO Auto-generated method stub
			return null; //faDataRequest.getRawValues(name, start, end);
		}

		/** {@inheritDoc}*/
		@Override
		public ValueIterator getOptimizedValues(int key, String name,
				Timestamp start, Timestamp end, int count)
				throws UnknownChannelException, Exception {
			// TODO Auto-generated method stub
			return null;
		}

		/** {@inheritDoc}*/
		@Override
		public void cancel() {
			// TODO Auto-generated method stub
			
		}

		/** {@inheritDoc}*/
		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}
		
		/* OWN METHODS */
		private String createDescription(){
			StringBuffer sb = new StringBuffer();
			sb.append("ArchiveReader to communicate with the Fast Archiver.\n");
			sb.append("version: " + version);
			return sb.toString();
			
		}
	}

