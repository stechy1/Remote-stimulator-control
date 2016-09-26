package cz.zcu.fav.remotestimulatorcontrol.ui.configurations.detail;

/**
 * Rozhraní definující kontrakt pro zachycení změny počtu výstupů
 */
interface OnOutputCountChange {

    /**
     * Metoda zachycující změnu počtu výstupů
     *
     * @param outputCount Nový počet výstupů
     */
    void onOutputCountChange(int outputCount);

}
