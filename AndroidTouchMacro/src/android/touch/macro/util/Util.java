package android.touch.macro.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import android.touch.macro.Constant;
import android.touch.macro.G;

public class Util {
	
	/*
	 * 주석 추가
	 * */
	/**
	 * @param img
	 * @return
	 * @throws IOException
	 */
	public static File BufferedImageToFile( BufferedImage img, File file ) throws IOException {
		ImageIO.write( img, "PNG", file);
		return file;
	}
	
	public static File BufferedImageToFile( BufferedImage img ) throws IOException {
		File file = File.createTempFile( "tmp", ".tmp", G.getTempPath());
		return BufferedImageToFile( img, file );
	}
	
	/**
	 * @param img_file
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage FileToBufferedImage( File img_file ) {
		try {
			return ImageIO.read( img_file );
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param is
	 * @return
	 */
	public static BufferedImage InputStreamToBufferedImage( InputStream is ) {
		BufferedImage ret = null;
		try {
			ret = ImageIO.read( is );
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}
	
	
	/**
	 * File로 부터 Properties 을 읽어서 반환한다. 
	 * 
	 * @param file
	 * @return
	 */
	public static Properties readProperties( File file ) {
		Properties prop = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream( file );
			prop.load( fis );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			if( fis!= null ) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return prop;
	}
	
	
	/**
	 * @param data
	 * @return
	 */
	public static String getColonValue( String data ) {
		try {
			String tokens [] = data.split(":");
			return tokens.length > 1 ? tokens[1] : tokens[0];
		} catch( Exception e ) {
			e.printStackTrace();
			Log.d( "Exception string : %s", data );
		}
		
		return null;
	}
	
	/**
	 * �ܺ� ���α׷��� ���ุ ��Ų��. 
	 * 
	 * @param prog
	 */
	public static void RuntimeExec( String prog ) {
		try {
			Runtime.getRuntime().exec( prog ).waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �ܺ� ���α׷��� �����ϰ� �� ��°��� String ������ ��ȯ�Ѵ�. 
	 * 
	 * @param prog �ܺ� ���α׷� ���� ��ɾ�
	 * @return �ܺ� ���α׷��� ���� ��� ���ڿ�, ��ɾ� ���� ���н� size 0�� ArrayList<String> ��ü�� ��ȯ��
	 */
	@SuppressWarnings("resource")
	public synchronized static ArrayList<String> getRuntimeExecResult( String prog ) {
		InputStream input = null;
		InputStream error = null;
		Process process = null;
		
		ArrayList<String> ret = new ArrayList<String>();
		try {
			process = Runtime.getRuntime().exec( prog );
			
			G.tempAdbProcess.add( process );
			
			input = process.getInputStream();
			error = process.getErrorStream();
		
			Scanner input_scaner = new Scanner(input).useDelimiter("\\n");
			while( input_scaner.hasNext() ) {
				ret.add( input_scaner.next());
			}
			
			Scanner error_scaner = new Scanner(error).useDelimiter("\\n");
			while( error_scaner.hasNext() ) {
				ret.add( error_scaner.next());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			
		} finally {
			if( input != null ) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if( error != null ) {
				try {
					error.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return ret;
	}

	private static JFrame frame = null;
	public static void setFrame( JFrame _frame ) { frame = _frame; }
	public static void alert( String title, String message ) {
		JOptionPane.showMessageDialog( frame, message, title, JOptionPane.INFORMATION_MESSAGE );
	}
	
	
	public static File openFolderChooserDialog( File base_folder, boolean bSave ) {
		JFileChooser j = new JFileChooser();
		j.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		if( base_folder != null ) j.setCurrentDirectory( base_folder );
				
		int result = bSave ? j.showSaveDialog( frame ) : j.showOpenDialog( frame );
		if( JFileChooser.APPROVE_OPTION == result ) {
			File ret = j.getSelectedFile();
			
			G.getDefaultProperties().setProperty( Constant.LAST_SELECTED_FOLDER, ret.getAbsolutePath());
			G.saveDefaultProperties();
			
			return ret;
		}
		return null;
	}
	
	/**
	 * 오픈할 폴더를 선택하는 Dialog 창을 띄운다.
	 * 
	 * @return 선택된 폴더의 File 객체
	 */
	public static File openFolderChooserDialog() {
		return openFolderChooserDialog( new File( G.getDefaultProperties().getProperty( Constant.LAST_SELECTED_FOLDER, G.getDefaultPath().getAbsolutePath()) ), false );
	}	
	
	/**
	 * 저장할 폴더를 선택하는 Dialog 창을 띄운다.
	 * 
	 * @return 선택된 폴더의 File 객체
	 */
	public static File saveFolderChooserDialog() {
		return openFolderChooserDialog( new File( G.getDefaultProperties().getProperty( Constant.LAST_SELECTED_FOLDER, G.getDefaultPath().getAbsolutePath()) ), true );
	}
	
	/**
	 * @param base_folder
	 * @param bSave
	 * @return
	 */
	public static File openFileChooserDialog( File base_folder, boolean bSave ) {
		JFileChooser j = new JFileChooser();
		j.setFileSelectionMode( JFileChooser.FILES_ONLY );
		if( base_folder != null ) j.setCurrentDirectory( base_folder );
				
		int result = bSave ? j.showSaveDialog( frame ) : j.showOpenDialog( frame );
		if( JFileChooser.APPROVE_OPTION == result ) {
			File ret = j.getSelectedFile();
			
			G.getDefaultProperties().setProperty( Constant.LAST_SELECTED_FOLDER, ret.getAbsolutePath());
			G.saveDefaultProperties();
			
			return ret;
		}
		return null;
	}
	
	/**
	 * 오픈할 파일을 선택할 Dialog 창을 띄운다. 
	 * 
	 * @return 선택된 파일의 File 객체
	 */
	public static File openFileChooserDialog() {
		return openFileChooserDialog( new File( G.getDefaultProperties().getProperty( Constant.LAST_SELECTED_FOLDER, G.getDefaultPath().getAbsolutePath()) ), false );
	}
	
	/**
	 * 저장할 파일을 선택할 Dialog 창을 띄운다.
	 * 
	 * @return 선택된 파일의 File 객체
	 */
	public static File saveFileChooserDialog() {
		return openFileChooserDialog( new File( G.getDefaultProperties().getProperty( Constant.LAST_SELECTED_FOLDER, G.getDefaultPath().getAbsolutePath()) ), true );
	}
	
	// Return an ArrayList containing histogram values for separate R, G, B channels
    public static int[] imageHistogram( BufferedImage input ) {
 
    	int rhistogram [] = new int[ 256 * 3 ];
    	for( int i = 0; i < rhistogram.length; i++ ) rhistogram[i] = 0;
 
        for(int i=0; i<input.getWidth(); i++) {
            for(int j=0; j<input.getHeight(); j++) {
 
                int red = new Color(input.getRGB (i, j)).getRed();
                int green = new Color(input.getRGB (i, j)).getGreen();
                int blue = new Color(input.getRGB (i, j)).getBlue();
 
                // Increase the values of colors
                rhistogram[red]++; rhistogram[256+green]++; rhistogram[512+blue]++;
            }
        }
        return rhistogram;
    }

    
 // Convert R, G, B, Alpha to standard 8 bit
    public static int colorToRGB(int alpha, int red, int green, int blue) {
 
        int newPixel = 0;
        newPixel += alpha; newPixel = newPixel << 8;
        newPixel += red; newPixel = newPixel << 8;
        newPixel += green; newPixel = newPixel << 8;
        newPixel += blue;
 
        return newPixel;
 
    }
}
