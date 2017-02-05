package org.csstudio.diag.epics.pvtree.model;

public interface TreeModelListener
{
    /** Notification that the type, value or severity changed
     *
     *  @param item Item that needs to be refreshed in UI
     */
    void itemChanged(TreeModelItem item);

    /** Notification that item has a new link
     *
     *  <p>Receiver should represent the link,
     *  then start the new link.
     *
     *  @param item Item that has a new link
     *  @param link The new link
     *  @see TreeModelItem#start()
     */
    void itemLinkAdded(TreeModelItem item, TreeModelItem link);

    /** Notification that model has latched updates
     *  @param latched Updates are latched, or resume
     */
    default void latchStateChanged(boolean latched) {};

    /** Notification that model has for now resolved all links
     *
     *  <p>UI may now expand the tree
     */
    default void allLinksResolved() {};
}
