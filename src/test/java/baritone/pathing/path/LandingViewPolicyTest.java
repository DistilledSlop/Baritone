package baritone.pathing.path;

import org.junit.Test;
import static org.junit.Assert.*;

public class LandingViewPolicyTest {
    @Test public void onlyOrdinaryAirborneLandingMayHoldView() {
        assertTrue(PathExecutor.holdLandingViewAllowed(true, false, true, false));
        assertFalse(PathExecutor.holdLandingViewAllowed(false, false, true, false));
        assertFalse(PathExecutor.holdLandingViewAllowed(true, true, true, false));
        assertFalse(PathExecutor.holdLandingViewAllowed(true, false, false, false));
        assertFalse(PathExecutor.holdLandingViewAllowed(true, false, true, true));
    }
}
