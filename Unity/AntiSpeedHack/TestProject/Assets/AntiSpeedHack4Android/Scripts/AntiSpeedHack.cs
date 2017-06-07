using System;
using System.IO;
using System.Runtime.InteropServices;
using System.Reflection;
using UnityEngine;
using System.Collections;

namespace AntiSpeedHack4Android {

	public delegate void OnDetectListener();

	public class AntiSpeedHack : MonoBehaviour {
	
		public static OnDetectListener detectListener;

	#if UNITY_ANDROID
	    [DllImport("ash")]	// plguin
	    private static extern int doNothing(string path);

        private FileSystemWatcher watcher;

		void Start () {
	        if (Application.platform == RuntimePlatform.Android) {
	            string path = Application.persistentDataPath + "/ash";
	            if (!Directory.Exists(path)) Directory.CreateDirectory(path);

	            CreateFileWatcher(path);
	            doNothing(path);
	        }
		}

		private void CreateFileWatcher(string path) {
            watcher = new FileSystemWatcher( path, "*" );
	        watcher.NotifyFilter = NotifyFilters.CreationTime | NotifyFilters.LastAccess | NotifyFilters.LastWrite | NotifyFilters.FileName | NotifyFilters.DirectoryName;
	        
	        // Add event handlers.
	        watcher.Changed += new FileSystemEventHandler(OnDetected);

            // Begin watching.
            watcher.EnableRaisingEvents = true;
	    }

	    // Define the event handlers.
	    private void OnDetected(object source, FileSystemEventArgs e) {
	        //watcher.EnableRaisingEvents = false;
	        //File.Delete(e.FullPath);
	        
			if (detectListener != null) {
				detectListener ();
			} 	        
	    }
	#endif
	}

}