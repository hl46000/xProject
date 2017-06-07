using UnityEngine;
using System.Collections;
using AntiSpeedHack4Android;

public class SampleScript : MonoBehaviour {

    Vector3 speed = new Vector3( 0, 100, 0 );

	private bool bUiDisplay = false;

	void Start () {
		AntiSpeedHack.detectListener = new OnDetectListener(OnDetected);
	}
	
	// Update is called once per frame
	void Update () {
        transform.Rotate(speed * Time.deltaTime);
	}

	void OnGUI() {
		if (bUiDisplay)
		{
			int box_width = Screen.width / 3;
			int box_height = Screen.height / 3;
			GUI.BeginGroup(new Rect((Screen.width - box_width) / 2, (Screen.height - box_height) / 2, box_width, box_height));
			GUI.Box(new Rect(0, 0, box_width, box_height), "");

			GUIStyle btnFont = GUI.skin.GetStyle("button");
			btnFont.fontSize = 32;
			int btn_height = 50;
			if (GUI.Button(new Rect(10, box_height - btn_height - 5, box_width - 20, btn_height), "OK", btnFont)) 
			{
				Application.Quit(); 
			}

			GUIStyle textFont = GUI.skin.GetStyle("label");
			textFont.fontSize = 32;
			int lb_width = box_width - 20;
			int lb_height = box_height - btn_height - 20;
			GUI.Label(new Rect(10, 10, lb_width, lb_height), "This app shuts down because time speed is incorrect or attached debugger", textFont);
			GUI.EndGroup();
		}
	}

	void OnDetected() {
		bUiDisplay = true;
	}
}
