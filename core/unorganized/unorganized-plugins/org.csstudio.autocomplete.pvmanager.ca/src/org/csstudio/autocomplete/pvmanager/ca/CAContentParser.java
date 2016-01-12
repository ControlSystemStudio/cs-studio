/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.ca;

import org.csstudio.autocomplete.AutoCompleteConstants;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.IContentParser;

/**
 * Channel Access Data Source content parser.
 *
 * @author Fred Arnaud (Sopra Group)
 */
public class CAContentParser implements IContentParser {

    public static final String CA_SOURCE = "ca://";
    public static final String OPTION_START = "{";
    public static final String OPTION_END = "}";

    private CAContentDescriptor currentDescriptor;
    private String contentToParse;

    @Override
    public boolean accept(final ContentDescriptor desc) {
        if (desc.getValue().startsWith(AutoCompleteConstants.FORMULA_PREFIX)) {
            return false;
        }
        if (desc.getValue().startsWith(CA_SOURCE)
                || (desc.getValue().indexOf(AutoCompleteConstants.DATA_SOURCE_NAME_SEPARATOR) == -1
                    && CA_SOURCE.equals(desc.getDefaultDataSource()))) {
            return true;
        }
        return false;
    }

    @Override
    public ContentDescriptor parse(final ContentDescriptor desc) {
        int startIndex = 0;
        contentToParse = desc.getValue();
        if (contentToParse.startsWith(CA_SOURCE)) {
            contentToParse = contentToParse.substring(CA_SOURCE.length());
        }
        currentDescriptor = new CAContentDescriptor();
        currentDescriptor.setContentType(CAContentType.CAPV);
        currentDescriptor.setStartIndex(startIndex);
        currentDescriptor.setValue(contentToParse);
        parseCAContent(contentToParse);
        return currentDescriptor;
    }

    private void parseCAContent(String caContent) {
        String pvName = null;
        String option = null;

        // handle option
        int ltIndex = caContent.indexOf(OPTION_START);
        int gtIndex = caContent.indexOf(OPTION_END);

        if (ltIndex > 0) { // pvname
            pvName = caContent.substring(0, ltIndex).trim();
            if (gtIndex > 0 && gtIndex > ltIndex) {
                option = caContent.substring(ltIndex, gtIndex + 1);
                currentDescriptor.setComplete(true);
            } else { // complete option
                option = caContent.substring(ltIndex);
                currentDescriptor.setCompletingOption(true);
            }
            currentDescriptor.setOption(option);
        } else {
            pvName = caContent.trim();
            if (!caContent.isEmpty()
                    && caContent.substring(caContent.length() - 1).equals(" ")) {
                currentDescriptor.setOption("");
                currentDescriptor.setCompletingOption(true);
            }
        }
        currentDescriptor.setPvName(pvName);
    }

}
