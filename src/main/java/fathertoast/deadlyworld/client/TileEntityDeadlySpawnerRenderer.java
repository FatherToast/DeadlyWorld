package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.tileentity.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public
class TileEntityDeadlySpawnerRenderer extends TileEntitySpecialRenderer< TileEntityDeadlySpawner >
{
	public
	void render( TileEntityDeadlySpawner tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha )
	{
		GlStateManager.pushMatrix( );
		GlStateManager.translate( (float) x + 0.5F, (float) y, (float) z + 0.5F );
		renderMob( tileEntity, x, y, z, partialTicks );
		GlStateManager.popMatrix( );
	}
	
	private static
	void renderMob( TileEntityDeadlySpawner spawner, double posX, double posY, double posZ, float partialTicks )
	{
		Entity entity = spawner.getRenderEntity( );
		if( entity != null ) {
			float scale = 0.53125F;
			
			// Try to scale the mob to fit inside the block; relies on having decent bounding boxes (lol)
			float entityGirth = Math.max( entity.width, entity.height );
			if( entityGirth > 1.0F ) {
				scale /= entityGirth;
			}
			
			// Render that little cutie
			GlStateManager.translate( 0.0F, 0.4F, 0.0F );
			GlStateManager.rotate( spawner.getRenderEntityRotation( partialTicks ) * 10.0F, 0.0F, 1.0F, 0.0F );
			GlStateManager.translate( 0.0F, -0.2F, 0.0F );
			GlStateManager.rotate( -30.0F, 1.0F, 0.0F, 0.0F );
			GlStateManager.scale( scale, scale, scale );
			entity.setLocationAndAngles( posX, posY, posZ, 0.0F, 0.0F );
			Minecraft.getMinecraft( ).getRenderManager( ).renderEntity( entity, 0.0, 0.0, 0.0, 0.0F, partialTicks, false );
		}
	}
}