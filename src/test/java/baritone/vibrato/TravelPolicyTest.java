package baritone.vibrato;

import org.junit.Test;
import static org.junit.Assert.*;

public class TravelPolicyTest {
    @Test
    public void cameraLookRequiresGroundAndClearOrdinaryMovement() {
        assertTrue(TravelPolicy.lookAhead(true, true, true));
        assertFalse(TravelPolicy.lookAhead(false, true, true));
        assertFalse(TravelPolicy.lookAhead(true, false, true));
        assertFalse(TravelPolicy.lookAhead(true, true, false));
    }

    @Test
    public void jumpWaitsForEligibilityAndRespectsCooldown() {
        TravelPolicy policy = new TravelPolicy();
        for (int i = 0; i < 60; i++) assertFalse(policy.jump(false));
        assertTrue(policy.jump(true));
        for (int i = 0; i < 99; i++) assertFalse(policy.jump(true));
        assertFalse(policy.jump(false)); // a new hazard cancels the next opportunity
        assertTrue(policy.jump(true));
    }
}
