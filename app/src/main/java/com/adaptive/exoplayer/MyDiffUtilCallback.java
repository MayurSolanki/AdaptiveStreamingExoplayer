package com.adaptive.exoplayer;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;


import com.google.android.exoplayer2.offline.Download;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mayur Solanki (mayursolanki120@gmail.com) on 05/06/20, 7:23 PM.
 */
public class MyDiffUtilCallback extends DiffUtil.Callback {
    List<Download> newList = new ArrayList<>();
    List<Download> oldList = new ArrayList<>();

    public MyDiffUtilCallback(List<Download> newList, List<Download> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0 ;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0 ;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).request.id.equalsIgnoreCase(newList.get(newItemPosition).request.id);
    }


    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        float oldPercentage = oldList.get(oldItemPosition).getBytesDownloaded();
        float newPercentage = newList.get(newItemPosition).getBytesDownloaded();

        Log.d(" MyDiffUtilCallback ","oldPercentage : "+oldPercentage);
        Log.d(" MyDiffUtilCallback ","newPercentage : "+newPercentage);

        return Float.compare(oldPercentage, newPercentage) != 0;

//        return newList.get(newItemPosition).getBytesDownloaded() ==  oldList.get(oldItemPosition).getBytesDownloaded();

    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Download newModel = newList.get(newItemPosition);
        Download oldModel = oldList.get(oldItemPosition);

        Bundle difference = new Bundle();
        if(newModel.getPercentDownloaded() != oldModel.getPercentDownloaded()){
            difference.putFloat("percentDownloaded", newModel.getPercentDownloaded());
        }


        if (difference.size()==0){
            return null;
        }
        return difference;
    }
}
