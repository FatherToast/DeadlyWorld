package fathertoast.deadlyworld.common.item;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.deadlyworld.common.core.registry.DWItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import javax.annotation.Nullable;
import java.util.List;

/**
 * An item that can be used to place configured features with right click.
 * <p>
 * The feature to place is defined by the item's NBT; it does nothing without its NBT set.
 * <p>
 * Based somewhat on {@link net.minecraft.world.level.block.grower.AbstractTreeGrower} logic.
 */
public class FeaturePlacerItem extends Item {
    
    /** The NBT tag for the configured feature's resource location. */
    public static final String TAG_FEATURE = "CfgFeature";
    
    /** @return A new feature placer item stack that will place the specified configured feature. */
    public static ItemStack of( ResourceLocation cfgFeatureKey ) {
        return setFeatureKey( new ItemStack( DWItems.FEATURE_PLACER.get() ), cfgFeatureKey );
    }
    
    /** @return The item stack, set to place the specified configured feature. */
    public static ItemStack setFeatureKey( ItemStack item, ResourceLocation cfgFeatureKey ) {
        item.getOrCreateTag().putString( TAG_FEATURE, cfgFeatureKey.toString() );
        return item;
    }
    
    /** @return The registry key resource location of the configured feature to place. */
    @Nullable
    public static ResourceLocation getFeatureKey( ItemStack item ) { return getFeatureKey( item.getTag() ); }
    
    /** @return The registry key resource location of the configured feature to place. */
    @Nullable
    private static ResourceLocation getFeatureKey( @Nullable CompoundTag tag ) {
        if( tag == null || !NBTHelper.containsString( tag, TAG_FEATURE ) ) return null;
        return ResourceLocation.tryParse( tag.getString( TAG_FEATURE ) );
    }
    
    
    public FeaturePlacerItem( Properties builder ) { super( builder ); }
    
    @Override
    public InteractionResult useOn( UseOnContext context ) {
        if( !(context.getLevel() instanceof ServerLevel level) ) return InteractionResult.SUCCESS;
        
        BlockPos pos = context.getClickedPos().relative( context.getClickedFace() );
        ItemStack item = context.getItemInHand();
        
        final Registry<ConfiguredFeature<?, ?>> registry = level.registryAccess().registryOrThrow( Registries.CONFIGURED_FEATURE );
        ConfiguredFeature<?, ?> feature = registry.get( getFeatureKey( item ) );
        
        if( feature != null && feature.place( level, level.getChunkSource().getGenerator(), level.getRandom(), pos ) ) {
            item.shrink( 1 );
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }
    
    @Override
    public void appendHoverText( ItemStack item, @Nullable Level level, List<Component> tooltip, TooltipFlag verbose ) {
        ResourceLocation featureKey = getFeatureKey( item );
        if( featureKey != null ) {
            tooltip.add( Component.literal( featureKey.toString() ).withStyle( ChatFormatting.GRAY ) );
        }
    }
}