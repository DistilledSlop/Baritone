/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.vibrato;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Extra, per-process knobs that Vibrato needs on top of Baritone's public settings. Everything here is read from the
 * pathfinding thread and written from the game thread, so every collection is concurrent and the hot-path accessors
 * early-out when nothing is registered (no allocation, no iteration).
 * <p>
 * The {@code get*Mutex()} methods exist so a caller can snapshot/restore several collections atomically with respect to
 * each other; the collections themselves are safe to read without holding them.
 */
public final class VibratoSettings {

    private static final VibratoSettings INSTANCE = new VibratoSettings();

    public static VibratoSettings getInstance() {
        return INSTANCE;
    }

    private final Object breakMutex = new Object();
    private final Object placeMutex = new Object();
    private final Object propertiesMutex = new Object();
    private final Object globalHeuristicMutex = new Object();

    private final Set<BlockPos> blocksToAvoidBreaking = ConcurrentHashMap.newKeySet();
    private final List<Predicate<BlockPos>> breakAvoiders = new CopyOnWriteArrayList<>();
    private final List<Predicate<BlockPos>> placeAvoiders = new CopyOnWriteArrayList<>();
    private final List<Item> protectedItems = new CopyOnWriteArrayList<>();
    private final List<Predicate<BlockPos>> forceWalkOn = new CopyOnWriteArrayList<>();
    private final List<Predicate<BlockPos>> forceAvoidWalkThrough = new CopyOnWriteArrayList<>();
    private final List<BiPredicate<BlockState, ItemStack>> forceUseTool = new CopyOnWriteArrayList<>();
    private final List<BiFunction<Double, BlockPos, Double>> globalHeuristics = new CopyOnWriteArrayList<>();

    private record OptionalHop(Object player, int originY) {}
    private volatile OptionalHop optionalHop;

    /** The token belongs to exactly the player that requested the verified optional hop. */
    public void setOptionalHop(Object player, boolean active, int originY) {
        optionalHop = active ? new OptionalHop(player, originY) : null;
    }

    public boolean allowOptionalHopTraverse(Object player, boolean onGround, int feetY, int destY) {
        OptionalHop hop = optionalHop;
        return hop != null && hop.player() == player
                && OptionalHopPolicy.permit(true, onGround, hop.originY(), feetY, destY);
    }

    private volatile boolean flowingWaterPass;
    private volatile boolean swimThroughLava;
    private volatile boolean interactionPaused;
    private volatile boolean placeBucketButDontFall;
    private volatile RotationSmoother rotationSmoother;
    private volatile long lookTick;
    private volatile long lastBlockInteractLookTick = Long.MIN_VALUE;

    private VibratoSettings() {}

    // ---- mutexes -------------------------------------------------------------------------------------------------

    public Object getBreakMutex() {
        return breakMutex;
    }

    public Object getPlaceMutex() {
        return placeMutex;
    }

    public Object getPropertiesMutex() {
        return propertiesMutex;
    }

    public Object getGlobalHeuristicMutex() {
        return globalHeuristicMutex;
    }

    // ---- raw collections (used to snapshot/restore state) --------------------------------------------------------

    public Set<BlockPos> getBlocksToAvoidBreaking() {
        return blocksToAvoidBreaking;
    }

    public List<Predicate<BlockPos>> getBreakAvoiders() {
        return breakAvoiders;
    }

    public List<Predicate<BlockPos>> getPlaceAvoiders() {
        return placeAvoiders;
    }

    public List<Item> getProtectedItems() {
        return protectedItems;
    }

    public List<Predicate<BlockPos>> getForceWalkOnPredicates() {
        return forceWalkOn;
    }

    public List<Predicate<BlockPos>> getForceAvoidWalkThroughPredicates() {
        return forceAvoidWalkThrough;
    }

    public List<BiPredicate<BlockState, ItemStack>> getForceUseToolPredicates() {
        return forceUseTool;
    }

    public List<BiFunction<Double, BlockPos, Double>> getGlobalHeuristics() {
        return globalHeuristics;
    }

    // ---- configuration -------------------------------------------------------------------------------------------

    public void avoidBlockBreak(Predicate<BlockPos> avoider) {
        breakAvoiders.add(avoider);
    }

    public void avoidBlockPlace(Predicate<BlockPos> avoider) {
        placeAvoiders.add(avoider);
    }

    public void forceWalkOn(Predicate<BlockPos> predicate) {
        forceWalkOn.add(predicate);
    }

    public void forceAvoidWalkThrough(Predicate<BlockPos> predicate) {
        forceAvoidWalkThrough.add(predicate);
    }

