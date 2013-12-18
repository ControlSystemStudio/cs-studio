/*******************************************************************************
* Copyright (c) 2010-2013 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.util;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgets.symbol.Activator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

/**
 * Utility class to change image behavior like color, shape, rotation
 * management, ...
 * @author Fred Arnaud (Sopra Group)
 */
public final class ImageUtils {

	/**
	 * Regular expression to find 'on.' characters ignoring case.
	 */
	private static final String REGEX_ON_IMG = "^(.*\\b)(?i)(on)(\\.\\w+)$";
	/**
	 * Regular expression to find 'off.' characters ignoring case.
	 */
	private static final String REGEX_OFF_IMG = "^(.*\\b)(?i)(off)(\\.\\w+)$";
	/**
	 * Regular expression to find base state position in image name
	 */
	private static final String STATE_MARKER = "@@state@@";
	/**
	 * Allowed images extensions
	 */
	private static final String[] IMAGE_EXTENSIONS = new String[] { "gif",
		"png", "svg", "GIF", "PNG", "SVG"};

	
	/**
	 * Constructor cannot be call because of static invocation.
	 */
	private ImageUtils() {}
	
	
	public static ImageData applyMatrix(ImageData srcData, PermutationMatrix pm) {
		double[][] matrix = pm.getMatrix();

		// point to rotate about => center of image
		double x0 = 0.5 * (srcData.width - 1);
		double y0 = 0.5 * (srcData.height - 1);

		// apply permutation to 4 corners
		int[] a = translate(0, 0, x0, y0, matrix);
		int[] b = translate(srcData.width - 1, 0, x0, y0, matrix);
		int[] c = translate(srcData.width - 1, srcData.height - 1, x0, y0, matrix);
		int[] d = translate(0, srcData.height - 1, x0, y0, matrix);

		// find new point
		int minX = findMin(a[0], b[0], c[0], d[0]);
		int minY = findMin(a[1], b[1], c[1], d[1]);
		int maxX = findMax(a[0], b[0], c[0], d[0]);
		int maxY = findMax(a[1], b[1], c[1], d[1]);
		int newWidth = maxX - minX;
		int newHeight = maxY - minY;

		ImageData newImageData = new ImageData(newWidth, newHeight,
				srcData.depth, srcData.palette);

		for (int srcX = 0; srcX < srcData.width; srcX++) {
			for (int srcY = 0; srcY < srcData.height; srcY++) {
				int destX = 0, destY = 0;
				int[] destP = translate(srcX, srcY, x0, y0, matrix);
				destX = (int) (destP[0] - minX);
				destY = (int) (destP[1] - minY);

				if (destX >= 0 && destX < newWidth && destY >= 0
						&& destY < newHeight) {
					newImageData.setPixel(destX, destY, srcData.getPixel(srcX, srcY));
					newImageData.setAlpha(destX, destY, srcData.getAlpha(srcX, srcY));
				}
			}
		}
		// Re-set the lost transparency
		newImageData.transparentPixel = srcData.transparentPixel;
		return newImageData;
	}
	
	// multiply matrices
	private static double[][] multiply(double[][] m1, double[][] m2) {
		int p1 = m1.length, p2 = m2.length, q2 = m2[0].length;
		double[][] result = new double[p1][q2];
		for (int i = 0; i < p1; i++)
			for (int j = 0; j < q2; j++)
				for (int k = 0; k < p2; k++)
					result[i][j] += m1[i][k] * m2[k][j];
		return result;
	}
	
	// calculate new coordinates
	private static int[] translate(int x, int y, double x0, double y0, double[][] matrix) {
		// translate coordinates
		double[][] p = new double[2][1];
		p[0][0] = x - x0;
		p[1][0] = y - y0;
		// apply permutation
		double[][] pp = multiply(matrix, p);
		// translate back
		int[] result = new int[2];
		result[0] = (int) (pp[0][0] + x0);
		result[1] = (int) (pp[1][0] + y0);
		return result;
	}
	
