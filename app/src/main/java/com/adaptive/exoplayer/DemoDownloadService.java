/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.adaptive.exoplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.PlatformScheduler;
import com.google.android.exoplayer2.ui.DownloadNotificationHelper;
import com.google.android.exoplayer2.util.Util;

import java.util.List;
import java.util.Objects;


/**
 * A service for downloading media.
 */
public class DemoDownloadService extends DownloadService implements DownloadTracker.Listener {

    private static final int JOB_ID = 1;
//  private static final int FOREGROUND_NOTIFICATION_ID = 1;


    Handler handler = new Handler();
    private Runnable runnableCode;
    DownloadManager downloadManager;
    DownloadTracker downloadTracker;


    public DemoDownloadService() {
        super(FOREGROUND_NOTIFICATION_ID_NONE);
    }


    @Override
    protected DownloadManager getDownloadManager() {
        // This will only happen once, because getDownloadManager is guaranteed to be called only once
        // in the life cycle of the process.
        AdaptiveExoplayer application = (AdaptiveExoplayer) getApplication();
        DownloadManager downloadManager = application.getDownloadManager();
        DownloadNotificationHelper downloadNotificationHelper = application.getDownloadNotificationHelper();



//    Requirements requirements = new Requirements(Requirements.NETWORK_UNMETERED);
        downloadManager.setMaxParallelDownloads(1);
//    downloadManager.setRequirements(requirements);

        return downloadManager;
    }

    @Override
    protected PlatformScheduler getScheduler() {
        return Util.SDK_INT >= 21 ? new PlatformScheduler(this, JOB_ID) : null;
    }

    @Override
    protected Notification getForegroundNotification(List<Download> downloads) {

        return null;

//  return   ((LakshyaOnline) getApplication())
//            .getDownloadNotificationHelper()
//            .buildProgressNotification(R.drawable.logo_notification,null,null,downloads);

    }



    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        AdaptiveExoplayer application = (AdaptiveExoplayer) getApplication();
        downloadManager = application.getDownloadManager();
        downloadTracker = application.getDownloadTracker();

        downloadTracker.addListener(this);


        if(intent != null){
            Uri uri = intent.getData();
            switch (Objects.requireNonNull(intent.getAction())){
                case AppConstant.EXO_DOWNLOAD_ACTION_PAUSE:
                    ((AdaptiveExoplayer) getApplication()).getDownloadManager().addDownload(downloadTracker.getDownloadRequest(uri), Download.STATE_STOPPED);

                    break;
                case AppConstant.EXO_DOWNLOAD_ACTION_START:
                    ((AdaptiveExoplayer) getApplication()).getDownloadManager().addDownload(downloadTracker.getDownloadRequest(uri), Download.STOP_REASON_NONE);

                    break;

                case AppConstant.EXO_DOWNLOAD_ACTION_CANCEL:
                    ((AdaptiveExoplayer) getApplication()).getDownloadManager().removeDownload(downloadTracker.getDownloadRequest(uri).id);

                    break;
            }
        }

        String channelId = AppUtil.createExoDownloadNotificationChannel(this);