    public void forceUseTool(BiPredicate<BlockState, ItemStack> predicate) {
        forceUseTool.add(predicate);
    }

    public void addGlobalHeuristic(BiFunction<Double, BlockPos, Double> heuristic) {
        globalHeuristics.add(heuristic);
    }

    public boolean isFlowingWaterPassAllowed() {
        return flowingWaterPass;
    }

    public void setFlowingWaterPass(boolean allowed) {
        this.flowingWaterPass = allowed;
    }

    public boolean isSwimThroughLavaAllowed() {
        return swimThroughLava;
    }

    public void allowSwimThroughLava(boolean allowed) {
        this.swimThroughLava = allowed;
    }

    /**
     * While paused Baritone will not left/right click, so another task can use the hands without a fight.
     */
    public void setInteractionPaused(boolean paused) {
        this.interactionPaused = paused;
    }

    public boolean isInteractionPaused() {
        return interactionPaused;
    }

    /**
     * Recorded for callers that want to ask whether a bucket should be placed without committing to the fall.
     * Baritone itself does not branch on it yet; it is the single place the flag lives.
     */
    public void configurePlaceBucketButDontFall(boolean value) {
        this.placeBucketButDontFall = value;
    }

    public boolean isPlaceBucketButDontFall() {
        return placeBucketButDontFall;
    }

    /**
     * Installs the humanizing camera model. {@code null} restores Baritone's stock aiming.
     */
    public void setRotationSmoother(RotationSmoother smoother) {
        this.rotationSmoother = smoother;
    }

    public RotationSmoother getRotationSmoother() {
        return rotationSmoother;
    }

    /**
     * Advances once per game tick, from LookBehavior.onTick.
     */
    public void advanceLookTick() {
        lookTick++;
    }

    /**
     * Records that Baritone claimed the look with a block-interact target now (pillar, bridge, place, mine).
     */
    public void noteBlockInteractTarget() {
        lastBlockInteractLookTick = lookTick;
    }

    public long getLookTick() {
        return lookTick;
    }

    /**
     * @return the look tick of the latest block-interact target, or {@link Long#MIN_VALUE} if there has been none
     */
    public long getLastBlockInteractLookTick() {
        return lastBlockInteractLookTick;
    }

    // ---- hot-path queries (called from the pathfinding thread, possibly millions of times) -----------------------

    public boolean shouldAvoidBreaking(int x, int y, int z) {
        if (blocksToAvoidBreaking.isEmpty() && breakAvoiders.isEmpty()) {
            return false;
        }
        return shouldAvoidBreaking(new BlockPos(x, y, z));
    }

    public boolean shouldAvoidBreaking(BlockPos pos) {
        if (blocksToAvoidBreaking.contains(pos)) {
            return true;
        }
        for (Predicate<BlockPos> avoider : breakAvoiders) {
            if (avoider.test(pos)) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldAvoidPlacingAt(int x, int y, int z) {
        if (placeAvoiders.isEmpty()) {
            return false;
        }
        return shouldAvoidPlacingAt(new BlockPos(x, y, z));
    }

    public boolean shouldAvoidPlacingAt(BlockPos pos) {
        for (Predicate<BlockPos> avoider : placeAvoiders) {
            if (avoider.test(pos)) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldForceWalkOn(int x, int y, int z) {
        if (forceWalkOn.isEmpty()) {
            return false;
        }
        BlockPos pos = new BlockPos(x, y, z);
        for (Predicate<BlockPos> predicate : forceWalkOn) {
            if (predicate.test(pos)) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldAvoidWalkThrough(int x, int y, int z) {
        if (forceAvoidWalkThrough.isEmpty()) {
            return false;
        }
        BlockPos pos = new BlockPos(x, y, z);
        for (Predicate<BlockPos> predicate : forceAvoidWalkThrough) {
            if (predicate.test(pos)) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldForceUseTool(BlockState state, ItemStack stack) {
        if (forceUseTool.isEmpty()) {
            return false;
        }
        for (BiPredicate<BlockState, ItemStack> predicate : forceUseTool) {
            if (predicate.test(state, stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean isProtectedItem(Item item) {
        return !protectedItems.isEmpty() && protectedItems.contains(item);
    }

    /**
     * Folds the registered global heuristics over a goal heuristic.
     */
    public double applyGlobalHeuristics(double heuristic, int x, int y, int z) {
        if (globalHeuristics.isEmpty()) {
            return heuristic;
        }
        BlockPos pos = new BlockPos(x, y, z);
        for (BiFunction<Double, BlockPos, Double> function : globalHeuristics) {
            heuristic = function.apply(heuristic, pos);
        }
        return heuristic;
    }
}
