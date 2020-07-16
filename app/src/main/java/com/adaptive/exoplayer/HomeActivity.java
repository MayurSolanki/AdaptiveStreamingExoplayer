package com.adaptive.exoplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * Created by Mayur Solanki (mayursolanki120@gmail.com) on 22/06/20, 12:14 PM.
 */
public class HomeActivity extends AppCompatActivity {



    RecyclerView recyclerView;
    SampleVideoAdapter sampleVideoAdapter;
    FloatingActionButton fabMyDownloads;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recycler_view);
        fabMyDownloads = findViewById(R.id.fab_my_downloads);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(HomeActivity.this, DividerItemDecoration.VERTICAL));


        sampleVideoAdapter = new SampleVideoAdapter(HomeActivity.this);
        recyclerView.setAdapter(sampleVideoAdapter);
        sampleVideoAdapter.addItems(AdaptiveExoplayer.getInstance().videoModels);


        fabMyDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DownloadActivity.class);
                startActivity(intent);
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(HomeActivity.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        Bundle bundle = new Bundle();
                        bundle.putString("video_id", AdaptiveExoplayer.getInstance().videoModels.get(position).getVideoId());
                        bundle.putString("video_name", AdaptiveExoplayer.getInstance().videoModels.get(position).getVideoName());
                        bundle.putString("video_url", AdaptiveExoplayer.getInstance().videoModels.get(position).getVideoUrl());
                        bundle.putLong("video_duration", AdaptiveExoplayer.getInstance().videoModels.get(position).getVideoDuration());

                        Intent intent = new Intent(HomeActivity.this,OnlinePlayerActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );
    }
}
