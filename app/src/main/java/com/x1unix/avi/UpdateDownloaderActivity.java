package com.x1unix.avi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.x1unix.avi.helpers.DownloadFileFromURL;
import com.x1unix.avi.helpers.PermissionHelper;
import com.x1unix.avi.model.AviSemVersion;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class UpdateDownloaderActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private WebView webView;

    private TextView txUpdateProgress;
    private TextView txUpdateTag;
    private AviSemVersion updatePkg;
    private Button btnInstall;

    private TextView txUpdateStatus;
    private Resources res;

    private final String APK_NAME = "avi.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_downloader);

        final Intent intent = getIntent();
        res = getResources();

        if (intent != null) {
            updatePkg = (AviSemVersion) intent.getSerializableExtra("update");
            if (updatePkg != null) {
                initView();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Re-require permissions for lollipop and more
                    if (!PermissionHelper.hasWritePermission(this)) {
                        PermissionHelper.verifyStoragePermissions(this);
                    } else {
                        startDownload();
                    }
                } else {
                    startDownload();
                }

                loadUpdateInformation();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(webView != null) {
            webView.stopLoading();
            webView.onPause(); //pauses background threads, stops playing sound
            webView.pauseTimers(); //pauses the WebViewCore
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionHelper.REQUEST_EXTERNAL_STORAGE: {
                if ((grantResults.length > 0) && (grantResults[0] != PackageManager.PERMISSION_DENIED)) {
                    startDownload();
                } else {
                    panic(res.getString(R.string.no_permissions));
                }
                return;
            }
        }
    }

    private void startDownload() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                downloadPackage();
            }
        }, 2000);
    }

    private void initView() {
        txUpdateProgress = (TextView) findViewById(R.id.avi_update_progress);
        txUpdateTag = (TextView) findViewById(R.id.avi_update_tag);
        txUpdateStatus = (TextView) findViewById(R.id.avi_update_status);
        btnInstall = (Button) findViewById(R.id.avi_update_install);

        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initInstallPackage();
            }
        });

        initProgressBar();
        initWebView();

        if (!updatePkg.isStable()) {
            final String betaTitle = res.getString(R.string.app_name) +
                    " (" + res.getString(R.string.preview_version) + ")";

            ((TextView) findViewById(R.id.avi_update_title)).setText(betaTitle);
        }
    }

    private void setDownloadProgress(int progress) {
        progressBar.setProgress(progress);
        txUpdateProgress.setText(String.valueOf(progress) + "%");
    }

    private String getApkPath() {
        return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + APK_NAME;
    }

    private void downloadPackage() {
        boolean deleted = true;
        File f = new File(getApkPath());
        if (f.exists()) {
            deleted = f.delete();
        }

        f = null;

        // Fire UP download!
        AsyncTask<String, String, String> task = new DownloadFileFromURL() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                txUpdateStatus.setText(res.getString(R.string.downloading_update));
                progressBar.setIndeterminate(false);
                setDownloadProgress(0);
            }

            protected void onProgressUpdate(String... progress) {
                setDownloadProgress(Integer.parseInt(progress[0]));
            }

            @Override
            protected void onPostExecute(String file_url) {
                if (failed) {
                    panic(error);
                } else {
                    progressBar.setVisibility(View.GONE);
                    txUpdateProgress.setText("");
                    txUpdateStatus.setText(res.getString(R.string.installing_package));
                    initInstallPackage();
                }
            }
        };

        try {
            task.execute(updatePkg.getApkUrl(), APK_NAME);
        } catch (Exception ex) {
            panic(ex.getMessage());
        }
    }

    private void initInstallPackage() {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        final String apkDir = getApkPath();
        intent.setDataAndType(Uri.fromFile(new File(apkDir)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        // Workaround for Android Nougat
        StrictMode.VmPolicy oldVmPolicy = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            oldVmPolicy = StrictMode.getVmPolicy();

            StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
                    .penaltyLog()
                    .build();

            StrictMode.setVmPolicy(policy);
        }
        startActivity(intent);

        txUpdateStatus.setVisibility(View.GONE);
        btnInstall.setVisibility(View.VISIBLE);

        if (oldVmPolicy != null) {
            StrictMode.setVmPolicy(oldVmPolicy);
        }
    }

    private void panic(String err) {
        AlertDialog.Builder dial = new AlertDialog.Builder(this);
        dial.setTitle(res.getString(R.string.error))
                .setMessage(res.getString(R.string.failed_to_download_update) + err)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                }).show();
    }

    private void loadUpdateInformation() {

        txUpdateTag.setText(updatePkg.getTag());

        if (updatePkg.hasChangelog()) {
            webView.loadData(getDecoratedChangelogHTML(updatePkg.getChangelog()), "text/html; charset=UTF-8", null);
        }
    }

    private String getDecoratedChangelogHTML(String changelogHTML) {
        String result = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("changelog_template.html"), "UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine;
            String mTemplate = "";
            while ((mLine = reader.readLine()) != null) {
                mTemplate += mLine;
            }

            result = mTemplate.replaceAll("%CONTENT%", changelogHTML);

        } catch (IOException e) {
            //log the exception
            result = changelogHTML;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    result = changelogHTML;
                }
            }
        }
        return result;
    }

    private void initProgressBar() {
        progressBar = (ProgressBar) findViewById(R.id.avi_update_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.colorAccentDark),
                android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.getProgressDrawable().setColorFilter(
                getResources().getColor(R.color.colorAccentDark),
                android.graphics.PorterDuff.Mode.SRC_IN);

        progressBar.setVisibility(View.VISIBLE);
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.avi_update_changelog);

        // Disable hardware acceleration to prevent crashes on Android 4.1
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        webView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDefault));
        webView.setVisibility(View.VISIBLE);
    }
}
