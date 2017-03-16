package com.x1unix.avi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import butterknife.BindView;
import butterknife.OnClick;

public class PlayerActivity extends AppCompatActivity {

    // Root view components
    @BindView(R.id.player) SimpleExoPlayerView player;
    @BindView(R.id.player_loading) ProgressBar preloader;
    @BindView(R.id.player_ui) RelativeLayout ui;

    // First header elements
    @BindView(R.id.btn_back) ImageView btnBack;
    @BindView(R.id.player_title) TextView title;
    @BindView(R.id.player_subtitle) TextView subtitle;

    // Menu
    @BindView(R.id.btn_quality) ImageView btnQuality;
    @BindView(R.id.btn_share) ImageView btnShare;
    @BindView(R.id.btn_select) ImageView btnSelect;

    // Player controls
    @BindView(R.id.player_play) ImageView btnPlay;
    @BindView(R.id.player_pause) ImageView btnPause;

    // Bottom controls
    @BindView(R.id.time_current) TextView timeCurrentLabel;
    @BindView(R.id.time_total) TextView timeTotalLabel;
    @BindView(R.id.player_seekbar) SeekBar seekBar;

    String kpId;

    public static final String ARG_KPID = "kpId";
    public static final String ARG_TITLE = "title";
    public static final String ARG_DESCRIPTION = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent i = getIntent();

        try {
            initializeActivity(i);
        } catch (Exception ex) {
            Toast.makeText(this, "Failed to start player, please try again later", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private void initializeActivity(Intent i) {
        title.setText(i.getStringExtra(ARG_TITLE));

    }

    @OnClick(R.id.btn_back)
    public void goBack() {
        finish();
    }


}
