package marki.at.Client.adapter;

import android.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;

import at.marki.Client.R;
import marki.at.Client.utils.Data;
import marki.at.Client.utils.Message;

/**
 * Created by marki on 30.10.13.
 */
public class AdapterMainFragment extends ArrayAdapter<Message> implements UndoAdapter {

	private final LayoutInflater inflater;
	private Context context;

	public AdapterMainFragment(Fragment fragment) {
		Data.sortMessages(fragment.getActivity());
		inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = fragment.getActivity();
	}

	public void setData() {
		clear();
		for (Message message : Data.getMessages(context)) {
			add(message);
		}
		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public View getUndoView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.undo_row, parent, false);
		}
		return view;
	}

	@NonNull
	@Override
	public View getUndoClickView(@NonNull View view) {
		return view.findViewById(R.id.undo_row_undobutton);
	}

	public static class ViewHolder {
		public TextView message;
	}

	@Override
	public View getView(int i, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Message message = getItem(i);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_lv_main, parent, false);
			holder = new ViewHolder();
			holder.message = (TextView) convertView.findViewById(R.id.tv_item_lv_main_message);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.message.setText(message.message);
		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}
}