	private static int findMax(int a, int b, int c, int d) {
		int result = Math.max(a, b);
		result = Math.max(result, c);
		result = Math.max(result, d);
		return result;
	}
	
	private static int findMin(int a, int b, int c, int d) {
		int result = Math.min(a, b);
		result = Math.min(result, c);
		result = Math.min(result, d);
		return result;
	}
	
	/**
	 * Apply color change on an image.
	 * 
	 * @param color
	 * @param imageData
	 */
	public static void oldChangeImageColor(Color color, ImageData imageData) {
		if (color == null || imageData == null)
			return;

		int newColor = 0;
		int[] lineData = new int[imageData.width];

		// Calculate pixel value (integer)
		if (imageData.palette.isDirect) {
			RGB rgb = color.getRGB();

			int redMask = imageData.palette.redMask;
			int blueMask = imageData.palette.blueMask;
			int greenMask = imageData.palette.greenMask;

			int redShift = imageData.palette.redShift;
			int greenShift = imageData.palette.greenShift;
			int blueShift = imageData.palette.blueShift;

			newColor |= (redShift < 0 ? rgb.red << -redShift
					: rgb.red >>> redShift) & redMask;
			newColor |= (greenShift < 0 ? rgb.green << -greenShift
					: rgb.green >>> greenShift) & greenMask;
			newColor |= (blueShift < 0 ? rgb.blue << -blueShift
					: rgb.blue >>> blueShift) & blueMask;
		} else {
			// Add new color in PaletteData colors
			int paletteLength = imageData.palette.colors.length;
			newColor = (imageData.transparentPixel + 1) % paletteLength;
			imageData.palette.colors[newColor] = color.getRGB();
		}

		for (int y = 0; y < imageData.height; y++) {
			imageData.getPixels(0, y, imageData.width, lineData, 0);

			// Analyze each pixel value in the line
			for (int x = 0; x < lineData.length; x++) {
				// Do not set transparent pixel && change only black pixel
				int pixelValue = lineData[x];
				if (!imageData.palette.isDirect) {
					pixelValue = imageData.palette.getPixel(imageData.palette.colors[lineData[x]]);
				}
				if (lineData[x] != imageData.transparentPixel
						&& isShadeOfGray(pixelValue, imageData.palette)) {
					int appliedColor = applyShade(pixelValue, newColor, imageData.palette);
					if (imageData.alphaData == null) {
						// appliedColor = applyShade(pixelValue, newColor, imageData.palette);
					}
					imageData.setPixel(x, y, appliedColor);
				}
			}
		}
	}
	
	public static void oldChangeImageColor2(Color color, ImageData imageData) {
		if (color == null || imageData == null)
			return;
		if (color.getRGB().equals(new RGB(0, 0, 0))) // Avoid black
			return;

		float[] hsb = new float[3];
		java.awt.Color.RGBtoHSB(color.getRGB().red, color.getRGB().green, color.getRGB().blue, hsb);
		int[] lineData = new int[imageData.width];
		PaletteData palette = imageData.palette;

		for (int y = 0; y < imageData.height; y++) {
			imageData.getPixels(0, y, imageData.width, lineData, 0);

			// Analyze each pixel value in the line
			for (int x = 0; x < lineData.length; x++) {
				int pixelValue = lineData[x];

				// Do not set transparent pixel
				if (lineData[x] != imageData.transparentPixel) {
					// Get pixel color value if not using direct palette
					if (!palette.isDirect) {
						pixelValue = palette.getPixel(palette.colors[lineData[x]]);
					}
					if(!skipPixel(pixelValue, palette))
					{
						float[] pixelHSB = Arrays.copyOf(hsb, hsb.length);
						// In gray scale r == g == b, so we can take anyone
						int lvl = (pixelValue & palette.blueMask) >> palette.blueShift;
						// Gray scale pixel value = brightness
						float saturation = (float) (1 - (lvl / 255f)) < 0 ? 0 : (float) (1 - (lvl / 255f));
						
						pixelHSB[1] = saturation;
						int appliedColor = java.awt.Color.HSBtoRGB(pixelHSB[0], pixelHSB[1], pixelHSB[2]);
						if (palette.isDirect) {
							imageData.setPixel(x, y, appliedColor);
						} else {
							palette.colors[lineData[x]] = palette.getRGB(appliedColor);
						}
					}
				}
			}
		}
	}
	
