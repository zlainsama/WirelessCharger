package lain.mods.wireless;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class PowerManager
{

    private static final Predicate<? super TileEntity> FILTER = tile -> {
        if (tile.isInvalid())
            return false;
        if (tile instanceof TileEntityWirelessCharger)
            return !((TileEntityWirelessCharger) tile).isDisabled();
        return true;
    };

    int scanDelay = 0;

    final List<TileEntity> loadedChargers = new ArrayList<TileEntity>();
    final Map<EntityPlayerMP, List<TileEntity>> nearbyChargers = new WeakHashMap<EntityPlayerMP, List<TileEntity>>();

    public void addCharger(TileEntity tileentity)
    {
        if (tileentity.hasCapability(CapabilityEnergy.ENERGY, null))
            loadedChargers.add(tileentity);
    }

    public void clear()
    {
        scanDelay = 0;

        loadedChargers.clear();
        nearbyChargers.clear();
    }

    private void doCharge(EntityPlayerMP player, Collection<TileEntity> chargers)
    {
        if (chargers.isEmpty())
            return;

        IntStream.range(0, player.inventory.getSizeInventory()).mapToObj(player.inventory::getStackInSlot).filter(s -> {
            IEnergyStorage c = s.getCapability(CapabilityEnergy.ENERGY, null);
            if (c != null && c.canReceive() && c.getMaxEnergyStored() > c.getEnergyStored() && !ConfigOptions.BlacklistedItems.contains(s.getItem().getRegistryName()))
                return true;
            return false;
        }).map(s -> s.getCapability(CapabilityEnergy.ENERGY, null)).forEachOrdered(cStack -> {
            int needed = cStack.getMaxEnergyStored() - cStack.getEnergyStored();
            int available = chargers.stream().filter(FILTER).mapToInt(c -> c.getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(needed, true)).sum();
            int drain = Math.max(Math.min(available / chargers.size(), cStack.receiveEnergy(needed, true) / chargers.size()), 1);
            chargers.stream().filter(FILTER).forEach(c -> cStack.receiveEnergy(c.getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(drain, false), false));
        });
    }

    private void doScan()
    {
        int sqRange = ConfigOptions.Range * ConfigOptions.Range;

        nearbyChargers.clear();
        getChargers().stream().filter(FILTER).forEach(c -> {
            boolean handled = false;

            if (c instanceof TileEntityWirelessCharger)
            {
                TileEntityWirelessCharger t = (TileEntityWirelessCharger) c;
                if (t.isUpgraded())
                {
                    handled = true;

                    EntityPlayerMP p = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(t.getTargetUser());
                    if (p != null)
                    {
                        List<TileEntity> l = nearbyChargers.get(p);
                        if (l == null)
                            nearbyChargers.put(p, l = new ArrayList<TileEntity>());
                        l.add(c);
                    }
                }
            }

            if (!handled)
            {
                c.getWorld().getPlayers(EntityPlayerMP.class, p -> p.getDistanceSqToCenter(c.getPos()) <= sqRange).forEach(p -> {
                    List<TileEntity> l = nearbyChargers.get(p);
                    if (l == null)
                        nearbyChargers.put(p, l = new ArrayList<TileEntity>());
                    l.add(c);
                });
            }
        });
    }

    public Collection<TileEntity> getChargers()
    {
        return Collections.unmodifiableList(loadedChargers);
    }

    public Collection<TileEntity> getChargersInRange(EntityPlayerMP player)
    {
        if (nearbyChargers.containsKey(player))
            return Collections.unmodifiableList(nearbyChargers.get(player));
        return Collections.emptyList();
    }

    public void removeCharger(TileEntity tileentity)
    {
        loadedChargers.remove(tileentity);
    }

    public void updateChargers()
    {
        updateChargers(false);
    }

    public void updateChargers(boolean force)
    {
        if (force || ++scanDelay == 20)
        {
            scanDelay = 0;
            doScan();
        }
    }

    public void updatePlayer(EntityPlayerMP player)
    {
        if (player.isEntityAlive())
            doCharge(player, getChargersInRange(player));
    }

}
