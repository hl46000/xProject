package com.inka.util;
import java.io.File;

import org.junit.Assert;
import org.junit.Test;


public class ApkBuildTest {

	@Test
	public void test() {
		File testProjectFolder = new File("D:\\workTemp\\x\\AndroidSmcTest");
		
		ApkBuild apkBuild = new ApkBuild();
		File newApkFile = apkBuild.build( testProjectFolder );
		
		Assert.assertNotNull( newApkFile );
		Assert.assertTrue( newApkFile.exists() );
	}
}
