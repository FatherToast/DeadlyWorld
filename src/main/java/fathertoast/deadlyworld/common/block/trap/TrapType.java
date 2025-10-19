package fathertoast.deadlyworld.common.block.trap;

import fathertoast.crust.api.config.common.value.WeightedList;
import fathertoast.deadlyworld.common.block.entity.DeadlyTrapBlockEntity;
import fathertoast.deadlyworld.common.block.entity.PotionTrapBlockEntity;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineType;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.TrapConfig;
import fathertoast.deadlyworld.common.config.dimension.WaterTrapConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.util.References;
import fathertoast.deadlyworld.common.util.TrapHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public enum TrapType {
    
    TNT( "tnt", ( dimConfig ) -> dimConfig.TRAPS.TNT ) {
        @Override
        public void triggerTrap( DimensionConfigGroup dimConfig, DeadlyTrapBlockEntity trapEntity ) {
            TrapConfig.TntTrapTypeCategory config = dimConfig.TRAPS.TNT;
            Level level = trapEntity.getLevel();
            
            double x = trapEntity.getBlockPos().getX() + 0.5;
            double y = trapEntity.getBlockPos().getY() + 1;
            double z = trapEntity.getBlockPos().getZ() + 0.5;
            
            int fuseRange = config.fuseTime.getMax() - config.fuseTime.getMin();
            if( fuseRange <= 0 ) {
                fuseRange = 1;
            }
            // Spawn the primed tnt blocks
            for( int i = 0; i < config.tntCount.get(); i++ ) {
                PrimedTnt tnt = new PrimedTnt( level, x, y, z, null );
                
                float speed = (float) config.launchSpeed.get() * level.random.nextFloat() + 0.02F;
                tnt.setFuse( config.fuseTime.getMin() + level.random.nextInt( fuseRange ) );
                tnt.getDeltaMovement().multiply( speed, 0.1F * level.random.nextDouble(), speed );
                level.addFreshEntity( tnt );
            }
            level.playSound( null, x, y, z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F );
        }
    },
    
    TNT_MOB( "tnt_mob", ( dimConfig ) -> dimConfig.TRAPS.TNT_MOB ) {
        @Override
        public boolean spawnsMonster() { return true; }
        
        @Override
        public void triggerTrap( DimensionConfigGroup dimConfig, DeadlyTrapBlockEntity trapEntity ) {
            TrapConfig.TntMobTrapTypeCategory config = dimConfig.TRAPS.TNT_MOB;
            Level level = trapEntity.getLevel();
            
            double x = trapEntity.getBlockPos().getX() + 0.5;
            double y = trapEntity.getBlockPos().getY() + 1;
            double z = trapEntity.getBlockPos().getZ() + 0.5;
            
            int fuseRange = config.fuseTime.getMax() - config.fuseTime.getMin();
            
            if( fuseRange <= 0 ) {
                fuseRange = 1;
            }
            // Pick an entity to spawn
            EntityType<?> entityType = config.spawnList.get().next( level.random );
            
            if( entityType == null ) {
                DeadlyWorld.LOG.warn(
                        "TNT mob floor trap received null entity type!" +
                                " - This is probably caused by an error or change in the config for DIM_{} (defaulting to zombie)", level.dimension()
                );
                entityType = EntityType.ZOMBIE;
            }
            
            // Try to create the entity to spawn
            Entity entity;
            LivingEntity livingEntity = null;
            try {
                entity = entityType.create( level );
            }
            catch( Exception ex ) {
                DeadlyWorld.LOG.error( "Encountered exception while constructing entity '{}'", ForgeRegistries.ENTITY_TYPES.getKey( entityType ), ex );
                return;
            }
            if( entity == null ) {
                DeadlyWorld.LOG.error( "Encountered exception while constructing entity '{}'", ForgeRegistries.ENTITY_TYPES.getKey( entityType ) );
                return;
            }
            
            // Initialize the entity
            entity.setPos( x, y, z );
            entity.setYRot( level.random.nextFloat() * 2.0F * (float) Math.PI );
            entity.setXRot( 0.0F );
            entity.setDeltaMovement( entity.getDeltaMovement().x, 0.3D, entity.getDeltaMovement().z );
            
            if( entity instanceof LivingEntity ) {
                livingEntity = (LivingEntity) entity;
                AttributeInstance attribute;
                
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
                Entity tripTarget = trapEntity.getTrapLogic().getTripTarget();
                
                if( tripTarget instanceof LivingEntity livingTarget ) {
                    livingEntity.setLastHurtByMob( livingTarget );
                }
            }
            level.addFreshEntity( entity );
            
            // Make the tnt "hat"
            PrimedTnt tnt = new PrimedTnt( level, x, y, z, livingEntity );
            tnt.copyPosition( entity );
            tnt.setFuse( config.fuseTime.getMin() + level.random.nextInt( fuseRange ) );
            tnt.startRiding( entity, true );
            
            // Spawn the entities and play alert sound
            level.addFreshEntity( tnt );
            level.playSound( null, x, y, z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F );
        }
    },
    
    POTION( "potion", ( dimConfig ) -> dimConfig.TRAPS.POTION ) {
        @Override
        public void triggerTrap( DimensionConfigGroup dimConfig, DeadlyTrapBlockEntity trapEntity ) {
            PotionTrapBlockEntity potionTrap = (PotionTrapBlockEntity) trapEntity;
            TrapConfig.PotionTrapTypeCategory config = dimConfig.TRAPS.POTION;
            
            Level level = trapEntity.getLevel();
            
            double x = trapEntity.getBlockPos().getX() + 0.5;
            double y = trapEntity.getBlockPos().getY() + 1.1;
            double z = trapEntity.getBlockPos().getZ() + 0.5;
            
            ItemStack potionStack;
            
            // Pick potion
            if( potionTrap.isDynamic() ) {
                potionStack = TrapHelper.getPotionFromList( config.potionList.get(), level.random );
            }
            else {
                potionStack = TrapHelper.getPotionFromInstance( potionTrap.getPotionCopy() );
            }
            
            // Spawn the thrown potion
            ThrownPotion potionEntity = new ThrownPotion( level, x, y, z );
            potionEntity.setItem( potionStack );
            potionEntity.setDeltaMovement( potionEntity.getDeltaMovement().x, 0.33D + 0.04D * level.random.nextDouble(), potionEntity.getDeltaMovement().z );
            level.addFreshEntity( potionEntity );
            
            level.playSound( null, x, y, z, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 1.0F, 1.0F );
        }
        
        @Override
        public Supplier<DeadlyTrapBlock> getBlock() { return PotionTrapBlock::new; }
    },
    
    LAVA( "lava", ( dimConfig ) -> dimConfig.TRAPS.LAVA ) {
        @Override
        public void triggerTrap( DimensionConfigGroup dimConfig, DeadlyTrapBlockEntity trapEntity ) {
            TrapConfig.LavaTrapTypeCategory config = dimConfig.TRAPS.LAVA;
            
            Level level = trapEntity.getLevel();
            BlockPos pos = trapEntity.getBlockPos();
            
            boolean placedLava = false;
            
            for( int i = 1; i < 3; i++ ) {
                if( level.getBlockState( pos.above() ).canBeReplaced( Fluids.LAVA ) ) {
                    BlockState block = config.runnyChance.rollChance( level.random ) ?
                            DWBlocks.RUNNY_LAVA.get().defaultBlockState() : Blocks.LAVA.defaultBlockState();
                    level.setBlock( pos.above( i ), block, Block.UPDATE_ALL );
                    placedLava = true;
                }
            }
            if( placedLava ) {
                level.playSound( null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F );
            }
        }
    },
    
    FIRE( "fire", ( dimConfig ) -> dimConfig.TRAPS.FIRE ) {
        @Override
        public void triggerTrap( DimensionConfigGroup dimConfig, DeadlyTrapBlockEntity trapEntity ) {
            TrapConfig.FireTrapTypeCategory config = dimConfig.TRAPS.FIRE;
            
            Level level = trapEntity.getLevel();
            BlockPos pos = trapEntity.getBlockPos();
            
            // Abort if there is something blocking the trap above
            if( level.getBlockState( pos.above() ).isFaceSturdy( level, pos.above(), Direction.DOWN, SupportType.CENTER ) )
                return;
            
            FallingBlockEntity fire = new FallingBlockEntity( level, pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, Blocks.FIRE.defaultBlockState() );
            fire.time = 1; // Prevent the entity from instantly dying
            fire.dropItem = false;
            fire.fallDistance = 3.0F;
            fire.blocksBuilding = false; // Make it somewhat possible to break the trap from above
            
            final float throwPower = config.throwPower.getFloat();
            
            final float speed = (throwPower * 0.7F + level.random.nextFloat() * throwPower) / 20.0F;
            final float pitch = level.random.nextFloat() * (float) Math.PI;
            final float yaw = level.random.nextFloat() * 2.0F * (float) Math.PI;
            
            fire.setDeltaMovement(
                    Mth.cos( yaw ) * speed,
                    Mth.sin( pitch ) * (throwPower + level.random.nextFloat() * throwPower) / 18.0F,
                    Mth.sin( yaw ) * speed );
            level.addFreshEntity( fire );
            
            level.playSound( null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F );
        }
    },

    SEA_MINE_MOB( "sea_mine_mob", ( dimConfig ) -> dimConfig.WATER_TRAPS.SEA_MINE_MOB ) {
        @Override
        public boolean spawnsMonster() { return true; }

        @Override
        public void triggerTrap( DimensionConfigGroup dimConfig, DeadlyTrapBlockEntity trapEntity ) {
            WaterTrapConfig.SeaMineMobTrapTypeCategory config = dimConfig.WATER_TRAPS.SEA_MINE_MOB;
            Level level = trapEntity.getLevel();

            double x = trapEntity.getBlockPos().getX() + 0.5;
            double y = trapEntity.getBlockPos().getY() + 1;
            double z = trapEntity.getBlockPos().getZ() + 0.5;

            // Pick an entity to spawn
            EntityType<?> entityType = config.spawnList.get().next( level.random );

            if( entityType == null ) {
                DeadlyWorld.LOG.warn(
                        "Sea mine mob floor trap received null entity type!" +
                                " - This is probably caused by an error or change in the config for DIM_{} (defaulting to drowned)", level.dimension()
                );
                entityType = EntityType.DROWNED;
            }

            // Try to create the entity to spawn
            Entity entity;
            LivingEntity livingEntity = null;
            try {
                entity = entityType.create( level );
            }
            catch( Exception ex ) {
                DeadlyWorld.LOG.error( "Encountered exception while constructing entity '{}'", ForgeRegistries.ENTITY_TYPES.getKey( entityType ), ex );
                return;
            }
            if( entity == null ) {
                DeadlyWorld.LOG.error( "Encountered exception while constructing entity '{}'", ForgeRegistries.ENTITY_TYPES.getKey( entityType ) );
                return;
            }

            // Initialize the entity
            entity.setPos( x, y, z );
            entity.setYRot( level.random.nextFloat() * 2.0F * (float) Math.PI );
            entity.setXRot( 0.0F );
            entity.setDeltaMovement( entity.getDeltaMovement().x, 0.3D, entity.getDeltaMovement().z );

            if( entity instanceof LivingEntity ) {
                livingEntity = (LivingEntity) entity;
                AttributeInstance attribute;

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

                // Equip a sea mine on the mob's head
                livingEntity.setItemSlot( EquipmentSlot.HEAD, new ItemStack( DWBlocks.seaMine( SeaMineType.fromIndex( 0 ) ).get() ) );

                Entity tripTarget = trapEntity.getTrapLogic().getTripTarget();

                if( tripTarget instanceof LivingEntity livingTarget ) {
                    livingEntity.setLastHurtByMob( livingTarget );
                }
            }
            level.addFreshEntity( entity );
        }
    };
    
    
    /** The path for loot tables associated with these types. */
    public static final String LOOT_TABLE_PATH = "traps/";
    public static final String BLOCK_CATEGORY = "trap";
    
    private final String id;
    private final String displayName;
    /** A function that returns the feature config associated with this spawner type for a given dimension config. */
    private final Function<DimensionConfigGroup, TrapConfig.TrapTypeCategory> configFunction;
    
    
    TrapType( String id, Function<DimensionConfigGroup, TrapConfig.TrapTypeCategory> configFunction ) {
        this( id, id.replace( "_", " " ) + " floor traps", configFunction );
    }
    
    TrapType( String id, String displayName, Function<DimensionConfigGroup, TrapConfig.TrapTypeCategory> configFunction ) {
        this.id = id;
        this.displayName = displayName;
        this.configFunction = configFunction;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public ResourceLocation getChestLootTable() {
        return DeadlyWorld.resourceLoc( References.CHEST_LOOT_PATH + LOOT_TABLE_PATH + this );
    }
    
    /** @return A Supplier of the Spawner Block to register for this Spawner Type */
    public Supplier<DeadlyTrapBlock> getBlock() { return () -> new DeadlyTrapBlock( this ); }
    
    public boolean spawnsMonster() {
        return false;
    }
    
    public final TrapConfig.TrapTypeCategory getFeatureConfig( DimensionConfigGroup dimConfigs ) { return configFunction.apply( dimConfigs ); }
    
    public abstract void triggerTrap( DimensionConfigGroup dimConfig, DeadlyTrapBlockEntity trapEntity );
    
    @Override
    public String toString() { return id; }
    
    public static TrapType fromIndex( int index ) {
        if( index < 0 || index >= values().length ) {
            DeadlyWorld.LOG.warn( "Attempted to fetch invalid floor trap type from index '{}'", index );
            return TNT;
        }
        return values()[index];
    }
}