package marki.at.Client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import at.marki.Client.R;
import marki.at.Client.utils.Data;
import marki.at.Client.utils.Log;

public class ShowLogDialog extends DialogFragment {

	public static final String TAG = ShowLogDialog.class.getSimpleName();

	public static ShowLogDialog newInstance() {
		ShowLogDialog showLogDialog = new ShowLogDialog();
		return showLogDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Log");
        ListView listView = new ListView(getActivity());
        listView.setAdapter(new LogAdapter(getActivity()));
        builder.setView(listView);

		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

    public class LogAdapter extends BaseAdapter{

        private final LayoutInflater inflater;
        private Context context;
        private List<Log> logs;

        public class ViewHolder {
            public TextView logMessage;
        }

        public LogAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.context = context;
            this.logs = Data.getLogs(context);
        }

        @Override
        public int getCount() {
            if(logs != null){
                return logs.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            if(logs != null && i < logs.size()){
                return logs.get(i);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Log log = logs.get(i);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_lv_log, parent, false);
                holder = new ViewHolder();
                holder.logMessage = (TextView) convertView.findViewById(R.id.tv_item_log_message_date);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.logMessage.setText(log.getDate(log.date) + " " + log.logMessage);
            return convertView;
        }
    }

}
