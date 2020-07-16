package com.adaptive.exoplayer;

/**
 * Created by Mayur Solanki (mayursolanki120@gmail.com) on 27/05/20, 2:32 PM.
 */

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;


public final class TrackKey {
    private TrackGroupArray trackGroupArray;
    private TrackGroup trackGroup;
    private Format trackFormat;

    public TrackKey(TrackGroupArray trackGroupArray, TrackGroup trackGroup, Format trackFormat) {
        this.trackGroupArray = trackGroupArray;
        this.trackGroup = trackGroup;
        this.trackFormat = trackFormat;
    }


    public TrackGroupArray getTrackGroupArray() {
        return trackGroupArray;
    }

    public void setTrackGroupArray(TrackGroupArray trackGroupArray) {
        this.trackGroupArray = trackGroupArray;
    }

    public TrackGroup getTrackGroup() {
        return trackGroup;
    }

    public void setTrackGroup(TrackGroup trackGroup) {
        this.trackGroup = trackGroup;
    }

    public Format getTrackFormat() {
        return trackFormat;
    }

    public void setTrackFormat(Format trackFormat) {
        this.trackFormat = trackFormat;
    }
}
