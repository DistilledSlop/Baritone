package baritone.vibrato;

/** Small state machine for optional travel actions. Safety is rechecked every tick. */
public final class TravelPolicy {
    private int cooldown = 60;

    public boolean jump(boolean eligible) {
        if (cooldown > 0) cooldown--;
        if (!eligible || cooldown > 0) return false;
        cooldown = 100;
        return true;
    }

    public static boolean lookAhead(boolean grounded, boolean clear, boolean ordinaryMovement) {
        return grounded && clear && ordinaryMovement;
    }
}
