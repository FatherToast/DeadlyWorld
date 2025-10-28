package fathertoast.deadlyworld.common.util;

import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.field.WeightedPotionList;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public final class TrapHelper {
    
    /** @return True if any player within range can activate a spawner, optionally requiring line-of-sight (ray trace). */
    public static boolean isPlayerInSpawnerRange( Level level, BlockPos pos, double range, boolean checkSight ) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        
        double rangeSq = range * range;
        for( int i = 0; i < level.players().size(); i++ ) {
            Player player = level.players().get( i );
            if( canActivateSpawner( player ) && player.distanceToSqr( x, y, z ) <= rangeSq &&
                    ( !checkSight || canEntitySeeBlock( level, pos, player ) ) ) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return An arbitrary player within range that can be targeted by a trap, optionally requiring line-of-sight (ray trace).
     * Null if there are no valid targets.
     */
    @Nullable
    public static Player getTrapTargetInRange( Level level, BlockPos pos, double range, boolean checkSight ) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        
        double rangeSq = range * range;
        for( int i = 0; i < level.players().size(); i++ ) {
            Player player = level.players().get( i );
            if( canTrapTarget( player ) && player.distanceToSqr( x, y, z ) <= rangeSq &&
                    ( !checkSight || canEntitySeeBlock( level, pos, player ) ) ) {
                return player;
            }
        }
        return null;
    }
    
    /**
     * @return The closest player within range that can be targeted by a trap, optionally requiring line-of-sight (ray trace).
     * Null if there are no valid targets.
     */
    @Nullable
    public static Player getNearestTrapTargetInRange( Level level, BlockPos pos, double range, boolean checkSight ) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        
        double rangeSq = range * range;
        Player closestPlayer = null;
        double closestDistSq = Double.POSITIVE_INFINITY;
        
        for( Player player : level.players() ) {
            double distSq = player.distanceToSqr( x, y, z );
            
            if( canTrapTarget( player ) && distSq <= rangeSq && distSq < closestDistSq &&
                    ( !checkSight || canEntitySeeBlock( level, pos, player ) ) ) {
                closestPlayer = player;
                closestDistSq = distSq;
            }
        }
        return closestPlayer;
    }
    
    /** @return True if the entity can activate a spawner. */
    public static boolean canActivateSpawner( Entity entity ) {
        return isTangible( entity ) && (Config.MAIN.GENERAL.activateSpawnersVsCreative.get() || isVulnerable( entity ));
    }
    
    /** @return True if the entity can be targeted by a trap. */
    public static boolean canTrapTarget( Entity entity ) {
        return ( Config.MAIN.GENERAL.activateTrapsInPeaceful.get() || entity.level().getDifficulty() != Difficulty.PEACEFUL ) &&
                isTangible( entity ) && (Config.MAIN.GENERAL.activateTrapsVsCreative.get() || isVulnerable( entity ));
    }
    
    /** @return True if the entity is vulnerable (not invulnerable nor a creative mode player). */
    public static boolean isVulnerable( Entity entity ) { return !entity.isInvulnerable() && (!(entity instanceof Player) || !((Player) entity).isCreative()); }
    
    /** @return True if the entity is tangible (not spectating nor dead). */
    public static boolean isTangible( Entity entity ) { return entity.isAlive() && !entity.isSpectator(); }
    
    /** @return True if the entity has clear line-of-sight to the block position. This is a ray trace, please use responsibly. */
    public static boolean canEntitySeeBlock( Level level, BlockPos pos, Entity entity ) {
        BlockHitResult result = level.clip( new ClipContext(
                new Vec3( entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ() ),
                new Vec3( pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5 ),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null ) );
        
        // No colliding blocks in the path or at the destination, can see
        return result.getType() == BlockHitResult.Type.MISS ||
                // Hit something, can see if the passed position (or the block above) is the hit
                (result.getBlockPos().equals( pos ) || result.getBlockPos().equals( pos.offset( 0, 1, 0 ) ));
    }
    
    public static boolean isSolidBlock( BlockGetter level, BlockPos pos ) {
        BlockState state = level.getBlockState( pos );
        
        for( Direction direction : Direction.values() ) {
            if( !state.isFaceSturdy( level, pos, direction ) )
                return false;
        }
        return true;
    }

    /** Creates a "cloud poof" effect with cloud particles. */
    public static void spawnPoofCloud( ServerLevel level, BlockPos pos ) {
        level.sendParticles( ParticleTypes.CLOUD,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                10,
                0, 0, 0,
                0.1 );
    }
    
    /** @return A thrown potion item stack with a randomly picked effect instance from the given weighted potion list. */
    public static ItemStack getPotionFromList( WeightedPotionList potionList, RandomSource random ) {
        ItemStack potionStack = new ItemStack( Items.SPLASH_POTION );
        MobEffectInstance effectInstance = potionList.next( random );
        
        if( effectInstance != null ) {
            PotionUtils.setCustomEffects( potionStack, List.of( effectInstance ) );
        }
        setStackPotionColor( potionStack );
        return potionStack;
    }
    
    public static ItemStack getPotionFromInstance( @Nullable MobEffectInstance effectInstance ) {
        ItemStack potionStack = new ItemStack( Items.SPLASH_POTION );
        
        if( effectInstance != null ) {
            PotionUtils.setCustomEffects( potionStack, List.of( effectInstance ) );
        }
        setStackPotionColor( potionStack );
        return potionStack;
    }
    
    public static void setStackPotionColor( ItemStack potionStack ) {
        List<MobEffectInstance> effects = PotionUtils.getCustomEffects( potionStack );
        if( !effects.isEmpty() ) {
            int color = PotionUtils.getColor( effects );
            potionStack.getOrCreateTag().putInt( PotionUtils.TAG_CUSTOM_POTION_COLOR, color );
        }
    }
    
    // TODO - Might as well move this to Crust at some point
    
    /**
     * Modified copy-paste of {@link net.minecraft.nbt.NbtUtils#readBlockState(HolderGetter, CompoundTag)}.<br>
     * Original implementation requires level access. This one checks the forge registry for blocks.
     */
    public static BlockState readBlockState( CompoundTag compoundTag ) {
        if( !compoundTag.contains( "Name", Tag.TAG_STRING ) ) {
            return Blocks.AIR.defaultBlockState();
        }
        else {
            ResourceLocation blockId = ResourceLocation.parse( compoundTag.getString( "Name" ) );
            Block block = ForgeRegistries.BLOCKS.getValue( blockId );
            
            if( block == null ) {
                return Blocks.AIR.defaultBlockState();
            }
            else {
                BlockState blockState = block.defaultBlockState();
                
                if( compoundTag.contains( "Properties", Tag.TAG_COMPOUND ) ) {
                    CompoundTag propertiesTag = compoundTag.getCompound( "Properties" );
                    StateDefinition<Block, BlockState> statedefinition = block.getStateDefinition();
                    
                    for( String key : propertiesTag.getAllKeys() ) {
                        Property<?> property = statedefinition.getProperty( key );
                        
                        if( property != null ) {
                            blockState = setValueHelper( blockState, property, key, propertiesTag, compoundTag );
                        }
                    }
                }
                return blockState;
            }
        }
    }
    
    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper( S state, Property<T> property, String key,
                                                                                            CompoundTag propertiesTag, CompoundTag blockStateBlock ) {
        Optional<T> optional = property.getValue( propertiesTag.getString( key ) );
        
        if( optional.isPresent() ) {
            return state.setValue( property, optional.get() );
        }
        else {
            DeadlyWorld.LOG.warn( "Unable to read property: {} with value: {} for blockstate: {}", key, propertiesTag.getString( key ), blockStateBlock.toString() );
            return state;
        }
    }
}