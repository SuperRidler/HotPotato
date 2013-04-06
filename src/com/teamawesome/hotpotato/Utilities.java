package com.teamawesome.hotpotato;

import android.content.Context;

public class Utilities {
	static final String SERVER_URL = "http://95.138.182.175/hotpotato/registerclient.php"; 
	 
    static final String SENDER_ID = "181203878661"; 

    static final String TAG = "GCM";
 
    static final String DISPLAY_MESSAGE_ACTION =
            "com.teamawesome.hotpotato.DISPLAY_MESSAGE";
 
    static final String EXTRA_MESSAGE = "message";
 
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        //Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        //intent.putExtra(EXTRA_MESSAGE, message);
        //context.sendBroadcast(intent);
    }
}
