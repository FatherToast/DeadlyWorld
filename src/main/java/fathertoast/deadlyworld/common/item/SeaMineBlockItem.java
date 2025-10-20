package fathertoast.deadlyworld.common.item;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineBlock;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.List;
import java.util.function.Predicate;

public class SeaMineBlockItem extends BlockItem {
    
    public static final String MOD_DATA_KEY = "DeadlyWorldData";
    public static final String ARMED_KEY = "IsArmed";
    
    private final SeaMineType type;
    
    public SeaMineBlockItem( SeaMineBlock block, Properties properties ) {
        super( block, properties );
        this.type = block.getSeaMineType();
    }
    
    /**
     * Called from {@link fathertoast.deadlyworld.common.event.GameEventHandler#onLivingTick(LivingEvent.LivingTickEvent)}.
     * <br><br>
     * Checks if the sea mine item stack is armed, and triggers a mine explosion if so.<br>
     * Otherwise, we check for nearby valid player targets and arm the sea mine item stack.
     */
    public void onLivingUpdate( LivingEntity livingEntity, ItemStack seaMine ) {
        if( livingEntity.level().isClientSide ) return;
        
        // Single second delay between each check
        if( livingEntity.tickCount % 20 == 0 ) {
            Level level = livingEntity.level();
            
            CompoundTag tag = seaMine.getOrCreateTag();
            CompoundTag modData = NBTHelper.getOrCreateCompound( tag, MOD_DATA_KEY );
            
            if( modData.contains( ARMED_KEY, Tag.TAG_BYTE ) && modData.getBoolean( ARMED_KEY ) ) {
                DWBlocks.seaMine( type ).get().explode( level, livingEntity.blockPosition(), livingEntity.getRandom() );
                return;
            }
            
            List<Player> nearbyPlayers = level.getEntitiesOfClass( Player.class,
                    new AABB( livingEntity.blockPosition() ).move( 0.0D, livingEntity.getEyeHeight(), 0.0D ).inflate( 1.5D ) );
            
            // Pick appropriate predicate
            Predicate<Player> predicate = Config.MAIN.GENERAL.activateTrapsVsCreative.get()
                    ? SeaMineBlock.NO_SPECTATORS
                    : SeaMineBlock.NO_CREATIVE_OR_SPEC;
            
            for( Player player : nearbyPlayers ) {
                if( predicate.test( player ) ) {
                    modData.putBoolean( ARMED_KEY, true );
                    level.playSound( null, livingEntity.blockPosition(), SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS );
                    break;
                }
            }
        }
    }
}