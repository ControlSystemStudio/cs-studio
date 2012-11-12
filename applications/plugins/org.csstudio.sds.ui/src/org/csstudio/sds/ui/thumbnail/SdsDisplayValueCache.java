package org.csstudio.sds.ui.thumbnail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;

import org.csstudio.sds.ui.SdsUiPlugin;
import org.eclipse.core.resources.ResourcesPlugin;

public class SdsDisplayValueCache {

	private static final String CACHE_FOLDER_NAME = "libraryCache";

	private final static Cache cache;
	private final static CacheManager cacheManager;

	static {

		File cacheDirectory = getImageCacheDirectory();

		Configuration configuration = new Configuration();
		configuration.addDiskStore(new DiskStoreConfiguration()
				.path(cacheDirectory.getAbsolutePath()));
		cacheManager = CacheManager.create(configuration);

		CacheConfiguration config = new CacheConfiguration(
				"DisplayThumbnailCache", 1000);
		config.overflowToOffHeap(false);
		config.overflowToDisk(true);
		config.setDiskPersistent(true);
		config.setTimeToIdleSeconds(60 * 60 * 24 * 7);
		config.setTimeToLiveSeconds(60 * 60 * 24 * 30);
		config.setMaxEntriesLocalHeap(100);
		config.setMaxBytesLocalDisk("25M");
		config.setMaxEntriesLocalDisk(10000);
		cache = new Cache(config);

		cacheManager.addCache(cache);
	}
	
	public DisplayCacheValue getCacheValue(File file) {
		DisplayCacheValue result = null;
		
		ClassLoader prevContextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			Element element = cache.get(file);
			if(element != null) {
				DisplayCacheValue value = (DisplayCacheValue) element.getValue();
				// Check if cached values are valid
				if(Arrays.equals(value.getHash(),hashFile(file))) {
					result = value;
				}
			}
		} finally {
			Thread.currentThread().setContextClassLoader(prevContextClassLoader);
		}
		return result;
	}
	
	public void cacheValueForFile(File file, DisplayCacheValue value) {
		ClassLoader prevContextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			cache.put(new Element(file, value));
		} finally {
			Thread.currentThread().setContextClassLoader(prevContextClassLoader);
		}
	}
	
	public void flush() {
		cache.flush();
	}
	
	public void shutdown() {
		if(cache.getStatus() == Status.STATUS_ALIVE) {
			cache.flush();
		}
		cacheManager.shutdown();
	}

	private static File getImageCacheDirectory() {
		File workspaceFile = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toFile();
	
		File result = new File(workspaceFile, ".metadata/.plugins/"
				+ SdsUiPlugin.PLUGIN_ID + "/" + CACHE_FOLDER_NAME);
		if (!result.exists()) {
			result.mkdirs();
		}
		return result;
	}

	private static byte[] hashFile(File file) {
		assert file != null : "Precondition failed: file != null";
		assert file.exists() : "Precondition failed: file.exists()";
	
		FileInputStream fis = null;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
			fis = new FileInputStream(file);
			byte[] dataBytes = new byte[1024];
	
			int nread = 0;
	
			while ((nread = fis.read(dataBytes)) != -1) {
				messageDigest.update(dataBytes, 0, nread);
			}
			return messageDigest.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
		return null;
	}
}
