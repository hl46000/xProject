package com.covault.appsec;

import android.app.Application;

public class CovaultApplication extends Application {
	static {
		System.loadLibrary( "covault-appsec" );
	}
}
