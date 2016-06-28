package com.inka.util;

public class G {
	public static void log( String fmt, Object...args ) 	{ System.out.printf( fmt + "\n", args ); }
	public static void log( String msg ) 					{ System.out.printf( msg  + "\n" ); }
	public static void errLog( String fmt, Object...args ) 	{ System.err.printf( fmt + "\n", args ); }
	public static void errLog( String msg ) 				{ System.err.printf( msg  + "\n" ); }
	
	/**
     * 구동중인 OS가 Window 계열인가를 확인 한다. 
     * @return
     */
    public static boolean isWindowsOS() {
 		return System.getProperty( "os.name" ).contains( "Window" );
    }
	
	/**
	 * @param pAddr
	 * @param offset
	 * @param len
	 */
	public static void printBytes16( byte [] pAddr, int offset, int len ) {
		int i = offset;
		int ofs_len = 0;
		
		int nCnt = len / 16;
		for( int loop = 0; loop < nCnt; loop++, i+= 16, ofs_len+=16 )
		{
			log( "0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x",
				pAddr[i],   pAddr[i+1],  pAddr[i+2],  pAddr[i+3],  pAddr[i+4],  pAddr[i+5],  pAddr[i+6], pAddr[i+7],
				pAddr[i+8], pAddr[i+9], pAddr[i+10], pAddr[i+11], pAddr[i+12], pAddr[i+13], pAddr[i+14], pAddr[i+15] );
		}

		if( ofs_len < len )
		{
			String strTemp = String.format( "0x%02x", pAddr[i++] );
			for( ++ofs_len; ofs_len < len; ofs_len++ )
			{
				strTemp += String.format( " 0x%02x", pAddr[i++] );
			}

			log( strTemp );
		}
	}
	
	/**
	 * @param pAddr
	 * @param offset
	 * @param len
	 */
	public static void printBytes( byte [] pAddr, int offset, int len ) {
		int i = offset;
		int ofs_len = 0;
		
		int nCnt = len / 16;
		for( int loop = 0; loop < nCnt; loop++, i+= 16, ofs_len+=16 )
		{
			log( "%02d %02d %02d %02d %02d %02d %02d %02d %02d %02d %02d %02d %02d %02d %02d %02d",
				pAddr[i],   pAddr[i+1],  pAddr[i+2],  pAddr[i+3],  pAddr[i+4],  pAddr[i+5],  pAddr[i+6], pAddr[i+7],
				pAddr[i+8], pAddr[i+9], pAddr[i+10], pAddr[i+11], pAddr[i+12], pAddr[i+13], pAddr[i+14], pAddr[i+15] );
		}

		if( ofs_len < len )
		{
			String strTemp = String.format( "%02d", pAddr[i++] );
			for( ++ofs_len; ofs_len < len; ofs_len++ )
			{
				strTemp += String.format( " %02d", pAddr[i++] );
			}

			log( strTemp );
		}
	}
}
