package com.inka.smc;

public class SmcTagValueArmeabiV7a implements SmcTagValue {
	byte [] tag_values_index_define_tag_bytes = null;
	
	@Override
	public String GetName() {
		return "armeabi-v7a";
	}

	@Override
	public byte[] GetStartTag() {
		return new byte[] { 
			(byte)0x01, (byte)0xB4,				// pop ACC
			(byte)0x4F, (byte)0xF0, (byte)0x6E, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x78, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x82, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x8C, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x96, (byte)0x00,
			(byte)0x01, (byte)0xBC				// push ACC
		};
	}

	@Override
	public int[] GetStartTagValueIndexes() {
		return new int [] { 4, 8, 12, 16, 20 };
	}
	
	@Override
	public byte[] GetEndTag() {
		return new byte[] { 
			(byte)0x01, (byte)0xB4,				// pop ACC
			(byte)0x4F, (byte)0xF0, (byte)0x96, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x8C, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x82, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x78, (byte)0x00,
			(byte)0x4F, (byte)0xF0,	(byte)0x6E, (byte)0x00,
			(byte)0x01, (byte)0xBC				// push ACC
		};
	}

	@Override
	public byte[] GetDummyTag() {
		return new byte[] { 
			(byte)0x01, (byte)0xB4,				// pop ACC
			(byte)0x4F, (byte)0xF0, (byte)0x6F, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x7A, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x85, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x90, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x9B, (byte)0x00,
			(byte)0x01, (byte)0xBC				// push ACC
		};
	}
	
	@Override
	public byte[] getNopBytes() {
		return new byte[] { (byte)0x00, (byte)0xBF };
	}

	@Override
	public byte[] GetStartTagSizeDefineTag() {
		return new byte[] { 
			(byte)0x01, (byte)0xB4,				// pop ACC
			(byte)0x4F, (byte)0xF0, (byte)0x0A, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x14, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x1E, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x28, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x32, (byte)0x00,
			(byte)0x01, (byte)0xBC				// push ACC
		};
	}

	@Override
	public byte[] GetEndTagSizeDefineTag() {
		return new byte[] { 
			(byte)0x01, (byte)0xB4,				// pop ACC
			(byte)0x4F, (byte)0xF0, (byte)0x32, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x28, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x1E, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x14, (byte)0x00,
			(byte)0x4F, (byte)0xF0, (byte)0x0A, (byte)0x00,
			(byte)0x01, (byte)0xBC				// push ACC
		};
	}

	@Override
	public byte[] GetTagValuesIndexDefineTag() {
		if( tag_values_index_define_tag_bytes == null ) {
			tag_values_index_define_tag_bytes = new byte[] { 
				(byte)0x01, (byte)0xB4,				// pop ACC
				(byte)0x4F, (byte)0xF0, (byte)0x14, (byte)0x00,
				(byte)0x4F, (byte)0xF0, (byte)0x1E, (byte)0x00,
				(byte)0x4F, (byte)0xF0, (byte)0x28, (byte)0x00,
				(byte)0x4F, (byte)0xF0, (byte)0x32, (byte)0x00,
				(byte)0x4F, (byte)0xF0, (byte)0x3C, (byte)0x00,
				(byte)0x01, (byte)0xBC				// push ACC
			};
		}
		return tag_values_index_define_tag_bytes;
	}
	
}
