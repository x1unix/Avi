package com.x1unix.avi.video;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


import com.x1unix.avi.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SelectVideoDialog extends DialogFragment {
    private String[] seasons;
    private String[] episodes;
    private int episodeKey;
    private int seasonKey;

    private int newEpisodeKey;
    private int newSeasonKey;

    private Spinner seasonsSpinner;
    private Spinner episodesSpinner;
    private Bundle bundle;
    private boolean seasonChanged = false;


    public SelectVideoDialog() {}

    private static final String TAG = "SelectVideoDialog";
    public static final String ARG_SEASONS = "seasons";
    public static final String ARG_EPISODES = "episodes";
    public static final String ARG_KEY_SEASON = "season";
    public static final String ARG_KEY_EPISODE = "episode";

    public static SelectVideoDialog newInstance(ArrayList<String> seasons,
                                                ArrayList<String> episodes,
                                                int selectedEpisodeKey,
                                                int selectedSeasonKey) {
        final SelectVideoDialog dial = new SelectVideoDialog();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_EPISODES, episodes);
        args.putStringArrayList(ARG_SEASONS, seasons);
        args.putInt(ARG_KEY_EPISODE, selectedEpisodeKey);
        args.putInt(ARG_KEY_SEASON, selectedSeasonKey);

        dial.setArguments(args);

        return dial;
    }


    public static void setupSpinner(Activity context, Spinner s, ArrayList<String> strings, int pos, String label) {
        String[] newStrings = new String[strings.size()];

        for (int c = 0; c < strings.size(); c++) {
            newStrings[c] = String.format("%s %s", label, strings.get(c));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item,
                newStrings);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setSelection(pos);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        // Setup spinner
        View view = inflater.inflate(R.layout.dialog_select_video, null);
        seasonsSpinner = (Spinner) view.findViewById(R.id.seasons_spinner);
        episodesSpinner = (Spinner) view.findViewById(R.id.episodes_spinner);

        Bundle bundle = getArguments();
        if (bundle != null) {
            try {
                ArrayList<String> eps = bundle.getStringArrayList(ARG_EPISODES);
                ArrayList<String> seas = bundle.getStringArrayList(ARG_SEASONS);
                newEpisodeKey = bundle.getInt(ARG_KEY_EPISODE, 0);
                newSeasonKey = bundle.getInt(ARG_KEY_SEASON, 0);

                episodeKey = bundle.getInt(ARG_KEY_EPISODE, 0);
                seasonKey = bundle.getInt(ARG_KEY_SEASON, 0);


                Activity a = getActivity();
                String epLabel = getResources().getString(R.string.episode_cap);
                String seaLabel = getResources().getString(R.string.season_cap);

                setupSpinner(a, seasonsSpinner, seas, newSeasonKey, seaLabel);
                setupSpinner(a, episodesSpinner, eps, newEpisodeKey, epLabel);

                addSpinnerListeners();
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }

        builder.setView(view);
        builder.setTitle("Выбор сезона и серии")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SelectVideoDialog.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void addSpinnerListeners() {
        seasonsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                seasonChanged = (position != seasonKey);
                episodesSpinner.setVisibility(seasonChanged ? View.GONE : View.VISIBLE);

                newSeasonKey = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        episodesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                newEpisodeKey = position;
                newSeasonKey = seasonKey;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

}
