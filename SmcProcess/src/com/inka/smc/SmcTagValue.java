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
	 * start tag ���� ��������� �� 5���� index �迭�� ��ȯ �Ѵ�. 
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
	 * SMC Start tag size ��ȯ �Լ��� ã�� ���� Tag ��
	 * 
	 * @return
	 */
	public byte [] GetStartTagSizeDefineTag();
	
	/**
	 * SMC End tag size ��ȯ �Լ��� ã�� ���� Tag ��
	 * 
	 * @return
	 */
	public byte [] GetEndTagSizeDefineTag();
	
	/**
	 * SMC Tag�� value ���� indexes �� ��ȯ�� �ִ� �Լ��� ã�� ���� Tag
	 * 
	 * @return
	 */
	public byte [] GetTagValuesIndexDefineTag();
}
