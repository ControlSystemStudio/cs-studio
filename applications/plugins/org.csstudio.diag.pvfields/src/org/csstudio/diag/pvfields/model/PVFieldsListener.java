package org.csstudio.diag.pvfields.model;

/** Listener interface to PVFieldsModel */
public interface PVFieldsListener
{
	/** Something changed in the model
	 *  @param field Field that changed (new 'live' value)
	 *               or <code>null</code> if the whole model changed
	 */
	public void fieldChanged(PVInfo field);
}
