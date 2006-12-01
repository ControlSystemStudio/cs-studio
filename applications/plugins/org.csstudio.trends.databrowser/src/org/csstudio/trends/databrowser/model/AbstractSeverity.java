package org.csstudio.trends.databrowser.model;

import org.csstudio.archive.Severity;

/** Helper for implementing the Severity interface.
 *  <p>
 *  Handles the text portion, leaves the rest abstract.
 *  @author Kay Kasemir
 */
abstract class AbstractSeverity implements Severity
{
    final String text;
        
    AbstractSeverity(String text)
    {   this.text = text; }
    
    public String toString()
    {   return text; }
}
