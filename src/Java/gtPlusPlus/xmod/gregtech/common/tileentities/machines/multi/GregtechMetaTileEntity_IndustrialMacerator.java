package gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_OutputBus;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.lib.CORE;
import gtPlusPlus.core.util.Utils;
import gtPlusPlus.core.util.array.Pair;
import gtPlusPlus.xmod.gregtech.api.gui.GUI_MultiMachine;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.GregtechMeta_MultiBlockBase;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class GregtechMetaTileEntity_IndustrialMacerator
extends GregtechMeta_MultiBlockBase {
	private static boolean controller;

	public GregtechMetaTileEntity_IndustrialMacerator(int aID, String aName, String aNameRegional) {
		super(aID, aName, aNameRegional);
	}

	public GregtechMetaTileEntity_IndustrialMacerator(String aName) {
		super(aName);
	}

	@Override
	public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
		return new GregtechMetaTileEntity_IndustrialMacerator(this.mName);
	}

	@Override
	public String[] getDescription() {
		return new String[]{
				"Controller Block for the Industrial Maceration Stack",
				"Size[WxHxL]: 3x6x3 (Hollow)", 
				"Controller (Center Bottom)",
				"1x Input Bus (Any bottom layer casing)",
				"5x Output Bus (Any casing besides bottom layer)",
				"1x Maintenance Hatch (Any casing)",
				"1x Energy Hatch (Any casing)",
				"Maceration Stack Casings for the rest (26 at least!)",
				CORE.GT_Tooltip};
	}

	@Override
	public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
		if (aSide == aFacing) {
			return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[64], new GT_RenderedTexture(aActive ? TexturesGtBlock.Overlay_MatterFab_Active : TexturesGtBlock.Overlay_MatterFab)};
		}
		return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[64]};
	}

	@Override
	public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
		return new GUI_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "MacerationStack.png");
	}

	@Override
	public GT_Recipe.GT_Recipe_Map getRecipeMap() {
		return GT_Recipe.GT_Recipe_Map.sMaceratorRecipes;
	}

	/*@Override
	public boolean isCorrectMachinePart(ItemStack aStack) {
		return true;
	}*/

	@Override
	public boolean isFacingValid(byte aFacing) {
		return aFacing > 1;
	}

	@Override
	public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
		super.onPreTick(aBaseMetaTileEntity, aTick);
		if ((aBaseMetaTileEntity.isClientSide()) && (aBaseMetaTileEntity.isActive()) && (aBaseMetaTileEntity.getFrontFacing() != 1) && (aBaseMetaTileEntity.getCoverIDAtSide((byte) 1) == 0) && (!aBaseMetaTileEntity.getOpacityAtSide((byte) 1))) {
			Random tRandom = aBaseMetaTileEntity.getWorld().rand;
			aBaseMetaTileEntity.getWorld().spawnParticle("smoke", aBaseMetaTileEntity.getXCoord() + 0.8F - tRandom.nextFloat() * 0.6F, aBaseMetaTileEntity.getYCoord() + 0.3f + tRandom.nextFloat() * 0.2F, aBaseMetaTileEntity.getZCoord() + 1.2F - tRandom.nextFloat() * 1.6F, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public void startSoundLoop(byte aIndex, double aX, double aY, double aZ) {
		super.startSoundLoop(aIndex, aX, aY, aZ);
		if (aIndex == 1) {
			GT_Utility.doSoundAtClient((String) GregTech_API.sSoundList.get(Integer.valueOf(201)), 10, 1.0F, aX, aY, aZ);
		}
	}

	@Override
	public void startProcess() {
		sendLoopStart((byte) 1);
	}

	@Override
	public boolean checkRecipe(ItemStack aStack) {
		
		//Get inputs.
		ArrayList<ItemStack> tInputList = getStoredInputs();
		for (int i = 0; i < tInputList.size() - 1; i++) {
			for (int j = i + 1; j < tInputList.size(); j++) {
				if (GT_Utility.areStacksEqual((ItemStack) tInputList.get(i), (ItemStack) tInputList.get(j))) {
					if (((ItemStack) tInputList.get(i)).stackSize >= ((ItemStack) tInputList.get(j)).stackSize) {
						tInputList.remove(j--);
					} else {
						tInputList.remove(i--);
						break;
					}
				}
			}
		}
		
		//Temp var
		ItemStack[] tInputs = (ItemStack[]) Arrays.copyOfRange(tInputList.toArray(new ItemStack[tInputList.size()]), 0, 2);

		//Don't check the recipe if someone got around the output bus size check.
		if (this.mOutputBusses.size() != 5){
			return false;
		}
		
		//Make a recipe instance for the rest of the method.
		GT_Recipe tRecipe = GT_Recipe.GT_Recipe_Map.sMaceratorRecipes.findRecipe(getBaseMetaTileEntity(), false, 9223372036854775807L, null, tInputs);
		
		//Count free slots in output hatches - return if the 4/5 hatch is full
		ArrayList<Pair<GT_MetaTileEntity_Hatch_OutputBus, Integer>> rList = new ArrayList<Pair<GT_MetaTileEntity_Hatch_OutputBus, Integer>>();
		for (GT_MetaTileEntity_Hatch_OutputBus tHatch : mOutputBusses) {
			int hatchUsedSlotCount = 0;
			if (isValidMetaTileEntity(tHatch)) {
				//Loop slots in this hatch
				for (int i=0; i<tHatch.getBaseMetaTileEntity().getSizeInventory(); i++) {
					//if slot is not null
					if (tHatch.getBaseMetaTileEntity().getStackInSlot(i) != null){
						//Dummy Stack						
						hatchUsedSlotCount++;
					}					
				}
				//Add this hatch and its data to the ArrayList
				rList.add(new Pair<GT_MetaTileEntity_Hatch_OutputBus, Integer>(tHatch, hatchUsedSlotCount));
			}
		}
		
		//Temp Vars.
		boolean[] mValidOutputSlots = new boolean[5];
		int arrayPos=0;

		for (Pair<GT_MetaTileEntity_Hatch_OutputBus, Integer> IE : rList) {
			//Temp Vars.
			GT_MetaTileEntity_Hatch_OutputBus vTE = IE.getKey();
			int vUsedSlots = IE.getValue();
			//Hatch is empty
			if (vUsedSlots == 0){
				mValidOutputSlots[arrayPos] = true;
			}			
			//Hatch contains at least one item
			else if (vUsedSlots < vTE.getSizeInventory()){	
				//Temp variable for counting amount of output items
				int outputItemCount = tRecipe.mOutputs.length;
				//Hatch has more slots free than output count
				if (vUsedSlots < vTE.getSizeInventory()-outputItemCount){
					mValidOutputSlots[arrayPos] = true;
				}
				//Hatch has output count free
				else if (vUsedSlots >= vTE.getSizeInventory()-outputItemCount){		
					//Not enough output slots
					if (vUsedSlots > vTE.getSizeInventory()-outputItemCount){
						if (arrayPos == 5){
							Utils.LOG_INFO("Not Enough Output slots in top hatch");
							return false;
						}
					}				
				}			
			}
			
			//Hatch is full
			if (vUsedSlots == vTE.getSizeInventory()){				
				Utils.LOG_INFO("Not Enough Output slots in hatch - "+arrayPos+" - [0-4] - 0 = Bottom | 4 = Top");
				mValidOutputSlots[arrayPos] = false;
			}
			//Count up a position in the boolean array.
			arrayPos++;
		}
		
		int tValidOutputSlots = 0;
		for (int cr=0;cr<mValidOutputSlots.length;cr++){
			if (mValidOutputSlots[cr]){
				tValidOutputSlots++;
			}
		}

		Utils.LOG_WARNING("Valid Output Slots: "+tValidOutputSlots);
		
		//More than or one input
		if (tInputList.size() > 0 && tValidOutputSlots > 1) {
			if ((tRecipe != null) && (tRecipe.isRecipeInputEqual(true, null, tInputs))) {
				Utils.LOG_WARNING("Valid Recipe found - size "+tRecipe.mOutputs.length);
				this.mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
				this.mEfficiencyIncrease = 10000;


				this.mEUt = (-tRecipe.mEUt);
				this.mMaxProgresstime = Math.max(1, (tRecipe.mDuration/5));
				ItemStack[] outputs = new ItemStack[tRecipe.mOutputs.length];
				for (int i = 0; i < tRecipe.mOutputs.length; i++){
					if (i==0) {
						Utils.LOG_WARNING("Adding the default output");
						outputs[0] =  tRecipe.getOutput(i);
					}
					else if (getBaseMetaTileEntity().getRandomNumber(7500) < tRecipe.getOutputChance(i)){
						Utils.LOG_WARNING("Adding a bonus output");
						outputs[i] = tRecipe.getOutput(i); 
					}
					else {
						Utils.LOG_WARNING("Adding null output");
						outputs[i] = null;
					}
				}

				this.mOutputItems = outputs;
				sendLoopStart((byte) 20);
				updateSlots();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
		int xDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetX;
		int zDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetZ;
		if (!aBaseMetaTileEntity.getAirOffset(xDir, 1, zDir)) {
			return false;
		}
		int tAmount = 0;
		controller = false;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				for (int h = 0; h < 6; h++) {
					if (!(i == 0 && j == 0 && (h > 0 && h < 5)))//((h > 0)&&(h<5)) || (((xDir + i != 0) || (zDir + j != 0)) && ((i != 0) || (j != 0)))
					{
						IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + i, h, zDir + j);
						if ((!addMaintenanceToMachineList(tTileEntity, 64)) && (!addInputToMachineList(tTileEntity, 64)) && (!addOutputToMachineList(tTileEntity, 64)) && (!addEnergyInputToMachineList(tTileEntity, 64)) && (!ignoreController(aBaseMetaTileEntity.getBlockOffset(xDir + i, h, zDir + j)))) {
							if (aBaseMetaTileEntity.getBlockOffset(xDir + i, h, zDir + j) != ModBlocks.blockCasingsMisc) {
								Utils.LOG_INFO("Returned False 1");
								return false;
							}
							if (aBaseMetaTileEntity.getMetaIDOffset(xDir + i, h, zDir + j) != 7) {
								Utils.LOG_INFO("Returned False 2");
								return false;
							}
							tAmount++;
						}
					}
				}
			}
		}
		if (this.mOutputHatches.size() != 0 || this.mInputBusses.size() != 1 || this.mOutputBusses.size() != 5) {
			Utils.LOG_INFO("Returned False 3");
			return false;
		}
		int height = this.getBaseMetaTileEntity().getYCoord();
		if (this.mInputBusses.get(0).getBaseMetaTileEntity().getYCoord() != height) {
			Utils.LOG_INFO("height: "+height+" | Returned False 4");
			return false;
		}
		GT_MetaTileEntity_Hatch_OutputBus[] tmpHatches = new GT_MetaTileEntity_Hatch_OutputBus[5];
		for (int i = 0; i < this.mOutputBusses.size(); i++) {
			int hatchNumber = this.mOutputBusses.get(i).getBaseMetaTileEntity().getYCoord() - 1 - height;
			if (tmpHatches[hatchNumber] == null) {
				tmpHatches[hatchNumber] = this.mOutputBusses.get(i);
			} else {
				Utils.LOG_INFO("Returned False 5");
				return false;
			}
		}
		this.mOutputBusses.clear();
		for (int i = 0; i < tmpHatches.length; i++) {
			this.mOutputBusses.add(tmpHatches[i]);
		}
		return tAmount >= 26;
	}

	public boolean ignoreController(Block tTileEntity) {
		if (!controller && tTileEntity == GregTech_API.sBlockMachines) {
			return true;
		}
		return false;
	}

	@Override
	public int getMaxEfficiency(ItemStack aStack) {
		return 10000;
	}

	@Override
	public int getPollutionPerTick(ItemStack aStack) {
		return 0;
	}

	@Override
	public int getAmountOfOutputs() {
		return 16;
	}

	@Override
	public boolean explodesOnComponentBreak(ItemStack aStack) {
		return false;
	}

	@Override
	public boolean isOverclockerUpgradable() {
		return true;
	}
}