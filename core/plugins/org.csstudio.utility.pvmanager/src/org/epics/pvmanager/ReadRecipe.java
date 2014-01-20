/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents all the information necessary to connect to a {@link DataSource}.
 * It represents the contact between PVManager and the {@code DataSource}.
 *
 * @author carcassi
 */
public class ReadRecipe {
    
    private final Collection<ChannelReadRecipe> channelReadRecipes;

    ReadRecipe(Collection<ChannelReadRecipe> channelReadRecipes) {
        this.channelReadRecipes = channelReadRecipes;
    }

    /**
     * The recipes for each channel to connect.
     * 
     * @return a set of channel recipes
     */
    public Collection<ChannelReadRecipe> getChannelReadRecipes() {
        return channelReadRecipes;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.channelReadRecipes);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReadRecipe other = (ReadRecipe) obj;
        if (!Objects.equals(this.channelReadRecipes, other.channelReadRecipes)) {
            return false;
        }
        return true;
    }
    
}
