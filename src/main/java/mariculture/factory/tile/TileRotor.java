package mariculture.factory.tile;

import mariculture.api.util.CachedCoords;
import mariculture.core.Core;
import mariculture.core.lib.MetalMeta;
import mariculture.core.network.PacketHandler;
import mariculture.core.network.PacketRotorSpin;
import mariculture.core.util.IFaceable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/* This block does nothing, except animation */
public abstract class TileRotor extends TileEntity implements IFaceable {
    public ForgeDirection orientation;
    public CachedCoords master;

    //Client Sided animation stuff
    public int damage;
    public int isAnimating;
    public ForgeDirection dir;
    public float angle;

    //Set by the Rotor
    private final int maxDamage;
    private final String block;
    private final double tier;

    public TileRotor() {
        block = getBlock();
        tier = getTier();
        maxDamage = getMaxDamage();
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(1D, 1D, 1D);
    }

    public void setAnimating(ForgeDirection dir) {
        this.dir = dir;
        this.isAnimating = 1;
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) {
            if (isAnimating > 0) {
                if (dir == ForgeDirection.NORTH) {
                    angle -= getTier();
                } else if (dir == ForgeDirection.SOUTH) {
                    angle += getTier();
                } else if (dir == ForgeDirection.WEST) {
                    angle -= getTier();
                } else if (dir == ForgeDirection.EAST) {
                    angle += getTier();
                }

                isAnimating++;
                if (isAnimating > 360) {
                    isAnimating = 0;
                }
            }
        }
    }

    protected String getBlock() {
        return "plankWood";
    }

    protected double getTier() {
        return 0.5D;
    }
    
    protected int getMaxDamage() {
        return 100;
    }

    public void setMaster(CachedCoords master) {
        this.master = master;
    }

    //Adds Power from a certain direction
    public void addEnergy(ForgeDirection dir, int energy, int dmg) {
        TileGenerator generator = getMaster();
        if (generator != null) {
            generator.addEnergy((int) (energy * getTier()));
        }

        damage += dmg;
        if (damage >= maxDamage) {
            worldObj.setBlock(xCoord, yCoord, zCoord, Core.metals, MetalMeta.BASE_IRON, 2);
        }

        PacketHandler.sendAround(new PacketRotorSpin(xCoord, yCoord, zCoord, dir), this);
    }

    private TileGenerator getMaster() {
        if (master != null) {
            TileEntity tile = worldObj.getTileEntity(master.x, master.y, master.z);
            return (TileGenerator) (tile instanceof TileGenerator ? tile : null);
        } else return null;
    }

    //Called when this block is removed, or when it receives a block update, to reset the master
    public void recheck() {
        TileGenerator tile = getMaster();
        if (tile instanceof TileGenerator) {
            master = null; //Set the master to null, as this block may have been removed from the net
            ((TileGenerator) tile).reset();
        }
    }

    @Override
    public boolean rotate() {
        if (orientation == ForgeDirection.NORTH) {
            orientation = ForgeDirection.WEST;
        } else {
            orientation = ForgeDirection.NORTH;
        }

        return true;
    }

    @Override
    public ForgeDirection getFacing() {
        return orientation;
    }

    @Override
    public void setFacing(ForgeDirection dir) {
        switch (dir) {
            case NORTH:
            case SOUTH:
                orientation = ForgeDirection.NORTH;
                break;
            case EAST:
            case WEST:
                orientation = ForgeDirection.WEST;
                break;
            default:
                break;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.getBoolean("HasMaster")) {
            int x = nbt.getInteger("MasterX");
            int y = nbt.getInteger("MasterY");
            int z = nbt.getInteger("MasterZ");
            master = new CachedCoords(x, y, z);
        }

        orientation = ForgeDirection.getOrientation(nbt.getInteger("Orientation"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (master != null) {
            nbt.setBoolean("HasMaster", true);
            nbt.setInteger("MasterX", master.x);
            nbt.setInteger("MasterY", master.y);
            nbt.setInteger("MasterZ", master.z);
        } else nbt.setBoolean("HasMaster", false);

        nbt.setInteger("Orientation", orientation.ordinal());
    }
}