	public static void changeImageColor(Color color, ImageData imageData) {

		if (color == null || imageData == null)
			return;
		if (color.getRGB().equals(new RGB(0, 0, 0))) // Avoid black
			return;

		float[] hsb = new float[3];
		java.awt.Color.RGBtoHSB(
				color.getRGB().red, 
				color.getRGB().green,
				color.getRGB().blue, hsb);
		int[] lineData = new int[imageData.width];
		PaletteData palette = imageData.palette;

		for (int y = 0; y < imageData.height; y++) {
			imageData.getPixels(0, y, imageData.width, lineData, 0);

			// Analyze each pixel value in the line
			for (int x = 0; x < lineData.length; x++) {
				int pixelValue = lineData[x];

				// Do not set transparent pixel
				if (lineData[x] != imageData.transparentPixel) {
					// Get pixel color value if not using direct palette
					if (!palette.isDirect) {
						pixelValue = palette.getPixel(palette.colors[lineData[x]]);
					}
					if (!skipPixel(pixelValue, palette)) {
						RGB current = palette.getRGB(pixelValue);
						RGB degraded = degradeColor(color.getRGB(), 
								new RGB(255, 255, 255), current.red + 1);
						int appliedColor = palette.getPixel(degraded);

						if (palette.isDirect) {
							imageData.setPixel(x, y, appliedColor);
						} else {
							palette.colors[lineData[x]] = palette
									.getRGB(appliedColor);
						}
					}
				}
			}
		}
	}

	/**
	 * Compute the degraded color corresponding to a given color.
	 * 
	 * @param from: the color to degrade.
	 * @param to: the target color.
	 * @param nbOfShade: number of shade to degrade to.
	 * @return the degraded color.
	 */
	private static RGB degradeColor(RGB from, RGB to, int nbOfShade) {
		// from color data
		int r1 = from.red;
		int g1 = from.green;
		int b1 = from.blue;
		// to color data
		int r2 = to.red;
		int g2 = to.green;
		int b2 = to.blue;

		// for every channel, we compute the differential between every shade
		// (nbVal is the number of shade of the gradation)
		int dr = ((r2 - r1) / nbOfShade);
		int dg = ((g2 - g1) / nbOfShade);
		int db = ((b2 - b1) / nbOfShade);

		return new RGB(r2 - (dr * nbOfShade), g2 - (dg * nbOfShade), b2
				- (db * nbOfShade));
	}

	/**
	 * Skip White and non grey pixels
	 * 
	 * @param pixelColor
	 * @param palette
	 * @return
	 */
	private static boolean skipPixel(int pixelColor, PaletteData palette) {
		if (pixelColor == 16777215) { // white color: 16777215
			return true;
		} else {
			RGB rgb = palette.getRGB(pixelColor);
			if (rgb.blue == rgb.green && rgb.blue == rgb.red) // gray scale
				return false;
			else
				return true;
		}
	}

