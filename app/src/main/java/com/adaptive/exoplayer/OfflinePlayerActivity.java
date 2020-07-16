package com.adaptive.exoplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Formatter;
import java.util.Locale;


/**
 * Created by Mayur Solanki (mayursolanki120@gmail.com) on 22/03/19, 1:20 PM.
 */
public class OfflinePlayerActivity extends AppCompatActivity implements View.OnClickListener, VideoRendererEventListener, PlaybackPreparer, PlayerControlView.VisibilityListener {


    private static final String TAG = "OfflinePlayer";
    protected static CookieManager DEFAULT_COOKIE_MANAGER;
    // Exoplayer
    private ImageView imgBackPlayer;
    private ImageView imgBwd;
    private ImageView exoPlay;
    private ImageView exoPause;
    private ImageView imgFwd;
    private LinearLayout linMediaController;
    private TextView tvPlayerCurrentTime;
    private ProgressBar exoProgressbar;
    private TextView tvPlayerEndTime;
    private TextView tvPlaybackSpeed;
    private TextView tvPlayBackSpeedSymbol;
    private ImageView imgFullScreenEnterExit;
    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;
    private FrameLayout frameLayoutMain;
    int tapCount = 1;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private Handler handler;
    private MediaSource mediaSource;
    private DataSource.Factory dataSourceFactory;
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 2000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            hide();
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }

        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    String offlineVideoLink,title;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_offline_player);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title =  bundle.getString("video_title");
            offlineVideoLink =  bundle.getString("video_url");

        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dataSourceFactory = buildDataSourceFactory();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        FullScreencall();
        initView();
        prepareView();
        setOnClickListner();




    }



    private void initView() {
        playerView = findViewById(R.id.player_view);
        frameLayoutMain = findViewById(R.id.frame_layout_main);
        imgBwd = findViewById(R.id.img_bwd);
        exoPlay = findViewById(R.id.exo_play);
        exoPause = findViewById(R.id.exo_pause);
        imgBackPlayer = findViewById(R.id.img_back_player);
        imgBackPlayer.setOnClickListener(this);
        imgFwd = findViewById(R.id.img_fwd);
        linMediaController = findViewById(R.id.lin_media_controller);
        tvPlayerCurrentTime = findViewById(R.id.tv_player_current_time);
        exoProgressbar = findViewById(R.id.loading_exoplayer);
        tvPlayerEndTime = findViewById(R.id.tv_player_end_time);
        tvPlaybackSpeed = findViewById(R.id.tv_play_back_speed);
        tvPlaybackSpeed.setOnClickListener(this);
        tvPlaybackSpeed.setText("" + tapCount);
        tvPlayBackSpeedSymbol = findViewById(R.id.tv_play_back_speed_symbol);
        imgFullScreenEnterExit = findViewById(R.id.img_full_screen_enter_exit);

        tvPlayBackSpeedSymbol.setOnClickListener(this);
    }
    public void prepareView() {

        setProgress();
    }



    private void initExoplayer() {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);


        DefaultAllocator defaultAllocator = new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE);
        DefaultLoadControl defaultLoadControl = new DefaultLoadControl(defaultAllocator,
                DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
                DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES,
                DefaultLoadControl.DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS
        );

        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this, null,
                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this,renderersFactory, trackSelector,defaultLoadControl);
        playerView.setUseController(true);
        playerView.requestFocus();
        playerView.setPlayer(simpleExoPlayer);
        simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        simpleExoPlayer.setPlayWhenReady(true); //run file/link when ready to play.
        simpleExoPlayer.setVideoDebugListener(this); //for listening to resolution change and  outputing the resolution



        DownloadRequest downloadRequest = AdaptiveExoplayer.getInstance().getDownloadTracker().getDownloadRequest(Uri.parse(offlineVideoLink));
        MediaSource mediaSource = DownloadHelper.createMediaSource(downloadRequest,dataSourceFactory);

