package baritone.vibrato;

/** State for a single optional hop requested by the controlling client. */
public final class OptionalHopPolicy {
    private static final int MAX_TICKS = 25;
    private static final int TAKEOFF_TIMEOUT = 3;
    private boolean active;
    private boolean airborne;
    private int originY;
    private int age;

    public void begin(int y) {
        active = true;
        airborne = false;
        originY = y;
        age = 0;
    }

    public void update(boolean safe, boolean onGround) {
        if (!active) return;
        if (!safe || ++age > MAX_TICKS) { clear(); return; }
        if (!onGround) airborne = true;
        else if (airborne || age > TAKEOFF_TIMEOUT) clear();
    }

    public void clear() { active = false; airborne = false; }
    public boolean active() { return active; }
    public int originY() { return originY; }

    /** Only an already-verified optional hop projects feet to the traverse height for completion. */
    public static int completionFeetY(int feetY, int destY, boolean verifiedHop) {
        return verifiedHop ? destY : feetY;
    }

    /** The exception is restricted to airborne, one block above the original flat traverse. */
    public static boolean permit(boolean requested, boolean onGround, int originY, int feetY, int destY) {
        return requested && !onGround && originY == destY && feetY == destY + 1;
    }
}
