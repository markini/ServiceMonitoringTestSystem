package marki.at.Client;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import at.marki.Client.BuildConfig;
import at.marki.Client.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import marki.at.Client.adapter.AdapterMainFragment;
import marki.at.Client.download.GetNewDataService;
import marki.at.Client.events.FailedMessageDownloadEvent;
import marki.at.Client.events.deleteMessagesEvent;
import marki.at.Client.events.newMessageEvent;
import marki.at.Client.gcm.GCMHandler;
import marki.at.Client.utils.Data;
import marki.at.Client.utils.DialogStarter;
import marki.at.Client.utils.Message;
import timber.log.Timber;

/**
 * Created by marki on 24.10.13.
 */
public class FragmentMain extends Fragment {

	public static final String TAG = "at.marki.FragmentMain";
	private static final int INITIAL_DELAY_MILLIS = 300;

	private AdapterMainFragment baseAdapter;
	private boolean isRefreshing;
	private ImageView tempAnimationImageView;

	private BroadcastReceiver sentReceiver;
	private BroadcastReceiver deliveringReceiver;

	@InjectView(R.id.list)
	DynamicListView messagesListView;

	@Inject
	Bus bus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		((ClientApplication) getActivity().getApplication()).inject(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		ButterKnife.inject(this, view);
		isRefreshing = false;

		setAdapter();

		 /* Enable swipe to dismiss */
		messagesListView.enableSimpleSwipeUndo();

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_main, menu);

//		if (isRefreshing) {
//			//if we're refreshing, show the animation
//			MenuItem item = menu.findItem(R.id.menu_get_message);
//			item.setActionView(R.layout.refresh_action_image);
//			ImageView iv = (ImageView) item.getActionView().findViewById(R.id.iv_refresh_action_image);
//			((AnimationDrawable) iv.getDrawable()).start();
//		}
	}

	private void setAdapter() {
		baseAdapter = new AdapterMainFragment(this);
		baseAdapter.setData();
		SimpleSwipeUndoAdapter simpleSwipeUndoAdapter = new SimpleSwipeUndoAdapter(baseAdapter, getActivity(), new MyOnDismissCallback(baseAdapter));
		AlphaInAnimationAdapter animAdapter = new AlphaInAnimationAdapter(simpleSwipeUndoAdapter);
		animAdapter.setAbsListView(messagesListView);
		assert animAdapter.getViewAnimator() != null;
		animAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
		messagesListView.setAdapter(animAdapter);
	}

	private void updateAdapter() {
		baseAdapter.notifyDataSetChanged();
	}

	//--------------------------------------------------------------------------------------------------
	//PAUSE - RESUME -----------------------------------------------------------------------------------

	@Override
	public void onPause() {
		if (BuildConfig.DEBUG) {
			Timber.d("onPause");
		}
		bus.unregister(this);
		stopRefreshAnimation();
		if (sentReceiver != null) {
			getActivity().unregisterReceiver(sentReceiver);
		}
		if (deliveringReceiver != null) {
			getActivity().unregisterReceiver(deliveringReceiver);
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		if (BuildConfig.DEBUG) {
			Timber.d("onResume");
		}
		bus.register(this);

		//------------------------------------

		if (MainActivity.newMessage) {
			MainActivity.newMessage = false;
			((BaseAdapter) messagesListView.getAdapter()).notifyDataSetChanged();
		}


		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	//CLICKLISTENER ---------------------------------------------------------------------
	//-----------------------------------------------------------------------------------

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			return false;
		}
		switch (item.getItemId()) {
			case android.R.id.home:
				break;
			case R.id.menu_settings:
				((MainActivity) getActivity()).startTransaction(R.id.fragment_frame, new FragmentPrefs(), FragmentPrefs.TAG, true);
				break;
			case R.id.menu_clear:
				DialogStarter.startDeleteDialog(getActivity());
				break;
			case R.id.menu_start_monitor:
				clickStartMonitoring();
				break;
			case R.id.menu_stop_monitor:
				clickStopMonitoring();
				break;
			case R.id.menu_create_mock_messages:
				Data.createMockMessages(getActivity());
				updateAdapter();
				break;
			case R.id.menu_get_message:
				if (isRefreshing) {
					return true;
				}
				startGetMessageService(item);
				break;
			case R.id.menu_register_gcm:
				GCMHandler.with(getActivity()).initGcm();
				break;
			case R.id.menu_log_dialog:
				DialogStarter.startLogDialog(getActivity());
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void startGetMessageService(MenuItem item) {
		if (isRefreshing) {
			return;
		}
		startRefreshAnimation(item);
		Intent intent = new Intent(getActivity(), GetNewDataService.class);
		getActivity().startService(intent);
	}

	private void clickStartMonitoring() {
		clickStopMonitoring();
		((ClientApplication) getActivity().getApplication()).startMonitoring();
		Toast.makeText(getActivity(), "Starting Monitoring", Toast.LENGTH_SHORT).show();
	}

	private void clickStopMonitoring() {
		ClientApplication.serverMonitor.stopMonitoring(getActivity());
		ClientApplication.connectivityMonitor.stopMonitoring(getActivity());
		Toast.makeText(getActivity(), "Stopping Monitoring", Toast.LENGTH_SHORT).show();
	}

	private void startRefreshAnimation(MenuItem item) {
		isRefreshing = true;
		RotateAnimation anim = new RotateAnimation(0f, 360f, 24f, 24f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(700);

		// Start animating the image
		//getMessageRefresh.setVisibility(View.VISIBLE);
		//getMessageRefresh.startAnimation(anim);

		if (item == null) {
			return;
		}

		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		tempAnimationImageView = (ImageView) inflater.inflate(R.layout.refresh_action_image, null);

		Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.refresh_rotate);
		rotation.setRepeatCount(Animation.INFINITE);
		tempAnimationImageView.startAnimation(rotation);
		item.setActionView(tempAnimationImageView);
	}

	private void stopRefreshAnimation() {
		isRefreshing = false;
//		if (getMessageRefresh != null) {
//			getMessageRefresh.setVisibility(View.INVISIBLE);
//			getMessageRefresh.setAnimation(null);
//		}
		if (tempAnimationImageView != null) {
			tempAnimationImageView.clearAnimation();
		}
		getActivity().invalidateOptionsMenu();
		isRefreshing = false;
	}

	private void registerWithSms() {

		PendingIntent sendingPendingIntent = registerSentReceiver();

		PendingIntent deliveringPendingIntent = registerDeliveringReceiver();

		String gcmId = GCMHandler.with(getActivity()).initGcm().returnRegistrationId();

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(getString(R.string.server_sms_number), null, "MTClient GCM:" + gcmId, sendingPendingIntent, deliveringPendingIntent);
	}

	private PendingIntent registerSentReceiver() {
		String sent = "SMS_SENT";
		PendingIntent sendingPendingIntent = PendingIntent.getBroadcast(getActivity(), 0,
				new Intent(sent), 0);

		sentReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
					case Activity.RESULT_OK:

						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

						break;
					case SmsManager.RESULT_ERROR_NO_SERVICE:

						break;
					case SmsManager.RESULT_ERROR_NULL_PDU:

						break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:

						break;
				}
			}
		};
		getActivity().registerReceiver(sentReceiver, new IntentFilter(sent));

		return sendingPendingIntent;
	}

	private PendingIntent registerDeliveringReceiver() {
		String delivered = "SMS_DELIVERED";
		PendingIntent deliveringPendingIntent = PendingIntent.getBroadcast(getActivity(), 0,
				new Intent(delivered), 0);

		deliveringReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
					case Activity.RESULT_OK:

						break;
					case Activity.RESULT_CANCELED:

						break;
				}
			}
		};
		getActivity().registerReceiver(deliveringReceiver, new IntentFilter(delivered));

		return deliveringPendingIntent;
	}

	//--------------------------------------------------------------------------------------------------
	//EVENTS -------------------------------------------------------------------------------------------

	@Subscribe
	public void onNewMessageEvent(newMessageEvent event) {
		if (BuildConfig.DEBUG) {
			Timber.d("onNewMessageEvent");
		}
		if (!Data.getMessages(getActivity()).contains(event.message)) {
			Data.getMessages(getActivity()).add(event.message);
			updateAdapter();
		} else {
			Toast.makeText(getActivity(), "No new message available", Toast.LENGTH_SHORT).show();
		}
		stopRefreshAnimation();
	}

	@Subscribe
	public void onFailedMessageEvent(FailedMessageDownloadEvent event) {
		Toast.makeText(getActivity(), "Message download failed", Toast.LENGTH_LONG).show();
		stopRefreshAnimation();
	}

	@Subscribe
	public void onDeleteAllMessages(deleteMessagesEvent event) {
		Data.getMessages(getActivity()).clear();
		Data.deleteAllMessages(getActivity());
		updateAdapter();
	}

	//--------------------------------------------------------------------------------------------------
	//SUB CLASSES --------------------------------------------------------------------------------------
	private class MyOnDismissCallback implements OnDismissCallback {
		private final ArrayAdapter<Message> mAdapter;

		MyOnDismissCallback(final ArrayAdapter<Message> adapter) {
			mAdapter = adapter;
		}

		@Override
		public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
			for (int position : reverseSortedPositions) {
				Data.deleteMessage(getActivity(), mAdapter.getItem(position));
				mAdapter.remove(position);
			}
		}
	}
}
