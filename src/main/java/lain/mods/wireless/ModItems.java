package lain.mods.wireless;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder(WirelessCharger.MODID)
public class ModItems
{

    @SideOnly(Side.CLIENT)
    public static void initModels()
    {
    }

    public static void register(IForgeRegistry<Item> registry)
    {
        registry.register(new ItemBlock(ModBlocks.blockWirelessCharger).setRegistryName(ModBlocks.blockWirelessCharger.getRegistryName()));
    }

}
