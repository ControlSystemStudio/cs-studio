package org.csstudio.sds.ui.thumbnail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;

import org.csstudio.sds.ui.SdsUiPlugin;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

public class SdsThumbnailImageCache {

	private static final String CACHE_FOLDER_NAME = "libraryCache";

	private Cache cache;
	private CacheManager cacheManager;

	public SdsThumbnailImageCache() {

		File cacheDirectory = getImageCacheDirectory();

		Configuration configuration = new Configuration();
		configuration.addDiskStore(new DiskStoreConfiguration().path(cacheDirectory.getAbsolutePath()));
		cacheManager = CacheManager.create(configuration);
		
		CacheConfiguration config = new CacheConfiguration("DisplayThumbnailCache", 1000);
		config.overflowToOffHeap(false);
		config.overflowToDisk(true);
		config.setDiskPersistent(true);
		config.setTimeToIdleSeconds(60 * 60 * 24 * 7);
		config.setTimeToLiveSeconds(60 * 60 * 24 * 30);

		config.setEternal(true); // Overrides time to live
		
		config.setMaxEntriesLocalHeap(100);
		config.setMaxBytesLocalDisk("100M");
		config.setMaxEntriesLocalDisk(50000);
		cache = new Cache(config);

		cacheManager.addCache(cache);
	}

	public ImageData getCachedImage(File file) {
		assert file != null : "Precondition failed: file != null";

		Element element = cache.get(file);

		if (element != null) {
			byte[][] value =  (byte[][]) element.getValue();
			if (Arrays.equals(value[0], hashFile(file))) {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(value[1]);
				try {
					ImageLoader imageLoader = new ImageLoader();
					return imageLoader.load(inputStream)[0];
				} finally {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return null;
	}

	public void cacheImage(File file, ImageData imageData) {
		assert imageData != null;

		byte[] hash = hashFile(file);

		if (hash != null) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] { imageData };
			imageLoader.save(byteArrayOutputStream, SWT.IMAGE_PNG);
			byte[] thumbnailData = byteArrayOutputStream.toByteArray();

			cache.put(new Element(file, new byte[][] { hash, thumbnailData }));
			cache.flush();
		}
	}

	private File getImageCacheDirectory() {
		File workspaceFile = ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toFile();
		
		File result = new File(workspaceFile, ".metadata/.plugins/" + SdsUiPlugin.PLUGIN_ID + "/" + CACHE_FOLDER_NAME);
		if (!result.exists()) {
			result.mkdirs();
		}
		return result;
	}

	private byte[] hashFile(File file) {
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

	public void shutdown() {
		cacheManager.shutdown();
	}
}
