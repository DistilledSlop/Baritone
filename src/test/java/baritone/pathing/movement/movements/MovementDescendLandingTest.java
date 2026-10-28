package baritone.pathing.movement.movements;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MovementDescendLandingTest {
    @Test
    public void waitsOnlyAfterCrossingTheTargetWhileAirborneAndCentered() {
        assertTrue(MovementDescend.shouldWaitForLanding(false, 0.05, 0.1, 1, 0));
        assertTrue(MovementDescend.shouldWaitForLanding(false, 0.1, -0.05, 0, -1));
        assertFalse(MovementDescend.shouldWaitForLanding(true, 0.05, 0.1, 1, 0));
        assertFalse(MovementDescend.shouldWaitForLanding(false, -0.05, 0.1, 1, 0));
        assertFalse(MovementDescend.shouldWaitForLanding(false, 0.05, 0.2, 1, 0));
    }

    @Test
    public void normalDescentWaitsOnlyWhenAlreadyOnCourseForTheCenter() {
        assertTrue(MovementDescend.centeredInFlight(false, 0.1, -0.1, -0.02, 0.02));
        assertFalse(MovementDescend.centeredInFlight(true, 0.1, -0.1, -0.02, 0.02));
        assertFalse(MovementDescend.centeredInFlight(false, 0.3, 0, 0, 0));
        assertFalse(MovementDescend.centeredInFlight(false, 0.1, 0, 0.2, 0));
    }
}
