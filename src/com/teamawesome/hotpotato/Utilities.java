package com.teamawesome.hotpotato;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public final class Utilities {

	// give your server registration url here
	static final String SERVER_URL = "http://95.138.182.175/hotpotato/registerclient.php";

	// Google project id
	static final String SENDER_ID = "181203878661";

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "GCM";

	//static final String DISPLAY_MESSAGE_ACTION = "com.androidhive.pushnotifications.DISPLAY_MESSAGE";

	static final String EXTRA_MESSAGE = "message";

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	static void displayMessage(Context context, String message) {
		//Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		//intent.putExtra(EXTRA_MESSAGE, message);
		//context.sendBroadcast(intent);
		Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}
}
