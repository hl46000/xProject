package com.inka.util;
import java.io.File;

import org.junit.Assert;
import org.junit.Test;


public class ApkSignTest {

	@Test
	public void test() {
		File testApkFile = new File("D:\\workTemp\\x\\AndroidSmcTest\\bin\\AndroidSmcTest-release-unsigned.apk");
		
		ApkSign apkSign = new ApkSign();
		File signedApkFile = apkSign.sign( testApkFile );
		
		Assert.assertNotNull( signedApkFile );
		Assert.assertTrue( signedApkFile.exists() );
	}
}
