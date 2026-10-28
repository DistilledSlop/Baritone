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

import baritone.api.utils.Rotation;

/**
 * Turns the rotation Baritone wants into the rotation a human hand could actually produce this tick.
 * <p>
 * {@link #smooth} is consulted from {@code LookBehavior}'s aim processor, which Baritone may call several times within
 * one game tick (and on forked processors while simulating), so it must be a pure function of its arguments and of the
 * smoother's own state. State is advanced exactly once per real game tick through {@link #onTick}.
 */
public interface RotationSmoother {

    /**
     * @param previous the rotation the server currently believes (the last one sent)
     * @param desired  the rotation Baritone wants to look at
     * @return the rotation to adopt this tick; the result is still quantized to mouse pixels by Baritone afterwards
     */
    Rotation smooth(Rotation previous, Rotation desired);

    /**
     * Called once per game tick, after the aim processor has ticked.
     */
    default void onTick() {}

    /**
     * Returns an independent snapshot for speculative aim simulation. A null result leaves
     * simulations unsmoothed; a simulation must never borrow the live camera's mutable state.
     */
    default RotationSmoother fork() { return null; }
}
