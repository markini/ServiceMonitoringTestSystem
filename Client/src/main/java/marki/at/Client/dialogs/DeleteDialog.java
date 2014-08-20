package marki.at.Client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import marki.at.Client.ClientApplication;
import marki.at.Client.events.deleteMessagesEvent;

public class DeleteDialog extends DialogFragment {

	public static final String TAG = "at.marki.DeleteDialog";

	@Inject
	Bus bus;

	public static DeleteDialog newInstance() {
		DeleteDialog deleteDialog = new DeleteDialog();
		return deleteDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		((ClientApplication) getActivity().getApplication()).inject(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Delete?");
		builder.setMessage("Delete all messages?");
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				bus.post(new deleteMessagesEvent());
				DeleteDialog.this.dismiss();
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				DeleteDialog.this.dismiss();
			}
		});

		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

}
