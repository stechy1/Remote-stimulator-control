package cz.zcu.fav.remotestimulatorcontrol.util;

@SuppressWarnings("all")
public class MathUtils {

    private MathUtils() {
    }

    public static float constrain(float min, float max, float v) {
        return Math.max(min, Math.min(max, v));
    }
}
