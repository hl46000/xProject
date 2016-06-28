package com.inka.smc;

public interface SmcTagValue {
	/**
	 * Tag name
	 * 
	 * @return
	 */
	public String GetName();
	
	/**
	 * NOP opcode
	 * 
	 * @return
	 */
	public byte [] getNopBytes(); 
	
	
	/**
	 * start tag bytes
	 * 
	 * @return
	 */
	public byte [] GetStartTag();
	
	
	/**
	 * start tag 에서 사용자정의 값 5개의 index 배열을 반환 한다. 
	 * 
	 * @return
	 */
	public int [] GetStartTagValueIndexes();
	
	/**
	 * end tag bytes
	 * 
	 * @return
	 */
	public byte [] GetEndTag();
	
	/**
	 * dummy tag bytes
	 * 
	 * @return
	 */
	public byte [] GetDummyTag();
	
	
	/**
	 * SMC Start tag size 반환 함수를 찾기 위한 Tag 값
	 * 
	 * @return
	 */
	public byte [] GetStartTagSizeDefineTag();
	
	/**
	 * SMC End tag size 반환 함수를 찾기 위한 Tag 값
	 * 
	 * @return
	 */
	public byte [] GetEndTagSizeDefineTag();
	
	/**
	 * SMC Tag에 value 값의 indexes 을 반환해 주는 함수를 찾기 위한 Tag
	 * 
	 * @return
	 */
	public byte [] GetTagValuesIndexDefineTag();
}
