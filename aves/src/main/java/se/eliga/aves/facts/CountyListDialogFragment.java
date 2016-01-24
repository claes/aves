package se.eliga.aves.facts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import se.eliga.aves.BirdApp;
import se.eliga.aves.Constants;
import se.eliga.aves.R;
import se.eliga.aves.model.County;
import se.eliga.aves.model.DatabaseHandler;

/**
 * Created by vagrant on 1/10/16.
 */
public class CountyListDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DatabaseHandler databaseHandler = ((BirdApp) getActivity().getApplication())
                .getDbHandler(getActivity());
        List<County> counties = databaseHandler.getCounties();

        Map<String, String> countyMap = new LinkedHashMap<String, String>();
        for (County county : counties) {
            countyMap.put(county.getId(), county.getName());
        }

        final String[] countyIds = countyMap.keySet().toArray(new String[]{});
        final String[] countyNames = countyMap.values().toArray(new String[]{});

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Välj län")
                .setItems(countyNames, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences settings = getActivity().getSharedPreferences(Constants.BIRD_APP_SETTINGS, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(Constants.SELECTED_COUNTY_ID, countyIds[which]);
                        editor.putString(Constants.SELECTED_COUNTY_NAME, countyNames[which]);
                        editor.commit();
                    }
                });
        return builder.create();
    }
}
