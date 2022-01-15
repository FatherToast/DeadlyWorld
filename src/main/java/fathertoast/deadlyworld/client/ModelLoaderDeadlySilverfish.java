package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.*;
import fathertoast.deadlyworld.config.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.resource.IResourceType;

import java.util.Map;
import java.util.function.Predicate;

public
class ModelLoaderDeadlySilverfish implements ICustomModelLoader
{
	static final String PREFIX = DeadlyWorldMod.MOD_ID + ":" + BlockDeadlySilverfish.ID + "/";
	
	private static final ObfuscationHelper< Minecraft, ModelManager >
		Minecraft_modelManager = new ObfuscationHelper<>(
		Minecraft.class, "field_175617_aL", "modelManager"
	);
	
	
	@Override
	public
	void onResourceManagerReload( IResourceManager resourceManager ) { }
	
	@Override
	public
	void onResourceManagerReload( IResourceManager resourceManager, Predicate< IResourceType > resourcePredicate ) { }
	
	@Override
	public
	boolean accepts( ResourceLocation modelLocation )
	{
		return modelLocation.toString( ).startsWith( PREFIX );
	}
	
	@Override
	public
	IModel loadModel( ResourceLocation modelLocation ) throws Exception
	{
		// Translate the model's location into a disguise blockstate
		String   parentDefinition = modelLocation.toString( ).substring( PREFIX.length( ) );
		String[] nameVariant      = parentDefinition.split( "#", 2 );
		
		IBlockState disguiseState = TargetBlock.parseStateForMatch( nameVariant[ 0 ] + "[" + nameVariant[ 1 ] + "]" );
		if( disguiseState.getBlock( ) instanceof BlockDeadlySilverfish ) {
			return null; // In case someone tries to be cute; prevent circular dependency
		}
		
		// Find the disguise block's statemap
		Map< IBlockState, ModelResourceLocation > stateMap = Minecraft_modelManager.get( Minecraft.getMinecraft( ) )
			                                                     .getBlockModelShapes( ).getBlockStateMapper( )
			                                                     .getVariants( disguiseState.getBlock( ) );
		
		// Get the disguise blockstate's model from the statemap
		ModelResourceLocation disguiseModelLocation = stateMap.get( disguiseState );
		return disguiseModelLocation == null ? null : ModelLoaderRegistry.getModel( disguiseModelLocation );
	}
}