//         mediaSource = buildMediaSource(Uri.parse(offlineVideoLink));
        simpleExoPlayer.prepare(mediaSource,false,true);
        simpleExoPlayer.addListener(new ExoPlayer.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v(TAG, "Listener-onTracksChanged...");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.v(TAG, "Listener-onLoadingChanged...isLoading:" + isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState);


                switch (playbackState) {
                    case ExoPlayer.STATE_IDLE:
                        Log.d(TAG, "playbackState : " + "STATE_IDLE");
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        Log.d(TAG, "playbackState : " + "STATE_BUFFERING");
                        exoProgressbar.setVisibility(View.VISIBLE);
                        break;
                    case ExoPlayer.STATE_READY:
                        Log.d(TAG, "playbackState : " + "STATE_READY");
                        exoProgressbar.setVisibility(View.GONE);
                        break;
                    case ExoPlayer.STATE_ENDED:
                        Log.d(TAG, "playbackState : " + "STATE_ENDED");
                        break;
                    default:

                        break;
                }
            }
            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.v(TAG, "Listener-onRepeatModeChanged...");
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.v(TAG, "Listener-onPlayerError...");
                simpleExoPlayer.stop();
                simpleExoPlayer.prepare(mediaSource);
                simpleExoPlayer.setPlayWhenReady(true);
            }


            @Override
            public void onPositionDiscontinuity(int reason) {

            }


            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.v(TAG, "Listener-onPlaybackParametersChanged...");
            }

            /**
             * Called when all pending seek requests have been processed by the simpleExoPlayer. This is guaranteed
             * to happen after any necessary changes to the simpleExoPlayer state were reported to
             * {@link #onPlayerStateChanged(boolean, int)}.
             */
            @Override
            public void onSeekProcessed() {

            }
        });

        initBwd();
        initFwd();

    }

    private void setProgress() {

        handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {

            @Override
            public void run() {
                if (simpleExoPlayer != null) {
                    tvPlayerCurrentTime.setText(stringForTime((int) simpleExoPlayer.getCurrentPosition()));
                    tvPlayerEndTime.setText(stringForTime((int) simpleExoPlayer.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void initBwd() {
        imgBwd.requestFocus();
        imgBwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() - 10000);
            }
        });
    }

    private void initFwd() {
        imgFwd.requestFocus();
        imgFwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + 10000);
            }
        });

    }

    private void setOnClickListner() {
        imgFullScreenEnterExit.setOnClickListener(this);
        tvPlaybackSpeed.setOnClickListener(this);
        tvPlaybackSpeed.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.img_full_screen_enter_exit) {
            Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
            int orientation = display.getOrientation();

            if (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                frameLayoutMain.setLayoutParams(new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 600));
                imgFullScreenEnterExit.setImageResource(R.drawable.exo_controls_fullscreen_enter);

                hide();
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                frameLayoutMain.setLayoutParams(new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

                imgFullScreenEnterExit.setImageResource(R.drawable.exo_controls_fullscreen_exit);
                hide();

            }
        } else if (view.getId() == R.id.tv_play_back_speed || view.getId() == R.id.tv_play_back_speed_symbol) {
            if (tvPlaybackSpeed.getText().equals("1")) {
                tapCount++;
                PlaybackParameters param = new PlaybackParameters(1.25f);
                simpleExoPlayer.setPlaybackParameters(param);
                tvPlaybackSpeed.setText("" + 1.25);
            } else if (tvPlaybackSpeed.getText().equals("1.25")) {
                tapCount++;
                PlaybackParameters param = new PlaybackParameters(1.5f);
                simpleExoPlayer.setPlaybackParameters(param);
                tvPlaybackSpeed.setText("" +1.5);

            } else if (tvPlaybackSpeed.getText().equals("1.5")) {
                tapCount++;
                PlaybackParameters param = new PlaybackParameters(1.75f);
                simpleExoPlayer.setPlaybackParameters(param);
                tvPlaybackSpeed.setText("" + 1.75);
            } else if (tvPlaybackSpeed.getText().equals("1.75")) {
                tapCount++;
                PlaybackParameters param = new PlaybackParameters(2f);
                simpleExoPlayer.setPlaybackParameters(param);
                tvPlaybackSpeed.setText("" + 2);
            }else {
                tapCount = 0;
                simpleExoPlayer.setPlaybackParameters(null);
                tvPlaybackSpeed.setText("" + 1);

            }
        }else if(view.getId() == R.id.img_back_player){
            onBackPressed();
        }

    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        releasePlayer();
        setIntent(intent);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString("title");
            offlineVideoLink = bundle.getString("link");
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initExoplayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || playerView == null) {
            initExoplayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }

        FullScreencall();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }


    public void finishActivity(){
        OfflinePlayerActivity.this.finish();
    }


    private void releasePlayer() {
        if (simpleExoPlayer != null) {

            simpleExoPlayer.release();
            simpleExoPlayer = null;

        }

    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }

    public void FullScreencall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private String stringForTime(int timeMs) {
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    @Override
    public void preparePlayback() {
        initExoplayer();

    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    private DataSource.Factory buildDataSourceFactory() {
        return ((AdaptiveExoplayer) getApplication()).buildDataSourceFactory();
    }

    private MediaSource buildMediaSource(Uri uri) {
        return buildMediaSource(uri, null);
    }

    @SuppressWarnings("unchecked")
    private MediaSource buildMediaSource(Uri uri, @Nullable String overrideExtension) {
        @C.ContentType int type = Util.inferContentType(uri, overrideExtension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

}