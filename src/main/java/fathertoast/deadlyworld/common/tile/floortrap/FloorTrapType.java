package fathertoast.deadlyworld.common.tile.floortrap;

import fathertoast.deadlyworld.common.block.FloorTrapBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.config.FloorTrapConfig;
import fathertoast.deadlyworld.common.core.config.util.EntityList;
import fathertoast.deadlyworld.common.util.References;
import fathertoast.deadlyworld.common.util.TrapHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Function;
import java.util.function.Supplier;

public enum FloorTrapType implements IStringSerializable {

    TNT( "tnt", (dimConfig) -> dimConfig.FLOOR_TRAPS.TNT) {
        @Override
        public void triggerTrap( DimensionConfigGroup dimConfig, FloorTrapTileEntity trapEntity ) {
            FloorTrapConfig.TntTrapTypeCategory config = dimConfig.FLOOR_TRAPS.TNT;
            World world = trapEntity.getLevel();

            double x = trapEntity.getBlockPos().getX() + 0.5;
            double y = trapEntity.getBlockPos().getY() + 1;
            double z = trapEntity.getBlockPos().getZ() + 0.5;

            int fuseRange = config.maxFuseTime.get() - config.minFuseTime.get();
            if( fuseRange <= 0 ) {
                fuseRange = 1;
            }

            // Spawn the primed tnt blocks
            for( int i = 0; i < config.tntCount.get(); i++ ) {
                TNTEntity tnt = new TNTEntity( world, x, y, z, null );

                float speed = (float)config.launchSpeed.get() * world.random.nextFloat( ) + 0.02F;
                tnt.setFuse( config.minFuseTime.get() + world.random.nextInt( fuseRange ) );
                tnt.getDeltaMovement().multiply(speed, 0.1F * world.random.nextDouble(), speed);
                world.addFreshEntity( tnt );
            }
            world.playSound( null, x, y, z, SoundEvents.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F );
        }
    },

    TNT_MOB( "tnt_mob", (dimConfig) -> dimConfig.FLOOR_TRAPS.TNT_MOB ) {
        @Override
        public void triggerTrap( DimensionConfigGroup dimConfig, FloorTrapTileEntity trapEntity ) {
            FloorTrapConfig.TntMobTrapTypeCategory config = dimConfig.FLOOR_TRAPS.TNT_MOB;
            World world = trapEntity.getLevel();

            double x = trapEntity.getBlockPos().getX() + 0.5;
            double y = trapEntity.getBlockPos().getY() + 1;
            double z = trapEntity.getBlockPos().getZ() + 0.5;

            int fuseRange = config.maxFuseTime.get() - config.minFuseTime.get();
            if( fuseRange <= 0 ) {
                fuseRange = 1;
            }

            // Pick an entity to spawn
            EntityType<?> entityType = config.spawnList.get().next( world.random );

            if( entityType == null ) {
                DeadlyWorld.LOG.warn(
                        "TNT mob floor trap received null entity type!" +
                                " - This is probably caused by an error or change in the config for DIM_{} (defaulting to zombie)", world.dimension()
                );
                entityType = EntityType.ZOMBIE;
            }

            // Try to create the entity to spawn
            Entity entity;
            LivingEntity livingEntity = null;
            try {
                entity = entityType.create( world );
            }
            catch( Exception ex ) {
                DeadlyWorld.LOG.error( "Encountered exception while constructing entity '{}'", entityType.getRegistryName(), ex );
                return;
            }

            // Initialize the entity
            entity.setPos( x, y, z );
            entity.setRot(world.random.nextFloat() * 2.0F * (float) Math.PI, 0.0F);
            entity.setDeltaMovement(entity.getDeltaMovement().x, 0.3D, entity.getDeltaMovement().z);

            if( entity instanceof LivingEntity ) {
                livingEntity = (LivingEntity) entity;
                ModifiableAttributeInstance attribute;

                if( config.healthMultiplier.get() != 1.0D ) {
                    try {
                        attribute = livingEntity.getAttribute( Attributes.MAX_HEALTH );
                        attribute.setBaseValue( attribute.getBaseValue() * config.healthMultiplier.get() );
                    }
                    catch( Exception ex ) {
                        // This is fine, entity just doesn't have the attribute
                    }
                }
                if( config.speedMultiplier.get() != 1.0F ) {
                    try {
                        attribute = livingEntity.getAttribute( Attributes.MOVEMENT_SPEED );
                        attribute.setBaseValue( attribute.getBaseValue() * config.speedMultiplier.get() );
                    }
                    catch( Exception ex ) {
                        // This is fine, entity just doesn't have the attribute
                    }
                }
                livingEntity.setHealth( livingEntity.getMaxHealth() );
                livingEntity.setLastHurtByMob( trapEntity.getTarget() );
            }

            // Make the tnt "hat"
            TNTEntity tnt = new TNTEntity( world, x, y, z, livingEntity );
            tnt.copyPosition( entity );
            tnt.setFuse( config.minFuseTime.get() + world.random.nextInt( fuseRange ) );
            tnt.startRiding( entity, true );

            // Spawn the entities and play alert sound
            world.addFreshEntity( entity );
            world.playSound( null, x, y, z, SoundEvents.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F );
        }
    },

