package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityMediaChoserBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.media.AMedia;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.DividerItemDecoration;

public class MediaChoserActivity extends AppCompatActivity implements RecyclerView.OnItemTouchListener {

    public static final String MEDIA_LIST = "media_list";
    public static final String MEDIA_INDEX = "index";

    private MediaAdapter adapter;
    private RecyclerView recyclerView;
    private GestureDetectorCompat gestureDetector;
    private ArrayList<AMedia> mediaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMediaChoserBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_media_choser);
        recyclerView = binding.recyclerViewMedia;

        if (savedInstanceState != null) {
            mediaList = savedInstanceState.getParcelableArrayList(MEDIA_LIST);
        } else {
            Intent intent = getIntent();
            mediaList = intent.getParcelableArrayListExtra(MEDIA_LIST);
        }

        adapter = new MediaAdapter(mediaList, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.Orientation.HORIZONTAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(this);
        gestureDetector = new GestureDetectorCompat(this, new RecyclerViewGestureListener());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MEDIA_LIST, mediaList);
        super.onSaveInstanceState(outState);
    }

    // region RecyclerView Handlers
    // region Recycler view OnItemTouchListner

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    // endregion

    // region GestureDetector for RecyclerView

    private class RecyclerViewGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            int position = recyclerView.getChildAdapterPosition(view);

            if (position == -1) {
                return false;
            }

            Intent intent = new Intent();
            intent.putExtra(MEDIA_INDEX, position);
            setResult(RESULT_OK, intent);
            finish();

            return true;
        }

    }
        // endregion

    // endregion
}
