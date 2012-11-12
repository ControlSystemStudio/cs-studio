package org.csstudio.sds.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.internal.persistence.PersistenceUtil;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.ui.thumbnail.DisplayCacheValue;
import org.csstudio.sds.ui.thumbnail.SdsDisplayValueCache;
import org.csstudio.sds.ui.thumbnail.SdsThumbnailCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class DisplayInfoService {
	
	private SdsThumbnailCreator thumbnailCreator;
	private SdsDisplayValueCache cache;
	

	public DisplayInfoService() {
		thumbnailCreator = new SdsThumbnailCreator();
		cache = new SdsDisplayValueCache();
		
		final Display display = Display.getCurrent();
		display.addListener(SWT.Dispose, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				display.removeListener(SWT.Dispose, this);
				cache.shutdown();
			}
		});

	}
	
	public ImageData getImage(File file) {
		return getCachedValue(file).getImage();
	}

	public Set<IProcessVariableAddress> getProcessVariableAddresses(File file) {
		return getCachedValue(file).getProcessVariableAddresses();
	}
	
	private DisplayCacheValue getCachedValue(File file) {
		DisplayCacheValue cacheValue = cache.getCacheValue(file);
		
		if(cacheValue == null) {
			cacheValue = createDisplayCacheValue(file);
			cache.cacheValueForFile(file, cacheValue);
		}
		return cacheValue;
	}
	
	private DisplayCacheValue createDisplayCacheValue(File file) {
		// Get DisplayModel for file 
		DisplayModel model = new DisplayModel();
		FileInputStream fip = null;
		try {
			fip = new FileInputStream(file);
			PersistenceUtil.syncFillModel(model, fip);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fip.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ImageData imageData = thumbnailCreator.createImage(model, 100, Display.getDefault());
		
		HashSet<IProcessVariableAddress> allProcessVariableAddresses = new HashSet<IProcessVariableAddress>();
		findAllPVsInModel(model, allProcessVariableAddresses);
		
		return new DisplayCacheValue(file, imageData, allProcessVariableAddresses);
	}
	
	private void findAllPVsInModel(AbstractWidgetModel model,
			Set<IProcessVariableAddress> processVariables) {
		List<IProcessVariableAddress> allPvAdresses = model
				.getAllPvAdresses();
		for (IProcessVariableAddress iProcessVariableAddress : allPvAdresses) {
			processVariables.add(iProcessVariableAddress);
		}
		if (model instanceof ContainerModel) {
			for (AbstractWidgetModel childModel : ((ContainerModel) model)
					.getWidgets()) {
				findAllPVsInModel(childModel, processVariables);
			}
		}
	}
}