	/**
	 * Get rotation direction from origin degree to target degree.
	 * 
	 * @param oldDegree
	 * @param newDegree
	 * @return the direction
	 */
	public static int getRotationDirection(int oldDegree, int newDegree) {
		int clockwise = -1;
		switch (oldDegree) {
		case 0:
			if (newDegree == 90) {
				clockwise = SWT.RIGHT;
			} else if (newDegree == 180) {
				clockwise = SWT.DOWN;
			} else if (newDegree == 270) {
				clockwise = SWT.LEFT;
			} else {
				Activator.getLogger().log(Level.WARNING,
						"ERROR in value of new degree " + newDegree);
			}
			break;
		case 90:
			if (newDegree == 180) {
				clockwise = SWT.RIGHT;
			} else if (newDegree == 270) {
				clockwise = SWT.DOWN;
			} else if (newDegree == 0) {
				clockwise = SWT.LEFT;
			} else {
				Activator.getLogger().log(Level.WARNING,
						"ERROR in value of new degree " + newDegree);
			}
			break;
		case 180:
			if (newDegree == 270) {
				clockwise = SWT.RIGHT;
			} else if (newDegree == 0) {
				clockwise = SWT.DOWN;
			} else if (newDegree == 90) {
				clockwise = SWT.LEFT;
			} else {
				Activator.getLogger().log(Level.WARNING,
						"ERROR in value of new degree " + newDegree);
			}
			break;
		case 270:
			if (newDegree == 0) {
				clockwise = SWT.RIGHT;
			} else if (newDegree == 90) {
				clockwise = SWT.DOWN;
			} else if (newDegree == 180) {
				clockwise = SWT.LEFT;
			} else {
				Activator.getLogger().log(Level.WARNING,
						"ERROR in value of new degree " + newDegree);
			}
			break;
		default:
			Activator.getLogger().log(Level.WARNING,
					"ERROR in value of old degree " + oldDegree + ". The degree can only be " +
							"0, 90, 180 or 270");
		}
		return clockwise;
	}

	/**
	 * Rotate image to a direction.
	 * 
	 * @param srcData
	 * @param direction
	 * @return the image after rotation
	 */
	public static ImageData rotate(ImageData srcData, int direction) {
		int bytesPerPixel = srcData.bytesPerLine / srcData.width;
		int destBytesPerLine = (direction == SWT.DOWN) ? srcData.width
				* bytesPerPixel : srcData.height * bytesPerPixel;
		int alphaBytesPerLine = (direction == SWT.DOWN) ? srcData.width : srcData.height;
		byte[] newData = new byte[srcData.data.length];
		byte[] newAlphaData = null;
		if (srcData.alphaData != null) {
			newAlphaData = new byte[srcData.alphaData.length];
		}
		int width = 0, height = 0;
		for (int srcY = 0; srcY < srcData.height; srcY++) {
			for (int srcX = 0; srcX < srcData.width; srcX++) {
				int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
				int destAlphaIndex = 0, srcAlphaIndex = 0;
				switch (direction) {
				case SWT.LEFT: // left 90 degrees
					destX = srcY;
					destY = srcData.width - srcX - 1;
					width = srcData.height;
					height = srcData.width;
					break;
				case SWT.RIGHT: // right 90 degrees
					destX = srcData.height - srcY - 1;
					destY = srcX;
					width = srcData.height;
					height = srcData.width;
					break;
				case SWT.DOWN: // 180 degrees
					destX = srcData.width - srcX - 1;
					destY = srcData.height - srcY - 1;
					width = srcData.width;
					height = srcData.height;
					break;
				default:
					Activator.getLogger().log(
							Level.WARNING,
							"ERROR in rotation due to wrong direction value: "
									+ direction);
					return srcData;
				}
				destIndex = (destY * destBytesPerLine)
						+ (destX * bytesPerPixel);
				srcIndex = (srcY * srcData.bytesPerLine)
						+ (srcX * bytesPerPixel);
				System.arraycopy(srcData.data, srcIndex, newData, destIndex,
						bytesPerPixel);
				if (newAlphaData != null) {
					destAlphaIndex = (destY * alphaBytesPerLine) + destX;
					srcAlphaIndex = (srcY * srcData.width) + srcX;
					System.arraycopy(srcData.alphaData, srcAlphaIndex,
							newAlphaData, destAlphaIndex, 1);
				}
			}
		}
		// destBytesPerLine is used as scanlinePad
		// to ensure that no padding is required
		ImageData newImageData = new ImageData(width, height, srcData.depth,
				srcData.palette, destBytesPerLine, newData);

		// Re-set the lost transparency
		newImageData.transparentPixel = srcData.transparentPixel;
		if (newAlphaData != null) {
			newImageData.alphaData = newAlphaData;
		}
		return newImageData;
	}

