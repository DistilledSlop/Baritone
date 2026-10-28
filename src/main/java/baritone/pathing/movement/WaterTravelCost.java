package baritone.pathing.movement;

import baritone.api.pathing.movement.ActionCosts;

/** Estimate movement cost from the player's vanilla water efficiency attribute. */
final class WaterTravelCost {
    private WaterTravelCost() {}

    static double forEfficiency(double efficiency) {
        double bounded = Double.isFinite(efficiency) ? Math.max(0, Math.min(1, efficiency)) : 0;
        return ActionCosts.WALK_ONE_IN_WATER_COST * (1 - bounded) + ActionCosts.WALK_ONE_BLOCK_COST * bounded;
    }
}
