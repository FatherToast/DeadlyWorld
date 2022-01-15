package fathertoast.deadlyworld.block;

import fathertoast.deadlyworld.*;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public
class SilverfishBlockEventHandler
{
	@SubscribeEvent( priority = EventPriority.NORMAL )
	public
	void onEntitySpawn( EntityJoinWorldEvent event )
	{
		if( !event.getWorld( ).isRemote && event.getEntity( ) instanceof EntitySilverfish ) {
			// Replace the silverfish ais with ones that recognize this mod's silverfish blocks
			EntitySilverfish silverfish = (EntitySilverfish) event.getEntity( );
			
			boolean addedHideAI   = false;
			boolean addedSummonAI = false;
			for( EntityAITasks.EntityAITaskEntry entry : silverfish.tasks.taskEntries.toArray( new EntityAITasks.EntityAITaskEntry[ 0 ] ) ) {
				Class< ? > enclosing = entry.action.getClass( ).getEnclosingClass( );
				if( enclosing != null && enclosing.equals( EntitySilverfish.class ) ) {
					// Remove the old ai
					silverfish.tasks.removeTask( entry.action );
					
					// Add the replacement ai
					if( !addedHideAI && entry.action instanceof EntityAIWander ) {
						addedHideAI = true;
						silverfish.tasks.addTask( entry.priority, new EntityAIHideInBlock( silverfish ) );
					}
					else if( !addedSummonAI ) {
						addedSummonAI = true;
						silverfish.tasks.addTask( entry.priority, new EntityAISummonSilverfish( silverfish ) );
					}
					
					// Break out once both ais have been added
					if( addedHideAI && addedSummonAI ) {
						break;
					}
				}
			}
		}
	}
	
	@SubscribeEvent( priority = EventPriority.HIGHEST )
	public
	void onLivingAttack( LivingAttackEvent event )
	{
		if( !event.getEntity( ).world.isRemote && event.getEntity( ) instanceof EntitySilverfish ) {
			if( event.getSource( ) instanceof EntityDamageSource || event.getSource( ) == DamageSource.MAGIC ) {
				// Trigger the 'call for help' ai
				EntitySilverfish silverfish = (EntitySilverfish) event.getEntity( );
				for( EntityAITasks.EntityAITaskEntry entry : silverfish.tasks.taskEntries.toArray( new EntityAITasks.EntityAITaskEntry[ 0 ] ) ) {
					if( entry.action instanceof EntityAISummonSilverfish ) {
						((EntityAISummonSilverfish) entry.action).notifyHurt( );
						break;
					}
				}
			}
		}
	}
	
	static
	class EntityAIHideInBlock extends EntityAIWander
	{
		private EnumFacing facing;
		private boolean    doMerge;
		
		EntityAIHideInBlock( EntitySilverfish silverfish )
		{
			super( silverfish, 1.0, 10 );
			setMutexBits( 1 );
		}
		
		@Override
		public
		boolean shouldExecute( )
		{
			if( entity.getAttackTarget( ) != null ) {
				return false;
			}
			else if( !entity.getNavigator( ).noPath( ) ) {
				return false;
			}
			else {
				Random random = entity.getRNG( );
				
				if( ForgeEventFactory.getMobGriefingEvent( entity.world, entity ) && random.nextInt( 10 ) == 0 ) {
					facing = EnumFacing.random( random );
					BlockPos    pos   = new BlockPos( entity.posX, entity.posY + 0.5, entity.posZ ).offset( facing );
					IBlockState state = entity.world.getBlockState( pos );
					
					if( BlockSilverfish.canContainSilverfish( state ) || ModObjects.getInfestedVersionIfReplaceable( state ) != null ) {
						doMerge = true;
						return true;
					}
				}
				
				doMerge = false;
				return super.shouldExecute( );
			}
		}
		
		@Override
		public
		boolean shouldContinueExecuting( ) { return !doMerge && super.shouldContinueExecuting( ); }
		
		@Override
		public
		void startExecuting( )
		{
			if( !doMerge ) {
				super.startExecuting( );
			}
			else {
				BlockPos    pos           = new BlockPos( entity.posX, entity.posY + 0.5, entity.posZ ).offset( facing );
				IBlockState state         = entity.world.getBlockState( pos );
				IBlockState infestedState = ModObjects.getInfestedVersion( state );
				
				boolean success;
				if( infestedState != null ) {
					// Silverfish block added by this mod
					entity.world.setBlockState( pos, infestedState, 3 );
					success = true;
				}
				else if( BlockSilverfish.canContainSilverfish( state ) ) {
					// Vanilla silverfish block
					entity.world.setBlockState( pos, Blocks.MONSTER_EGG.getDefaultState( ).withProperty( BlockSilverfish.VARIANT, BlockSilverfish.EnumType.forModelBlock( state ) ), 3 );
					success = true;
				}
				else {
					success = false;
				}
				if( success ) {
					entity.spawnExplosionParticle( );
					entity.setDead( );
				}
			}
		}
	}
	
	static
	class EntityAISummonSilverfish extends EntityAIBase
	{
		private final EntitySilverfish silverfish;
		private       int              lookForFriends;
		
		EntityAISummonSilverfish( EntitySilverfish silverfishIn )
		{
			silverfish = silverfishIn;
		}
		
		void notifyHurt( )
		{
			if( lookForFriends == 0 ) {
				lookForFriends = 20;
			}
		}
		
		@Override
		public
		boolean shouldExecute( ) { return lookForFriends > 0; }
		
		@Override
		public
		void updateTask( )
		{
			lookForFriends--;
			if( lookForFriends <= 0 ) {
				summonSilverfish( );
			}
		}
		
		void summonSilverfish( )
		{
			World    world         = silverfish.world;
			Random   random        = silverfish.getRNG( );
			BlockPos silverfishPos = new BlockPos( silverfish );
			
			// Search for surrounding silverfish blocks
			for( int y = 0; y <= 5 && y >= -5; y = (y <= 0 ? 1 : 0) - y ) {
				for( int x = 0; x <= 10 && x >= -10; x = (x <= 0 ? 1 : 0) - x ) {
					for( int z = 0; z <= 10 && z >= -10; z = (z <= 0 ? 1 : 0) - z ) {
						BlockPos    pos   = silverfishPos.add( x, y, z );
						IBlockState state = world.getBlockState( pos );
						
						if( state.getBlock( ) instanceof BlockDeadlySilverfish || state.getBlock( ) == Blocks.MONSTER_EGG ) {
							if( ForgeEventFactory.getMobGriefingEvent( world, silverfish ) ) {
								// 'Harvest' the block, releasing the silverfish inside
								world.destroyBlock( pos, true );
							}
							else if( state.getBlock( ) instanceof BlockDeadlySilverfish ) {
								// Mod block grief failure
								world.setBlockState( pos, ((BlockDeadlySilverfish) state.getBlock( )).toDisguise( state ), 3 );
							}
							else {
								// Vanilla block grief failure
								world.setBlockState( pos, state.getValue( BlockSilverfish.VARIANT ).getModelBlock( ), 3 );
							}
							
							// Chance to stop searching after each silverfish block found
							if( random.nextBoolean( ) ) {
								return;
							}
						}
					}
				}
			}
		}
	}
}
