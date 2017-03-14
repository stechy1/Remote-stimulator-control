package cz.zcu.fav.remotestimulatorcontrol.ui;

import java.util.List;

/**
 * Rozhraní pro adaptery, které obsahují vybíratelné položky
 */
public interface ISelectable {

    /**
     * Vloží, nebo odebere položky z vybraných
     *
     * @param position Pozice položky
     */
    void toggleSelection(int position);

    /**
     * Označí všechny položky jako vybrané
     */
    void selectAll();

    /**
     * Invertuje výběr položek
     */
    void invertSelection();

    /**
     * Zruší výběr všech položek
     */
    void selectNone();

    /**
     * Vrátí počet vybraných položek
     *
     * @return Počet vybraných položek
     */
    int getSelectedItemCount();

    /**
     * Vyčistí vybrané položky
     */
    void clearSelections();

    /**
     * Vrátí kolekci indexů vybraných položek
     *
     * @return Kolekci indexů vybraných položek
     */
    List<Integer> getSelectedItemsIndex();

    /**
     * Nastaví poožky jako vybrané
     *
     * @param selectedItems Kolekce indexů vybraných položek
     */
    void selectItems(List<Integer> selectedItems);
}
