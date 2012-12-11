package org.csstudio.trends.databrowser2.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;


/**The path hosting an URL String.
 * @author Xihui Chen
 *
 */
public class URLPath implements IPath {
	private static final String ELLIPSIS = ".."; //$NON-NLS-1$
	private String url = null;
	/** The path segments */
	private String[] segments;
	
	/** The device id string. May be null if there is no device. */
	private String device = null;
	
	/**
	 * Path separator character constant "/" used in paths.
	 */
	public static final String SEPARATOR = "/"; //$NON-NLS-1$
	
	/**
	 * @param url
	 */
	public URLPath(String url){
		this.url = url;	
		int i = url.indexOf(DEVICE_SEPARATOR);
		if(i != -1){
			device = url.substring(0, i+1);
			url = url.substring(i+1);
		}
		segments = url.split(SEPARATOR);	
		
	}

	public URLPath(String device, String[] segments, boolean hasTrailing){
		this.device = device;
		this.segments = segments;
		StringBuilder sb;
		if(device == null)
			sb = new StringBuilder();
		else 
			sb = new StringBuilder(device); //$NON-NLS-1$
		for (String s : segments) {
			sb.append(s);
			sb.append(SEPARATOR);
		}
		if(hasTrailing)
			this.url = sb.toString();
		else
			this.url = sb.deleteCharAt(sb.length()-1).toString();
	}
	
	public IPath addFileExtension(String extension) {
		return new URLPath(url.concat(extension));
	}

	public IPath addTrailingSeparator() {
		if (hasTrailingSeparator() || isRoot()) {
			return this;
		}
		return new URLPath(url.concat(SEPARATOR));
	}

	public IPath append(String path) {
		return append(new URLPath(path));
	}

	public IPath append(IPath path) {
		String[] inputSegs = path.segments();
		List<String>resultSegs = new ArrayList<String>(Arrays.asList(segments));
		for (String s : inputSegs) {
			if(s.equals(ELLIPSIS))
				resultSegs.remove(resultSegs.size()-1);
			else{
				resultSegs.add(s);
			}
		}		
		return new URLPath(device, resultSegs.toArray(new String[0]), path.hasTrailingSeparator());
	}
	

	/* (Intentionally not included in javadoc)
	 * Clones this object.
	 */
	@Override
    public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public String getDevice() {
		return device;
	}

	public String getFileExtension() {
		if (hasTrailingSeparator()) {
			return null;
		}
		String lastSegment = lastSegment();
		if (lastSegment == null) {
			return null;
		}
		int index = lastSegment.lastIndexOf('.');
		if (index == -1) {
			return null;
		}
		return lastSegment.substring(index + 1);
	}

	public boolean hasTrailingSeparator() {
		String lastSegment = lastSegment();
		if (lastSegment == null) {
			return false;
		}
		boolean hasTrailing = 
			Character.valueOf(url.charAt(url.length()-1)).equals(IPath.SEPARATOR);
		return hasTrailing;
	}

	public boolean isAbsolute() {
		if(device != null)
			return true;
		if(!isEmpty())
			return Character.valueOf(url.charAt(0)).equals(IPath.SEPARATOR);
		return false;
	}

	public boolean isEmpty() {		
		return url.length() <= 0;
	}

	public boolean isPrefixOf(IPath anotherPath) {
		if(anotherPath.getDevice().equals(device)){
			if(segmentCount() <= anotherPath.segmentCount()){
				int i=0;
				for (String s : segments()) {
					if(!s.equals(anotherPath.segment(i++)))
						return false;
				}
				return true;
			}				
		}
			
		return false;
	}

	public boolean isRoot() {
		if(device != null && segmentCount()==0)
			return true;
		return false;
	}

	public boolean isUNC() {
		return false;
	}

	public boolean isValidPath(String path) {
		URLPath test = new URLPath(path);
		for (int i = 0, max = test.segmentCount(); i < max; i++)
			if (!isValidSegment(test.segment(i)))
				return false;
		return true;
	}

	public boolean isValidSegment(String segment) {
		int size = segment.length();
		if (size == 0)
			return false;
		for (int i = 0; i < size; i++) {
			char c = segment.charAt(i);
			if (c == '/')
				return false;
			if (c == '\\' || c == ':')
				return false;
		}
		return true;
	}

