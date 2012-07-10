package org.csstudio.opibuilder.widgets.symbol.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;

public class ImagePermuter {

	private Map<String, ImageOperation[]> ops;
			
	public ImagePermuter() {
		ops = new HashMap<String, ImageOperation[]>();
		init();
	}

	private void init() {
		ops.put("1234", new ImageOperation[] { ImageOperation.DEFAULT });
		ops.put("4123", new ImageOperation[] { ImageOperation.RR90 });
		ops.put("3412", new ImageOperation[] { ImageOperation.R180 });
		ops.put("2341", new ImageOperation[] { ImageOperation.RL90 });
		ops.put("2143", new ImageOperation[] { ImageOperation.FH });
		ops.put("4321", new ImageOperation[] { ImageOperation.FV });
		ops.put("4312", new ImageOperation[] { ImageOperation.FH, ImageOperation.FV });
		ops.put("2413", new ImageOperation[] { ImageOperation.FV, ImageOperation.FH });
		ops.put("1432", new ImageOperation[] { ImageOperation.RR90, ImageOperation.FH });
		ops.put("3214", new ImageOperation[] { ImageOperation.RR90, ImageOperation.FV });
	}
	
	public ImageData applyPermutation(ImageData data, String result) {
		if (result == null || result.isEmpty())
			return data;
		ImageData newData = (ImageData) data.clone();
		ImageOperation[] opList = ops.get(result);
		for (ImageOperation op : opList) {
			switch (op) {
			case FV:
				newData = ImageUtils.flip(newData, true);
				break;
			case FH:
				newData = ImageUtils.flip(newData, false);
				break;
			case RR90:
				newData = ImageUtils.rotate(newData, SWT.RIGHT);
				break;
			case RL90:
				newData = ImageUtils.rotate(newData, SWT.LEFT);
				break;
			case R180:
				newData = ImageUtils.rotate(newData, SWT.DOWN);
				break;
			default:
				break;
			}
		}
		return newData;
	}
	
	public static char[] applyOperation(char[] m, ImageOperation op) {
		char[] tm = new char[4];
		switch (op) {
		case FH:
			tm[0] = m[1];
			tm[1] = m[0];
			tm[2] = m[3];
			tm[3] = m[2];
			break;
		case FV:
			tm[0] = m[3];
			tm[3] = m[0];
			tm[1] = m[2];
			tm[2] = m[1];
			break;
		case RR90:
			tm[3] = m[2];
			tm[2] = m[1];
			tm[1] = m[0];
			tm[0] = m[3];
			break;
		case RL90:
			tm[0] = m[1];
			tm[1] = m[2];
			tm[2] = m[3];
			tm[3] = m[0];
			break;
		case R180:
			tm[0] = m[2];
			tm[1] = m[3];
			tm[2] = m[0];
			tm[3] = m[1];
			break;
		}
		return tm;
	}
}
