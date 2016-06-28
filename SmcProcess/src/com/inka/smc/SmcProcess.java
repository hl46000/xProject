package com.inka.smc;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import com.inka.util.G;

/**
 * @author purehero2
 *
 */
@SuppressWarnings("unused")
public class SmcProcess {
	
	private Random rand = null;
	/*
	* 
	*/
	final int TAG_INDEX_VALUE_START_TAG_OFFSET		= 0;
	final int TAG_INDEX_VALUE_CONTENT_OFFSET		= 1;
	final int TAG_INDEX_VALUE_CONTENT_LENGTH		= 2;
	
	/*
	* 
	*/
	final int TAG_DEFINE_VALUE_INDEX_TYPE			= 0;	// 0:NONE, 1:XOR, 2:AES-128
	final int TAG_DEFINE_VALUE_INDEX_KEY_OFFSET		= 1;	//
	final int TAG_DEFINE_VALUE_INDEX_KEY_LENGTH		= 2;	// 
	final int TAG_DEFINE_VALUE_INDEX_IV_OFFSET		= 3;	//
	final int TAG_DEFINE_VALUE_INDEX_IV_LENGTH		= 4;	// 
	
	/*
	* 
	*/
	final int TAG_DEFINE_VALUE_NONE					= 0;	//
	final int TAG_DEFINE_VALUE_XOR					= 1;	//
	final int TAG_DEFINE_VALUE_AES					= 2;	// 
	
	private ArrayList<File> targets = new ArrayList<File>(); 
	private SmcTagFactory tagFactory = new SmcTagFactory();
	
	public SmcProcess() {
		rand = new Random( System.currentTimeMillis()) ;
	}
	
	/**
	 * ó���ؾ��� ���ϵ��� ��� �մϴ�. 
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public void add( File file ) throws FileNotFoundException {
		if( !file.exists()) {
			throw new FileNotFoundException( file.getAbsolutePath() );
		}
		
		targets.add( file );
	}

	/**
	 * ��ϵ� ���ϵ��� ó�� �մϴ�. 
	 * @throws IOException 
	 */
	public void process() throws IOException {
		if( targets.size() < 1 ) return;
		
		for( File file : targets ) {
			processImpl( file );			
		}
	}

	/**
	 * @param name
	 * @param ofs
	 * @param buff
	 * @param tag
	 */
	private void print( String name, int ofs, byte [] buff, int len ) {
		G.log( "%s : %d", name, ofs );
		if( ofs > 0 ) {
			G.printBytes( buff, ofs - 1, len );
		}	
	}
	
	private void print16( String name, int ofs, byte [] buff, int len ) {
		G.log( "=======================================================================================" );
		G.log( "%s : offset[%d] bytes[%d]", name, ofs, len );
		G.log( "=======================================================================================" );
		if( ofs > 0 ) {
			G.printBytes16( buff, ofs, len );
		}	
		G.log( "=======================================================================================" );
	}
	
