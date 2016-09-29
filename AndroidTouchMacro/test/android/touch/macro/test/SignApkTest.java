package android.touch.macro.test;

import java.io.File;

import org.junit.Test;

import com.android.signapk.SignApk;

public class SignApkTest {

	@Test
	public void test() {
		File apkfile = new File("e:\\workTemp\\N300_NativeApp.apk");
		apkfile = new File("e:\\workTemp\\CaoCao_adr_dev_osx86_kor_29797.apk.sealed.apk");
		
		File outfile = new File( apkfile.getParentFile(), apkfile.getName().replace( ".apk", "_signed.apk"));
		
		SignApk signApk = new SignApk();
		signApk.sign( apkfile, outfile );
	}

}
