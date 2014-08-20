package marki.at.Client;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import at.marki.Client.R;

class FragmentPrefs extends PreferenceFragment {

	public static final String TAG = "at.marki.FragmentPrefs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
