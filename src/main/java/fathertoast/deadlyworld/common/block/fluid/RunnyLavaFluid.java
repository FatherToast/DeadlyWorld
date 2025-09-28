package fathertoast.deadlyworld.common.block.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class RunnyLavaFluid extends ForgeFlowingFluid {

    private RunnyLavaFluid( Properties properties ) {
        super( properties );
    }

    @Override
    public void animateTick( Level level, BlockPos pos, FluidState fluidState, RandomSource random ) {
        BlockPos abovePos = pos.above();

        if ( level.getBlockState( abovePos ).isAir() && !level.getBlockState( abovePos ).isSolidRender( level, abovePos ) ) {
            if ( random.nextInt( 100 ) == 0 ) {
                double x = (double) pos.getX() + random.nextDouble();
                double y = (double) pos.getY() + 1.0D;
                double z = (double) pos.getZ() + random.nextDouble();

                level.addParticle(
                        ParticleTypes.LAVA,
                        x, y, z,
                        0.0D, 0.0D, 0.0D
                );
                level.playLocalSound( x, y, z,
                        SoundEvents.LAVA_POP,
                        SoundSource.BLOCKS,
                        0.2F + random.nextFloat() * 0.2F,
                        0.9F + random.nextFloat() * 0.15F,
                        false
                );
            }

            if ( random.nextInt( 200 ) == 0 ) {
                level.playLocalSound(
                        pos.getX(), pos.getY(), pos.getZ(),
                        SoundEvents.LAVA_AMBIENT,
                        SoundSource.BLOCKS,
                        0.2F + random.nextFloat() * 0.2F,
                        0.9F + random.nextFloat() * 0.15F,
                        false
                );
            }
        }
    }

    @Override
    public void randomTick( Level level, BlockPos pos, FluidState fluidState, RandomSource random ) {
        if ( level.getGameRules().getBoolean( GameRules.RULE_DOFIRETICK ) ) {
            int igniteCount = random.nextInt( 3 );

            if ( igniteCount > 0 ) {
                BlockPos ignitePos = pos;

                for( int j = 0; j < igniteCount; ++j ) {
                    ignitePos = ignitePos.offset( random.nextInt( 3 ) - 1, 1, random.nextInt( 3 ) - 1 );

                    if ( !level.isLoaded( ignitePos ) ) {
                        return;
                    }
                    BlockState state = level.getBlockState( ignitePos );

                    if ( state.isAir() ) {
                        if ( hasFlammableNeighbours( level, ignitePos ) ) {
                            level.setBlockAndUpdate( ignitePos, ForgeEventFactory.fireFluidPlaceBlockEvent( level, ignitePos, pos, Blocks.FIRE.defaultBlockState() ) );
                            return;
                        }
                    }
                    else if ( state.blocksMotion() ) {
                        return;
                    }
                }
            }
            else {
                for ( int k = 0; k < 3; ++k ) {
                    BlockPos ignitePos = pos.offset( random.nextInt( 3 ) - 1, 0, random.nextInt( 3 ) - 1 );

                    if ( !level.isLoaded( ignitePos ) ) {
                        return;
                    }
                    if ( level.isEmptyBlock( ignitePos.above() ) && this.isFlammable( level, ignitePos, Direction.UP ) ) {
                        level.setBlockAndUpdate( ignitePos.above(), ForgeEventFactory.fireFluidPlaceBlockEvent( level, ignitePos.above(), pos, Blocks.FIRE.defaultBlockState() ) );
                    }
                }
            }
        }
    }

    private boolean hasFlammableNeighbours( LevelReader level, BlockPos pos ) {
        for( Direction direction : Direction.values() ) {
            if ( isFlammable( level, pos.relative( direction ), direction.getOpposite() ) ) {
                return true;
            }
        }
        return false;
    }

    private boolean isFlammable( LevelReader level, BlockPos pos, Direction face ) {
        return pos.getY() >= level.getMinBuildHeight()
                && pos.getY() <= level.getMaxBuildHeight()
                && level.hasChunkAt( pos )
                && level.getBlockState( pos ).isFlammable( level, pos, face );
    }

    @Override
    protected void beforeDestroyingBlock( LevelAccessor level, BlockPos pos, BlockState state ) {
        // Lava extinguish sound
        level.levelEvent( 1501, pos, 0 );
    }

    @Override
    protected void spreadTo( LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluidState ) {
        // Just like vanilla lava, create stone when in contact with water
        if ( direction == Direction.DOWN ) {
            FluidState fluidstate = level.getFluidState( pos );

            if ( fluidstate.is( FluidTags.WATER ) ) {
                if ( state.getBlock() instanceof LiquidBlock ) {
                    level.setBlock( pos, ForgeEventFactory.fireFluidPlaceBlockEvent( level, pos, pos, Blocks.STONE.defaultBlockState() ), 3 );
                }
                // Lava extinguish sound
                level.levelEvent( 1501, pos, 0 );
                return;
            }
        }
        super.spreadTo( level, pos, state, direction, fluidState );
    }

    @Override
    protected boolean isRandomlyTicking() {
        return true;
    }

    /** The flowing variant of the fluid. */
    public static class Flowing extends RunnyLavaFluid {

        public Flowing( Properties properties ) {
            super( properties );
            registerDefaultState(getStateDefinition().any().setValue( LEVEL, 7 ) );
        }

        @Override
        protected void createFluidStateDefinition( StateDefinition.Builder<Fluid, FluidState> builder ) {
            super.createFluidStateDefinition( builder );
            builder.add( LEVEL );
        }

        @Override
        public int getAmount( FluidState state ) {
            return state.getValue( LEVEL );
        }

        @Override
        public boolean isSource( FluidState state ) {
            return false;
        }
    }

    /** The still/source variant of the fluid. */
    public static class Source extends RunnyLavaFluid {

        public Source( Properties properties ) {
            super(properties);
        }

        @Override
        public int getAmount( FluidState state ) {
            return 8;
        }

        @Override
        public boolean isSource( FluidState state ) {
            return true;
        }
    }
}
