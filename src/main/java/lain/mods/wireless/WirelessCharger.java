package lain.mods.wireless;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = WirelessCharger.MODID, useMetadata = true, acceptedMinecraftVersions = "[1.12],[1.12.1]")
public class WirelessCharger
{

    public static final String MODID = "wirelesscharger";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        EventHandler.init(event);
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStoppingEvent event)
    {
        EventHandler.onServerStopping(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        EventHandler.postInit(event);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        EventHandler.preInit(event);
    }

}
