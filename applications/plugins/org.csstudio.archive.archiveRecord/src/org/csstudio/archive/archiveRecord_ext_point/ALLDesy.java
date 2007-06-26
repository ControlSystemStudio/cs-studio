package org.csstudio.archive.archiveRecord_ext_point;

import java.util.ArrayList;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.IArchiveImplementation;

public class ALLDesy implements IArchiveImplementation
{

	public ArchiveServer getServerInstance(String url) throws Exception {
        return org.csstudio.archive.archiveRecordInterface.ArchiveServer.getInstance(url);
	}

	@SuppressWarnings("nls")
	/*
    public String[] getURLList()
    {
        return new String[]
        {
            "archiveRecord://",
            "archiveRecord://",
            "archiveRecord://"
        };
	}
	*/
		public String[] getURLList()
    {   return null;	}
}
