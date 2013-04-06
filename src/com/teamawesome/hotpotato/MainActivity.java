package com.teamawesome.hotpotato;

import static com.teamawesome.hotpotato.Utilities.EXTRA_MESSAGE;
import static com.teamawesome.hotpotato.Utilities.SENDER_ID;

import java.util.ArrayList;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LauncherActivity.ListItem;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.teamawesome.hotpotato.list.ExpandListAdapter;
import com.teamawesome.hotpotato.list.ExpandListChild;
import com.teamawesome.hotpotato.list.ExpandListGroup;

public class MainActivity extends Activity {

	TextView lblMessage;
	AsyncTask<Void, Void, Void> mRegisterTask;
	ConnectionDetector cd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		cd = new ConnectionDetector(getApplicationContext());

		setupList();
		
		if (!cd.isConnectingToInternet()) {
			return;
		}

		GCMRegistrar.checkDevice(this);

		/*
		 * Make sure the manifest was properly set - comment out this line while
		 * developing the app, then uncomment it when it's ready.
		 */
		// GCMRegistrar.checkManifest(this);

		// registerReceiver(mHandleMessageReceiver, new IntentFilter(
		// DISPLAY_MESSAGE_ACTION));

		/* Get GCM registration id. */
		final String regId = GCMRegistrar.getRegistrationId(this);

		/* Check if we already have a reg id. */
		if (regId.equals("")) {
			/* Registration is not present, register now with GCM */
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			/* Device is already registered on GCM */
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				Toast.makeText(getApplicationContext(),
						"Already registered with GCM", Toast.LENGTH_LONG)
						.show();
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						// Register on our server
						// On server creates a new user
						ServerUtilities.register(context, regId);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
		/*
		 * AudioManager audio = (AudioManager) getApplicationContext()
		 * .getSystemService(Context.AUDIO_SERVICE); int max =
		 * audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		 * audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		 * audio.setStreamVolume(AudioManager.STREAM_MUSIC, max,
		 * AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE); MediaPlayer player =
		 * MediaPlayer.create(this, Settings.System.DEFAULT_NOTIFICATION_URI);
		 * player.setLooping(true); player.start(); Vibrator myVib = (Vibrator)
		 * this.getSystemService(VIBRATOR_SERVICE);
		 * myVib.vibrate(Long.MAX_VALUE);
		 */
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/* Overwrite the volume settings to set max volume. */
			AudioManager audio = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			int max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			audio.setStreamVolume(AudioManager.STREAM_MUSIC, max,
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			/* Play ring tone. */
			MediaPlayer player = MediaPlayer.create(getApplicationContext(),
					Settings.System.DEFAULT_NOTIFICATION_URI);
			player.setLooping(true);
			player.start();

			Vibrator myVib = (Vibrator) getApplicationContext()
					.getSystemService(VIBRATOR_SERVICE);
			myVib.vibrate(10000);

			/* Showing received message. */
			lblMessage.append(newMessage + "\n");
			Toast.makeText(getApplicationContext(),
					"New Message: " + newMessage, Toast.LENGTH_LONG).show();

			/* Releasing wake lock. */
			WakeLocker.release();
		}
	};

	@Override
	protected void onDestroy() {
		Vibrator myVib = (Vibrator) getApplicationContext().getSystemService(
				VIBRATOR_SERVICE);
		myVib.cancel();
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void setupList() {
		ExpandableListView ExpandList = (ExpandableListView) findViewById(R.id.ExpList);
		ArrayList<ExpandListGroup> ExpListItems = SetStandardGroups();
		ExpandListAdapter ExpAdapter = new ExpandListAdapter(MainActivity.this,
				ExpListItems);
		ExpandList.setAdapter(ExpAdapter);
		ExpandList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}

	public ArrayList<ExpandListGroup> SetStandardGroups() {
		ArrayList<ExpandListGroup> list = new ArrayList<ExpandListGroup>();
		ArrayList<ExpandListChild> list2 = new ArrayList<ExpandListChild>();
		ExpandListGroup gru1 = new ExpandListGroup();
		gru1.setName("Cities");
		String[] cities = {"London", "Paris", "Birmingham", "Warsaw", "Berlin", "New York", "Manchester"};
		for (String city : cities) {
			ExpandListChild ch = new ExpandListChild();
			ch.setName(city);
			ch.setTag(null);
			list2.add(ch);
		}
		gru1.setItems(list2);
		list2 = new ArrayList<ExpandListChild>();
		ExpandListGroup gru2 = new ExpandListGroup();
		gru2.setName("Universities");
		String[] unis = {"Imperial", "UCL", "Newcastle", "Leeds", "Manchester", "Oxford", "City", "Some Art One"};
		for (String uni : unis) {
			ExpandListChild ch = new ExpandListChild();
			ch.setName(uni);
			ch.setTag(null);
			list2.add(ch);
		}
		gru2.setItems(list2);
		list2 = new ArrayList<ExpandListChild>();
		ExpandListGroup gru3 = new ExpandListGroup();
		gru3.setName("Transport");
		String[] trans = {"Tube", "West Midlands", "Virgin Trains", "London Buses", "Heathrow", "Gatwick", "Luton", "New York"};
		for (String tran : trans) {
			ExpandListChild ch = new ExpandListChild();
			ch.setName(tran);
			ch.setTag(null);
			list2.add(ch);
		}
		gru3.setItems(list2);
		list2 = new ArrayList<ExpandListChild>();
		list.add(gru1);
		list.add(gru2);
		list.add(gru3);
		return list;
	}

}
