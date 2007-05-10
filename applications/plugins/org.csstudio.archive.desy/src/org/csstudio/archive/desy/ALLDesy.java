package org.csstudio.archive.desy;

import java.util.ArrayList;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.IArchiveImplementation;

public class ALLDesy implements IArchiveImplementation
{

	public ArchiveServer getServerInstance(String url) throws Exception {
        return org.csstudio.archive.desy.aapi.ArchiveServer.getInstance(url);
	}

	@SuppressWarnings("nls")
    public String[] getURLList()
    {
        return new String[]
        {
            "aapi://desy url 1",
		    "aapi://desy url 2",
		    "aapi://desy url 3"
        };
	}
}
