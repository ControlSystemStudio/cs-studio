/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csstudio.autocomplete.AutoCompleteConstants;
import org.csstudio.autocomplete.AutoCompleteHelper;
import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;

/**
 * DataSource prefix (loc://, sim://, ...) provider. Provides only top
 * proposals if the content match a defined data source.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class DataSourceProvider implements IAutoCompleteProvider {

	public static final String NAME = "DataSources";
	private List<String> dataSources;

	public DataSourceProvider() {
		loadDataSources();
	}

	private void loadDataSources() {
		Set<String> supportedTypes = new HashSet<String>();
		supportedTypes.addAll(AutoCompleteHelper.retrieveUtilityPVSupported());
		supportedTypes.addAll(AutoCompleteHelper.retrievePVManagerSupported());
		dataSources = new ArrayList<String>();
		for (String supportedType : supportedTypes)
			dataSources.add(supportedType + AutoCompleteConstants.DATA_SOURCE_NAME_SEPARATOR);
		Collections.sort(dataSources);
	}

	@Override
	public boolean accept(final ContentType type) {
		if (type == ContentType.PVDataSource)
			return true;
		return false;
	}

	@Override
	public AutoCompleteResult listResult(final ContentDescriptor desc, final int limit) {
		AutoCompleteResult result = new AutoCompleteResult();
		for (String ds : dataSources) {
			if (ds.startsWith(desc.getValue())) {
				Proposal proposal = new Proposal(ds, true);
				proposal.addStyle(ProposalStyle.getDefault(0, desc.getValue().length() - 1));
				proposal.setInsertionPos(desc.getStartIndex());
				result.addTopProposal(proposal);
			}
		}
		return result;
	}

	@Override
	public void cancel() {
	}

}
