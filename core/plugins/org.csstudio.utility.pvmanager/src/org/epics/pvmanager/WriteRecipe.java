/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents all the values, channel names and ordering information needed
 * for writing
 *
 * @author carcassi
 */
public class WriteRecipe {
    private final Collection<ChannelWriteRecipe> channelWriteRecipes;

    WriteRecipe(Collection<ChannelWriteRecipe> channelWriteRecipes) {
        this.channelWriteRecipes = channelWriteRecipes;
    }

    /**
     * The channel recipes for this recipe.
     * 
     * @return the set of channel recipe
     */
    public Collection<ChannelWriteRecipe> getChannelWriteRecipes() {
        return channelWriteRecipes;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.channelWriteRecipes);
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
        final WriteRecipe other = (WriteRecipe) obj;
        if (!Objects.equals(this.channelWriteRecipes, other.channelWriteRecipes)) {
            return false;
        }
        return true;
    }
    
}
