package com.inka.smc;

public class SmcTagValueArmeabi implements SmcTagValue {
	byte [] start_tag_bytes = null;
	byte [] end_tag_bytes = null;
	byte [] dummy_tag_bytes = null;
	
	byte [] start_tag_size_define_tag_bytes = null;
	byte [] end_tag_size_define_tag_bytes = null;
	byte [] tag_values_index_define_tag_bytes = null;
	
	@Override
	public String GetName() {
		return "armeabi";
	}

	
	
	@Override
	public byte[] GetStartTag() {
		if( start_tag_bytes == null ) {
			start_tag_bytes = new byte[] { 
				(byte)0x01, (byte)0xB4,				// pop ACC
				(byte)0x6E, (byte)0x20,  
				(byte)0x78, (byte)0x20, 
				(byte)0x82, (byte)0x20, 
				(byte)0x8C, (byte)0x20, 
				(byte)0x96, (byte)0x20,  
				(byte)0x01, (byte)0xBC				// push ACC
			}; 
		}
		return start_tag_bytes;
	}

	@Override
	public int[] GetStartTagValueIndexes() {
		return new int [] { 2, 4, 6, 8, 10 };
	}
	
	@Override
	public byte[] GetEndTag() {
		if( end_tag_bytes == null ) {
			end_tag_bytes = new byte[] { 
				(byte)0x01, (byte)0xB4,				// pop ACC
				(byte)0x96, (byte)0x20,  
				(byte)0x8C, (byte)0x20, 
				(byte)0x82, (byte)0x20, 
				(byte)0x78, (byte)0x20, 
				(byte)0x6E, (byte)0x20,			 
				(byte)0x01, (byte)0xBC				// push ACC
			}; 
		}
		return end_tag_bytes;
	}

	@Override
	public byte[] GetDummyTag() {
		if( dummy_tag_bytes == null ) {
			dummy_tag_bytes = new byte[] { 
				(byte)0x01, (byte)0xB4,				// pop ACC
				(byte)0x6F, (byte)0x20,  
				(byte)0x7A, (byte)0x20, 
				(byte)0x85, (byte)0x20, 
				(byte)0x90, (byte)0x20, 
				(byte)0x9B, (byte)0x20,  
				(byte)0x01, (byte)0xBC				// push ACC
			}; 
		}
		return dummy_tag_bytes;
	}
	
	@Override
	public byte[] getNopBytes() {
		return new byte[] { (byte)0xC0, 0x46 };
	}



	@Override
	public byte[] GetStartTagSizeDefineTag() {
		if( start_tag_size_define_tag_bytes == null ) {
			start_tag_size_define_tag_bytes = new byte[] { 
				(byte)0x01, (byte)0xB4,				// pop ACC
				(byte)0x0A, (byte)0x20,  
				(byte)0x14, (byte)0x20, 
				(byte)0x1E, (byte)0x20, 
				(byte)0x28, (byte)0x20, 
				(byte)0x32, (byte)0x20,			 
				(byte)0x01, (byte)0xBC				// push ACC
			}; 
		}
		return start_tag_size_define_tag_bytes;
	}



	@Override
	public byte[] GetEndTagSizeDefineTag() {
		if( end_tag_size_define_tag_bytes == null ) {
			end_tag_size_define_tag_bytes = new byte[] { 
				(byte)0x01, (byte)0xB4,				// pop ACC
				(byte)0x32, (byte)0x20,  
				(byte)0x28, (byte)0x20, 
				(byte)0x1E, (byte)0x20, 
				(byte)0x14, (byte)0x20, 
				(byte)0x0A, (byte)0x20,			 
				(byte)0x01, (byte)0xBC				// push ACC
			}; 
		}
		return end_tag_size_define_tag_bytes;
	}



	@Override
	public byte[] GetTagValuesIndexDefineTag() {
		if( tag_values_index_define_tag_bytes == null ) {
			tag_values_index_define_tag_bytes = new byte[] { 
				(byte)0x01, (byte)0xB4,				// pop ACC
				(byte)0x14, (byte)0x20, 
				(byte)0x1E, (byte)0x20, 
				(byte)0x28, (byte)0x20, 
				(byte)0x32, (byte)0x20,
				(byte)0x3C, (byte)0x20,
				(byte)0x01, (byte)0xBC				// push ACC
			}; 
		}
		return tag_values_index_define_tag_bytes;
	}
}
