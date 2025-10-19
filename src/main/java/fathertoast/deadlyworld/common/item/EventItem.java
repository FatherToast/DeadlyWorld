package fathertoast.deadlyworld.common.item;

import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * An item that, when placed in a compatible container, triggers an event when that
 * container is either opened or destroyed.
 */
public class EventItem<T extends IEventType> extends Item implements ICustomTabContents {
    
    /** The NBT tag for the event's save id. */
    public static final String TAG_EVENT = "EventId";
    
    /** @return The item stack, now set to trigger the specified event by its save id. */
    public static ItemStack saveId( ItemStack item, byte id ) {
        item.getOrCreateTag().putByte( TAG_EVENT, id );
        return item;
    }
    
    /** @return The event id the item was set to trigger. */
    public static byte loadId( ItemStack item ) { return loadId( item.getTag() ); }
    
    /** @return The event id the item was set to trigger. */
    public static byte loadId( @Nullable CompoundTag tag ) { return tag == null ? 0 : tag.getByte( TAG_EVENT ); }
    
    
    /** This event item's event types. */
    public final T[] eventTypes;
    
    public EventItem( T[] types, Properties builder ) {
        super( builder );
        eventTypes = types;
    }
    
    @Override
    public void appendHoverText( ItemStack item, @Nullable Level level, List<Component> tooltip, TooltipFlag verbose ) {
        T event = getEvent( item );
        if( event != null ) {
            tooltip.add( CommonComponents.EMPTY );
            tooltip.add( Component.translatable( getDescriptionId() + ".tooltip" ).withStyle( ChatFormatting.GRAY ) );
            tooltip.add( CommonComponents.space().append( Component.translatable( getDescriptionId() + ".tooltip." + event.getId() )
                    .withStyle( ChatFormatting.RED ) ) );
        }
    }
    
    /** @return A new event item stack that will trigger the specified event. */
    public ItemStack of( T type ) {
        return saveId( new ItemStack( this ), toSaveId( type ) );
    }
    
    /** Triggers the item's event. */
    public void triggerEvent( ServerLevel level, BlockPos pos, BlockState state, Direction blockFacing, @Nullable Player player, ItemStack item ) {
        final DimensionConfigGroup dimConfigs = Config.getDimensionConfigs( level );
        loadForTrigger( loadId( item ), dimConfigs, level.random ).triggerEvent( level, dimConfigs, pos, state, blockFacing, player, item );
    }
    
    
    /** @return The event the item was set to trigger, if any. */
    @Nullable
    private T getEvent( ItemStack item ) { return fromIndex( loadId( item ) - 1 ); }
    
    // We use index + 1 for the save id so that when the save tag is not present, the loaded id of 0 is the default behavior
    private byte toSaveId( @Nullable T type ) { return (byte) (type == null ? 0 : type.getIndex() + 1); }
    
    private T loadForTrigger( int id, DimensionConfigGroup dimConfigs, RandomSource random ) {
        return id == 0 ? nextEvent( dimConfigs, random ) : fromIndex( id - 1, random );
    }
    
    private T nextEvent( DimensionConfigGroup dimConfigs, RandomSource random ) {
        return fromIndex( eventTypes[0].getFeatureConfig( dimConfigs ).nextEventIndex( random ), random );
    }
    
    private T fromIndex( int index, RandomSource random ) {
        T type = fromIndex( index );
        // On invalid index, pick one at random (unweighted)
        return type != null ? type : eventTypes[random.nextInt( eventTypes.length )];
    }
    
    @Nullable
    private T fromIndex( int index ) { return index < 0 || index >= eventTypes.length ? null : eventTypes[index]; }
    
    @Override
    public List<ItemStack> buildTabContents() {
        List<ItemStack> contents = new ArrayList<>();
        contents.add( new ItemStack( this ) );
        for( T type : eventTypes ) contents.add( this.of( type ) );
        return contents;
    }
}