package mariculture.factory.blocks;

import mariculture.core.blocks.base.TileStorage;
import mariculture.core.gui.ContainerMariculture;
import mariculture.core.helpers.DictionaryHelper;
import mariculture.core.helpers.InventoHelper;
import mariculture.core.lib.MachineSpeeds;
import mariculture.core.network.Packets;
import mariculture.core.util.IItemDropBlacklist;
import mariculture.core.util.IMachine;
import mariculture.core.util.IProgressable;
import mariculture.factory.items.ItemFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.StatCollector;

public class TileDictionary extends TileStorage implements ISidedInventory, IItemDropBlacklist, IMachine, IProgressable {
	private int processed = 0;
	private int max;

	public TileDictionary() {
		inventory = new ItemStack[21];
		max = MachineSpeeds.getDictionarySpeed();
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return(i < 9)? false: true;
	}
	
	private static final int[] slots_sides = new int[] { 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return slots_sides;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return i > 8 && i < 15 && j < 2;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return i > 14 && j > 1;
	}
	
	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			if (swap(false)) {
				this.processed++;
				if (this.processed >= max) {
					processed = 0;
					swap(true);
				}
			} else {
				this.processed = 0;
			}
		}
	}

	private boolean swap(boolean doSwap) {
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 3; i++) {
				if (getStackInSlot(9 + (i + (j * 3))) != null) {
					if(swap(9 + (i + (j * 3)), doSwap) && !doSwap) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	public ItemStack getNewStack(ItemStack stack) {
		for(int i = 0; i < 9; i++) {
			if(getStackInSlot(i) != null) {
				ItemStack item = getStackInSlot(i);
				
				if(item.getItem() instanceof ItemFilter && item.hasTagCompound()) {
					NBTTagList tagList = item.stackTagCompound.getTagList("Inventory");
					if (tagList != null) {
						for(int j = 0; j < tagList.tagCount(); j++) {
							NBTTagCompound nbt = (NBTTagCompound) tagList.tagAt(j);
							byte byte0 = nbt.getByte("Slot");
							if (byte0 >= 0 && byte0 < ItemFilter.SIZE) {
								ItemStack aStack = ItemStack.loadItemStackFromNBT(nbt);
								if(DictionaryHelper.areEqual(aStack, stack)) {
									return aStack;
								}
							}
						}
					}
					
					continue;
				}
				
				if(DictionaryHelper.areEqual(stack, item)) {
					return item;
				}
			}
		}
		
		return stack;
	}

	private boolean swap(int slot, boolean doSwap) {
		return moveStack(getNewStack(getStackInSlot(slot)), slot, doSwap);
	}

	private boolean moveStack(ItemStack stack, int origSlot, boolean doSwap) {
		
		ItemStack newStack = stack.copy();
		newStack.stackSize = 1;
		
		if (getNextSlot(stack) != -1) {
			if(doSwap) {				
				if(!InventoHelper.addToInventory(0, worldObj, xCoord, yCoord, zCoord, newStack, new int[]{ 2, 3, 4, 5 })) {
					int newSlot = getNextSlot(stack);
					if (getStackInSlot(newSlot) != null) {
						getStackInSlot(newSlot).stackSize++;
					} else {
						setInventorySlotContents(newSlot, newStack);
					}
				}
	
				decrStackSize(origSlot, 1);
			}
			
			return true;
		}
		return false;
	}

	private int getNextSlot(ItemStack stack) {
		if (stack == null) {
			return -1;
		}
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 3; i++) {
				ItemStack itemstack = getStackInSlot(15 + (i + (j * 3)));
				if (itemstack != null) {
					if (itemstack.isItemEqual(stack) && itemstack.stackSize < itemstack.getMaxStackSize()) {
						return 15 + (i + (j * 3));
					}
				}
			}
		}
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 3; i++) {
				ItemStack itemstack = getStackInSlot(15 + (i + (j * 3)));
				if (itemstack == null) {
					return 15 + (i + (j * 3));
				}
			}
		}

		return -1;
	}

	@Override
	public void getGUINetworkData(int i, int j) {
		switch (i) {
		case 0:
			processed = j;
			break;
		}
	}

	@Override
	public void sendGUINetworkData(ContainerMariculture container, EntityPlayer player) {
		Packets.updateGUI(player, container, 0, processed);
	}

	@Override
	public boolean doesDrop(int slot) {
		return slot > 8;
	}

	@Override
	public ItemStack[] getInventory() {
		return inventory;
	}

	@Override
	public int getProgressScaled(int i) {
		return (processed * i) / max;
	}

	@Override
	public String getProgessText() {
		return getProgressScaled(100) + "% " + StatCollector.translateToLocal("mariculture.process.converted");
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		this.processed = tagCompound.getShort("CookTime");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setShort("CookTime", (short) this.processed);
	}
}