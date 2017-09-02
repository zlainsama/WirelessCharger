package lain.mods.wireless;

import lain.mods.wireless.TileEntityWirelessCharger.EnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
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

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntityWirelessCharger)
            {
                EnergyStorage energy = ((TileEntityWirelessCharger) tile).getEnergyStorage();
                playerIn.sendStatusMessage(new TextComponentString(String.format("%d/%d", energy.getEnergyStored(), energy.getMaxEnergyStored())), true);
            }
        }
        return true;
    }

}
