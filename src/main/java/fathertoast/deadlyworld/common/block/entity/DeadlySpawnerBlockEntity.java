package fathertoast.deadlyworld.common.block.entity;

import fathertoast.crust.api.util.BoxShape;
import fathertoast.crust.api.util.IBlockEntityDebugShapeProvider;
import fathertoast.crust.api.util.IDebugShape;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.world.logic.ISpawnerObject;
import fathertoast.deadlyworld.common.world.logic.ProgressiveDelaySpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Modified copy-paste of {@link net.minecraft.world.level.block.entity.SpawnerBlockEntity}.
 */
public class DeadlySpawnerBlockEntity extends BlockEntity implements ISpawnerObject, IBlockEntityDebugShapeProvider {
    
    private static final Vec3 DEFAULT_EFFECT_OFFSETS = new Vec3( 0.5, 0.2, 0.5 );
    
    protected final ProgressiveDelaySpawner spawnerLogic;
    protected final SpawnerType spawnerType;
    
    public DeadlySpawnerBlockEntity( BlockPos pos, BlockState state ) { this( DWBlockEntities.DEADLY_SPAWNER.get(), pos, state ); }
    
    public DeadlySpawnerBlockEntity( BlockEntityType<?> type, BlockPos pos, BlockState state ) {
        super( type, pos, state );
        spawnerType = ((DeadlySpawnerBlock) state.getBlock()).getSpawnerType();
        spawnerLogic = new ProgressiveDelaySpawner( spawnerType, this );
    }
    
    @Override
    public void load( CompoundTag loadTag ) {
        super.load( loadTag );
        spawnerLogic.load( level, worldPosition, loadTag );
    }
    
    @Override
    protected void saveAdditional( CompoundTag saveTag ) {
        super.saveAdditional( saveTag );
        spawnerLogic.save( saveTag );
    }
    
    public static void clientTick( Level level, BlockPos pos, @SuppressWarnings( "unused" ) BlockState state, DeadlySpawnerBlockEntity blockEntity ) {
        blockEntity.getSpawnerLogic().clientTick( level, pos );
    }
    
    public static void serverTick( Level level, BlockPos pos, @SuppressWarnings( "unused" ) BlockState state, DeadlySpawnerBlockEntity blockEntity ) {
        blockEntity.getSpawnerLogic().serverTick( (ServerLevel) level, pos );
    }
    
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create( this );
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = saveWithoutMetadata();
        tag.remove( ProgressiveDelaySpawner.TAG_DYNAMIC_SPAWN_LIST );
        tag.remove( ProgressiveDelaySpawner.TAG_SPAWN_POTENTIALS );
        tag.remove( ProgressiveDelaySpawner.TAG_IS_MIMIC );
        return tag;
    }
    
    @Override
    public boolean triggerEvent( int id, int type ) {
        return level != null && spawnerLogic.onEventTriggered( level, id ) ||
                super.triggerEvent( id, type );
    }
    
    @Override
    public boolean onlyOpCanSetNbt() { return true; }
    
    @Override // ISpawnerObject
    public void sendUpdate( ProgressiveDelaySpawner spawner, Level level, BlockPos pos ) {
        setChanged();
        BlockState state = level.getBlockState( pos );
        level.sendBlockUpdated( pos, state, state, Block.UPDATE_ALL );
    }
    
    @Override // ISpawnerObject
    public void broadcastEvent( ProgressiveDelaySpawner spawner, Level level, BlockPos pos, int eventId ) {
        level.blockEvent( pos, DWBlocks.spawner( spawnerType ).get(), eventId, 0 );
    }
    
    @Override // ISpawnerObject
    public void spawnEffectParticle( ProgressiveDelaySpawner spawner, Level level, BlockPos pos ) {
        RandomSource random = level.getRandom();
        double x = (double) pos.getX() + random.nextDouble();
        double y = (double) pos.getY() + random.nextDouble();
        double z = (double) pos.getZ() + random.nextDouble();
        level.addParticle( ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0 );
        level.addParticle( ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0 );
    }
    
    public ProgressiveDelaySpawner getSpawnerLogic() { return spawnerLogic; }
    
    public void setEntityId( EntityType<?> entityType, RandomSource random ) {
        spawnerLogic.setEntityId( entityType, level, random, worldPosition );
    }
    
    public float getEntityRenderScale() { return 0.53125F; }
    
    public Vec3 getEntityRenderOffset() { return DEFAULT_EFFECT_OFFSETS; }
    
    @Nullable
    @Override // IBlockEntityDebugShapeProvider
    public List<IDebugShape> getDebugShapes() {
        // Show spawn range (activation range is spherical, so won't work yet)
        return List.of( new BoxShape( new AABB( worldPosition )
                .inflate( spawnerLogic.getSpawnRange(), 1.0, spawnerLogic.getSpawnRange() ) ) );
    }
}