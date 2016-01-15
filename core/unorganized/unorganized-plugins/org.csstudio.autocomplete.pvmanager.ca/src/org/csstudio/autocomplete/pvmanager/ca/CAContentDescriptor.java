/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.ca;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.csstudio.autocomplete.parser.ContentDescriptor;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class CAContentDescriptor extends ContentDescriptor {

    private static List<String> options = new ArrayList<String>();
    static {
        options.add("{\"putCallback\":true}");
        options.add("{\"putCallback\":false}");
        options.add("{\"longString\":true}");
        options.add("{\"longString\":false}");
        options = Collections.unmodifiableList(options);
    }

    private String pvName;
    private String option;

    private boolean completingOption = false;
    private boolean complete = false;

    public static Collection<String> listOptions() {
        return options;
    }

    public String getPvName() {
        return pvName;
    }

    public void setPvName(String pvName) {
        this.pvName = pvName;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public boolean isCompletingOption() {
        return completingOption;
    }

    public void setCompletingOption(boolean completingOption) {
        this.completingOption = completingOption;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @Override
    public String toString() {
        return "CAContentDescriptor [pvName=" + pvName + ", option=" + option
                + ", completingOption=" + completingOption + ", complete="
                + complete + "]";
    }

}
