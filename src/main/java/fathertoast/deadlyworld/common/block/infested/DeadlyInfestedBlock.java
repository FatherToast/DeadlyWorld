package fathertoast.deadlyworld.common.block.infested;

import fathertoast.crust.api.lib.LevelEventHelper;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.InfestedBlocksConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;

public class DeadlyInfestedBlock extends InfestedBlock {
    /**
     * This holds the 'hostBlock' for the current DeadlyInfestedBlock construction.
     * We do this hacky workaround so that we have access to the parameter during block state definition,
     * which is permanently assigned in the superclass constructor before 'hostBlock' is available.
     */
    @Nullable
    protected static Block blockForStateDef;
    
    public static DeadlyInfestedBlock factory( ResourceLocation hostBlockLoc ) {
        Block hostBlock = InfestedBlockAutoGen.getHostBlockOrThrow( hostBlockLoc );
        blockForStateDef = hostBlock;
        DeadlyInfestedBlock infestedBlock = new DeadlyInfestedBlock( hostBlock, hostBlockLoc, copyProperties( hostBlock ) );
        // Note: Properties are copied, but destroy time is fixed at half and explosion resist fixed at 0.75 by superclass
        blockForStateDef = null;
        return infestedBlock;
    }
    
    public static String pathFor( ResourceLocation hostBlockLoc ) {
        return "infested/" + hostBlockLoc.getNamespace() + "/" + hostBlockLoc.getPath();
    }
    
    public static boolean tryCleanseBlock( PlayerInteractEvent.RightClickBlock event, ServerLevel level ) {
        if( config().GENERAL.cleanseTools.isEmpty() ) return false;
        
        ItemStack heldItem = event.getItemStack();
        BlockPos pos = event.getPos();
        BlockState blockState = level.getBlockState( pos );
        
        if( config().GENERAL.cleanseTools.get().containsOrTag( heldItem.getItem(), heldItem::is ) &&
                blockState.getBlock() instanceof InfestedBlock block ) {
            Player player = event.getEntity();
            
            // Spawn the silverfish
            if( config().GENERAL.cleanseSpawnsSilverfish.get() ) {
                Direction facing = event.getHitVec().getDirection();
                BlockPos spawnPos = pos.relative( facing );
                Silverfish silverfish = EntityType.SILVERFISH.create( level );
                if( silverfish != null ) {
                    silverfish.moveTo( spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                            facing.getAxis() == Direction.Axis.Y ? 360.0F * level.random.nextFloat() : facing.toYRot(), 0.0F );
                    level.addFreshEntity( silverfish );
                    silverfish.spawnAnim();
                    silverfish.setTarget( player );
                }
            }
            
            // Replace the infested block with its respective host block
            BlockState hostState = block.hostStateByInfested( blockState );
            level.setBlock( pos, hostState, Block.UPDATE_ALL_IMMEDIATE );
            level.gameEvent( GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of( player, hostState ) );
            LevelEventHelper.BLOCK_BREAK_FX.play( level, null, pos, hostState );
            
            // Damage the item
            if( config().GENERAL.cleanseDamage.get() > 0 ) {
                heldItem.hurtAndBreak( config().GENERAL.cleanseDamage.get(),
                        player, ( p ) -> p.broadcastBreakEvent( event.getHand() ) );
            }
            
            return true;
        }
        return false;
    }
    
    private static InfestedBlocksConfig config() { return Config.INFESTED_BLOCKS; }
    
    
    // Infested block implementation
    
    private final ResourceLocation hostBlockLocation;
    
    protected DeadlyInfestedBlock( Block hostBlock, ResourceLocation hostBlockLoc, BlockBehaviour.Properties properties ) {
        super( hostBlock, properties );
        // May as well grab this since we have it handy, might be useful later
        hostBlockLocation = hostBlockLoc;
        
        // Copy default block state from the host
        if( blockForStateDef != null ) {
            BlockState hostState = blockForStateDef.defaultBlockState();
            BlockState defaultState = defaultBlockState();
            //noinspection rawtypes
            for( Property property : hostState.getProperties() ) {
                //noinspection unchecked
                defaultState.setValue( property, hostState.getValue( property ) );
            }
            registerDefaultState( defaultState );
        }
    }
    
    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        super.createBlockStateDefinition( builder );
        // Copy the block state definition from the host
        if( blockForStateDef != null ) {
            for( Property<?> property : blockForStateDef.getStateDefinition().getProperties() ) {
                builder.add( property );
            }
        }
    }
    
    public ResourceLocation getHostBlockLocation() { return hostBlockLocation; }
    
    //TODO Perhaps AT InfestedBlock#spawnInfestation and override it (or do more work and override spawnAfterBreak)
    // to make spawned silverfish target the entity that breaks their block or copy target when a silverfish breaks it;
    // might require some way to figure out who broke the block...
    
    
    // Host block emulation
    
    public BlockState toInfested( BlockState hostState ) { return infestedStateByHost( hostState ); }
    
    public BlockState toHost( BlockState infestedState ) { return hostStateByInfested( infestedState ); }
    
    @Override
    public MutableComponent getName() {
        return Component.translatable( "block.deadlyworld.infested_block." + config().AUTO_GEN.nameStyle.get().getCode(),
                Component.translatable( getHostBlock().getDescriptionId() ) );
    }
    
    @Override
    public String getDescriptionId() { return getName().getString(); } // Kinda hacky, feels like it might be illegal
    
    // Properties copying
    
    private static BlockBehaviour.Properties copyProperties( Block hostBlock ) {
        final BlockState hostState = hostBlock.defaultBlockState();
        //noinspection deprecation
        return BlockBehaviour.Properties.of()
                .mapColor( hostBlock.defaultMapColor() )
                .friction( hostBlock.getFriction() )
                .speedFactor( hostBlock.getSpeedFactor() )
                .jumpFactor( hostBlock.getJumpFactor() )
                .sound( hostBlock.getSoundType( hostState ) )
                .instrument( hostState.instrument() );
    }
    
    @Override
    public MapColor getMapColor( BlockState infestedState, BlockGetter level, BlockPos pos, MapColor defaultColor ) {
        return getHostBlock().getMapColor( toHost( infestedState ), level, pos, defaultColor );
    }
    
    @Override
    public SoundType getSoundType( BlockState infestedState, LevelReader level, BlockPos pos, @Nullable Entity entity ) {
        return getHostBlock().getSoundType( toHost( infestedState ), level, pos, entity );
    }
    
    @Override
    public int getLightEmission( BlockState infestedState, BlockGetter level, BlockPos pos ) {
        return getHostBlock().getLightEmission( toHost( infestedState ), level, pos );
    }
    
    // Behavior copying
    
    @Override
    public int getFlammability( BlockState infestedState, BlockGetter level, BlockPos pos, Direction direction ) {
        return getHostBlock().getFlammability( toHost( infestedState ), level, pos, direction );
    }
    
    @Override
    public boolean isFlammable( BlockState infestedState, BlockGetter level, BlockPos pos, Direction direction ) {
        return getHostBlock().isFlammable( toHost( infestedState ), level, pos, direction );
    }
    
    @Override
    public int getFireSpreadSpeed( BlockState infestedState, BlockGetter level, BlockPos pos, Direction direction ) {
        return getHostBlock().getFireSpreadSpeed( toHost( infestedState ), level, pos, direction );
    }
    
    @Override
    public boolean isFireSource( BlockState infestedState, LevelReader level, BlockPos pos, Direction direction ) {
        return getHostBlock().isFireSource( toHost( infestedState ), level, pos, direction );
    }
}