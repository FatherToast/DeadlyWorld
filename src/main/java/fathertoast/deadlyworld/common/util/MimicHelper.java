package fathertoast.deadlyworld.common.util;

import fathertoast.deadlyworld.common.core.registry.DWEntities;
import fathertoast.deadlyworld.common.core.registry.DWItems;
import fathertoast.deadlyworld.common.core.registry.DWSoundEvents;
import fathertoast.deadlyworld.common.entity.ChestMimic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MimicHelper {

    /**
     * Attempts to spawn a Chest Mimic from a chest at the given location.
     *
     * @param level The world we live in. Absolutely mad.
     * @param pos The block position of the chest we are spawning from.
     * @param state The block state of the chest.
     * @param chestBlockEntity The chest block entity.
     * @param player Optionally the player who caused this mimic to be spawned.
     * @return True if the mimic was successfully spawned, false if not.
     */
    public static boolean spawnChestMimicFrom( ServerLevel level, BlockPos pos, BlockState state, ChestBlockEntity chestBlockEntity, @Nullable Player player ) {
        boolean spawned = false;

        chestBlockEntity.unpackLootTable( player );
        boolean spawnMimic = false;

        // If inventory contains mimic core, do mimic stuff
        for ( ItemStack itemStack : chestBlockEntity.getItems() ) {
            if ( itemStack.getItem() == DWItems.MIMIC_CORE.get() ) {
                spawnMimic = true;
                break;
            }
        }
        if ( !spawnMimic || level.isClientSide )
            return false;

        // Spawn mimic!
        ChestMimic chestMimic = DWEntities.CHEST_MIMIC.get().create( level );

        if ( chestMimic != null ) {
            chestMimic.setDisguiseState( state );

            // Copy block rotation over to mimic for smoother transition
            Direction facing = Direction.NORTH;

            if ( state.hasProperty( ChestBlock.FACING ) ) {
                facing = state.getValue( ChestBlock.FACING );
            }
            chestMimic.setPos( pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5 );
            chestMimic.setYHeadRot( facing.toYRot() );

            // Copy items from chest and save them to the mimic
            chestMimic.setItems( chestBlockEntity.getItems() );
            chestBlockEntity.getItems().clear();

            level.addFreshEntity( chestMimic );

            if ( chestMimic.isAddedToWorld() ) {
                spawned = true;
                if ( player != null )
                    chestMimic.setTarget( player );

                chestMimic.playSound( DWSoundEvents.MIMIC_APPEAR.get() );

                // Poof cloud
                level.sendParticles(
                        ParticleTypes.CLOUD,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        10,
                        level.random.nextGaussian(),
                        level.random.nextGaussian(),
                        level.random.nextGaussian(),
                        0.1
                );
            }
        }
        return spawned;
    }
}
