package android.touch.macro.v2;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class UtilV2 {
	
	/**
	 * 
	 * 
	 * @param runnableClass
	 * @return
	 */
	public static String GetCurrentPath( Object runnableClass ) {
		String jarDir = runnableClass.getClass().getClassLoader().getResource("").getPath();
		if( System.getProperty( "os.name" ).contains( "Window" )) {
			if( jarDir.startsWith("/")) jarDir = jarDir.substring(1);
		}
		
		return jarDir;
	}
	
	
	/**
	 * @param originalImage
	 * @param destWidth
	 * @param destHeight
	 * @return
	 */
	public static BufferedImage resizeImage( BufferedImage originalImage, int destWidth, int destHeight ) {
		BufferedImage resizedImage = new BufferedImage( destWidth, destHeight, originalImage.getType());
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, destWidth, destHeight, null);
		g.dispose();
	 
		return resizedImage;
	}
	
	
	/**
	 * Rotates an image. Actually rotates a new copy of the image.
	 * 
	 * @param img The image to be rotated
	 * @param angle The angle in degrees
	 * @return The rotated image
	 */
	public static BufferedImage rotate(BufferedImage img, double angle)	{
		if( angle == 0.0 ) return img;
		
		double sin = Math.abs(Math.sin(Math.toRadians(angle)));
		double cos = Math.abs(Math.cos(Math.toRadians(angle)));

	    int w = img.getWidth(null), h = img.getHeight(null);

	    int neww = (int) Math.floor(w*cos + h*sin);
	    int newh = (int) Math.floor(h*cos + w*sin);

	    BufferedImage bimg = new BufferedImage( neww, newh, img.getType());
	    Graphics2D g = bimg.createGraphics();

	    g.translate((neww-w)/2, (newh-h)/2);
	    g.rotate(Math.toRadians(angle), w/2, h/2);
	    g.drawRenderedImage(img, null);
	    g.dispose();

	    //img.flush();
	    //img = null;
	    
	    return bimg;
	}
	
	/**
	 * 입력된 좌표값을 화면좌표값을 화면에 적용된 확대/축소값을 적용하여 원래의 X,Y 값을 반환 합니다. <br>즉, 화면좌표을 입력 받아 디바이스의 좌표로 변경시켜반환합니다.
	 * 
	 * @param pt
	 * @param ratio
	 * @return
	 */
	public static Point getRatioedPoint(Point pt, double ratio) {
		return new Point((int)(pt.x/ratio), (int)(pt.y/ratio));
	}
	
	
	/**
	 * 디바이스 좌표를 입력받아 화면 좌표값의 비율료 조정된 값을 반환합니다. 
	 * 
	 * @param pt
	 * @param ratio
	 * @return
	 */
	public static Point getUnratioedPoint(Point pt, double ratio) {
		return new Point((int)(pt.x*ratio), (int)(pt.y*ratio));
	}
	
	
	/**
	 * 이미지의 angle 이 적용된 좌표로 변환하여 반환 합니다. 
	 * 
	 * @param pt
	 * @param width
	 * @param height
	 * @param angle
	 * @return
	 */
	public static Point getAngledPoint( Point pt, int width, int height, int angle) {
		Point ret = new Point( pt );
		
		if( angle == 90 ) {		
			ret.x = pt.y; ret.y = height - pt.x;
		} else if( angle == 180 ) {
			ret.x = width - pt.x; ret.y = height - pt.y;
		} else if( angle == 270 ) {
			ret.x = width - pt.y; ret.y = pt.x;
		}
		
		return ret;
	}
	
	/**
	 * 이미지의 angle 이 적용된 좌표로 변환하여 반환 합니다.
	 * 
	 * @param pt
	 * @param width
	 * @param height
	 * @param angle
	 * @return
	 */
	public static Point getUnangledPoint(Point pt, int width, int height, int angle) {
		Point ret = new Point( pt );
		
		if( angle == 90 ) {		
			ret.y = pt.x; ret.x = width - pt.y; 
		} else if( angle == 180 ) {
			ret.x = width - pt.x; ret.y = height - pt.y;
		} else if( angle == 270 ) {
			ret.x = pt.y; ret.y = height - pt.x;
		}
		
		return ret;
	}
}