	/**
	 * Flip image horizontally or vertically.
	 * 
	 * @param srcData
	 * @param vertical
	 * @return the image after flip
	 */
	public static ImageData flip(ImageData srcData, boolean vertical) {
		int bytesPerPixel = srcData.bytesPerLine / srcData.width;
		int destBytesPerLine = srcData.width * bytesPerPixel;
		byte[] newData = new byte[srcData.data.length];
		byte[] newAlphaData = null;
		if (srcData.alphaData != null) {
			newAlphaData = new byte[srcData.alphaData.length];
		}
		for (int srcY = 0; srcY < srcData.height; srcY++) {
			for (int srcX = 0; srcX < srcData.width; srcX++) {
				int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
				int destAlphaIndex = 0, srcAlphaIndex = 0;
				if (vertical) {
					destX = srcX;
					destY = srcData.height - srcY - 1;
				} else {
					destX = srcData.width - srcX - 1;
					destY = srcY;
				}
				destIndex = (destY * destBytesPerLine)
						+ (destX * bytesPerPixel);
				srcIndex = (srcY * srcData.bytesPerLine)
						+ (srcX * bytesPerPixel);
				System.arraycopy(srcData.data, srcIndex, newData, destIndex,
						bytesPerPixel);
				if (newAlphaData != null) {
					destAlphaIndex = (destY * srcData.width) + destX;
					srcAlphaIndex = (srcY * srcData.width) + srcX;
					System.arraycopy(srcData.alphaData, srcAlphaIndex,
							newAlphaData, destAlphaIndex, 1);
				}
			}
		}
		// destBytesPerLine is used as scanlinePad
		// to ensure that no padding is required
		ImageData newImageData = new ImageData(srcData.width, srcData.height,
				srcData.depth, srcData.palette, destBytesPerLine, newData);

		// Re-set the lost transparency
		newImageData.transparentPixel = srcData.transparentPixel;
		if (newAlphaData != null) {
			newImageData.alphaData = newAlphaData;
		}
		return newImageData;
	}

	/**
	 * Check that image is ON for the following image path.
	 * 
	 * @param imagePath
	 * @return the check result
	 */
	public static boolean isOnImage(IPath imagePath) {
		return checkPatternOnFilePath(REGEX_ON_IMG, imagePath);
	}

	/**
	 * Check that image is OFF for the given image path.
	 * 
	 * @param imagePath
	 * @return the check result
	 */
	public static boolean isOffImage(IPath imagePath) {
		return checkPatternOnFilePath(REGEX_OFF_IMG, imagePath);
	}

	/**
	 * Search ON image path from OFF image path.
	 * 
	 * @param offImagePath
	 * @return The image path found, otherwise null
	 */
	public static IPath searchOnImage(IPath offImagePath) {
		String[] suffixes = { "On", "ON", "on" };

		Pattern pattern = Pattern.compile(REGEX_OFF_IMG);
		Matcher matcher = pattern.matcher(offImagePath.toString());
		if (matcher.matches()) {
			StringBuffer sb = new StringBuffer();
			sb.append(matcher.group(1));
			sb.append("***");
			sb.append(matcher.group(3));
			String tmpPath = sb.toString();

			IPath onImagePath = null;
			for (String suffix : suffixes) {
				onImagePath = new Path(tmpPath.replace("***", suffix));
				if (isFileExists(onImagePath)) {
					return onImagePath;
				}
			}
		}
		return null;
	}