	/**
	 *  
	 * 
	 * @param file
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void processImpl(File file) throws IOException {
		byte [] file_buff = FileUtils.readFileToByteArray( file );
		if( file_buff == null ) {
			G.log( "ERROR : '%s' can't read", file.getAbsolutePath() );
			return;
		}
		
		G.log( "'%s' %d bytes", file.getAbsolutePath(), file_buff.length );
		
		SmcTagValue [] smcTagValues = tagFactory.getValues();
		for( SmcTagValue tagVal : smcTagValues ) {
			process_dummy_tags( file_buff, tagVal );
			process_protection_tags( file.getName(), file_buff, tagVal );
		}
		
		FileUtils.writeByteArrayToFile( file, file_buff );				
	}
	
	
	/**
	 * ��ȣ�ؾ� �� �����͸� �����Ͽ� ��ȣȭ �� ������ �����ش�. 
	 * 
	 * @param file_buff
	 * @param tagVal
	 */
	private void process_protection_tags( final String filename, byte[] file_buff, final SmcTagValue tagVal ) {
		/*
		* ��ȣ�ؾ� �� �������� ������ �����Ѵ�. 
		*/ 
		final byte [] start_tag = tagVal.GetStartTag();
		final byte [] end_tag	= tagVal.GetEndTag();
		
		int contents_ofs = 0;
		int contents_len = 0;
		
		
		// [0] start tag index, [1] index of protection data, [2] data length
		Vector<int[]> seachedTagValue = new Vector<int[]>(); 
		while( true ) {
			int start_ofs = searchingTag( file_buff, contents_ofs, start_tag );	// ���� Tag �˻�
			if( start_ofs < 0) {
				break;
			}
			
			G.log( "=> 0x%x %d", file_buff[start_ofs], start_ofs );
			
			contents_ofs = start_ofs + start_tag.length;
							
			int end_ofs = searchingTag( file_buff, contents_ofs, end_tag );		// ���� Tag �˻�
			if( end_ofs < 0 ) {
				G.log( "'%s' start tag %d but not found end tag ( %s tag )", filename, start_ofs, tagVal.GetName() );
				continue;
			}
			
			contents_len = end_ofs - contents_ofs;
			G.log( "'%s' contents offset %d, len %d ( %s tag )", filename, contents_ofs, contents_len, tagVal.GetName() );
			
			// next tag finding
			contents_ofs = end_ofs + end_tag.length;
			
			seachedTagValue.add( new int[]{ start_ofs, start_ofs + start_tag.length, contents_len } );
		} 
		
		/*
		* ����� ��ȣ ������ ������ �����͸� ��ȣȭ �մϴ�.  
		*/ 
		process_protection_tags_crypto( filename, file_buff, tagVal, seachedTagValue );
	}

	/**
	 * ����� ��ȣ ������ ������ �����͵��� ��ȣȭ �մϴ�.  
	 * 
	 * @param filename
	 * @param file_buff
	 * @param tagVal
	 * @param seachedTagValue
	 */
	private void process_protection_tags_crypto( final String filename, byte[] file_buff, final SmcTagValue tagVal, final Vector<int[]> seachedTagValue ) {
		if( seachedTagValue.size() > 0 ) {
			G.log( "'%s' found %d %s tag", filename, seachedTagValue.size(), tagVal.GetName() );
			
			final int tagValueIndexes [] = tagVal.GetStartTagValueIndexes();		// Start tag value indexes
			final int startTagLength = tagVal.GetStartTag().length;					// Start tag lenght
			
			for( int [] tagIndexValue : seachedTagValue ) {
								
				/*
				* SMC �� ��ȣ�ؾ� �Ǵ� �����͸� ��ȣȭ �մϴ�.    
				*/
				G.log( "s_ofs(%d) s_data_ofs(%d), s_data_len(%d)", 
						tagIndexValue[TAG_INDEX_VALUE_START_TAG_OFFSET], 			// Start tag offset
						tagIndexValue[TAG_INDEX_VALUE_CONTENT_OFFSET], 				// Content data offset contained in Start tag 
						tagIndexValue[TAG_INDEX_VALUE_CONTENT_LENGTH]);				// Content data length
				process_protection_tags_crypto_impl( file_buff, tagIndexValue, tagValueIndexes, startTagLength );
				
				/*
				G.log( "==========================================================================" );
				print16( String.format( "SMC content data (%s)", filename ),
						tagIndexValue[TAG_INDEX_VALUE_CONTENT_OFFSET], 
						file_buff, 
						tagIndexValue[TAG_INDEX_VALUE_CONTENT_LENGTH] );
				G.log( "==========================================================================" );
				*/
			}
			
			/*
			* SMC Start/End tag size �Լ��� ��ȯ���� �����մϴ�.   
			*/
			processTagSizeDefineTag( "Start Tag Size", file_buff, tagVal.GetStartTagSizeDefineTag() );
			processTagSizeDefineTag( "End Tag Size",   file_buff, tagVal.GetEndTagSizeDefineTag() );
			
			/*
			* SMC Tag values index ��ȯ���� ���� �մϴ�.    
			*/
			processTagValueIndexDefineTag( "Tag Value Index DEF", file_buff, tagVal.GetTagValuesIndexDefineTag(), tagVal.GetStartTagValueIndexes());
			
		} else {
			G.log( "'%s' not found %s tag", filename, tagVal.GetName() );
		}
	}

	

