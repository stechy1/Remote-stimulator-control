package cz.zcu.fav.remotestimulatorcontrol.model;

import java.util.List;

import cz.zcu.fav.remotestimulatorcontrol.io.IOHandler;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacketOld;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.AConfiguration;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.cvep.ConfigurationCVEP;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.erp.ConfigurationERP;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.fvep.ConfigurationFVEP;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.rea.ConfigurationREA;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.tvep.ConfigurationTVEP;

/**
 * Pomocná knihovní třída pro tvorbu konkrétních konfigurací
 */
public final class ConfigurationHelper {

    // region Constructors
    /**
     * Privátní konstruktor k zabránění vytvoření instance knihovní třídy
     */
    private ConfigurationHelper() {
        throw new AssertionError();
    }
    // endregion

    /**
     * Vytvoří prázdnou implementaci třídy {@link AConfiguration}
     *
     * @param name Název konfigurace
     * @return {@link AConfiguration}
     */
    private static AConfiguration build(String name) {
        return new AConfiguration(name) {
            @Override
            public AConfiguration duplicate(String newName) {
                throw new UnsupportedOperationException();
            }

            @Override
            public IOHandler getHandler() {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<BtPacketOld> getPackets() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Vytvoří novou konfiguraci s nedefinovaným typem konfigurace
     *
     * @param name Název konfigurace
     * @return {@link AConfiguration} Nedefinovanou konfiguraci
     */
    public static AConfiguration from(String name) {
        return build(name);
    }

    /**
     * Vytvoří novou konfiguraci na základě jejího jména a typu
     *
     * @param name Název konfigurace
     * @param type Typ konfigurace
     * @return Novou instanci konfigurace
     */
    public static AConfiguration from(String name, String type) {
        return from(name, ConfigurationType.valueOf(type));
    }

    /**
     * Vytvoří novou konfiguraci na základě jejího jména a typu
     * Pokud nebude nalezen požadovaný typ konfigurace, vrátí se čistá implementace třídy {@link AConfiguration}
     *
     * @param name Název konfigurace
     * @param type Typ konfigurace
     * @return Novou instanci konfigurace
     */
    public static AConfiguration from(String name, ConfigurationType type) {
        switch (type) {
            case ERP:
                return new ConfigurationERP(name);
            case TVEP:
                return new ConfigurationTVEP(name);
            case FVEP:
                return new ConfigurationFVEP(name);
            case CVEP:
                return new ConfigurationCVEP(name);
            case REA:
                return new ConfigurationREA(name);
            default:
                return build(name);
        }
    }

}
