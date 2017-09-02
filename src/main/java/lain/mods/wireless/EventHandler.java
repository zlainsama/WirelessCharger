package lain.mods.wireless;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = WirelessCharger.MODID)
public class EventHandler
{

    public static void init(FMLInitializationEvent event)
    {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event)
    {
        if (event.phase == Phase.END)
            return;
        if (event.side == Side.CLIENT)
            return;
        manager.updatePlayer((EntityPlayerMP) event.player);
    }

    public static void onServerStopping(FMLServerStoppingEvent event)
    {
        manager.clear();
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent event)
    {
        if (event.phase == Phase.END)
            return;
        manager.updateChargers();
    }

    public static void postInit(FMLPostInitializationEvent event)
    {
    }

    public static void preInit(FMLPreInitializationEvent event)
    {
        ConfigOptions.loadConfig(event.getSuggestedConfigurationFile());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        ModBlocks.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        ModItems.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        ModBlocks.initModels();
        ModItems.initModels();
    }

    public static final PowerManager manager = new PowerManager();

}
