/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.diirt.datasource.ChannelHandler;
import org.diirt.datasource.ChannelReadRecipe;
import org.diirt.datasource.ChannelWriteRecipe;
import org.diirt.datasource.DataSource;
import org.diirt.datasource.ReadRecipe;
import org.diirt.datasource.WriteRecipe;
import org.diirt.datasource.util.FunctionParser;
import org.diirt.datasource.vtype.DataTypeSupport;

/**
 * @author Kunal Shroff
 *
 */
public class BeastDataSource extends DataSource {

    private static final Logger log = Logger.getLogger(BeastDataSource.class.getName());

    private final BeastDataSourceConfiguration configuration;
    private final BeastTypeSupport typeSupport;

    private Connection connection;
    private Session session;

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    public BeastDataSource(BeastDataSourceConfiguration configuration) {
        super(true);
        this.configuration = configuration;

        typeSupport = new BeastTypeSupport();

        // Create a ConnectionFactory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(configuration.getBrokerUrl());

        // Create a Connection
        try {
            connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected ChannelHandler createChannel(String channelName) {
        /**
         * Parse the name to support defining the read and write types / the
         * sytax is as follows
         * 
         * jms://topic_name<readType, writeType>{filter}
         **/
        List<Object> parsedTokens = parseName(channelName);
        return new BeastChannelHandler(parsedTokens.get(0).toString(), this);
    }

    @Override
    public void connectRead(ReadRecipe readRecipe) {
        super.connectRead(readRecipe);

        // Initialize all values
        for (ChannelReadRecipe channelReadRecipe : readRecipe.getChannelReadRecipes()) {
            initializeRead(channelReadRecipe.getChannelName());
        }
    }

    private void initializeRead(String channelName) {
        log.info("Initalizing Read :" + channelName);
        List<Object> parsedTokens = parseName(channelName);

        BeastChannelHandler channel = (BeastChannelHandler) getChannels().get(channelHandlerLookupName(channelName));
        if (channel != null ) {
            if (parsedTokens.get(1) != null) {
                log.info("setting selection" + parsedTokens.get(1).toString());
                channel.setSelectors(parsedTokens.get(1).toString());
            }
            if (parsedTokens.get(2) != null  && !parsedTokens.get(2).toString().isEmpty()){
                channel.setReadType(parsedTokens.get(2).toString());
            }
        }
    }

    @Override
    public void connectWrite(WriteRecipe writeRecipe) {
        super.connectWrite(writeRecipe);
        // Initialize all values
        for (ChannelWriteRecipe channelWriteRecipe : writeRecipe.getChannelWriteRecipes()) {
            initializeWrite(channelWriteRecipe.getChannelName());
        }
    }

    
    private void initializeWrite(String channelName) {
        log.info("Initalizing Write :" + channelName);
        List<Object> parsedTokens = parseName(channelName);

        BeastChannelHandler channel = (BeastChannelHandler) getChannels().get(channelHandlerLookupName(channelName));
        if (channel != null ) {
            if (parsedTokens.get(3) != null && !parsedTokens.get(3).toString().isEmpty()) {
                channel.setWriteType(parsedTokens.get(3).toString());
            }else{
                channel.setWriteType("VString");
            }
        }
    }

    private List<Object> parseName(String channelName) {
        List<Object> tokens = FunctionParser.parseFunctionAnyParameter(".+", channelName);
        String nameAndTypeAndFilter = tokens.get(0).toString();
        String name = nameAndTypeAndFilter;
        String filter = null;
        String type = "VString";
        int index = nameAndTypeAndFilter.lastIndexOf('{');
        if (nameAndTypeAndFilter.endsWith("}") && index != -1) {
            name = nameAndTypeAndFilter.substring(0, index);
            filter = nameAndTypeAndFilter.substring(index + 1,
                    nameAndTypeAndFilter.length() - 1);
        }
        index = nameAndTypeAndFilter.lastIndexOf('<');
        if (nameAndTypeAndFilter.endsWith(">") && index != -1) {
            name = nameAndTypeAndFilter.substring(0, index);
            type = nameAndTypeAndFilter.substring(index + 1,
                    nameAndTypeAndFilter.length() - 1);
        }
        List<Object> newTokens = new ArrayList<>();
        newTokens.add(name);
        newTokens.add(filter);
        String readType = type;
        String writeType = "VString";
        if (type != null && type.contains(",")) {
            String[] types = type.split(",");
            readType = types[0].trim();
            if (types[1] != null && !types[1].isEmpty())
                writeType = types[1].trim();
        }
        newTokens.add(readType);
        newTokens.add(writeType);
        return newTokens;
    }

    @Override
    public void close() {
        super.close();
        log.info("closing");
        try {
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public Session getSession() {
        return session;
    }

    public BeastTypeSupport getTypeSupport() {
        return typeSupport;
    }

}
