package fathertoast.deadlyworld.common.tile.floortrap;

import fathertoast.deadlyworld.common.block.FloorTrapBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.config.FloorTrapConfig;
import fathertoast.deadlyworld.common.util.References;
import net.minecraft.entity.item.TNTEntity;
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
    };

    /*
    TNT_MOB( "tnt_mob" ) {
        @Override
        public void triggerTrap( Config dimConfig, FloorTrapTileEntity trapEntity ) {
            World world = trapEntity.getLevel( );

            double x = trapEntity.getPos( ).getX( ) + 0.5;
            double y = trapEntity.getPos( ).getY( ) + 1;
            double z = trapEntity.getPos( ).getZ( ) + 0.5;

            int fuseRange = dimConfig.FLOOR_TRAP_TNT_MOB.FUSE_TIME_MAX - dimConfig.FLOOR_TRAP_TNT_MOB.FUSE_TIME_MIN;
            if( fuseRange <= 0 ) {
                fuseRange = 1;
            }

            // Pick an entity to spawn
            ResourceLocation          registryName  = dimConfig.FLOOR_TRAP_TNT_MOB.SPAWN_LIST.nextItem( world.rand );
            Class< ? extends Entity > entityToSpawn = EntityList.getClass( registryName );
            if( entityToSpawn == null ) {
                DeadlyWorldMod.log( ).warn(
                        "TNT mob floor trap received non-registered entity name '{}'" +
                                " - This is probably caused by an error or change in the config for DIM_{} (defaulting to zombie)",
                        registryName, world.provider.getDimension( )
                );
                entityToSpawn = EntityZombie.class;
            }

            // Try to create the entity to spawn
            Entity           entity;
            EntityLivingBase livingEntity = null;
            try {
                entity = entityToSpawn.getConstructor( World.class ).newInstance( world );
            }
            catch( Exception ex ) {
                DeadlyWorldMod.log( ).error( "Encountered exception while constructing entity '{}'", entityToSpawn, ex );
                entity = new EntityZombie( world );
            }

            // Initialize the entity
            entity.setPositionAndRotation( x, y, z, world.rand.nextFloat( ) * 2.0F * (float) Math.PI, 0.0F );
            entity.motionY = 0.3F;
            if( entity instanceof EntityLivingBase ) {
                livingEntity = (EntityLivingBase) entity;

                IAttributeInstance attrib;
                if( dimConfig.FLOOR_TRAP_TNT_MOB.MULTIPLIER_HEALTH != 1.0F ) {
                    try {
                        attrib = livingEntity.getEntityAttribute( SharedMonsterAttributes.MAX_HEALTH );
                        attrib.setBaseValue( attrib.getBaseValue( ) * dimConfig.FLOOR_TRAP_TNT_MOB.MULTIPLIER_HEALTH );
                    }
                    catch( Exception ex ) {
                        // This is fine, entity just doesn't have the attribute
                    }
                }
                if( dimConfig.FLOOR_TRAP_TNT_MOB.MULTIPLIER_SPEED != 1.0F ) {
                    try {
                        attrib = livingEntity.getEntityAttribute( SharedMonsterAttributes.MOVEMENT_SPEED );
                        attrib.setBaseValue( attrib.getBaseValue( ) * dimConfig.FLOOR_TRAP_TNT_MOB.MULTIPLIER_SPEED );
                    }
                    catch( Exception ex ) {
                        // This is fine, entity just doesn't have the attribute
                    }
                }
                livingEntity.setHealth( livingEntity.getMaxHealth( ) );
                livingEntity.setRevengeTarget( trapEntity.getTarget( ) );
            }

            // Make the tnt "hat"
            EntityTNTPrimed tntPrimed = new EntityTNTPrimed( world, x, y, z, livingEntity );
            tntPrimed.copyLocationAndAnglesFrom( entity );
            tntPrimed.setFuse( dimConfig.FLOOR_TRAP_TNT_MOB.FUSE_TIME_MIN + world.rand.nextInt( fuseRange ) );
            tntPrimed.startRiding( entity, true );

            // Spawn the entities and play alert sound
            world.spawnEntity( entity );
            world.spawnEntity( tntPrimed );
            world.playSound( null, x, y, z, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F );
        }
    },

    POTION( "potion" ) {
        @Override
        public void triggerTrap( Config dimConfig, FloorTrapTileEntity trapEntity ) {
            final String TAG_POTION_TYPE = "PotionType";

            World world = trapEntity.getLevel( );

            double x = trapEntity.getPos( ).getX( ) + 0.5;
            double y = trapEntity.getPos( ).getY( ) + 1.1;
            double z = trapEntity.getPos( ).getZ( ) + 0.5;

            int resetRange = dimConfig.FLOOR_TRAP_POTION.RESET_TIME_MAX - dimConfig.FLOOR_TRAP_POTION.RESET_TIME_MIN;
            if( resetRange <= 0 ) {
                resetRange = 1;
            }
            trapEntity.disableTrap( dimConfig.FLOOR_TRAP_POTION.RESET_TIME_MIN + world.rand.nextInt( resetRange ) );

            // Load or pick the trap type
            EnumPotionTrapType type;
            NBTTagCompound     typeData = trapEntity.getOrCreateTypeData( );
            if( typeData.hasKey( TAG_POTION_TYPE, TrapHelper.NBT_TYPE_STRING ) ) {
                type = EnumPotionTrapType.fromString( typeData.getString( TAG_POTION_TYPE ) );
            }
            else {
                type = dimConfig.FLOOR_TRAP_POTION.POTION_TYPE_LIST.nextItem( world.rand );
                if( type == null ) {
                    type = EnumPotionTrapType.HARM;
                }
                typeData.setString( TAG_POTION_TYPE, type.toString( ) );
            }
            ItemStack potionStack = type.getPotion( dimConfig );
            TrapHelper.setPotionColorFromEffects( potionStack );

            // Spawn the thrown potion
            EntityPotion potionEntity = new EntityPotion( world, x, y, z, potionStack );
            potionEntity.motionY = 0.33F + 0.04F * world.rand.nextFloat( );
            world.spawnEntity( potionEntity );

            world.playSound( null, x, y, z, SoundEvents.DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0F, 1.0F );
        }
    };

     */

    /** The path for loot tables associated with these types. */
    public static final String LOOT_TABLE_PATH = "floor_traps/";

    public static final String CATEGORY = "spawner";

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
