package cz.zcu.fav.remotestimulatorcontrol.ui.outputs;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ProfileItemBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.profiles.OutputProfile;
import cz.zcu.fav.remotestimulatorcontrol.ui.ISelectable;

/**
 * Adapter spravující profily výstupů
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileHolder> implements ISelectable {

    // region Variables

    // Kolekce profilů
    private final List<OutputProfile> mProfiles;
    // Pole vybraných profilů
    private final SparseBooleanArray mSelectedItems;
    // Kopie pole vybraných profilů
    private SparseBooleanArray mCopyOfSelectedItems;

    // endregion

    // region Constructors

    public ProfileAdapter(List<OutputProfile> profiles) {
        this.mProfiles = profiles;
        mSelectedItems = new SparseBooleanArray(profiles.size());
        mCopyOfSelectedItems = new SparseBooleanArray(profiles.size());
    }

    // endregion

    @Override
    public ProfileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProfileItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.profile_item, parent, false
        );
        return new ProfileHolder(binding);
    }

    @Override
    public void onBindViewHolder(ProfileHolder holder, int position) {
        OutputProfile profile = mProfiles.get(position);
        holder.bindTo(profile);
        boolean selected = mSelectedItems.get(position, false);
        holder.itemView.setSelected(selected);
        holder.setIsRecyclable(!selected);
    }

    @Override
    public int getItemCount() {
        return mProfiles.size();
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
        for (int i = 0; i < mProfiles.size(); i++)
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
        for (int i = 0; i < mProfiles.size(); i++)
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

    class ProfileHolder extends RecyclerView.ViewHolder {
        private final ProfileItemBinding mmBinding;

        public ProfileHolder(final ProfileItemBinding binding) {
            super(binding.getRoot());

            mmBinding = binding;
        }

        void bindTo(OutputProfile profile) {
            mmBinding.setProfile(profile);
            mmBinding.executePendingBindings();
        }
    }
}
