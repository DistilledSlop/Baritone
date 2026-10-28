package baritone.pathing.movement.movements;

/** Accounts for one fall's actual water use without harvesting unrelated water. */
final class WaterLandingRecovery {
    static final int MAX_RECOVERY_TICKS = 20;
    private int originalWaterBuckets = -1;
    private int recoveryTicks;

    void requestPlacement(int waterBuckets) {
        if (originalWaterBuckets < 0 && waterBuckets > 0) originalWaterBuckets = waterBuckets;
    }

    boolean tryRecovery(boolean sourceWater, int waterBuckets) {
        return sourceWater && originalWaterBuckets > 0 && waterBuckets < originalWaterBuckets
                && recoveryTicks++ < MAX_RECOVERY_TICKS;
    }

    void reset() { originalWaterBuckets = -1; recoveryTicks = 0; }
}
