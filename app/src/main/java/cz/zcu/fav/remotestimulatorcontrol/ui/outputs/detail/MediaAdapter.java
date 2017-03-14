package cz.zcu.fav.remotestimulatorcontrol.ui.outputs.detail;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.MediaItemSelectBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.media.AMedia;

/**
 * Adapter spravující média
 */
public final class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaHolder> {

    // region Variables

    // Kolekce médií
    private final List<AMedia> mMediaList;

    public MediaAdapter(List<AMedia> mediaList) {
        this.mMediaList = mediaList;
    }
    // endregion

    @Override
    public MediaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MediaItemSelectBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.media_item_select, parent, false
        );
        return new MediaHolder(binding);
    }

    @Override
    public void onBindViewHolder(MediaHolder holder, int position) {
        AMedia media = mMediaList.get(position);
        holder.bindTo(media);
    }

    @Override
    public int getItemCount() {
        return mMediaList.size();
    }

    class MediaHolder extends RecyclerView.ViewHolder {
        private final MediaItemSelectBinding mmBinding;

        public MediaHolder(MediaItemSelectBinding binding) {
            super(binding.getRoot());
            this.mmBinding = binding;
        }

        void bindTo(AMedia media) {
            mmBinding.setMedia(media);
            mmBinding.executePendingBindings();
        }
    }
}
