import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class BinderRemover {
	public static void main(String[] args) {
		if( args == null ) 		{ print_useage(); return; }
		if( args.length < 1 ) 	{ print_useage(); return; }
		
		File base_folder = new File( args[0] );
		if( !base_folder.exists()) { print_useage(); return; }
				
		Stack<File> folder = new Stack<File>(); 
		folder.push( base_folder );
		
		while( !folder.empty()) {
			File cur_folder = folder.pop(); 
			System.out.printf( "Current Folder : %s\n", cur_folder.getAbsolutePath() );
			
			File files [] = cur_folder.listFiles(); 
			for( File file : files ) {
				String name = file.getName();
				if( name.compareTo(".") == 0 ) 		 continue;
				if( name.compareTo("..") == 0 ) 	 continue;
				if( file.isDirectory()) {
					folder.push( file );
					continue;
				}
				if( !name.endsWith( ".txt")) 		 continue;
				if( !name.contains( "Fatal signal")) continue;
				
				System.out.printf( "\nCHECK : %s ==> ", file.getAbsolutePath() );
				if( !is_real_fatal_error( file )) {
					file.delete();
					System.out.println( "DELETED" );
				} else {
					System.out.println( "PASS" );
				}
			}
		}
		
	}

	/**
	 * ������� ǥ�� �մϴ�. 
	 */
	private static void print_useage() {
		System.err.println( "USEAGE : java -jar <jar file> <folder>" );
		
	}

	/**
	 * Fatal ���� �� Binder ������ �߻��� ���� ������ Ȯ�� �մϴ�. 
	 * 
	 * @param file
	 * @return true : binder ���� �߻��� ������ �ƴ�, false : binder ���� �߻��� ������
	 */
	private static boolean is_real_fatal_error( File file ) {
		BufferedReader br = null;
		try {
			br = new BufferedReader( new FileReader( file ));
			
			String line;
			while(( line = br.readLine().toLowerCase()) != null ) {
				if( line.contains( "fatal" ) && !line.contains( "binder" )) {	// fatal �� �ִµ� binder �� ������ ��¥�� ���� �´�.
					return true;
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			
		} finally {
			if( br != null ) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return false;	// �츮�� ã�� ������ �ƴѴ�. 
	}

}
