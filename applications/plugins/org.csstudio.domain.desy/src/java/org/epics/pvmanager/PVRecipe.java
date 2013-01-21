/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 * Represents all information needed to create a PV.
 * <p>
 * Once a recipe is created, it cannot be changed.
 *
 * @author carcassi
 */
class PVRecipe {

    private final DataRecipe dataSourceRecipe;
    private final DataSource dataSource;
    private final Notifier notifier;

    /**
     * Creates a new recipe. The collections passed to the constructor must
     * already be immutable copies.
     *
     * @param dataSourceRecipe the list of all channels needed by each collector
     * @param notifier
     */
    PVRecipe(DataRecipe dataSourceRecipe, DataSource dataSource, Notifier notifier) {
        this.dataSourceRecipe = dataSourceRecipe;
        this.dataSource = dataSource;
        this.notifier = notifier;
    }

    /**
     * The recipe used by the DataSource to connect all required channels.
     *
     * @return the data recipe
     */
    DataRecipe getDataSourceRecipe() {
        return dataSourceRecipe;
    }

    /**
     * The notifier used for the PV.
     *
     * @return the notifier
     */
    Notifier getNotifier() {
        return notifier;
    }

    /**
     * The data source used to connect to the data.
     *
     * @return the data source
     */
    DataSource getDataSource() {
        return dataSource;
    }

}
