package lain.mods.wireless;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileEntityWirelessCharger extends TileEntity
{

    class EnergyStorage extends net.minecraftforge.energy.EnergyStorage
    {

        final TileEntity owner;

        EnergyStorage(TileEntity owner)
        {
            super(0);
            this.owner = owner;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate)
        {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (!simulate && extracted != 0)
                owner.markDirty();
            return extracted;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate)
        {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && received != 0)
                owner.markDirty();
            return received;
        }

        EnergyStorage setEnergyStored(int energy)
        {
            this.energy = MathHelper.clamp(energy, 0, getMaxEnergyStored());
            return this;
        }

        EnergyStorage setMaxEnergyStored(int capacity)
        {
            this.capacity = Math.max(capacity, 0);
            return this;
        }

        EnergyStorage setMaxExtract(int maxExtract)
        {
            this.maxExtract = Math.max(maxExtract, 0);
            return this;
        }

        EnergyStorage setMaxReceive(int maxReceive)
        {
            this.maxReceive = Math.max(maxReceive, 0);
            return this;
        }

    }

    EnergyStorage energy;

    EnergyStorage createEnergyStorage()
    {
        return new EnergyStorage(this).setMaxEnergyStored(ConfigOptions.Capacity).setMaxReceive(Integer.MAX_VALUE).setMaxExtract(ConfigOptions.TransferRate).setEnergyStored(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
            return (T) getEnergyStorage();
        return super.getCapability(capability, facing);
    }

    public EnergyStorage getEnergyStorage()
    {
        if (energy == null)
            energy = createEnergyStorage();
        return energy;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        if (!getWorld().isRemote)
            EventHandler.manager.removeCharger(this);
    }

    @Override
    public void onChunkUnload()
    {
        super.onChunkUnload();
        if (!getWorld().isRemote)
            EventHandler.manager.removeCharger(this);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (!getWorld().isRemote)
            EventHandler.manager.addCharger(this);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        getEnergyStorage().setEnergyStored(compound.getInteger("StoredEnergy"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("StoredEnergy", energy.getEnergyStored());
        return compound;
    }

}
