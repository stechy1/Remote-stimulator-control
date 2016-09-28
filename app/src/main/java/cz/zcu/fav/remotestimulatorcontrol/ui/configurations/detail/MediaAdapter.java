package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.MediaItemBinding;
import cz.zcu.fav.remotestimulatorcontrol.databinding.MediaItemButtonBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.media.AMedia;

/**
 * Adapter pro {@link android.support.v7.widget.RecyclerView}
 */
class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MediaAdapter";

    private static final int ITEM_VIEW_TYPE_STANDART = 0;
    private static final int ITEM_VIEW_TYPE_BUTTON = 1;

    private final List<AMedia> mediaList;

    private OnAddMediaClickListener mListener;

    MediaAdapter(List<AMedia> mediaList, OnAddMediaClickListener listener) {
        this.mediaList = mediaList;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_STANDART) {
            MediaItemBinding bindings = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.media_item, parent, false);
            return new MediaHolder(bindings);
        } else {
            MediaItemButtonBinding bindings = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.media_item_button, parent, false);
            return new MediaButtonHolder(bindings);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == mediaList.size())
            return;

        AMedia media = mediaList.get(position);
        ((MediaHolder) holder).bindTo(media);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mediaList.size()) {
            Log.d(TAG, "Vracím typ položky: STANDART");
            return ITEM_VIEW_TYPE_STANDART;
        }

        Log.d(TAG, "Vracím typ položky: BUTTON");
        return ITEM_VIEW_TYPE_BUTTON;
    }

    @Override
    public int getItemCount() {
        // Vracíme o 1 více, protože poslední prvek bude tlačítko pro přidání další položky
        return mediaList.size() + 1;
    }

    class MediaHolder extends RecyclerView.ViewHolder {

        private final MediaItemBinding mBinding;

        MediaHolder(MediaItemBinding binding) {
            super(binding.getRoot());

            mBinding = binding;
        }

        public void bindTo(AMedia media) {
            mBinding.setMedia(media);
            mBinding.executePendingBindings();
        }
    }

    class MediaButtonHolder extends RecyclerView.ViewHolder {
        private final MediaItemButtonBinding mBinding;

        public MediaButtonHolder(MediaItemButtonBinding binding) {
            super(binding.getRoot());

            mBinding = binding;
            init();
        }

        private void init() {
            Button addBtn = mBinding.buttonAddMedia;
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null)
                        mListener.onAddMediaClick();
                }
            });
        }
    }

    interface OnAddMediaClickListener {

        /**
         * Zavolá se při kliknutí na tlačítko pro přidání nového externího média
         */
        void onAddMediaClick();
    }
}
