package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.tileentity.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;

@SuppressWarnings( "unused" )
public
class ClientProxy extends SidedModProxy
{
	@Override
	public
	void preInit( )
	{
		ModelLoaderRegistry.registerLoader( new ModelLoaderDeadlySilverfish( ) );
	}
	
	@Override
	public
	void registerModels( )
	{
		registerModels( Item.getItemFromBlock( ModObjects.DEADLY_SPAWNER ), EnumSpawnerType.values( ) );
		if( Config.get( ).GENERAL.FEATURE_TESTER ) {
			registerModel( ModObjects.FEATURE_TESTER );
		}
		
		StateMapperDeadlySilverfish stateMapper = new StateMapperDeadlySilverfish( );
		for( BlockDeadlySilverfish infestedBlock : ModObjects.getSilverfishBlocksOrdered( ) ) {
			ModelLoader.setCustomStateMapper( infestedBlock, stateMapper );
			
			Item infestedItem = Item.getItemFromBlock( infestedBlock );
			if( infestedBlock.DISGUISE_PROPERTY == null ) {
				ModelLoader.setCustomModelResourceLocation(
					infestedItem, 0, stateMapper.getModelResourceLocation( infestedBlock.getDefaultState( ) )
				);
			}
			else {
				for( int meta : infestedBlock.DISGUISE_PROPERTY.getAllowedValues( ) ) {
					ModelLoader.setCustomModelResourceLocation(
						infestedItem, meta, stateMapper.getModelResourceLocation( infestedBlock.getStateFromMeta( meta ) )
					);
				}
			}
		}
	}
	
	private
	void registerModel( Item item )
	{
		registerModel( item, 0 );
	}
	
	private
	void registerModel( Item item, int meta )
	{
		ModelLoader.setCustomModelResourceLocation( item, meta, new ModelResourceLocation( item.getRegistryName( ), "normal" ) );
	}
	
	private
	void registerModels( Item item, IExclusiveMetaProvider... metaProviders )
	{
		String regName = item.getRegistryName( ).toString( );
		for( IExclusiveMetaProvider provider : metaProviders ) {
			ModelLoader.setCustomModelResourceLocation(
				item, provider.getMetadata( ), new ModelResourceLocation( regName, "type=" + provider.getName( ) )
			);
		}
	}
	
	@Override
	public
	void init( )
	{
		ClientRegistry.bindTileEntitySpecialRenderer( TileEntityDeadlySpawner.class, new TileEntityDeadlySpawnerRenderer( ) );
	}
}
