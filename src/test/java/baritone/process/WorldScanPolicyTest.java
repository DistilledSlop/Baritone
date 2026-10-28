package baritone.process;

import org.junit.Test;
import static org.junit.Assert.*;

public class WorldScanPolicyTest {
    @Test
    public void trackingAlwaysScansLoadedChunksEvenWithFullCache() {
        assertTrue(WorldScanPolicy.scanLoaded(false, false, false, 100, 100));
        assertTrue(WorldScanPolicy.scanLoaded(false, false, true, 100, 100));
    }

    @Test
    public void miningRetainsExistingThresholdAndUntrackedRules() {
        assertFalse(WorldScanPolicy.scanLoaded(true, false, true, 100, 100));
        assertFalse(WorldScanPolicy.scanLoaded(true, false, false, 0, 100));
        assertTrue(WorldScanPolicy.scanLoaded(true, false, true, 99, 100));
        assertTrue(WorldScanPolicy.scanLoaded(true, true, false, 100, 100));
    }
}
