package baritone.pathing.movement.movements;

import org.junit.Test;
import static org.junit.Assert.*;

public class WaterLandingRecoveryTest {
    @Test public void naturalWaterLandingNeverConsumesSpareBuckets() {
        WaterLandingRecovery recovery = new WaterLandingRecovery();
        assertFalse(recovery.tryRecovery(true, 0));
        assertFalse(recovery.tryRecovery(false, 0));
    }
    @Test public void actualClutchRecoversOnlyItsMissingWaterBucket() {
        WaterLandingRecovery recovery = new WaterLandingRecovery();
        recovery.requestPlacement(2);
        assertFalse(recovery.tryRecovery(true, 2)); // No actual bucket use observed.
        recovery.requestPlacement(1); // Repeated action must not replace the original count.
        assertTrue(recovery.tryRecovery(true, 1));
        assertFalse(recovery.tryRecovery(true, 2)); // Recovered: leave the spare empty bucket alone.
    }
    @Test public void flowingWaterAndUnacknowledgedPickupsCannotBlockFallCompletion() {
        WaterLandingRecovery recovery = new WaterLandingRecovery();
        recovery.requestPlacement(1);
        assertFalse(recovery.tryRecovery(false, 0));
        for (int tick = 0; tick < WaterLandingRecovery.MAX_RECOVERY_TICKS; tick++) assertTrue(recovery.tryRecovery(true, 0));
        assertFalse(recovery.tryRecovery(true, 0));
        recovery.reset();
        assertFalse(recovery.tryRecovery(true, 0));
        recovery.requestPlacement(1);
        assertTrue(recovery.tryRecovery(true, 0));
    }
}
