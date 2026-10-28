package baritone.pathing.movement;

import baritone.api.pathing.movement.ActionCosts;
import org.junit.Test;
import static org.junit.Assert.*;

public class WaterTravelCostTest {
    @Test public void normalWaterIsSlowerAndEfficiencyInterpolatesTowardDryLand() {
        double water = ActionCosts.WALK_ONE_IN_WATER_COST;
        double dry = ActionCosts.WALK_ONE_BLOCK_COST;
        assertTrue(water > dry);
        assertEquals(water, WaterTravelCost.forEfficiency(0), 1e-9);
        assertEquals(water * (2.0 / 3) + dry / 3, WaterTravelCost.forEfficiency(1.0 / 3), 1e-9);
        assertEquals(dry, WaterTravelCost.forEfficiency(1), 1e-9);
    }

    @Test public void outOfRangeAndInvalidEfficiencyCannotMakeWaterCheaperThanDryLand() {
        assertEquals(ActionCosts.WALK_ONE_BLOCK_COST, WaterTravelCost.forEfficiency(3), 1e-9);
        assertEquals(ActionCosts.WALK_ONE_IN_WATER_COST, WaterTravelCost.forEfficiency(-1), 1e-9);
        assertEquals(ActionCosts.WALK_ONE_IN_WATER_COST, WaterTravelCost.forEfficiency(Double.NaN), 1e-9);
    }
}