	/**
	 * Search OFF image path from ON image path.
	 * 
	 * @param onImagePath
	 * @return The image path found, otherwise null
	 */
	public static IPath searchOffImage(IPath onImagePath) {
		String[] suffixes = { "Off", "OFF", "off" };

		Pattern pattern = Pattern.compile(REGEX_ON_IMG);
		Matcher matcher = pattern.matcher(onImagePath.toString());
		if (matcher.matches()) {
			StringBuffer sb = new StringBuffer();
			sb.append(matcher.group(1));
			sb.append("***");
			sb.append(matcher.group(3));
			String tmpPath = sb.toString();

			IPath offImagePath = null;
			for (String suffix : suffixes) {
				offImagePath = new Path(tmpPath.replace("***", suffix));
				if (isFileExists(offImagePath)) {
					return offImagePath;
				}
			}
		}
		return null;
	}

	/**
	 * Check if the file path exists into the current workspace.
	 * 
	 * @param path
	 * @return the check result
	 */
	private static boolean isFileExists(IPath path) {
		return ResourceUtil.isExsitingFile(path, false);
//		try {
//			final IResource fileResource = ResourcesPlugin.getWorkspace()
//					.getRoot().findMember(path, false);
//			if (fileResource != null && fileResource instanceof IFile) {
//				final IFile file = (IFile) fileResource;
//				if (file.exists()) {
//					return true;
//				}
//			}
//		} catch (Exception e) {
//			Activator.getLogger().log(
//					Level.WARNING,
//					"ERROR in checking if file path exists in the workspace "
//							+ path, e);
//		}
//		return false;
	}

