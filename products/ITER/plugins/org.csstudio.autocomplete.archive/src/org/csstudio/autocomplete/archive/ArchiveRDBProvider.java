/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.archive;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveRepository;
import org.csstudio.autocomplete.AutoCompleteHelper;
import org.csstudio.autocomplete.AutoCompletePlugin;
import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.parser.PVDescriptor;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;
import org.csstudio.autocomplete.proposals.TopProposalFinder;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * PON Archive RDB Provider for PV names.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class ArchiveRDBProvider implements IAutoCompleteProvider {

	final public static String ARCHIVE_PLUGIN_ID = org.csstudio.trends.databrowser2.Activator.PLUGIN_ID;

	private class InitTask implements Runnable {

		@Override
		public void run() {
			synchronized (readers) {
				for (ArchiveReader reader : readers.values())
					reader.close();
				readers.clear();
			}
			ArchiveDataSource[] dataSources = org.csstudio.trends.databrowser2.preferences.Preferences.getArchives();
			if (dataSources != null && dataSources.length > 0) {
				ArchiveReader reader = null;
				for (ArchiveDataSource ds : dataSources) {
					try {
						reader = ArchiveRepository.getInstance().getArchiveReader(ds.getUrl());
						readers.put(ds, reader);
						AutoCompletePlugin.getLogger().log(Level.CONFIG,
								"Successfully created archive reader for " + ds.getUrl());
					} catch (final Exception ex) {
						AutoCompletePlugin.getLogger().log(Level.SEVERE,
								"Failed to create archive reader for " + ds.getUrl() + ": " + ex.getMessage());
					}
				}
			} else {
				AutoCompletePlugin.getLogger().log(Level.WARNING,
						"Failed to read URLs from Preference Store of "
								+ ARCHIVE_PLUGIN_ID + ": empty list");
			}
		}

	}

	private Map<ArchiveDataSource, ArchiveReader> readers;
	private final IPreferenceStore dataBrowserStore;

	public ArchiveRDBProvider() {
		readers = Collections.synchronizedMap(new LinkedHashMap<ArchiveDataSource, ArchiveReader>());
		new Thread(new InitTask()).start();

		dataBrowserStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, ARCHIVE_PLUGIN_ID);
		dataBrowserStore.addPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty()
						.equals(org.csstudio.trends.databrowser2.preferences.Preferences.ARCHIVES))
					new Thread(new InitTask()).start();
			}
		});

		final IPreferenceStore archiveRDBStore = new ScopedPreferenceStore(
				InstanceScope.INSTANCE, org.csstudio.archive.rdb.Activator.ID);
		archiveRDBStore.addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
					new Thread(new InitTask()).start();
			}
		});
		final IPreferenceStore archiveReaderRDBStore = new ScopedPreferenceStore(
				InstanceScope.INSTANCE, org.csstudio.archive.reader.rdb.Activator.ID);
		archiveReaderRDBStore.addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
					new Thread(new InitTask()).start();
			}
		});
	}

	@Override
	public boolean accept(ContentType type) {
		if (type == ContentType.PVName)
			return true;
		return false;
	}

	@Override
	public AutoCompleteResult listResult(ContentDescriptor desc, int limit) {
		AutoCompleteResult result = new AutoCompleteResult();

		PVDescriptor pvDesc = null;
		if (desc instanceof PVDescriptor) {
			pvDesc = (PVDescriptor) desc;
		} else {
			return result; // empty result
		}

		String cleanedName = AutoCompleteHelper.trimWildcards(pvDesc.getName());
		Pattern namePattern = AutoCompleteHelper.convertToPattern(cleanedName);
		if (namePattern == null)
			return result;
		// adapt because we do not know how ArchiveReader can be implemented and
		// we need to known the position
		String regex = "^.*(" + namePattern.pattern() + ").*$";
		namePattern = Pattern.compile(regex);

		Matcher m = null;
		// map name => description
		Map<String, String> matchingNames = new TreeMap<String, String>();
		for (ArchiveDataSource ds : readers.keySet()) {
			try {
				String[] names = readers.get(ds).getNamesByPattern(ds.getKey(), "*" + pvDesc.getName() + "*");
				if (names != null) {
					for (String name : names) {
						if (matchingNames.size() <= limit) {
							m = namePattern.matcher(name);
							if (m.matches()) {
								Proposal proposal = new Proposal(name, false);
								proposal.setDescription(ds.getName());
								proposal.addStyle(ProposalStyle.getDefault(m.start(1), m.end(1) - 1));
								proposal.setInsertionPos(pvDesc.getStartIndex());
								result.addProposal(proposal);
							}
						}
						matchingNames.put(name, ds.getName());
					}
				}
			} catch (Exception e) {
				AutoCompletePlugin.getLogger().log(Level.WARNING,
						"Failed to retrieve names in " + ds.getName()
								+ " for " + namePattern.pattern() + ": " + e.getMessage());
			}
		}
		result.setCount(matchingNames.size());
		// handle top proposals
		TopProposalFinder trf = new TopProposalFinder(Preferences.getSeparators());
		List<Proposal> tops = trf.getTopProposals(pvDesc.getValue(), matchingNames.keySet());
		for (Proposal p : tops) {
			p.setInsertionPos(pvDesc.getStartIndex());
			if (!p.isPartial())
				p.setDescription(matchingNames.get(p.getValue()));
			result.addTopProposal(p);
		}
		Collections.sort(result.getProposals());
		return result;
	}

	@Override
	public void cancel() {
		for (ArchiveReader reader : readers.values())
			reader.cancel();
	}

}
