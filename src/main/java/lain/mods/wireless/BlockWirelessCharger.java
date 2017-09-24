package lain.mods.wireless;

import javax.annotation.Nullable;
import lain.mods.wireless.TileEntityWirelessCharger.EnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.UsernameCache;
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
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        super.getDrops(drops, world, pos, state, fortune);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityWirelessCharger)
        {
            TileEntityWirelessCharger t = (TileEntityWirelessCharger) tile;

            if (t.hasUpgradeItem())
                drops.add(t.getUpgradeItem().copy());
        }
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool)
    {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        updateRedstoneState(worldIn, pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntityWirelessCharger)
            {
                TileEntityWirelessCharger t = (TileEntityWirelessCharger) tile;

                if (!t.isUpgraded())
                {
                    ItemStack s = playerIn.getHeldItem(hand);
                    if (!s.isEmpty() && ConfigOptions.UpgradeItem.equals(s.getItem().getRegistryName()) && s.getCount() > 0)
                    {
                        ItemStack put = s.copy();
                        put.setCount(1);
                        t.setUpgradeItem(put);
                        t.setTargetUser(playerIn.getUniqueID());
                        t.setUpgraded(true);

                        if (!playerIn.capabilities.isCreativeMode)
                            s.shrink(1);
                        return true;
                    }
                }

                EnergyStorage energy = t.getEnergyStorage();
                StringBuilder s = new StringBuilder();
                s.append(String.format("%d/%d", energy.getEnergyStored(), energy.getMaxEnergyStored()));
                if (t.isUpgraded())
                {
                    s.append(" ");
                    String name = UsernameCache.getLastKnownUsername(t.getTargetUser());
                    if (name == null)
                        name = t.getTargetUser().toString();
                    s.append(name);
                }
                playerIn.sendStatusMessage(new TextComponentString(s.toString()), true);
            }
        }
        return true;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        updateRedstoneState(worldIn, pos);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        if (willHarvest)
            return true; // If it will harvest, delay deletion of the block until after getDrops
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    private void updateRedstoneState(World worldIn, BlockPos pos)
    {
        if (!worldIn.isRemote)
        {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntityWirelessCharger)
            {
                TileEntityWirelessCharger t = (TileEntityWirelessCharger) tile;

                t.setDisabled(worldIn.isBlockPowered(pos));
            }
        }
    }

}
