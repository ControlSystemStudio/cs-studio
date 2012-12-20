package gov.bnl.channelfinder.api;

import gov.bnl.channelfinder.api.Channel.Builder;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;

public class ChannelFinderClientComp implements ChannelFinderClient {

	private volatile ChannelFinderClient reader;
	private volatile ChannelFinderClient writer;

	private static ChannelFinderClientComp channelFinderClientComp;

	private static final Logger log = Logger.getLogger(ChannelFinderClientComp.class
			.getName());
	
	private ChannelFinderClientComp() {
	}
	
	public static ChannelFinderClientComp getInstance(){
		if(channelFinderClientComp == null){
			channelFinderClientComp = new ChannelFinderClientComp();
		}
		return channelFinderClientComp;
		
	}
	
	public void setReader(ChannelFinderClient reader){
		this.reader = reader;
	}
	
	public void setWriter(ChannelFinderClient writer){
		this.writer = writer;
	}

	@Override
	public Collection<String> getAllProperties() {
		return this.reader.getAllProperties();
	}

	@Override
	public Collection<String> getAllTags() {
		return this.reader.getAllTags();
	}

	@Override
	public Channel getChannel(String channelName) throws ChannelFinderException {
		return this.reader.getChannel(channelName);
	}

	@Override
	public Collection<Channel> findByName(String pattern)
			throws ChannelFinderException {
		return this.reader.findByName(pattern);
	}

	@Override
	public Collection<Channel> findByTag(String pattern)
			throws ChannelFinderException {
		return this.reader.findByTag(pattern);
	}

	@Override
	public Collection<Channel> findByProperty(String property,
			String... pattern) throws ChannelFinderException {
		return this.reader.findByProperty(property, pattern);
	}

	@Override
	public Collection<Channel> find(String query) throws ChannelFinderException{
		return this.reader.find(query);
	}
	
	@Override
	public Collection<Channel> find(Map<String, String> map)
			throws ChannelFinderException {
		return this.reader.find(map);
	}

	@Override
	public Collection<Channel> find(MultivaluedMap<String, String> map)
			throws ChannelFinderException {
		return this.reader.find(map);
	}

	@Override
	public void set(Builder channel) throws ChannelFinderException {
		this.writer.set(channel);
	}

	@Override
	public void set(Tag.Builder tag) {
		this.writer.set(tag);
	}

	@Override
	public void set(Property.Builder property) throws ChannelFinderException {
		this.writer.set(property);
	}

	@Override
	public void set(Collection<Builder> channels) throws ChannelFinderException {
		this.writer.set(channels);
	}

	@Override
	public void set(Tag.Builder tag, String channelName)
			throws ChannelFinderException {
		this.writer.set(tag, channelName);
	}

	@Override
	public void set(Property.Builder property, String channelName) {
		this.writer.set(property, channelName);
	}

	@Override
	public void set(Tag.Builder tag, Collection<String> channelNames)
			throws ChannelFinderException {
		this.writer.set(tag, channelNames);
	}

	@Override
	public void set(Property.Builder property, Collection<String> channelNames) {
		this.writer.set(property, channelNames);
	}

	@Override
	public void set(Property.Builder prop,
			Map<String, String> channelPropertyMap) {
		this.writer.set(prop, channelPropertyMap);
	}

	@Override
	public void update(Builder channel) throws ChannelFinderException {
		this.writer.update(channel);
	}

	@Override
	public void update(Tag.Builder tag, String channelName)
			throws ChannelFinderException {
		this.writer.update(tag, channelName);
	}

	@Override
	public void update(Property.Builder property, String channelName)
			throws ChannelFinderException {
		this.writer.update(property, channelName);
	}

	@Override
	public void update(Tag.Builder tag, Collection<String> channelNames)
			throws ChannelFinderException {
		this.writer.update(tag, channelNames);
	}

	@Override
	public void update(Property.Builder property,
			Collection<String> channelNames) throws ChannelFinderException {
		this.writer.update(property, channelNames);
	}

	@Override
	public void update(Property.Builder property,
			Map<String, String> channelPropValueMap)
			throws ChannelFinderException {
		this.writer.update(property, channelPropValueMap);
	}

	@Override
	public void deleteTag(String tagName) throws ChannelFinderException {
		this.writer.deleteTag(tagName);
	}

	@Override
	public void deleteProperty(String propertyName)
			throws ChannelFinderException {
		this.writer.deleteProperty(propertyName);
	}

	@Override
	public void deleteChannel(String channelName) throws ChannelFinderException {
		this.writer.deleteChannel(channelName);
	}

	@Override
	public void delete(Collection<Builder> channels)
			throws ChannelFinderException {
		this.writer.delete(channels);
	}

	@Override
	public void delete(Tag.Builder tag, String channelName)
			throws ChannelFinderException {
		this.writer.delete(tag, channelName);
	}

	@Override
	public void delete(Property.Builder property, String channelName)
			throws ChannelFinderException {
		this.writer.delete(property, channelName);
	}

	@Override
	public void delete(Tag.Builder tag, Collection<String> channelNames)
			throws ChannelFinderException {
		this.writer.delete(tag, channelNames);
	}

	@Override
	public void delete(Property.Builder property,
			Collection<String> channelNames) {
		this.writer.delete(property, channelNames);
	}

	@Override
	public void close() {
		this.reader.close();
		this.writer.close();
	}


}
