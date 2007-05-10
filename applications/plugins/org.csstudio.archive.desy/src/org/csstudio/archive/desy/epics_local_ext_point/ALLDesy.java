package org.csstudio.archive.desy.epics_local_ext_point;

import java.util.ArrayList;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.IArchiveImplementation;

public class ALLDesy implements IArchiveImplementation
{

	public ArchiveServer getServerInstance(String url) throws Exception {
        return org.csstudio.archive.desy.epics_local.ArchiveServer.getInstance(url);
	}

	@SuppressWarnings("nls")
    public String[] getURLList()
    {
        return new String[]
        {
            "archiveRecord://",
            "archiveRecord://",
            "archiveRecord://"
        };
	}
}
