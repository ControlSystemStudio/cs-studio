/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.test;

/** Test/Demo setup info: URLs, ...
 *
 *  This setup needs to be adjusted to site-specific needs.
 *  Problem is that adjustment should not require other plugins
 *  which in turn log invoke logging, so for now it's edit-before-run.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public interface DemoSetup
{
    final public String url = "failover:(tcp://ics-srv02.sns.ornl.gov:61616)";
    final public String topic = "TEST";
}
