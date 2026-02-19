package aervial.rideanymob;

public class RideToggle {

    private static boolean enabled = true;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void toggle() {
        enabled = !enabled;
    }

    public static void set(boolean value) {
        enabled = value;
    }
}
