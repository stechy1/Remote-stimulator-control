package cz.zcu.fav.remotestimulatorcontrol.io;

public enum ExtensionType {

    JSON, XML, CSV;

    @Override
    public String toString() {
        return "." + name().toLowerCase();
    }
}
