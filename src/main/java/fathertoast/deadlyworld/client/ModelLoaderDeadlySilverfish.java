package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.resource.IResourceType;

import java.util.function.Predicate;

public
class ModelLoaderDeadlySilverfish implements ICustomModelLoader
{
	public static final String PREFIX = "infested/";
	
	@Override
	public
	void onResourceManagerReload( IResourceManager resourceManager )
	{
	
	}
	
	@Override
	public
	void onResourceManagerReload( IResourceManager resourceManager, Predicate< IResourceType > resourcePredicate )
	{
	
	}
	
	@Override
	public
	boolean accepts( ResourceLocation modelLocation )
	{
		return modelLocation.getResourceDomain( ).equals( DeadlyWorldMod.MOD_ID ) && modelLocation.getResourcePath( ).startsWith( PREFIX );
	}
	
	@Override
	public
	IModel loadModel( ResourceLocation modelLocation ) throws Exception
	{
		ModelResourceLocation parentResource = new ModelResourceLocation(
			modelLocation.getResourcePath( ).substring( PREFIX.length( ) )
		);
		// TODO try and return the parent model
		return null;
	}
}
