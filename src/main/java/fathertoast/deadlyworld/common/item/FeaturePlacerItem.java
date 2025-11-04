package fathertoast.deadlyworld.common.item;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.lib.DeferredAction;
import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWItems;
import fathertoast.deadlyworld.common.core.registry.DWTags;
import fathertoast.deadlyworld.common.world.levelgen.trap.DeadlyFeature;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * An item that can be used to place configured features with right click.
 * <p>
 * The feature to place is defined by the item's NBT; it does nothing without its NBT set.
 * <p>
 * Based somewhat on {@link net.minecraft.world.level.block.grower.AbstractTreeGrower} logic.
 */
public class FeaturePlacerItem extends Item implements ICustomTabContents {
    
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
    
    /** @return A list of all feature keys (as strings) of configured features that can be placed by a feature placer. */
    public static List<String> buildFeatureKeysList( ServerLevel level ) {
        final ArrayList<String> featureKeys = new ArrayList<>();
        final Registry<ConfiguredFeature<?, ?>> registry = level.registryAccess().registryOrThrow( Registries.CONFIGURED_FEATURE );
        
        buildFeatureKeysFor( featureKeys, registry, DWTags.ConfiguredFeatures.OVERWORLD );
        buildFeatureKeysFor( featureKeys, registry, DWTags.ConfiguredFeatures.THE_NETHER );
        buildFeatureKeysFor( featureKeys, registry, DWTags.ConfiguredFeatures.ANY_DIMENSION_POST_DECORATION );
        // Note: ores are tagged as not placeable by default - putting these in just in case
        buildFeatureKeysFor( featureKeys, registry, DWTags.ConfiguredFeatures.OVERWORLD_ORE );
        buildFeatureKeysFor( featureKeys, registry, DWTags.ConfiguredFeatures.THE_NETHER_ORE );
        
        return featureKeys;
    }
    
    private static void buildFeatureKeysFor( ArrayList<String> featureKeys, Registry<ConfiguredFeature<?, ?>> registry,
                                             TagKey<ConfiguredFeature<?, ?>> tag ) {
        registry.getTag( tag ).ifPresent( ( features ) -> features.forEach( ( feature ) -> {
            if( !feature.is( DWTags.ConfiguredFeatures.NOT_PLACEABLE ) ) {
                feature.unwrapKey().ifPresent( ( key ) -> featureKeys.add( key.location().toString() ) );
            }
        } ) );
    }
    
    
    public FeaturePlacerItem( Properties builder ) { super( builder ); }
    
    @Override
    public InteractionResult useOn( UseOnContext context ) {
        if( !(context.getLevel() instanceof ServerLevel level) ) return InteractionResult.SUCCESS;
        
        BlockPos pos = context.getClickedPos().relative( context.getClickedFace() );
        ItemStack item = context.getItemInHand();
        
        DeferredAction.queue( () -> {
            DeadlyFeature.placeSubfeature( level, pos, getFeatureKey( item ), null );
            return true;
        } );
        
        item.shrink( 1 );
        return InteractionResult.CONSUME;
    }
    
    @Override
    public void appendHoverText( ItemStack item, @Nullable Level level, List<Component> tooltip, TooltipFlag verbose ) {
        ResourceLocation featureKey = getFeatureKey( item );
        if( featureKey != null ) {
            String key = featureKey.toString();
            String fallback = tryConvertToReadable( featureKey );
            tooltip.add( CommonComponents.EMPTY );
            tooltip.add( Component.translatable( getDescriptionId() + ".tooltip" ).withStyle( ChatFormatting.GRAY ) );
            tooltip.add( CommonComponents.space().append( Component.translatableWithFallback( getDescriptionId() + ".tooltip." + key, fallback )
                    .withStyle( ChatFormatting.AQUA ) ) );
        }
    }
    
    private static String tryConvertToReadable( ResourceLocation featureKey ) {
        String name = ConfigUtil.properCase( featureKey.getPath().replace( '_', ' ' ) );
        if( !DeadlyWorld.MOD_ID.equals( featureKey.getNamespace() ) ) {
            return ConfigUtil.properCase( featureKey.getNamespace() ) + ": " + name;
        }
        return name;
    }
    
    @Override
    public List<ItemStack> buildTabContents() { return new ArrayList<>(); } // Built in its own tab
}