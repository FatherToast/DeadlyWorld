package fathertoast.deadlyworld.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public
class DeadlyEventHandler
{
	// Track player interactions with entities and tile entities so we can get a better position for event item triggers.
	private final Map< EntityPlayer, TileEntity > TILE_ENTITY_INTERACTIONS = new ConcurrentHashMap<>( );
	private final Map< EntityPlayer, Entity >     ENTITY_INTERACTIONS      = new ConcurrentHashMap<>( );
	
	@SubscribeEvent( priority = EventPriority.HIGHEST )
	public
	void onEntityJoinWorld( EntityJoinWorldEvent event )
	{
		// Trigger event for dropped event item
		if( event.getEntity( ) instanceof EntityItem ) {
			ItemStack stack = ((EntityItem) event.getEntity( )).getItem( );
			
			Item item = stack.getItem( );
			if( item instanceof ItemDeadlyEvent ) {
				((ItemDeadlyEvent) stack.getItem( )).trigger( stack, event.getWorld( ), event.getEntity( ).getPositionVector( ), null );
				event.getEntity( ).setDead( );
				event.setCanceled( true );
			}
		}
	}
	
	@SubscribeEvent( priority = EventPriority.HIGH )
	public
	void onContainerOpen( PlayerContainerEvent.Open event )
	{
		// Check for any event items in container inventory and trigger them
		if( !event.getEntityPlayer( ).world.isRemote )
		{
			// Use the position of the block or entity if available, otherwise just use the player position
			Vec3d position;
			if( TILE_ENTITY_INTERACTIONS.containsKey( event.getEntityPlayer( ) ) ) {
				position = new Vec3d( TILE_ENTITY_INTERACTIONS.get( event.getEntityPlayer( ) ).getPos( ) ).addVector( 0.5, 0, 0.5 );
			}
			else if( ENTITY_INTERACTIONS.containsKey( event.getEntityPlayer( ) ) ) {
				position = ENTITY_INTERACTIONS.get( event.getEntityPlayer( ) ).getPositionVector( );
			}
			else {
				position = event.getEntityPlayer( ).getPositionVector( );
			}
			
			// Find all events to trigger and remove them from inventory
			List< ItemStack > eventsToTrigger = new ArrayList<>( );
			for( int i = 0; i < event.getContainer( ).inventoryItemStacks.size( ); i++ ) {
				ItemStack stack = event.getContainer( ).inventoryItemStacks.get( i );
				
				if( stack.getItem( ) instanceof ItemDeadlyEvent ) {
					eventsToTrigger.add( stack );
					event.getContainer( ).putStackInSlot( i, ItemStack.EMPTY );
				}
			}
			if( !eventsToTrigger.isEmpty( ) ) {
				// Update container contents
				event.getContainer( ).detectAndSendChanges( );
			}
			
			// Trigger the events found
			for( ItemStack stack : eventsToTrigger ) {
				((ItemDeadlyEvent) stack.getItem( )).trigger( stack, event.getEntityPlayer( ).world, position, event.getEntityPlayer( ) );
			}
		}
	}
	
	@SubscribeEvent( priority = EventPriority.LOWEST )
	public
	void recordRightClickBlock( PlayerInteractEvent.RightClickBlock event )
	{
		if( !event.getWorld( ).isRemote ) {
			// Track player interacting with tile entity
			TileEntity tile = event.getWorld( ).getTileEntity( event.getPos( ) );
			if( tile != null ) {
				TILE_ENTITY_INTERACTIONS.put( event.getEntityPlayer( ), tile );
			}
		}
	}
	
	@SubscribeEvent( priority = EventPriority.LOWEST )
	public
	void recordRightClickEntity( PlayerInteractEvent.EntityInteract event )
	{
		if( !event.getWorld( ).isRemote ) {
			// Track player interacting with entity
			ENTITY_INTERACTIONS.put( event.getEntityPlayer( ), event.getTarget( ) );
		}
	}
	
	@SubscribeEvent( priority = EventPriority.NORMAL )
	public
	void onServerTick( TickEvent.ServerTickEvent event )
	{
		// Clear tracked interactions
		if( !TILE_ENTITY_INTERACTIONS.isEmpty( ) ) {
			TILE_ENTITY_INTERACTIONS.clear( );
		}
		if( !ENTITY_INTERACTIONS.isEmpty( ) ) {
			ENTITY_INTERACTIONS.clear( );
		}
	}
}
