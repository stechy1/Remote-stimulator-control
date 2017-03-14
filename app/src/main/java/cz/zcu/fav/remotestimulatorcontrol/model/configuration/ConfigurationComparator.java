package cz.zcu.fav.remotestimulatorcontrol.model.configuration;

import java.util.Comparator;
import java.util.List;

/**
 * Výčet použitelných komparátorů pro konfigurace
 */
public enum ConfigurationComparator implements Comparator<AConfiguration> {

    // Komparátor podle názvu
    NAME_COMPARATOR {
        @Override
        public int compare(AConfiguration lhs, AConfiguration rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    },
    // Komparátor podle typu konfigurace
    TYPE_COMPARATOR {
        @Override
        public int compare(AConfiguration lhs, AConfiguration rhs) {
            return lhs.getConfigurationType().compareTo(rhs.getConfigurationType());
        }
    };

    /**
     * Porovná dvě čísla a vrátí výsledek
     *      0 - čísla si jsou rovna
     *      1 - první číslo je VĚTŠÍ než druhé
     *     -1 - první číslo je MENŠÍ než druhé
     *
     * @param lhs První porovnávané číslo
     * @param rhs Druhé porovnávané číslo
     * @return Výsledek porovnání
     */
    private static int integerCompare(int lhs, int rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }

    /**
     * Vytvoří komparátor řadící sestupně
     *
     * @param other Komparátor řadící vzestupně
     * @return Komparátor řadící sestupně
     */
    public static Comparator<AConfiguration> decending(final Comparator<AConfiguration> other) {
        return new Comparator<AConfiguration>() {
            public int compare(AConfiguration o1, AConfiguration o2) {
                return -1 * other.compare(o1, o2);
            }
        };
    }

    /**
     * Vytvoří komparátor podle kriterií
     *
     * @param multipleOptions Komparátory
     *                        @see #NAME_COMPARATOR
     *                        @see #TYPE_COMPARATOR
     * @return Komparátor
     */
    public static Comparator<AConfiguration> getComparator(final List<ConfigurationComparator> multipleOptions) {
        return new Comparator<AConfiguration>() {
            @Override
            public int compare(AConfiguration lhs, AConfiguration rhs) {
                for (ConfigurationComparator option : multipleOptions) {
                    int result = option.compare(lhs, rhs);
                    if (result != 0) {
                        return result;
                    }
                }

                return 0;
            }
        };
    }

}
