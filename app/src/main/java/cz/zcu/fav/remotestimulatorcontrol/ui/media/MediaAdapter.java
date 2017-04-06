package cz.zcu.fav.remotestimulatorcontrol.ui.media;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.MediaItemBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.media.AMedia;
import cz.zcu.fav.remotestimulatorcontrol.ui.ISelectable;

/**
 * Adapter spravující média
 */
public final class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaHolder> implements ISelectable {

    // region Variables

    // Kolekce médií
    private final List<AMedia> mMediaList;
    // Pole vybraných médií
    private final SparseBooleanArray mSelectedItems;
    // Kopie pole vybraných médií
    private SparseBooleanArray mCopyOfSelectedItems;

    public MediaAdapter(List<AMedia> mediaList) {
        this.mMediaList = mediaList;
        this.mSelectedItems = new SparseBooleanArray(mediaList.size());
        this.mCopyOfSelectedItems = new SparseBooleanArray(mediaList.size());
    }
    // endregion

    @Override
    public MediaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MediaItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.media_item, parent, false
        );
        return new MediaHolder(binding);
    }

    @Override
    public void onBindViewHolder(MediaHolder holder, int position) {
        AMedia media = mMediaList.get(position);
        holder.bindTo(media);
        boolean selected = mSelectedItems.get(position, false);
        holder.itemView.setSelected(selected);
    }

    @Override
    public int getItemCount() {
        return mMediaList.size();
    }

    // region Selectable

    /**
     * Vloží, nebo odebere položky z vybraných
     *
     * @param position Pozice položky
     */
    public void toggleSelection(int position) {
        if (mSelectedItems.get(position, false)) {
            mSelectedItems.delete(position);
        } else {
            mSelectedItems.put(position, true);
        }

        notifyItemChanged(position);
    }

    /**
     * Označí všechny položky jako vybrané
     */
    @Override
    public void selectAll() {
        mSelectedItems.clear();
        for (int i = 0; i < mMediaList.size(); i++)
            mSelectedItems.put(i, true);

        notifyDataSetChanged();
    }

    /**
     * Invertuje výběr položek
     */
    @Override
    public void invertSelection() {
        SparseBooleanArray tempSelected = new SparseBooleanArray(mSelectedItems.size());
        // 1. Naplnit dočasné pole všemi prvky
        for (int i = 0; i < mMediaList.size(); i++)
            tempSelected.put(i, true);

        // 2. Z dočasného pole odstranit takové indexy, které jsou v hlavním poli
        for (int i = 0; i < mSelectedItems.size(); i++)
            tempSelected.delete(mSelectedItems.keyAt(i));

        // 3. smazat hlavní pole
        mSelectedItems.clear();

        // 4. naplnit hlavní pole doplňkem
        for (int i = 0; i < tempSelected.size(); i++)
            mSelectedItems.put(tempSelected.keyAt(i), true);

        // 5. Oznímit, že se změnila data
        notifyDataSetChanged();
    }

    /**
     * Zruší výběr všech položek
     */
    @Override
    public void selectNone() {
        if (mSelectedItems.size() == 0) {
            return;
        }

        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    /**
     * Vrátí počet vybraných položek
     *
     * @return Počet vybraných položek
     */
    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    /**
     * Vyčistí vybrané položky
     */
    public void clearSelections() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    /**
     * Vrátí kolekci indexů vybraných položek
     *
     * @return Kolekci indexů vybraných položek
     */
    public ArrayList<Integer> getSelectedItemsIndex() {
        ArrayList<Integer> items = new ArrayList<>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); i++)
            items.add(mSelectedItems.keyAt(i));

        return items;
    }

    /**
     * Nastaví poožky jako vybrané
     *
     * @param selectedItems Kolekce indexů vybraných položek
     */
    @Override
    public void selectItems(List<Integer> selectedItems) {
        for (Integer selectedItem : selectedItems) {
            mSelectedItems.put(selectedItem, true);
            notifyItemChanged(selectedItem);
        }
    }
    // endregion

    // region Public methods
    /**
     * Uloži vybrané itemy
     */
    public void saveSelectedItems() {
        mCopyOfSelectedItems.clear();
        mCopyOfSelectedItems = mSelectedItems.clone();
    }

    /**
     * Obnoví vybrané itemy
     */
    public void restoreSelectedItems() {
        mSelectedItems.clear();

        for (int i = 0; i < mCopyOfSelectedItems.size(); i++) {
            mSelectedItems.put(mCopyOfSelectedItems.keyAt(i), true);
        }
    }
    // endregion

    class MediaHolder extends RecyclerView.ViewHolder {
        private final MediaItemBinding mmBinding;

        public MediaHolder(MediaItemBinding binding) {
            super(binding.getRoot());
            this.mmBinding = binding;
        }

        void bindTo(AMedia media) {
            mmBinding.setMedia(media);
            mmBinding.executePendingBindings();
        }
    }
}
