package fathertoast.deadlyworld.common.item;

import fathertoast.deadlyworld.common.config.dimension.ChestConfig;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * This is intended to be implemented by enums and registered in the chest config, with one
 * {@link ChestConfig.EventChestTypeCategory} and one {@link EventItem} per event type enum.
 */
public interface IEventType {
    /** @return This event's id, used for the config field and localization keys. */
    String getId();
    
    /** @return This event's default weight in the config. */
    int getDefaultWeight();
    
    /** @return This event's description, used for the config field comment (e.g., "chance to ____ when triggered"). */
    String getDescription();
    
    /** @return This event's index. */
    int getIndex();
    
    /** @return This event's feature config. */
    ChestConfig.EventChestTypeCategory getFeatureConfig( DimensionConfigGroup dimConfigs );
    
    /** Triggers this event. */
    void triggerEvent( ServerLevel level, DimensionConfigGroup dimConfigs, BlockPos pos, BlockState state, Direction blockFacing, @Nullable Player player, ItemStack item );
}