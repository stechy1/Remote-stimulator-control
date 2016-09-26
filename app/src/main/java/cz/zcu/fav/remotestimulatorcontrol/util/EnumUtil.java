package cz.zcu.fav.remotestimulatorcontrol.util;

/**
 * Pomocná knihovní třída pro výčtové typy
 */
public final class EnumUtil {

    private EnumUtil(){
        //Knihovní třída má privátní konstruktor
    }

    /**
     * Vyhledá v zadaném výčtovém typu zadaný typ, kde nezáleží na velikosti písmen
     *
     * @param enumType Výčtový typ
     * @param name Hledané slovo
     * @return Odpovídající typ
     */
    public static <T extends Enum<?>> T lookup(Class<T> enumType,
                                               String name) {
        for (T enumn : enumType.getEnumConstants()) {
            if (enumn.name().equalsIgnoreCase(name)) {
                return enumn;
            }
        }
        return null;
    }

}
