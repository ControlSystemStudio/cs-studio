package org.csstudio.archive.reader.fastarchiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.csstudio.apputil.text.RegExHelper;
import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.fastarchiver.fast_archive_requests.FAArchivedDataRequest;
import org.csstudio.archive.reader.fastarchiver.fast_archive_requests.FAInfoRequest;
import org.epics.util.time.Timestamp;

/**
 * 
 * @author Friederike Johlinger
 *
 */
//most methods need to be implemented
public class FastArchiveReader implements ArchiveReader{
		
		private final String url;
		private final int version = 1;
		private final String description;
		private HashMap<String, int[]> mapping;
		
		/**
		 * Connect to the Fast Archiver
		 * @param url String should start with fads
		 */
		public FastArchiveReader (String url){
			this.url = url;
			description = createDescription();
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
				int numOfArchives = 1;
				ArchiveInfo[] archiveInfo = new ArchiveInfo[numOfArchives];
				archiveInfo[0] = new ArchiveInfo("Fast Archiver", "Fast Archiver of DLS", 1);
				return archiveInfo;
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
			// get all names
			FAInfoRequest faInfoRequest = new FAInfoRequest(url);
			mapping = faInfoRequest.createMapping();
			TreeSet<String> allNames = new TreeSet<String>(mapping.keySet());
			
			// find matching names
			List<String> matches = new ArrayList<String>();
			for (String name: allNames){
				if (Pattern.matches(reg_exp.toLowerCase(), name.toLowerCase())) matches.add(name);
			}
			String[] matchingNames = new String[matches.size()];
			int i = 0;
			for (String name: matches){
				matchingNames[i] = name;
				i++;
			}
			//System.out.println(Arrays.toString(matchingNames));
			return matchingNames;
			
		}

		/** {@inheritDoc}*/
		@Override
		public ValueIterator getRawValues(int key, String name, Timestamp start,
				Timestamp end) throws UnknownChannelException, Exception {
			System.out.println("getRawValues");
			if (mapping == null){
				mapping = new FAInfoRequest(url).createMapping();
			}
			FAArchivedDataRequest faDataRequest = new FAArchivedDataRequest(url, mapping);
			return faDataRequest.getRawValues(name, start, end);
		}

		/** {@inheritDoc}*/
		@Override
		public ValueIterator getOptimizedValues(int key, String name,
				Timestamp start, Timestamp end, int count)
				throws UnknownChannelException, Exception {
			System.out.printf("getOptimizedValues %d, %s, ..., %d\n", key, name, count);
			if (mapping == null){
				mapping = new FAInfoRequest(url).createMapping();
			}
			FAArchivedDataRequest faDataRequest = new FAArchivedDataRequest(url, mapping);
			return faDataRequest.getOptimisedValues(name, start, end, count);
			}

		/** {@inheritDoc}*/
		@Override
		public void cancel() {
			// TODO Auto-generated method stub
			
		}

		/** {@inheritDoc}*/
		@Override
		public void close() {
			//working with sockets, nothing to close			
		}
		
		/* OWN METHODS */
		private String createDescription(){
			StringBuffer sb = new StringBuffer();
			sb.append("ArchiveReader to communicate with the Fast Archiver.\n");
			sb.append("version: " + version);
			return sb.toString();
			
		}
	}

