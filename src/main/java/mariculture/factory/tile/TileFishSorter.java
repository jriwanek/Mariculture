package mariculture.factory.tile;

import java.util.ArrayList;
import java.util.HashMap;

import mariculture.api.core.ISpecialSorting;
import mariculture.core.gui.feature.Feature;
import mariculture.core.gui.feature.FeatureEject.EjectSetting;
import mariculture.core.helpers.OreDicHelper;
import mariculture.core.helpers.cofh.InventoryHelper;
import mariculture.core.tile.base.TileStorage;
import mariculture.core.util.IEjectable;
import mariculture.core.util.IItemDropBlacklist;
import mariculture.core.util.IMachine;
import mariculture.fishery.gui.ContainerFishTank;
import mariculture.fishery.tile.TileFishTank;
import mariculture.lib.helpers.ItemHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraftforge.common.util.ForgeDirection;

public class TileFishSorter extends TileStorage implements IItemDropBlacklist, IMachine, ISidedInventory, IEjectable {
    private int dft_side;
    private HashMap<Integer, Integer> sorting = new HashMap();
    private EjectSetting setting;

    public TileFishSorter() {
        inventory = new ItemStack[22];
        setting = EjectSetting.ITEM;
    }

    public static final int input = 21;

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] { input };
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return slot == input;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        if (slot < 21) return false;
        int stored = getSlotForStack(stack);
        if (stored == -1) return side == dft_side;
        if (sorting.containsKey(stored)) return sorting.get(stored) == side;
        return side == dft_side;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot == input) {
            inventory[slot] = ejectStack(stack);
        } else {
            inventory[slot] = stack;
        }

        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }

        markDirty();
    }

    public ItemStack ejectStack(ItemStack stack) {
        if (EjectSetting.canEject(setting, EjectSetting.ITEM)) {
            stack = ejectToSides(stack);
        }

        return stack;
    }

    private ItemStack ejectToSides(ItemStack stack) {
        if (stack == null) return null;
        
        int side = 0;
        int stored = getSlotForStack(stack);
        if (stored == -1) {
            side = dft_side;
        } else if (sorting.containsKey(stored)) {
            side = sorting.get(stored);
        } else {
            side = dft_side;
        }

        ForgeDirection dir = ForgeDirection.getOrientation(side);
        TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
        
        //Attempt to place the fish in matching slots in a fish tank block
        if (tile instanceof TileFishTank) {
            TileFishTank tank = (TileFishTank) tile;
            for (int i = 0; i < 54; i++) {
                if (tank.getStackInSlot(i) == null) continue;
                if (ContainerFishTank.isFishEqual(tank.getStackInSlot(i), stack)) {
                    ItemStack newStack = tank.getStackInSlot(i).copy();
                    newStack.stackSize += stack.stackSize;
                    tank.setInventorySlotContents(i, newStack);
                    return null;
                }
            }
        } 
        
        if (tile instanceof IInventory && !(tile instanceof TileEntityHopper)) {
            stack = InventoryHelper.insertItemStackIntoInventory((IInventory) tile, stack, dir.getOpposite().ordinal());
        }

        if (stack != null) {
            ItemHelper.spawnItem(worldObj, xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, stack);
        }

        return null;
    }

    public int getSlotForStack(ItemStack stack) {
        if (stack == null) return -1;
        for (int i = 0; i < input; i++)
            if (getStackInSlot(i) != null) {
                ItemStack item = getStackInSlot(i);
                if (item != null) {
                    if (item.getItem() instanceof ISpecialSorting) {
                        if (((ISpecialSorting) item.getItem()).isSame(item, stack, false)) return i;
                    } else if (OreDicHelper.convert(stack).equals(OreDicHelper.convert(item))) return i;
                }
            }

        return -1;
    }

    @Override
    public void setGUIData(int id, int value) {
        switch (id) {
            case 22:
                dft_side = value;
                break;
            case 21:
                setting = EjectSetting.values()[value];
                break;
            default:
                sorting.put(id, value);
                break;
        }
    }

    private HashMap<Integer, Integer> lastSorting = new HashMap();
    private int lastSetting;
    private int lastDefault;

    @Override
    public boolean hasChanged() {
        if (lastSetting != setting.ordinal() || lastDefault != dft_side) {
            return true;
        }

        for (int i = 0; i < input; i++) {
            if (sorting.containsKey(i)) {
                if (lastSorting.get(i) != sorting.get(i)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public ArrayList<Integer> getGUIData() {
        ArrayList<Integer> list = new ArrayList();
        for (int i = 0; i < input; i++) {
            if (sorting.containsKey(i)) {
                lastSorting.put(i, sorting.get(i));
                list.add(sorting.get(i));
            } else {
                lastSorting.put(i, 0);
                list.add(0);
            }
        }

        lastSetting = setting.ordinal();
        lastDefault = dft_side;

        list.add(lastSetting);
        list.add(lastDefault);
        return list;
    }

    @Override
    public ItemStack[] getInventory() {
        return inventory;
    }

    @Override
    public boolean doesDrop(int slot) {
        return slot == input;
    }

    @Override
    public EjectSetting getEjectType() {
        return EjectSetting.ITEM;
    }

    @Override
    public EjectSetting getEjectSetting() {
        return setting != null ? setting : EjectSetting.NONE;
    }

    @Override
    public void setEjectSetting(EjectSetting setting) {
        this.setting = setting;
    }
    
    @Override
    public boolean canEject(ForgeDirection dir) {
    	return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        setting = EjectSetting.readFromNBT(nbt);
        for (int i = 0; i < input; i++) {
            sorting.put(i, nbt.getInteger("SideSettingForSlot" + i));
        }

        dft_side = nbt.getInteger("DefaultSide");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        EjectSetting.writeToNBT(nbt, setting);
        for (int i = 0; i < input; i++) {
            int val = sorting.containsKey(i) ? sorting.get(i) : 0;
            nbt.setInteger("SideSettingForSlot" + i, val);
        }

        nbt.setInteger("DefaultSide", dft_side);
    }

    public int getSide(int slot) {
        if (sorting.containsKey(slot)) return sorting.get(slot);
        else return 0;
    }

    public void swapSide(int slot) {
        if (sorting.containsKey(slot)) {
            int side = sorting.get(slot);
            side = side + 1 < 6 ? side + 1 : 0;
            sorting.put(slot, side);
        } else {
            sorting.put(slot, 1);
        }
    }

    public int getDefaultSide() {
        return dft_side;
    }

    public static final int DFT_SWITCH = 0;

    @Override
    public void handleButtonClick(int id) {
        if (id == Feature.EJECT) {
            setEjectSetting(EjectSetting.toggle(getEjectType(), getEjectSetting()));
        }
        if (id == DFT_SWITCH) {
            dft_side = dft_side + 1 < 6 ? dft_side + 1 : 0;
        }
    }
}
