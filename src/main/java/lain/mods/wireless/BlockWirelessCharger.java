package lain.mods.wireless;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockWirelessCharger extends Block implements ITileEntityProvider
{

    public BlockWirelessCharger()
    {
        super(Material.ROCK);
        setUnlocalizedName(WirelessCharger.MODID + ".wirelesscharger");
        setRegistryName("wirelesscharger");
        setHardness(2);
        setResistance(10);
        setSoundType(SoundType.STONE);
        setCreativeTab(CreativeTabs.REDSTONE);

        GameRegistry.registerTileEntity(TileEntityWirelessCharger.class, WirelessCharger.MODID + "_wirelesscharger");
    }

    @Override
    public TileEntity createNewTileEntity(World arg0, int arg1)
    {
        return new TileEntityWirelessCharger();
    }

}
