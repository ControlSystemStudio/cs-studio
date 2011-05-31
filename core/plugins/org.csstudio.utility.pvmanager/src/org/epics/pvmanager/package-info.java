/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

/**
 * This package contains all the basic compononents of the PVManager framework
 * and the basic support for the language to define the creation.
 * <p>
 * There are two distinct parts in the PVManager framework. The first part
 * includes all the elements that deal with data directly: read from various
 * sources ({@link ConnectionManager}), performing computation ({@link Function}),
 * collecting data ({@link Collector}), scanning at the UI rate ({@link Scanner})
 * and notify on appropriate threads ({@link PullNotificator}).
 * <p>
 * The second part consists of an expression language that allows to define
 * how to connect the first set of objects with each other. {@link PVExpression}
 * describes data as it's coming out at the network rate, {@link AggregatedPVExpression}
 * defines data at the scanning rate for the UI, and {@link PVExpressionLanguage}
 * defines static methods that define the operator in the expression language.
 * <p>
 * Users can extend both the first part (by extending support for different types,
 * providing different support for different data source or creating new computation
 * elements) and the second part (by extending the language to support other cases.
 * All support for data types is relegated to separate packages: you can use
 * the same style to extend the framework to your needs.
 */
package org.epics.pvmanager;

