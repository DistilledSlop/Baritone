package baritone.process;

/** Decides when cached locations need a fresh loaded-chunk scan. */
final class WorldScanPolicy {
    private WorldScanPolicy() { }

    static boolean scanLoaded(boolean mining, boolean hasUntrackedBlocks, boolean extendCache,
                              int cachedCount, int max) {
        return !mining || hasUntrackedBlocks || (extendCache && cachedCount < max);
    }
}
