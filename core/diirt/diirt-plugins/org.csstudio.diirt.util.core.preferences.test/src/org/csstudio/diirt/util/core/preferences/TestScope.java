/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.core.preferences;


import java.text.MessageFormat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;


/**
 * @author claudiorosati, European Spallation Source ERIC
 * @version 1.0.0 19 Dec 2016
 */
public class TestScope implements IScopeContext {

    private final String qualifierPrefix;

    /**
     * Creates a new test scope, that is unique a non persisting.
     */
    public TestScope ( ) {
        this.qualifierPrefix = "{0}." + Long.toHexString(System.nanoTime());
    }

    @Override
    public boolean equals ( Object obj ) {

        if ( this == obj ) {
            return true;
        } else if ( !( obj instanceof IScopeContext ) ) {
            return false;
        }

        IScopeContext other = (IScopeContext) obj;

        if ( !getName().equals(other.getName()) ) {
            return false;
        }

        IPath location = getLocation();

        return location == null ? other.getLocation() == null : location.equals(other.getLocation());

    }

    @Override
    public IPath getLocation ( ) {
        //  We don't persist tests so return null.
        return null;
    }

    @Override
    public String getName ( ) {
        return DefaultScope.INSTANCE.getName();
    }

    @Override
    public IEclipsePreferences getNode ( String qualifier ) {
        return DefaultScope.INSTANCE.getNode(MessageFormat.format(qualifierPrefix, qualifier));
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

}
