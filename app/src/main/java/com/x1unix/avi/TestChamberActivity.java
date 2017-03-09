package com.x1unix.avi;

import android.content.DialogInterface;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.x1unix.moonwalker.Listener;
import com.x1unix.moonwalker.Moonwalker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class TestChamberActivity extends AppCompatActivity {

    @BindView(R.id.testKpId) EditText kpIdTextBox;
    @BindView(R.id.testMoonwalkBtn) Button testMovieInfoBtn;

    private Moonwalker moonwalker = new Moonwalker("http://avi.x1unix.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testchamber);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.testMoonwalkBtn)
    public void testMoonwalkParser() {
        moonwalker.getMovieByKinopoiskId(kpIdTextBox.getText().toString(),
            new Listener() {
                @Override
                public void onSuccess(String result) {
                    pop(result);
                }

                @Override
                public void onError(Exception ex) {
                    pop(ex.getMessage());
                }
            }
        );
    }

    private void pop(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(TestChamberActivity.this);
                builder
                        .setTitle("Result:")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }
}
