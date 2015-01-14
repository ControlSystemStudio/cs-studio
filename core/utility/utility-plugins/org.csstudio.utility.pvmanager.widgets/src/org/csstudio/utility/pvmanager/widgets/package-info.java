/**
 * Package containing all UI classes to display data defined in pvmanager and
 * widgets based on pvmanager connections.
 * <p>
 * There are two types of main visual components: Displays and Widgets.
 * <p>
 * Display are passive, in the sense that do not initiate any connections. They are
 * just able to display a specific data structure on screen and they provide a set
 * of parameters to tune the display. These elements are meant to be enclosed in
 * composites to create other widgets or applications.
 * <p>
 * Widgets are active, they manage the connection to a pv. They are meant to be used
 * to construct applications or elements in opis.
 */
package org.csstudio.utility.pvmanager.widgets;