	public String lastSegment() {
		if(segmentCount() == 0)
			return null;
		return segment(segmentCount()-1);
	}

	public IPath makeAbsolute() {
		if (isAbsolute()) {
			return this;
		}
		List<String> segList = Arrays.asList(segments());
		for (String s : segList) {
			if(s.equals(ELLIPSIS) || s.equals(".")) //$NON-NLS-1$
				segList.remove(s);
		}	
		URLPath result = new URLPath(device, segList.toArray(new String[0]),
				hasTrailingSeparator());
		return result;
	}

	public IPath makeRelative() {
		return this;
	}

	public IPath makeRelativeTo(IPath base) {
		int i=0;
		String[] absolutePathSegs = segments();
		for(String seg : base.segments()){
			if(!seg.equals(segment(i)))
				break;			
			i++;			
		}
		int ellipsisCount = base.segmentCount() - i;
		int remainedSegsCount = segmentCount() -i;
		StringBuilder sb = new StringBuilder();
		for(int j = 0; j<ellipsisCount + remainedSegsCount - 1; j++){
			if(j < ellipsisCount)
				sb.append(ELLIPSIS + IPath.SEPARATOR);
			else
				sb.append(absolutePathSegs[i++] + IPath.SEPARATOR);
		}
		sb.append(absolutePathSegs[i]);
		return new URLPath(sb.toString());
	}

	public IPath makeUNC(boolean toUNC) {
		return this;
	}

	public int matchingFirstSegments(IPath anotherPath) {
		Assert.isNotNull(anotherPath);
		int anotherPathLen = anotherPath.segmentCount();
		int max = Math.min(segments.length, anotherPathLen);
		int count = 0;
		for (int i = 0; i < max; i++) {
			if (!segments[i].equals(anotherPath.segment(i))) {
				return count;
			}
			count++;
		}
		return count;
	}

	public IPath removeFileExtension() {
		String extension = getFileExtension();
		if (extension == null || extension.equals("")) { //$NON-NLS-1$
			return this;
		}
		String lastSegment = lastSegment();
		int index = lastSegment.lastIndexOf(extension) - 1;
		return removeLastSegments(1).append(lastSegment.substring(0, index));
	}

	public IPath removeFirstSegments(int count) {
		if (count == 0)
			return this;
		if (count >= segments.length) {
			return new URLPath(device, new String[0], false);
		}
		Assert.isLegal(count > 0);
		int newSize = segments.length - count;
		String[] newSegments = new String[newSize];
		System.arraycopy(this.segments, count, newSegments, 0, newSize);

		//result is always a relative path
		return new URLPath(device, newSegments, hasTrailingSeparator());
	}

	public IPath removeLastSegments(int count) {
		if (count == 0)
			return this;
		if (count >= segments.length) {
			//result will have no trailing separator
			return new URLPath(device, new String[0], false);
		}
		Assert.isLegal(count > 0);
		int newSize = segments.length - count;
		String[] newSegments = new String[newSize];
		System.arraycopy(this.segments, 0, newSegments, 0, newSize);
		return new URLPath(device, newSegments, hasTrailingSeparator());
	}

	public IPath removeTrailingSeparator() {
		if(hasTrailingSeparator())
			return new URLPath(device, segments, false);
		return null;
	}

	public String segment(int index) {
		if (index >= segments.length)
			return null;
		return segments[index];
	}

	public int segmentCount() {
		return segments.length;
	}

	public String[] segments() {
		return segments;
	}

	public IPath setDevice(String device) {
		return new URLPath(device, segments, hasTrailingSeparator());
	}

	public File toFile() {
		return new File(toOSString());
	}

	public String toOSString() {
		return url;
	}

	public String toPortableString() {
		return url;
	}

	public IPath uptoSegment(int count) {
		if (count == 0)
			return new URLPath(device, new String[0], false);
		if (count >= segments.length)
			return this;
		Assert.isTrue(count > 0, "Invalid parameter to Path.uptoSegment"); //$NON-NLS-1$
		String[] newSegments = new String[count];
		System.arraycopy(segments, 0, newSegments, 0, count);
		return new URLPath(device, newSegments, hasTrailingSeparator());
	}

	@Override
	public String toString() {
		return url;
	}
}