	/**
	 * ���� �������� ��ȣȭ�� ���� �մϴ�. 
	 * 
	 * @param file_buff
	 * @param tagIndexValue
	 * @param tagValueIndexes
	 * @param startTagLength
	 */
	private void process_protection_tags_crypto_impl( byte[] file_buff, final int[] searchedTagIndexValue, final int[] tagValueIndexes, final int startTagLength ) {		
		/*
		* ��ȣȭ Type ���� 
		*/
		int type = 1 + (int)( Math.abs( rand.nextInt()) % 2 );		// 1 ~ 2
		file_buff[ searchedTagIndexValue[TAG_INDEX_VALUE_START_TAG_OFFSET] + tagValueIndexes[TAG_DEFINE_VALUE_INDEX_TYPE] ] = (byte) type;
		
		print16( "CONTENT DATA", searchedTagIndexValue[TAG_INDEX_VALUE_CONTENT_OFFSET], file_buff, searchedTagIndexValue[TAG_INDEX_VALUE_CONTENT_LENGTH] );
		
		// ���� Type �� ���� ó�� ��
		final int base_ofs = searchedTagIndexValue[TAG_INDEX_VALUE_CONTENT_OFFSET];
		switch( type ) {
		case TAG_DEFINE_VALUE_XOR : 
		case TAG_DEFINE_VALUE_AES : 
			int len = searchedTagIndexValue[TAG_INDEX_VALUE_CONTENT_LENGTH];
			for( int i = 0; i < len; i++ ) {
				file_buff[base_ofs+i] = ( byte )( file_buff[base_ofs+i] ^ 0xaa ); 
			}
		} 
		
		print16( "SMC CONTENT DATA", searchedTagIndexValue[TAG_INDEX_VALUE_CONTENT_OFFSET], file_buff, searchedTagIndexValue[TAG_INDEX_VALUE_CONTENT_LENGTH] );
				
		//G.log( "TAG_DEFINE_VALUE_INDEX_KEY_OFFSET ==> %d", file_buff[ searchedTagIndexValue[TAG_INDEX_VALUE_START_TAG_OFFSET] + tagValueIndexes[TAG_DEFINE_VALUE_INDEX_KEY_OFFSET] ] );
		//G.log( "TAG_DEFINE_VALUE_INDEX_KEY_LENGTH ==> %d", file_buff[ searchedTagIndexValue[TAG_INDEX_VALUE_START_TAG_OFFSET] + tagValueIndexes[TAG_DEFINE_VALUE_INDEX_KEY_LENGTH] ] );
		//G.log( "TAG_DEFINE_VALUE_INDEX_IV_OFFSET ==> %d", file_buff[ searchedTagIndexValue[TAG_INDEX_VALUE_START_TAG_OFFSET] + tagValueIndexes[TAG_DEFINE_VALUE_INDEX_IV_OFFSET] ] );
		//G.log( "TAG_DEFINE_VALUE_INDEX_IV_LENGTH ==> %d", file_buff[ searchedTagIndexValue[TAG_INDEX_VALUE_START_TAG_OFFSET] + tagValueIndexes[TAG_DEFINE_VALUE_INDEX_IV_LENGTH] ] );
		
		file_buff[ searchedTagIndexValue[TAG_INDEX_VALUE_START_TAG_OFFSET] + tagValueIndexes[TAG_DEFINE_VALUE_INDEX_KEY_OFFSET] ] = (byte) 0x10;
		file_buff[ searchedTagIndexValue[TAG_INDEX_VALUE_START_TAG_OFFSET] + tagValueIndexes[TAG_DEFINE_VALUE_INDEX_KEY_LENGTH] ] = (byte) 0x20;
		file_buff[ searchedTagIndexValue[TAG_INDEX_VALUE_START_TAG_OFFSET] + tagValueIndexes[TAG_DEFINE_VALUE_INDEX_IV_OFFSET]  ] = (byte) 0x30;
		file_buff[ searchedTagIndexValue[TAG_INDEX_VALUE_START_TAG_OFFSET] + tagValueIndexes[TAG_DEFINE_VALUE_INDEX_IV_LENGTH]  ] = (byte) 0x40;
		
		//G.log( "TAG_DEFINE_VALUE_INDEX_KEY_OFFSET ==> %d", file_buff[ searchedTagIndexValue[TAG_INDEX_VALUE_START_TAG_OFFSET] + tagValueIndexes[TAG_DEFINE_VALUE_INDEX_KEY_OFFSET] ] );
		//G.log( "TAG_DEFINE_VALUE_INDEX_KEY_LENGTH ==> %d", file_buff[ searchedTagIndexValue[TAG_INDEX_VALUE_START_TAG_OFFSET] + tagValueIndexes[TAG_DEFINE_VALUE_INDEX_KEY_LENGTH] ] );
		//G.log( "TAG_DEFINE_VALUE_INDEX_IV_OFFSET ==> %d", file_buff[ searchedTagIndexValue[TAG_INDEX_VALUE_START_TAG_OFFSET] + tagValueIndexes[TAG_DEFINE_VALUE_INDEX_IV_OFFSET] ] );
		//G.log( "TAG_DEFINE_VALUE_INDEX_IV_LENGTH ==> %d", file_buff[ searchedTagIndexValue[TAG_INDEX_VALUE_START_TAG_OFFSET] + tagValueIndexes[TAG_DEFINE_VALUE_INDEX_IV_LENGTH] ] );
	}

