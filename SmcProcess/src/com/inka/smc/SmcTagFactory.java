package com.inka.smc;

public class SmcTagFactory {
	public SmcTagValue getArmeabiValues() {
		return new SmcTagValueArmeabi();
	}
	
	public SmcTagValue getX86Values() {
		return new SmcTagValueX86();
	}
	
	public SmcTagValue [] getValues() {
		return new SmcTagValue []{ new SmcTagValueArmeabi(), new SmcTagValueArmeabiV7a(), new SmcTagValueX86() };
	}
}
