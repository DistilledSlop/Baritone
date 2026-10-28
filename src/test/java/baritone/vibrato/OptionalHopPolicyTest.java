package baritone.vibrato;

import org.junit.Test;
import static org.junit.Assert.*;

public class OptionalHopPolicyTest {
    @Test public void gateOnlyAcceptsRequestedAirborneOneBlockAboveSameFlatPath() {
        assertTrue(OptionalHopPolicy.permit(true, false, 64, 65, 64));
        assertFalse(OptionalHopPolicy.permit(false, false, 64, 65, 64));
        assertFalse(OptionalHopPolicy.permit(true, true, 64, 65, 64));
        assertFalse(OptionalHopPolicy.permit(true, false, 64, 66, 64));
        assertFalse(OptionalHopPolicy.permit(true, false, 63, 65, 64));
    }
    @Test public void completionProjectionIsRestrictedToVerifiedHop() {
        assertEquals(64, OptionalHopPolicy.completionFeetY(65, 64, true));
        assertEquals(65, OptionalHopPolicy.completionFeetY(65, 64, false));
        assertEquals(63, OptionalHopPolicy.completionFeetY(63, 64, false));
    }
    @Test public void clearsOnLandingHazardFailureToTakeOffAndTimeout() {
        OptionalHopPolicy state = new OptionalHopPolicy();
        state.begin(64); state.update(true, false); assertTrue(state.active());
        state.update(true, true); assertFalse(state.active());
        state.begin(64); state.update(false, false); assertFalse(state.active());
        state.begin(64); for (int i = 0; i < 4; i++) state.update(true, true);
        assertFalse(state.active());
        state.begin(64); for (int i = 0; i < 26; i++) state.update(true, false);
        assertFalse(state.active());
    }
}
