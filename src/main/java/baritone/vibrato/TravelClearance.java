package baritone.vibrato;

import baritone.api.utils.IPlayerContext;
import baritone.pathing.movement.MovementHelper;
import baritone.utils.BlockStateInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Conservative clearance for optional camera movement and jumping. */
public final class TravelClearance {
    private TravelClearance() {}

    public static boolean wideGround(IPlayerContext ctx, BlockPos center, int headroom) {
        BlockStateInterface bsi = new BlockStateInterface(ctx);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos feet = center.offset(x, 0, z);
                if (!bsi.worldContainsLoadedChunk(feet.getX(), feet.getZ())) return false;
                BlockPos floorPos = feet.below();
                BlockState floor = ctx.world().getBlockState(floorPos);
                if (!floor.isCollisionShapeFullBlock(ctx.world(), floorPos)
                        || floor.is(Blocks.MAGMA_BLOCK) || floor.is(Blocks.CACTUS)
                        || floor.is(Blocks.ICE) || floor.is(Blocks.PACKED_ICE) || floor.is(Blocks.BLUE_ICE)
                        || floor.is(Blocks.SLIME_BLOCK) || floor.is(Blocks.HONEY_BLOCK)
                        || !floor.getFluidState().isEmpty()) return false;
                for (int y = 0; y < headroom; y++) {
                    BlockPos p = feet.above(y);
                    BlockState state = ctx.world().getBlockState(p);
                    if (!state.getFluidState().isEmpty() || MovementHelper.avoidWalkingInto(state)
                            || !MovementHelper.fullyPassable(ctx, p)) return false;
                }
            }
        }
        return true;
    }
}
