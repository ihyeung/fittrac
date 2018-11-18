package com.utahmsd.cs6018.instyle.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.utahmsd.cs6018.instyle.BuildConfig;

import java.io.File;
import java.sql.Date;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.utahmsd.cs6018.instyle.util.Constants.CREATE;

public class StartupActivity extends AppCompatActivity {

    private static final String LOG_TAG = StartupActivity.class.getSimpleName();

    private static final String KEY = BuildConfig.AWS_API_KEY;
    private static final String SECRET = BuildConfig.AWS_API_SECRET;
    private static final String BUCKET_NAME = "instyle-database";
    private static final String DATABASE_FILE_PATH =
            "/data/user/0/com.example.mcresswell.project01/databases/instyle_database";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, CREATE);
        super.onCreate(savedInstanceState);
        initializeAwsClient();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void initializeAwsClient() {
        AWSMobileClient.getInstance().initialize(this).execute();

        BasicAWSCredentials credentials = new BasicAWSCredentials(KEY, SECRET);

        TransferUtility transferUtility =
                buildTransferUtility(this, new AmazonS3Client(credentials));

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(
                databaseBackupTask(transferUtility), 1, 60, TimeUnit.MINUTES);

    }

    private static Runnable databaseBackupTask(TransferUtility transferUtility) {
        return () -> {

            String timestamp = Date.from(Instant.now()).toString();
            Log.d(LOG_TAG, String.format("------ Kicking off scheduled database backup task:  %s --------",
                    timestamp));
            uploadFileToS3(DATABASE_FILE_PATH,
                    "db-backup-" + timestamp.replace(" ", "_"),
                    transferUtility);
        };
    }

    private static void uploadFileToS3(String inputFileName, String outputFileName, TransferUtility transferUtility) {

        TransferObserver uploadObserver =
                transferUtility.upload( BUCKET_NAME, outputFileName, new File(inputFileName));

        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    Log.d(LOG_TAG, String.format(Locale.US,
                            "TransferUtility Upload Completed Successfully at %s",
                            Date.from(Instant.now()).toString()));
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                //TODO: Add logic in here

                int percentProgress = (int) ((float)bytesCurrent/(float)bytesTotal) * 100;
                Log.d(LOG_TAG, String.format("Upload transfer for transfer ID #%d   ----   %d percent" +
                        " of %d bytes complete", id, percentProgress, bytesTotal));
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.d(LOG_TAG, String.format("Error: upload failed for transfer ID:%d", id));
                Log.d(LOG_TAG, String.format("Upload transfer exception: '%s'", ex.getMessage()));


            }

        });
    }

    private static TransferUtility buildTransferUtility(Context context, AmazonS3Client client) {
        return TransferUtility.builder()
                .context(context.getApplicationContext())
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .s3Client(client)
                .build();
    }

    private static void downloadFileFromS3(String inputFileName, String outputFileName, TransferUtility transferUtility) {

        TransferObserver uploadObserver =
                transferUtility.download(BUCKET_NAME, outputFileName, new File(inputFileName));

        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    Log.d(LOG_TAG, String.format(Locale.US,
                            "TransferUtility Download Completed Successfully at %s",
                            Date.from(Instant.now()).toString()));
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                //TODO: Add logic in here

                int percentProgress = (int) ((float)bytesCurrent/(float)bytesTotal) * 100;
                Log.d(LOG_TAG, String.format("Download transfer for transfer ID #%d   ----   %d percent" +
                        " of %d bytes complete", id, percentProgress, bytesTotal));
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.d(LOG_TAG, String.format("Error: download failed for transfer ID #%d", id));
                Log.d(LOG_TAG, String.format("Download transfer exception: '%s'", ex.getMessage()));

            }

        });
    }
}