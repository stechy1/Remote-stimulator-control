package cz.zcu.fav.remotestimulatorcontrol.ui.outputs.detail;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityMediaChoserBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.media.MediaManager;
import cz.zcu.fav.remotestimulatorcontrol.ui.configurations.DividerItemDecoration;

public class MediaChoserActivity extends AppCompatActivity implements RecyclerView.OnItemTouchListener {

    public static final String MEDIA_NAME = "media_name";

    private ActivityMediaChoserBinding mBinding;
    private MediaManager mManager;
    private MediaAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private GestureDetectorCompat mGestureDetector;

    private Handler.Callback managerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MediaManager.MESSAGE_MEDIA_LOADED:
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    break;
            }
            return true;
        }
    };
    private final Handler managerHandler = new Handler(managerCallback);

    /**
     * Inicializuje mRecyclerView
     */
    private void initRecyclerView() {
        mAdapter = new MediaAdapter(mManager.mediaList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.Orientation.HORIZONTAL));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(this);
        mGestureDetector = new GestureDetectorCompat(this, new RecyclerViewGestureListener());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = new MediaManager(getFilesDir());
        mManager.setHandler(managerHandler);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_media_choser);
        mRecyclerView = mBinding.recyclerViewMedia;

        initRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mManager.refresh();
    }

    // region RecyclerView Handlers
    // region Recycler view OnItemTouchListner

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
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
            View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            int position = mRecyclerView.getChildAdapterPosition(view);

            if (position == -1) {
                return false;
            }

            Intent intent = new Intent();
            intent.putExtra(MEDIA_NAME, mManager.mediaList.get(position).getMediaFile().getAbsolutePath());
            setResult(RESULT_OK, intent);
            finish();

            return true;
        }

    }
        // endregion

    // endregion
}
