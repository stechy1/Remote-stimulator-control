package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.MediaItemBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.media.AMedia;

/**
 * Adapter pro {@link android.support.v7.widget.RecyclerView}
 */
class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaHolder>{

    private final List<AMedia> mediaList;

    MediaAdapter(List<AMedia> mediaList) {
        this.mediaList = mediaList;
    }

    @Override
    public MediaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MediaItemBinding bindings = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.media_item, parent, false);
        return new MediaHolder(bindings);
    }

    @Override
    public void onBindViewHolder(MediaHolder holder, int position) {
        AMedia media = mediaList.get(position);
        holder.bindTo(media);
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
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

}
