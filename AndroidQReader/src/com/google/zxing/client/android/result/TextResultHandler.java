/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.result;

import android.app.Activity;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.purehero.qr.reader.R;

public final class TextResultHandler extends ResultHandler {
	public TextResultHandler(Activity activity, ParsedResult result, Result rawResult) {
		super(activity, result, rawResult);
	}

	private static final int[] buttons = {
			R.string.button_ok
	};
	
	@Override
	public int getButtonCount() {
		return buttons.length;
	}

	@Override
	public int getButtonText(int index) {
		return buttons[index];
	}

	@Override
	public int getDisplayTitle() {
		return R.string.result_text;
	}

	@Override
	public void handleButtonPress(int index) {		
	}
}
