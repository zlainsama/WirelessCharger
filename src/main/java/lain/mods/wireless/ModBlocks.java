package lain.mods.wireless;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder(WirelessCharger.MODID)
public class ModBlocks
{

    @ObjectHolder("wirelesscharger")
    public static final BlockWirelessCharger blockWirelessCharger = null;

    @SideOnly(Side.CLIENT)
    public static void initModels()
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.blockWirelessCharger), 0, new ModelResourceLocation(ModBlocks.blockWirelessCharger.getRegistryName(), "inventory"));
    }

    public static void register(IForgeRegistry<Block> registry)
    {
        registry.register(new BlockWirelessCharger());
    }

}