	/**
	 * Check the pattern applied on the image file path is verified.
	 * @param regex
	 * @param imagePath
	 * @return true if verified, else false
	 */
	private static boolean checkPatternOnFilePath(String regex, IPath imagePath) {
		if (imagePath.isEmpty()) {
			return false;
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(imagePath.toString());
		return matcher.matches();
	}

	/**
	 * Convert the given value from degree to SWT.
	 * 
	 * @param degree
	 * @return the SWT value. Default is 0.
	 */
	public static int degreeToSWT(int degree) {
		switch (degree) {
		case 0:
			return SWT.UP;
		case 90:
			return SWT.RIGHT;
		case 180:
			return SWT.DOWN;
		case 270:
			return SWT.LEFT;
		default:
			return 0;
		}
	}

	/**
	 * Convert the given value from SWT to degree.
	 * 
	 * @param degree
	 * @return the degree value. Default is 0.
	 */
	public static int swtToDegree(int swt) {
		switch (swt) {
		case SWT.UP:
			return 0;
		case SWT.RIGHT:
			return 90;
		case SWT.DOWN:
			return 180;
		case SWT.LEFT:
			return 270;
		default:
			return 0;
		}
	}
	
	//TODO: add comments
	
	public static boolean isExtensionAllowed(IPath imagePath) {
		List<String> extList = Arrays.asList(IMAGE_EXTENSIONS);
		return extList.contains(imagePath.getFileExtension());
	}

	public static String getMultistateBaseImagePath(IPath imagePath) {
		if (imagePath == null || imagePath.isEmpty())
			return null;

		Pattern pattern = Pattern.compile("^(.+)\\s+(\\w+)(_.+)?(\\.\\w+)$");
		Matcher matcher = pattern.matcher(imagePath.toString());
		// extract "<absolute base path> <state>_<comment>.<extension>"
		String ext = null, basePath = null;
		if (matcher.matches()) {
			basePath = matcher.group(1);
			ext = matcher.group(4);
		} else {
			return null;
		}
		// replace <state>_<comment> by the state marker
		String path = basePath + " " + STATE_MARKER + ext;
		return path;
	}

	// Bug 3479: update widget to use state index instead of string value
	public static IPath searchStateImage(int stateIndex, String basePath) {
		if (basePath == null || basePath.isEmpty())
			return null;
		String path = basePath.replace(STATE_MARKER, String.valueOf(stateIndex));
		IPath stateImagePath = new Path(path);
		if (isFileExists(stateImagePath)) {
			return stateImagePath;
		}
		return null;
	}
	
	public static void crop(Rectangle rect, Insets insets) {
		if (insets == null)
			return;
		rect.setX(rect.x + insets.left);
		rect.setY(rect.y + insets.top);
		rect.setWidth(rect.width - (insets.getWidth()));
		rect.setHeight(rect.height - (insets.getHeight()));
	}
	
	public static boolean isShadeOfGray(int pixel, PaletteData palette) {
		int r = (pixel & palette.redMask) >> palette.redShift;
		int g = (pixel & palette.greenMask) >> palette.greenShift;
		int b = (pixel & palette.blueMask) >> palette.blueShift;
		return (r == g) && (g == b);
	}

	public static int applyShade(int shadedPixel, int pixelToShade, PaletteData palette) {
		int newColor = 0
		,redMask = palette.redMask
		,blueMask = palette.blueMask
		,greenMask = palette.greenMask
		,redShift = palette.redShift
		,greenShift = palette.greenShift
		,blueShift = palette.blueShift;
		
		float ratioR = ((shadedPixel & redMask) >> redShift) / 255f;
		float ratioG = ((shadedPixel & greenMask) >> greenShift) / 255f;
		float ratioB = ((shadedPixel & blueMask) >> blueShift) / 255f;
		
		int r = (pixelToShade & redMask) >> redShift;
		int g = (pixelToShade & greenMask) >> greenShift;
		int b = (pixelToShade & blueMask) >> blueShift;
		r = (int) Math.round(r * ratioR);
		g = (int) Math.round(g * ratioG);
		b = (int) Math.round(b * ratioB);
		if (r < 0) r = 0; else if (r > 255) r = 255;
		if (g < 0) g = 0; else if (g > 255) g = 255;
		if (b < 0) b = 0; else if (b > 255) b = 255;

		newColor |= (redShift < 0 ? r << -redShift : r >>> redShift) & redMask;
		newColor |= (greenShift < 0 ? g << -greenShift : g >>> greenShift) & greenMask;
		newColor |= (blueShift < 0 ? b << -blueShift : b >>> blueShift) & blueMask;
		return newColor;
	}
	
	public static BufferedImage convertToAWT(ImageData data) {
		ColorModel colorModel = null;
		PaletteData palette = data.palette;
		if (palette.isDirect) {
			colorModel = new DirectColorModel(data.depth, palette.redMask,
					palette.greenMask, palette.blueMask);
			BufferedImage bufferedImage = new BufferedImage(colorModel,
					colorModel.createCompatibleWritableRaster(data.width,
							data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					RGB rgb = palette.getRGB(pixel);
					pixelArray[0] = rgb.red;
					pixelArray[1] = rgb.green;
					pixelArray[2] = rgb.blue;
					raster.setPixels(x, y, 1, 1, pixelArray);
				}
			}
			return bufferedImage;
		} else {
			RGB[] rgbs = palette.getRGBs();
			byte[] red = new byte[rgbs.length];
			byte[] green = new byte[rgbs.length];
			byte[] blue = new byte[rgbs.length];
			for (int i = 0; i < rgbs.length; i++) {
				RGB rgb = rgbs[i];
				red[i] = (byte) rgb.red;
				green[i] = (byte) rgb.green;
				blue[i] = (byte) rgb.blue;
			}
			if (data.transparentPixel != -1) {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red,
						green, blue, data.transparentPixel);
			} else {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red,
						green, blue);
			}
			BufferedImage bufferedImage = new BufferedImage(colorModel,
					colorModel.createCompatibleWritableRaster(data.width,
							data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					pixelArray[0] = pixel;
					raster.setPixel(x, y, pixelArray);
				}
			}
			return bufferedImage;
		}
	}

}