package marki.at.Client.download;

import android.content.Context;
import android.os.AsyncTask;

import com.github.kevinsawicki.http.HttpRequest;
import com.squareup.otto.Bus;

import org.json.JSONException;
import org.json.JSONObject;

import marki.at.Client.events.FailedMessageDownloadEvent;
import marki.at.Client.events.newMessageEvent;
import marki.at.Client.utils.Data;
import marki.at.Client.utils.Message;
import marki.at.Client.utils.Settingshandler;

/**
 * Created by marki on 29.10.13.
 */
class GetNewDataAsyncTask extends AsyncTask<Void, Message, Message> {

	private final Bus bus;
	private final Context context;

	public GetNewDataAsyncTask(Bus bus, Context context) {
		this.bus = bus;
		this.context = context;
	}

	@Override
	protected Message doInBackground(Void... voids) {

		try {
			String url = Settingshandler.getDownloadAddress(context);
			HttpRequest request = HttpRequest.get(url).connectTimeout(30000).readTimeout(30000);

			if (request.ok()) {
				JSONObject jsonObject = new JSONObject(request.body());
				String messageString = jsonObject.getString("message");
				String messageId = jsonObject.getString("messageId");
				Message message = new Message(messageId, messageString, System.currentTimeMillis());
				Data.addMessage(context, message); //saves message on the database
				return message;
			} else if (request.notFound()) {
				return null;
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(Message message) {
		if (message != null) {
			bus.post(new newMessageEvent(message));
		} else {
			bus.post(new FailedMessageDownloadEvent());
		}
	}

}
