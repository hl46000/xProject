import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class UnityVer20Test {

	public static void main(String[] args) {
		new UnityVer20Test().doRun();
	}
	
	public void doRun() {
		File test_file = new File( "c:\\workTemp\\final-output.dll");
		File test_output_file = new File("c:\\workTemp\\final-output_result.dll");
		
		try {
			byte fileBytes[] = FileUtils.readFileToByteArray( test_file );
			int signature_index = findAppSealingSignature( fileBytes );
			System.out.println( String.format( "signature index : %d(0x%x)", signature_index, signature_index ));
			System.out.println();
			
			new Unity20Tags( fileBytes, signature_index ).process();
					
			FileUtils.writeByteArrayToFile( test_output_file, fileBytes );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int findAppSealingSignature( byte [] fileBytes ) {
		TimeChecker t_check = new TimeChecker();
		t_check.setStartTime();
		
		AppSealingSignature a = new AppSealingSignature( fileBytes );
		int ret = a.findAppSealingSignature();
		t_check.setEndTime();
		
		System.out.println( String.format( "findAppSealingSignature : %dms", t_check.calcTerm() ));
	
		return ret;
	}
}

class Unity20Tags {
	final static byte [] start_tag 	= new byte[]{(byte)0x18,(byte)0x26,(byte)0x16,(byte)0x26,(byte)0x1C,(byte)0x26,(byte)0x1E,(byte)0x26,(byte)0x17,(byte)0x26,(byte)0x1B,(byte)0x26,(byte)0x17,(byte)0x26,(byte)0x1B,(byte)0x26,(byte)0x1D,(byte)0x26,(byte)0x1B,(byte)0x26};
	final static byte [] end_tag 	= new byte[]{(byte)0x18,(byte)0x26,(byte)0x16,(byte)0x26,(byte)0x1C,(byte)0x26,(byte)0x1E,(byte)0x26,(byte)0x17,(byte)0x26,(byte)0x1B,(byte)0x26,(byte)0x17,(byte)0x26,(byte)0x1B,(byte)0x26,(byte)0x1D,(byte)0x26,(byte)0x1D,(byte)0x26};
	final static byte [] offset_tag = new byte[]{(byte)0x20,(byte)0x17,(byte)0x7D,(byte)0x45,(byte)0x7B};
	final static byte [] length_tag = new byte[]{(byte)0x20,(byte)0x18,(byte)0x7D,(byte)0x45,(byte)0x7B};
	
	protected byte [] fileBytes ;
	protected int signature_index;
	public Unity20Tags( byte [] fileBytes, int signature_index ) {
		this.fileBytes = fileBytes;
		this.signature_index = signature_index;
	}
	
	/**
	 * 
	 * @return
	 */
	private List<TagValue> searchTags() {
		ArrayList<TagValue> ret = new ArrayList<TagValue>(); 
		
		int last_idx = fileBytes.length;
		int e_idx, s_idx = ByteUtils.searchBytes( fileBytes, start_tag );
		while( s_idx > 0 ) {
			e_idx = ByteUtils.searchBytes( fileBytes, s_idx + start_tag.length, last_idx, end_tag );
			if( e_idx < 0 ) break;
			
			ret.add( new TagValue(s_idx, e_idx));
			s_idx = ByteUtils.searchBytes( fileBytes, e_idx + end_tag.length, last_idx, start_tag );
		}
		
		return ret;
	}
	
	/**
	 * Tagging �Ǿ� ��ȣ�Ǿ�� �� �������� offset ���� length ���� DLL ���¿��� �����ϰ� �� ���� ��ȯ�� �ش�. <br>(��ȣȭ�� ���� �ʴ´�. )<br><br>
	 * offset : signature ���� ������ byte ��(���:Signature ���� ��, ����:Signature �ڿ� ������ ��Ÿ�� )<br>
	 * length : offset ���κ��� ��ȣ�ؾ��� �������� byte �� 
	 * 
	 * @param tagVal
	 * @return null �̸� ����, null�� �ƴϸ� ����( �ش� ���� ������ ��ȣȭ�� �����Ѵ�. ) 
	 */
	private PairValue placeHolder( TagValue tagVal ) {
		// ���� ���� : �������� start index �� �ݰ� 100 index ���� �ִ� ������ �����Ѵ�.
		
		int first_offset_index = -1;
		int second_offset_index =-1;
		
		int temp_index = ByteUtils.searchBytes( fileBytes, tagVal.start - 100, tagVal.start, offset_tag );
		while( temp_index > 0 ) {
			first_offset_index = second_offset_index;
			second_offset_index = temp_index;
						
			temp_index = ByteUtils.searchBytes( fileBytes, temp_index + offset_tag.length, tagVal.start, offset_tag ); 
		}
		if( first_offset_index < 0 ) {
			System.out.println( String.format( "[ERROR] Not found place holder => s_idx:%d", tagVal.start ));
			return null;
		}
		
		int first_length_index  = ByteUtils.searchBytes( fileBytes, first_offset_index + offset_tag.length, second_offset_index, length_tag );
		if( first_length_index < 0 ) {
			System.out.println( String.format( "[ERROR] Not found first length tag => s_idx:%d", tagVal.start ));
			return null;
		}
		int second_length_index = ByteUtils.searchBytes( fileBytes, second_offset_index + offset_tag.length, tagVal.start, length_tag );
		if( second_length_index < 0 ) {
			System.out.println( String.format( "[ERROR] Not found second length tag => s_idx:%d", tagVal.start ));
			return null;
		}
				
		// 
		PairValue ret = new PairValue( tagVal.start + start_tag.length, tagVal.end - ( tagVal.start + start_tag.length ));
		
		int nNewOffsetVal = signature_index - ret.first;
		int nNewLengthVal = ret.second;
				
		byte [] new_offset_value = ByteUtils.intTobyte( nNewOffsetVal, ByteOrder.LITTLE_ENDIAN );
		byte [] new_length_value = ByteUtils.intTobyte( nNewLengthVal, ByteOrder.LITTLE_ENDIAN );
		
		System.out.println( String.format( "found place holder => first  offset index : %d(0x%x), value : %d, %s", first_offset_index, first_offset_index, nNewOffsetVal, ByteUtils.byteArrayToHex(new_offset_value) ));
		System.out.println( String.format( "found place holder => first  length index : %d(0x%x), value : %d, %s", first_length_index, first_length_index, nNewLengthVal, ByteUtils.byteArrayToHex(new_length_value) ));
		System.out.println( String.format( "found place holder => second offset index : %d(0x%x), value : %d, %s", second_offset_index, second_offset_index, nNewOffsetVal, ByteUtils.byteArrayToHex(new_offset_value) ));
		System.out.println( String.format( "found place holder => second length index : %d(0x%x), value : %d, %s", second_length_index, second_length_index, nNewLengthVal, ByteUtils.byteArrayToHex(new_length_value) ));
		
		System.arraycopy( new_offset_value, 0, fileBytes, first_offset_index + 1, new_offset_value.length);
		System.arraycopy( new_offset_value, 0, fileBytes, second_offset_index+ 1, new_offset_value.length);
		System.arraycopy( new_length_value, 0, fileBytes, first_length_index + 1, new_length_value.length);
		System.arraycopy( new_length_value, 0, fileBytes, second_length_index+ 1, new_length_value.length);
		
		System.out.println();
		return ret;
	}
	
	/**
	 * @param pairVal
	 */
	private void encryptionTag(PairValue pairVal) {
		byte key = 0x5a;	// �׽�Ʈ�� ���� 0x5a �� XOR ���� ���Ѵ�. 
		int len = pairVal.first + pairVal.second;
		
		for( int i = pairVal.first; i < len; i++ ) {
			fileBytes[i] ^= key;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int process() {
		int ret = 0;
				
		List<TagValue> tag_values = searchTags();		
		for( TagValue val : tag_values ) {			
			/*
			int ofs = val.start + start_tag.length;
			int len = val.end - ofs;
			System.out.println( String.format( "Tag => s:%-8d(0x%-5x), e:%-8d(0x%-5x), ofs:%d(0x%-5x), l:%-3d", val.start, val.start, val.end, val.end, ofs, ofs, len ));
			*/
			
			PairValue pairVal = placeHolder( val );
			if( pairVal == null ) continue;
			
			encryptionTag( pairVal );
			ret++;
		}
		
		return ret;
	}
}

class PairValue {
	public PairValue(int f, int s){ first=f; second=s; }
	public int first;
	public int second;
}
class TagValue {
	public TagValue(int s, int e){ start=s; end=e; }
	public int start;
	public int end;
};


/**
 * @author purehero2
 *
 */
class ByteUtils {
	/**
	 * source byte [] ���� data byte [] �� ���� index �� ��ȯ�� �ִ� �Լ� �Դϴ�. <br>
	 * 
	 * 
	 * @param source
	 * @param data
	 * @return �˻� ���нÿ� -1 ��ȯ
	 */
	public static int searchBytes( final byte [] source, final byte [] data ) {
		return searchBytes( source, 0, source.length, data );
	}
	
	public static int searchBytes( final byte [] source, int start_ofs, int end_ofs, final byte [] data ) {
		int data_len = data.length;
		int data_last_index = data_len - 1;
		
		int source_len = end_ofs - data_len;
		boolean bFound = false;
		
		for( int i = start_ofs; i < source_len; i++ ) {
			if( source[i] == data[0] && source[i+data_last_index] == data[data_last_index]) {
			//if( source[i] == data[0] ) {
				bFound = true;
				for( int j = 0; j < data_len; j++ ) {
					if( source[i+j] != data[j] ) {
						bFound = false;
						break;
					}
				}
				
				if( bFound ) return i;
			}
		}
		return -1;
	}
	
	/**
	 * int���� byte�迭�� �ٲ�<br>
	 * @param integer
	 * @param order
	 * @return
	 */
	public static byte[] intTobyte(int integer, ByteOrder order) {
 
		ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE/8);
		buff.order(order);
 
		// �μ��� �Ѿ�� integer�� putInt�μ���
		buff.putInt(integer);
 
		//System.out.println("intTobyte : " + buff);
		return buff.array();
	}
	
	// byte[] to hex
	public static String byteArrayToHex(byte[] ba) {
	    if (ba == null || ba.length == 0) {
	        return null;
	    }
	 
	    StringBuffer sb = new StringBuffer(ba.length * 2);
	    String hexNumber;
	    for (int x = 0; x < ba.length; x++) {
	        hexNumber = "0" + Integer.toHexString(0xff & ba[x]);
	 
	        sb.append(hexNumber.substring(hexNumber.length() - 2));
	    }
	    return sb.toString();
	} 
}


/**
 * DLL ���Ͽ��� AppSealing signature �� index ���� �����մϴ�. 
 * 
 * @param bytes
 * @return
 */
class AppSealingSignature {
	final boolean bLogEnable = false;
	final byte [] prefix_tag = { 0x48, 0x00, 0x4B, 0x00  };	// signature �� ��/�ڿ� �ٴ� tag ��
	final String signature_string = "............................................................................................................................................................................................................................................................";
	
	final byte [] source_bytes;
	public AppSealingSignature( byte [] _bytes ) {
		source_bytes = _bytes;
	}
			
	/**
	 * @param index
	 * @return
	 */
	protected boolean BytesEqualsPreFixTags( int index ) {
		if( bLogEnable ) {
			System.out.println( String.format( "TID : index: 0x%x", index ));
		}
		int len = prefix_tag.length;
		for( int i = 0; i < len; i++ ) {
			if( source_bytes[index+i] != prefix_tag[i]) {
				return false;
			}
		}
		
		if( bLogEnable ) {
			System.out.println( String.format( "FOUND index: 0x%x",index ));
		}
		return true;
	}
	
	/**
	 * @return
	 */
	public int findAppSealingSignature() {
		int len = source_bytes.length;
		if( len < 1024 + prefix_tag.length * 2 ) return -9;		// (-9)�Է� bytes ���� signature ���̺��� ����
		
		int source_bytes_len = source_bytes.length;
		int signature_len = signature_string.length() * 2;		// signature �� ���ڿ��̾ *2���� byte�� ��ȯ�ȴ�.
		int find_jump_val = signature_len - prefix_tag.length;
		
		int end_index = source_bytes_len - signature_len;
		
		for( int i = prefix_tag.length; i < end_index; i+= find_jump_val ) {	// start_index �� �׻� 0���� Ŀ�� �մϴ�. 
			if(( source_bytes[i] == 0x00 && source_bytes[i-1] == 0x2E && source_bytes[i+1] == 0x2E ) ||
			   ( source_bytes[i] == 0x2E && source_bytes[i-1] == 0x00 && source_bytes[i+1] == 0x00 )) {
				// signature �ǽ� �ڵ� �߰�
				// ���� �Ĺ����� signature_len ���� prefix_tag ���� �ִ��� Ȯ�� �մϴ�.
				int j_len = i + signature_len;
				 
				for( int j = i; j < j_len; j++ ) {
					if( !BytesEqualsPreFixTags( j )) continue;
					// �ڿ� Tag�� ã������
					// Signature string �տ� Tag�� �ִ����� Ȯ���Ѵ�.
					if( !BytesEqualsPreFixTags( j - signature_len - prefix_tag.length)) continue;

					return j - signature_len - prefix_tag.length;						
				}
			}
		}
		
		return -1;												
	}
}

/**
 * @author purehero2
 *
 */
class TimeChecker {
	private long startTime;
	private long endTime;
	
	public void setStartTime(){ startTime = System.currentTimeMillis(); }
	public void setEndTime(){ endTime = System.currentTimeMillis(); }
	public long calcTerm(){ return endTime - startTime; }
}