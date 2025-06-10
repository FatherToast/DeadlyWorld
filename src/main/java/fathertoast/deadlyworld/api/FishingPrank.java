package fathertoast.deadlyworld.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * The logic for a fishing prank.<br><br>
 * To register a new prank, create a deferred register using
 * {@link DWRegistries#FISHING_PRANKS_REGISTRY}
 */
public interface FishingPrank {

    /**
     * @param level The world we live in. Absolutely mad.
     * @param player The player that is fishing.
     * @param hookPos The position of the fishing hook in the world.
     * @param moveVec The default movement vector applied to items when they are reeled in.
     */
    void prank( ServerLevel level, ServerPlayer player, Vec3 hookPos, Vec3 moveVec );

    /**
     * Called before executing the prank. If this check does not pass,
     * the prank is canceled.
     *
     * @param level The world be live in. Absolutely mad.
     * @param player The player that is fishing.
     * @param hookPos The position of the fishing hook.
     * @return True if this prank can be executed. False otherwise.
     */
    default boolean canUse( ServerLevel level, ServerPlayer player, Vec3 hookPos ) { return true; }
}
