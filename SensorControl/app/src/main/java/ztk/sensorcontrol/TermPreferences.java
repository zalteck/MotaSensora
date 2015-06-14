package ztk.sensorcontrol;

/**
 * Created by ztk on 4/6/15.
 */
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class TermPreferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}
