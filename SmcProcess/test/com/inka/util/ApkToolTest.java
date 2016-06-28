package com.inka.util;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;


public class ApkToolTest {

	@Test
	public void test() {
		File testApkFile = new File("D:\\workTemp\\x\\AndroidSmcTest\\bin\\AndroidSmcTest.apk");
		
		ApkTool apktool = new ApkTool();
		File decodedFolder = apktool.decode( testApkFile );
		
		Assert.assertNotNull( decodedFolder );
		Assert.assertTrue( decodedFolder.exists() );
		
		File buildApkFile = apktool.build( decodedFolder );
		try {
			FileUtils.deleteDirectory( decodedFolder );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Assert.assertNotNull( buildApkFile );
		Assert.assertTrue( buildApkFile.exists() );
	}
}
