package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
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

    // region Constants
    private static final String TAG = "MediaAdapter";

    private static final int ITEM_VIEW_TYPE_STANDART = 0;
    private static final int ITEM_VIEW_TYPE_BUTTON = 1;
    // endregion

    // region Variables
    private final List<AMedia> mMediaList;

    private final OnAddMediaClickListener mListener;
    // endregion

    // region Constructors
    /**
     * Vytvoří nový adapter
     *
     * @param mediaList Kolekce medií
     * @param listener {@link OnAddMediaClickListener}
     */
    MediaAdapter(List<AMedia> mediaList, OnAddMediaClickListener listener) {
        mMediaList = mediaList;
        mListener = listener;
    }
    // endregion

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
        if (position == mMediaList.size())
            return;

        AMedia media = mMediaList.get(position);
        ((MediaHolder) holder).bindTo(media);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mMediaList.size()) {
            return ITEM_VIEW_TYPE_STANDART;
        }

        return ITEM_VIEW_TYPE_BUTTON;
    }

    @Override
    public int getItemCount() {
        // Vracíme o 1 více, protože poslední prvek bude tlačítko pro přidání další položky
        return mMediaList.size() + 1;
    }

    class MediaHolder extends RecyclerView.ViewHolder {
        private final MediaItemBinding mmBinding;

        MediaHolder(MediaItemBinding binding) {
            super(binding.getRoot());

            mmBinding = binding;
        }

        public void bindTo(AMedia media) {
            mmBinding.setMedia(media);
            mmBinding.executePendingBindings();
        }
    }

    class MediaButtonHolder extends RecyclerView.ViewHolder {
        private final MediaItemButtonBinding mmBinding;

        public MediaButtonHolder(MediaItemButtonBinding binding) {
            super(binding.getRoot());

            mmBinding = binding;
            init();
        }

        private void init() {
            Button addBtn = mmBinding.buttonAddMedia;
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
