package mohammad.adib.roundr;

/**
 * Copyright 2013 Mohammad Adib
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import wei.mark.standout.StandOutWindow;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

public class Main extends Activity {

	/**
	 * Main Activity that launches the 4 floating windows (corners)
	 * 
	 * @author Mohammad Adib <m.a.adib96@gmail.com>
	 * 
	 *         Contributors: Mark Wei
	 * 
	 */

	ProgressDialog progress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!BootReceiver.boot_up || Corner.running) {
			Intent i = new Intent(this, Settings.class);
			startActivity(i);
		}
		StandOutWindow.show(this, Corner.class, 0);
		StandOutWindow.show(this, Corner.class, 1);
		StandOutWindow.show(this, Corner.class, 2);
		StandOutWindow.show(this, Corner.class, 3);
		finish();
	}

}