    POTION( "potion", (dimConfig) -> dimConfig.FLOOR_TRAPS.POTION ) {
        @Override
        public void triggerTrap( DimensionConfigGroup dimConfig, FloorTrapTileEntity trapEntity ) {
            FloorTrapConfig.PotionTrapTypeCategory config = dimConfig.FLOOR_TRAPS.POTION;

            World world = trapEntity.getLevel();

            double x = trapEntity.getBlockPos().getX( ) + 0.5;
            double y = trapEntity.getBlockPos().getY( ) + 1.1;
            double z = trapEntity.getBlockPos().getZ( ) + 0.5;

            int resetRange = config.maxResetTime.get() - config.minResetTime.get();
            if( resetRange <= 0 ) {
                resetRange = 1;
            }
            trapEntity.disableTrap( config.minResetTime.get() + world.random.nextInt( resetRange ) );

            // Load or pick the trap type
            ItemStack potionStack = trapEntity.getPotionStack(config.potionList.get(), world.random);

            // Spawn the thrown potion
            PotionEntity potionEntity = new PotionEntity( world, x, y, z );
            potionEntity.setItem(potionStack);
            potionEntity.setDeltaMovement(potionEntity.getDeltaMovement().x, 0.33D + 0.04D * world.random.nextDouble(), potionEntity.getDeltaMovement().z);
            world.addFreshEntity( potionEntity );

            world.playSound( null, x, y, z, SoundEvents.DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0F, 1.0F );
        }
    };


    /** The path for loot tables associated with these types. */
    public static final String LOOT_TABLE_PATH = "floor_traps/";
    public static final String CATEGORY = "floor_trap";

    private final String id;
    private final String displayName;
    /** A function that returns the feature config associated with this spawner type for a given dimension config. */
    private final Function<DimensionConfigGroup, FloorTrapConfig.FloorTrapTypeCategory> configFunction;


    FloorTrapType( String id, Function<DimensionConfigGroup, FloorTrapConfig.FloorTrapTypeCategory> configFunction) {
        this( id, id.replace( "_", " " ) + " floor traps", configFunction );
    }

    FloorTrapType( String id, String displayName, Function<DimensionConfigGroup, FloorTrapConfig.FloorTrapTypeCategory> configFunction ) {
        this.id = id;
        this.displayName = displayName;
        this.configFunction = configFunction;
    }

    @Override
    public String getSerializedName() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ResourceLocation getChestLootTable() {
        return DeadlyWorld.resourceLoc( References.CHEST_LOOT_PATH + LOOT_TABLE_PATH + this );
    }

    /** @return A Supplier of the Spawner Block to register for this Spawner Type */
    public Supplier<FloorTrapBlock> getBlock() { return () -> new FloorTrapBlock(this); }

    public final FloorTrapConfig.FloorTrapTypeCategory getFeatureConfig( DimensionConfigGroup dimConfigs ) { return configFunction.apply( dimConfigs ); }

    public boolean canTypeBePlaced( World world, BlockPos position ) {
        BlockPos above = position.above();
        BlockPos below = position.below();
        BlockPos veryAbove = position.offset(0, 2, 0);

        return !world.getBlockState( veryAbove ).isCollisionShapeFullBlock( world, veryAbove ) &&
                world.getBlockState( above ).isAir( world, above ) &&
                world.getBlockState( below ).isCollisionShapeFullBlock( world, below );
    }

    public abstract void triggerTrap( DimensionConfigGroup dimConfig, FloorTrapTileEntity trapEntity );

    @Override
    public String toString( ) { return id; }


    public static FloorTrapType fromIndex( int index ) {
        if( index < 0 || index >= values().length ) {
            DeadlyWorld.LOG.warn( "Attempted to load invalid floor trap type from index '{}'", index );
            return TNT;
        }
        return values()[index];
    }
}
