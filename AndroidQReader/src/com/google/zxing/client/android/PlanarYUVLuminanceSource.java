/*
 * Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import android.graphics.Bitmap;

import com.google.zxing.LuminanceSource;

/**
 * This object extends LuminanceSource around an array of YUV data returned from the camera driver,
 * with the option to crop to a rectangle within the full data. This can be used to exclude
 * superfluous pixels around the perimeter and speed up decoding.
 *
 * It works for any pixel format where the Y channel is planar and appears first, including
 * YCbCr_420_SP and YCbCr_422_SP.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class PlanarYUVLuminanceSource extends LuminanceSource {

  private byte[] yuvData;
  private int dataWidth;
  private int dataHeight;
  private final int left;
  private final int top;

  public PlanarYUVLuminanceSource(byte[] _yuvData,
                                  int dataWidth,
                                  int dataHeight,
                                  int left,
                                  int top,
                                  int width,
                                  int height,
                                  boolean reverseHorizontal) {
    super(width, height);

    if (left + width > dataWidth || top + height > dataHeight) {
      throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
    }

    this.yuvData = _yuvData;
    this.dataWidth = dataWidth;
    this.dataHeight = dataHeight;
    this.left = left;
    this.top = top;
    
    if (reverseHorizontal) {
      reverseHorizontal(width, height);
    }
  }

  @Override
  public byte[] getRow(int y, byte[] row) {
    if (y < 0 || y >= getHeight()) {
      throw new IllegalArgumentException("Requested row is outside the image: " + y);
    }
    int width = getWidth();
    if (row == null || row.length < width) {
      row = new byte[width];
    }
    int offset = (y + top) * dataWidth + left;
    System.arraycopy(yuvData, offset, row, 0, width);
    return row;
  }

  @Override
  public byte[] getMatrix() {
    int width = getWidth();
    int height = getHeight();

    // If the caller asks for the entire underlying image, save the copy and give them the
    // original data. The docs specifically warn that result.length must be ignored.
    if (width == dataWidth && height == dataHeight) {
      return yuvData;
    }

    int area = width * height;
    byte[] matrix = new byte[area];
    int inputOffset = top * dataWidth + left;

    // If the width matches the full width of the underlying data, perform a single copy.
    if (width == dataWidth) {
      System.arraycopy(yuvData, inputOffset, matrix, 0, area);
      return matrix;
    }

    // Otherwise copy one cropped row at a time.
    byte[] yuv = yuvData;
    for (int y = 0; y < height; y++) {
      int outputOffset = y * width;
      System.arraycopy(yuv, inputOffset, matrix, outputOffset, width);
      inputOffset += dataWidth;
    }
    return matrix;
  }

	private byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
		byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
	    // Rotate the Y luma
	    int i = 0;
	    for (int x = 0; x < imageWidth; x++) {
	        for (int y = imageHeight - 1; y >= 0; y--) {
	            yuv[i] = data[y * imageWidth + x];
	            i++;
	        }
	    }
	    // Rotate the U and V color components
	    i = imageWidth * imageHeight * 3 / 2 - 1;
	    for (int x = imageWidth - 1; x > 0; x = x - 2) {
	        for (int y = 0; y < imageHeight / 2; y++) {
	            yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
	            i--;
	            yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth)
	                    + (x - 1)];
	            i--;
	        }
	    }
	    return yuv;
	}

  
  	@Override
  	public LuminanceSource rotateCounterClockwise() {
  		yuvData = rotateYUV420Degree90( yuvData, dataWidth, dataHeight ); 
  		int tmp = dataWidth;
  		dataWidth = dataHeight;
  		dataHeight = tmp;
  		
  		return this;
  	}

	@Override
  	public boolean isRotateSupported() {
  		return true;
  	}

	@Override
	public boolean isCropSupported() {
		return true;
	}

  public int getDataWidth() {
    return dataWidth;
  }

  public int getDataHeight() {
    return dataHeight;
  }

  public Bitmap renderCroppedGreyscaleBitmap() {
    int width = getWidth();
    int height = getHeight();
    int[] pixels = new int[width * height];
    byte[] yuv = yuvData;
    int inputOffset = top * dataWidth + left;

    for (int y = 0; y < height; y++) {
      int outputOffset = y * width;
      for (int x = 0; x < width; x++) {
        int grey = yuv[inputOffset + x] & 0xff;
        pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
      }
      inputOffset += dataWidth;
    }

    //Matrix rotateMatrix = new Matrix();
    //rotateMatrix.postRotate(90);
    
    Bitmap bitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888);
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    return bitmap;
    /*
    Bitmap sideInversionImg = Bitmap.createBitmap(bitmap, 0, 0,
            bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, false);
	bitmap = null;
    return sideInversionImg;
    */
    
  }

	private void reverseHorizontal(int width, int height) {
		byte[] yuvData = this.yuvData;

		for (int y = 0, rowStart = top * dataWidth + left; y < height; y++, rowStart += dataWidth) {
			int middle = rowStart + width / 2;
			
			for (int x1 = rowStart, x2 = rowStart + width - 1; x1 < middle; x1++, x2--) {
				byte temp = yuvData[x1];
				yuvData[x1] = yuvData[x2];
				yuvData[x2] = temp;
			}
		}
	}

}
