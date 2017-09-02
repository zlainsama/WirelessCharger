package lain.mods.wireless;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileEntityWirelessCharger extends TileEntity
{

    class EnergyStorage extends net.minecraftforge.energy.EnergyStorage
    {

        EnergyStorage()
        {
            super(0);
        }

        EnergyStorage setEnergyStored(int energy)
        {
            this.energy = energy;
            return this;
        }

        EnergyStorage setMaxEnergyStored(int capacity)
        {
            this.capacity = capacity;
            return this;
        }

        EnergyStorage setMaxExtract(int maxExtract)
        {
            this.maxExtract = maxExtract;
            return this;
        }

        EnergyStorage setMaxReceive(int maxReceive)
        {
            this.maxReceive = maxReceive;
            return this;
        }

    }

    EnergyStorage energy = new EnergyStorage();

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
            return (T) energy;
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        int transfer = ConfigOptions.TransferRate;
        int capacity = transfer * 10;
        int stored = Math.min(compound.getInteger("StoredEnergy"), capacity);
        energy = new EnergyStorage().setMaxEnergyStored(capacity).setMaxReceive(Integer.MAX_VALUE).setMaxExtract(transfer).setEnergyStored(stored);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("StoredEnergy", energy.getEnergyStored());
        return compound;
    }

}
