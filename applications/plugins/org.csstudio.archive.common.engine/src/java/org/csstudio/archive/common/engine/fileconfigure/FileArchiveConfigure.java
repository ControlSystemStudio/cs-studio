package org.csstudio.archive.common.engine.fileconfigure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.archive.common.engine.model.ArchiveChannelBuffer;
import org.csstudio.archive.common.engine.model.EngineModel;
import org.csstudio.archive.common.engine.model.EngineModelException;
import org.csstudio.domain.desy.epics.name.EpicsChannelName;
import org.eclipse.core.runtime.Platform;

public class FileArchiveConfigure {

    private final EngineModel _model;

    public FileArchiveConfigure(final EngineModel model) {
        _model = model;
    }

    public List<EpicsChannelName> configureChannelsFromFile() {
        final List<EpicsChannelName> channelList = new ArrayList<EpicsChannelName>();
        try {
            final URL installDir = Platform.getInstallLocation().getURL();
            final URL importPath = new URL(installDir + "//import//archive.config");
            final File importFile = new File(importPath.toURI());
            final FileReader reader = new FileReader(importFile);
            final BufferedReader bufReader = new BufferedReader(reader);
            String line;
            String groupName = null;
            while ((line = bufReader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                final String[] split = line.split("\\s+");
                if (split.length == 0) {
                    continue;
                }
                if (split[0] == null) {
                    continue;
                }
                if (split[0].equals("group") && split.length > 1) {
                    groupName = split[1];
                    continue;
                }
                if (groupName == null) {
                    continue;
                }
                try {
                    final EpicsChannelName channelName = new EpicsChannelName(split[0]);
                    channelList.add(channelName);
                    addChannel(channelName, groupName);
                    startChannel(channelName);
                } catch (final EngineModelException e) {
                    System.out.println("add channel " + split[0] + " failed, " + e.toString());
                } catch (final IllegalArgumentException e) {
                    System.out.println("invalid name " + e.toString());
                }
            }
            Thread.sleep(2000);
        } catch (final URISyntaxException e1) {
            e1.printStackTrace();
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return channelList;
    }

    private void startChannel(final EpicsChannelName channelName) throws EngineModelException {
        final ArchiveChannelBuffer<?,?> buffer = _model.getChannel(channelName.toString());
        if (buffer == null) {
            return;
        }
        if (buffer.isStarted()) {
            return;
        }

        buffer.start("START FILE IMPORT");

    }

    private void addChannel(final EpicsChannelName channelName, final String groupName) throws EngineModelException {
        System.out.println("channel name: " + channelName.toString() + "  group: " + groupName);
        _model.configureNewChannel(channelName, groupName, null, null, null);
    }

}
