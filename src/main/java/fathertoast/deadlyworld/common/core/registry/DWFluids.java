package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.block.fluid.RunnyLavaFluid;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.awt.image.RenderedImage;
import java.util.function.Consumer;
import java.util.function.Supplier;

// Registering fluids is a mess, I feel immense sadness
public class DWFluids {
    public static final DeferredRegister<Fluid> REGISTRY = DeferredRegister.create( ForgeRegistries.FLUIDS, DeadlyWorld.MOD_ID );
    public static final DeferredRegister<FluidType> TYPE_REGISTRY = DeferredRegister.create( ForgeRegistries.Keys.FLUID_TYPES, DeadlyWorld.MOD_ID );

    //
    // FLUIDS
    //
    public static final RegistryObject<FlowingFluid> RUNNY_LAVA_SOURCE = RegistryObject.create( DeadlyWorld.resourceLoc( "runny_lava" ), ForgeRegistries.FLUIDS );
    public static final RegistryObject<FlowingFluid> RUNNY_LAVA_FLOWING = RegistryObject.create( DeadlyWorld.resourceLoc( "flowing_runny_lava" ), ForgeRegistries.FLUIDS );;

    //
    // TYPES
    //
    public static final RegistryObject<FluidType> RUNNY_LAVA_TYPE = registerType( "runny_lava", () -> new FluidType(
            FluidType.Properties.create()
                    .descriptionId( "block.deadlyworld.runny_lava" )
                    .canSwim( false )
                    .canDrown( false )
                    .pathType( BlockPathTypes.LAVA )
                    .adjacentPathType( null )
                    .sound( SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA )
                    .sound( SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA )
                    .lightLevel( 15 )
                    .density( 3000 )
                    .viscosity( 6000 )
                    .temperature( 1400 )
            )
    {
        @Override
        public double motionScale( Entity entity ) {
            return 0.007D;
        }

        @Override
        public void setItemMovement( ItemEntity entity ) {
            Vec3 vec3 = entity.getDeltaMovement();
            entity.setDeltaMovement(
                    vec3.x * (double) 0.95F,
                    vec3.y + (double)( vec3.y < (double) 0.06F ? 5.0E-4F : 0.0F ),
                    vec3.z * (double) 0.95F
            );
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {
                private static final ResourceLocation STILL = DeadlyWorld.resourceLoc( "block/runny_lava_still" );
                private static final ResourceLocation FLOWING = DeadlyWorld.resourceLoc( "block/runny_lava_flow" );

                @Override
                public ResourceLocation getStillTexture() {
                    return STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return FLOWING;
                }
            });
        }
    });

    // Note: fluids require properties, and the properties require the fluids. Lol!
    static {
        ForgeFlowingFluid.Properties runnyLavaProperties = new ForgeFlowingFluid.Properties( RUNNY_LAVA_TYPE, RUNNY_LAVA_SOURCE, RUNNY_LAVA_FLOWING )
                .block( DWBlocks.RUNNY_LAVA )
                .bucket( DWItems.RUNNY_LAVA_BUCKET )
                .tickRate( 4 )
                .levelDecreasePerBlock( 1 )
                .slopeFindDistance( 8 );
        REGISTRY.register( "runny_lava", () -> new RunnyLavaFluid.Source( runnyLavaProperties ) );
        REGISTRY.register( "flowing_runny_lava", () -> new RunnyLavaFluid.Flowing( runnyLavaProperties ) );
    }

    /**
     * Called from {@link DeadlyWorld#onCommonSetup(FMLCommonSetupEvent)}.<br><br>
     * Here we register the logic for how our custom fluids should interact with other fluids in the world.
     */
    public static void registerFluidInteractions() {
        FluidInteractionRegistry.addInteraction( DWFluids.RUNNY_LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                ForgeMod.WATER_TYPE.get(),
                fluidState -> fluidState.isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.COBBLESTONE.defaultBlockState()
        ));
    }


    private static <TYPE extends FluidType> RegistryObject<TYPE> registerType( String name, Supplier<TYPE> typeSupplier ) {
        return TYPE_REGISTRY.register( name, typeSupplier );
    }
}
