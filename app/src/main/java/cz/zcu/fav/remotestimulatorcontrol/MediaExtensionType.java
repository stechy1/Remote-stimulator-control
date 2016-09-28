package cz.zcu.fav.remotestimulatorcontrol;

public enum MediaExtensionType {

    PNG, JPEG, GIF, MP3;

    @Override
    public String toString() {
        return "." + name().toLowerCase();
    }
}
