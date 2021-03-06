package lain.mods.wireless;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

public class PowerManager
{

    private static final Predicate<? super TileEntity> FILTER = tile -> {
        if (tile.isInvalid())
            return false;
        if (tile instanceof TileEntityWirelessCharger)
            return !((TileEntityWirelessCharger) tile).isDisabled();
        return true;
    };

    private static final boolean fBaubles = Loader.isModLoaded("baubles");

    private static final Stream<ItemStack> sInv(EntityPlayer player)
    {
        Stream<ItemStack> res = IntStream.range(0, player.inventory.getSizeInventory()).mapToObj(player.inventory::getStackInSlot);

        if (fBaubles)
        {
            try
            {
                IBaublesItemHandler bih = BaublesApi.getBaublesHandler(player);
                res = Stream.concat(res, IntStream.range(0, bih.getSlots()).mapToObj(bih::getStackInSlot));
            }
            catch (Throwable ignored)
            {
            }
        }

        return res;
    }

    int scanDelay = 0;

    final Set<TileEntity> loadedChargers = new HashSet<TileEntity>();
    final Map<EntityPlayerMP, Set<TileEntity>> nearbyChargers = new WeakHashMap<EntityPlayerMP, Set<TileEntity>>();

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

        sInv(player).filter(s -> {
            if (s.isEmpty() || ConfigOptions.BlacklistedItems.contains(s.getItem().getRegistryName()) || !s.hasCapability(CapabilityEnergy.ENERGY, null))
                return false;
            IEnergyStorage c = s.getCapability(CapabilityEnergy.ENERGY, null);
            return c != null && c.canReceive() && c.getMaxEnergyStored() > c.getEnergyStored();
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
                        Set<TileEntity> s = nearbyChargers.get(p);
                        if (s == null)
                            nearbyChargers.put(p, s = new HashSet<TileEntity>());
                        s.add(c);
                    }
                }
            }

            if (!handled)
            {
                c.getWorld().getPlayers(EntityPlayerMP.class, p -> p.getDistanceSqToCenter(c.getPos()) <= sqRange).forEach(p -> {
                    Set<TileEntity> s = nearbyChargers.get(p);
                    if (s == null)
                        nearbyChargers.put(p, s = new HashSet<TileEntity>());
                    s.add(c);
                });
            }
        });
    }

    public Collection<TileEntity> getChargers()
    {
        return Collections.unmodifiableSet(loadedChargers);
    }

    public Collection<TileEntity> getChargersInRange(EntityPlayerMP player)
    {
        if (nearbyChargers.containsKey(player))
            return Collections.unmodifiableSet(nearbyChargers.get(player));
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
