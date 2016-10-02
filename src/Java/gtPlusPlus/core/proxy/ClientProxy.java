package gtPlusPlus.core.proxy;

import gtPlusPlus.GTplusplus;
import gtPlusPlus.core.common.CommonProxy;
import gtPlusPlus.core.util.particles.EntityParticleFXMysterious;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class ClientProxy extends CommonProxy{

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		// TODO Auto-generated method stub
		super.preInit(e);
		//Do this weird things for textures.
		GTplusplus.loadTextures();		
	}

	@Override
	public void init(FMLInitializationEvent e) {
		// TODO Auto-generated method stub
		super.init(e);
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		// TODO Auto-generated method stub
		super.postInit(e);
	}

	@Override
	public void registerRenderThings(){
		//MinecraftForgeClient.registerItemRenderer(ModItems.FluidCell.getItem(), new RenderLiquidCell());
		//RenderingRegistry.registerEntityRenderingHandler(EntityBloodSteelMob.class, new RenderBloodSteelMob(new ModelBloodSteelMob(), 0));
		//RenderingRegistry.registerEntityRenderingHandler(EntityBloodSteelHostileMob.class, new RenderBloodSteelMobHostile(new ModelBloodSteelMob(), 0));
		//RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, new RenderSnowball(ModItems.tutGrenade));

		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBloodSteelChest.class, new BloodSteelChestRenderer());
		//MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.tutChest), new ItemRenderBloodSteelChest());
	}

	@Override
	public int addArmor(String armor){
		return RenderingRegistry.addNewArmourRendererPrefix(armor);
	}



	@Override
	public void generateMysteriousParticles(Entity theEntity)
	{
		double motionX = theEntity.worldObj.rand.nextGaussian() * 0.02D;
		double motionY = theEntity.worldObj.rand.nextGaussian() * 0.02D;
		double motionZ = theEntity.worldObj.rand.nextGaussian() * 0.02D;
		EntityFX particleMysterious = new EntityParticleFXMysterious(

				theEntity.worldObj, 
				theEntity.posX + theEntity.worldObj.rand.nextFloat() * theEntity.width 

				* 2.0F - theEntity.width, 
				theEntity.posY + 0.5D + theEntity.worldObj.rand.nextFloat() 

				* theEntity.height, 
				theEntity.posZ + theEntity.worldObj.rand.nextFloat() * theEntity.width 

				* 2.0F - theEntity.width, 

				motionX, 

				motionY, 

				motionZ);
		Minecraft.getMinecraft().effectRenderer.addEffect(particleMysterious);        
	}

	@Override
	public void serverStarting(FMLServerStartingEvent e)
	{
		
	}



}