	/**
	 * DUMMY Tag ���� �˻��ϰ� �˻��� DUMMY Tag ���� �����Ѵ�. 
	 * 
	 * @param file_buff
	 * @param tagFactory
	 */
	private void process_dummy_tags( byte[] file_buff, final SmcTagValue tagValue ) {
		final byte [] search_bytes 		= tagValue.GetDummyTag();
		final int  [] tagValueIndexes 	= tagValue.GetStartTagValueIndexes(); 
				
		Random rand = new Random( System.currentTimeMillis() ); 
		
		int search_ofs = 0;
		while( true ) {
			search_ofs = searchingTag( file_buff, search_ofs, search_bytes );
			if( search_ofs < 0) { // ���̻� �˻��� �����Ͱ� ������ ���� �Ѵ�. 				
				break;
			}
			
			/*
			* �˻��� offset ������ ������ ó�� �۾�
			*/ 
			for( int i = 0; i < tagValueIndexes.length; i++ ) {
				byte tempByte = file_buff[ search_ofs + tagValueIndexes[i] ];
				file_buff[ search_ofs + tagValueIndexes[i] ] = (byte)( rand.nextInt() & 0x7F ); // dummy tag �� ���� �����Ѵ�.
				//G.log( "Changed dummy value : %d %d => %d", file_buff[search_ofs], tempByte, file_buff[ search_ofs + tagValueIndexes[i] ] );
			}
			//////////////////////////////////////////////
			
			/*
			* ���� �˻� ������ ���� �۾�
			*/
			search_ofs += search_bytes.length;
		}
	}