          runnableCode = new Runnable() {
            @Override
            public void run() {
                checkAndStartDownload(application.getApplicationContext());
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(runnableCode);



        return START_STICKY;
    }

    public void checkAndStartDownload(Context context){
        if (downloadManager.getCurrentDownloads().size() > 0) {
            for (Download download : downloadManager.getCurrentDownloads()) {
                if(download.state == Download.STATE_DOWNLOADING){
                    showNotification(context,download);
                }else {
                    stopForeground(true);
                }
            }
        }else {
            stopForeground(true);
            handler.removeCallbacks(runnableCode);
        }
    }

    @Override
    public void onDestroy() {
        if (downloadTracker != null) {
            downloadTracker.removeListener(this);
        }
        super.onDestroy();


    }

    @Override
    public void onDownloadsChanged(Download download) {
        switch (download.state) {
            case Download.STATE_COMPLETED:
                Log.d("onDownloadChanged", " Notification STATE_COMPLETED");

//                showNotification(context,download);


                break;
            case Download.STATE_FAILED:
                Log.d("onDownloadChanged", " Notification STATE_FAILED");


                break;
            case Download.STATE_QUEUED:
                Log.d("onDownloadChanged", " Notification STATE_QUEUED");


                break;
            case Download.STATE_STOPPED:
                Log.d("onDownloadChanged", " Notification STATE_STOPPED");

//                showNotification(context,download);
                break;

            case Download.STATE_DOWNLOADING:
                Log.d("onDownloadChanged", " Notification STATE_DOWNLOADING");

                break;
            case Download.STATE_REMOVING:
                Log.d("onDownloadChanged", " Notification STATE_REMOVING");

                break;
            case Download.STATE_RESTARTING:
                Log.d("onDownloadChanged", " Notification STATE_RESTARTING");
                break;
        }

    }



    public void showNotification(Context context, Download download) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = AppUtil.createExoDownloadNotificationChannel(context);

//       Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        // Start/Resume Download
        Intent pIntentStart = new Intent(context, DemoDownloadService.class);
        pIntentStart.setAction(AppConstant.EXO_DOWNLOAD_ACTION_START);
        pIntentStart.setData(download.request.uri);
        PendingIntent pendingIntentStart = PendingIntent.getService(this, 100, pIntentStart, 0);


        // Pause Download
        Intent pIntentPause = new Intent(context, DemoDownloadService.class);
        pIntentPause.setAction(AppConstant.EXO_DOWNLOAD_ACTION_PAUSE);
        pIntentPause.setData(download.request.uri);
        PendingIntent pendingIntentPause = PendingIntent.getService(this, 100, pIntentPause, 0);


        // Cancel Download
        Intent pIntentStartCancel = new Intent(context, DemoDownloadService.class);
        pIntentStartCancel.setData(download.request.uri);
        pIntentStartCancel.setAction(AppConstant.EXO_DOWNLOAD_ACTION_CANCEL);
        PendingIntent pendingIntentCancel = PendingIntent.getService(this, 100, pIntentStartCancel, 0);

        VideoModel videoModel = AppUtil.getVideoDetail(download.request.id);

        switch (download.state) {

            case Download.STATE_DOWNLOADING:


                Notification notificationDownloading = new NotificationCompat.Builder(context, channelId)
                        .setOngoing(true)
                        .setAutoCancel(false)
                        .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                        .setContentTitle(videoModel.getVideoName())
                        .setContentText("Downloading")
                        .setProgress(100, (int) download.getPercentDownloaded(), false)
                        .setOnlyAlertOnce(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        .addAction(new NotificationCompat.Action(R.drawable.ic_play_arrow_black_24dp, "Pause", pendingIntentPause))
                        .addAction(new NotificationCompat.Action(R.drawable.ic_play_arrow_black_24dp, "Cancel", pendingIntentCancel))
                        .build();


                startForeground(1001, notificationDownloading);

//                if (notificationManager != null) {
//                    notificationManager.notify(AppConstant.EXO_DOWNLOAD_NOTIFICATION_ID, notificationDownloading);
//                }


                break;
            case Download.STATE_COMPLETED:

                Notification notificationCompleted = new NotificationCompat.Builder(context, channelId)
                        .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                        .setContentTitle(videoModel.getVideoName())
                        .setContentText("Completed")
                        .setAutoCancel(false)
                        .setWhen(System.currentTimeMillis())
                        .setOnlyAlertOnce(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        .build();


                notificationManager.notify(1002, notificationCompleted);

                break;
            case Download.STATE_FAILED:
                break;
            case Download.STATE_QUEUED:


                break;
            case Download.STATE_REMOVING:
                break;
            case Download.STATE_RESTARTING:
                break;
            case Download.STATE_STOPPED:


                break;
        }
    }

}