	/**
	 * Tag �� ���̸� ��ȯ�� �ִ� �Լ��� ã������ Tag�� �˻��ؼ� ���� ���� ������ �ش�. 
	 * 
	 * @param title
	 * @param file_buff
	 * @param getEndTagSizeDefineTag
	 */
	private void processTagSizeDefineTag( final String title, byte[] file_buff, byte[] tagSizeDefineTag ) {
		int tag_size_def_ofs = searchingTag( file_buff, 0, tagSizeDefineTag );
		
		final byte prefix_value = (byte)250;
		
		if( tag_size_def_ofs > 0 ) {
			print( String.format( "found %s data TagLen[%d]", title, tagSizeDefineTag.length ), tag_size_def_ofs, file_buff, tagSizeDefineTag.length + 16 );
			
			int base_idx = tag_size_def_ofs + tagSizeDefineTag.length-1;
			for( int i = 0; i < 16; i++ ) {
				if( file_buff[i+base_idx] == prefix_value ) {
					//G.log( "Found prefix value : %d", file_buff[i+base_idx] );
					
					file_buff[i+base_idx] = (byte) tagSizeDefineTag.length;
					break;
				}
			}
			print( "modified tag size def data", tag_size_def_ofs, file_buff, tagSizeDefineTag.length + 16 );
			
		} else {
			print( "ERROR : could not found SMC Tag size defined TAG", 0, tagSizeDefineTag, tagSizeDefineTag.length );				
		}
	}

	/**
	 * SMC Tag values index ��ȯ���� ���� �մϴ�.
	 * 
	 * @param title
	 * @param file_buff
	 * @param js 
	 * @param getTagValuesIndexDefineTag
	 */
	private void processTagValueIndexDefineTag( String title, byte[] file_buff, byte[] tagValuesIndexDefineTag, int[] tagValueIndexValue ) {
		int searched_ofs = searchingTag( file_buff, 0, tagValuesIndexDefineTag );
		
		byte prefix_value = (byte)0x7A;
		
		if( searched_ofs > 0 ) {
			print( String.format( "found %s data offset[%d]", title, searched_ofs ), searched_ofs, file_buff, tagValuesIndexDefineTag.length + 50 );
			
			int base_idx = searched_ofs + tagValuesIndexDefineTag.length-1;
			
			for( int loop = 0; loop < tagValueIndexValue.length; loop++ ) {
				for( int i = 0; i < 16; i++, base_idx++ ) {
					if( file_buff[base_idx] == prefix_value ) {
						G.log( "Found prefix %d value : 0x%x => 0x%x", loop, file_buff[base_idx], tagValueIndexValue[loop] );
						
						file_buff[base_idx] = (byte) tagValueIndexValue[loop];
						prefix_value++;
						break;
					}
				}
			}
			print( "modified tag value index def data", searched_ofs, file_buff, tagValuesIndexDefineTag.length + 50 );
			
		} else {
			print( "ERROR : could not found SMC Tag value indexes defined TAG", 0, tagValuesIndexDefineTag, tagValuesIndexDefineTag.length );				
		}
	}
	
	/**
	 * contents ������ offset ��ġ ���� tagValues ���� �˻��մϴ�. 
	 * 
	 * @param contents
	 * @param offset
	 * @param tagValues
	 * @return ���� �߰��ϸ� �ش� index ���� ��ȯ�ϰ�, �߰����� ���ϸ� -1���� ��ȯ �մϴ�. 
	 */
	private int searchingTag( byte[] contents, int offset, byte[] tagValues ) {
		if( tagValues == null ) {
			throw new InvalidParameterException( "tagValues is null" );
		}
		
		int len = contents.length;
		int tagLen = tagValues.length;

		boolean found = false;
		for( ; offset < len && !found; offset++ ) {
			
			found = true;
			for( int t = 0; t < tagLen; t++ ) {
				if( contents[offset+t] != tagValues[t] ) {
					found = false;
					break;
				}
			}
			if( found ) {
				return offset;
			}
		}
		
		return -1;
	}
}
