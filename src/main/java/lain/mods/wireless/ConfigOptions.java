package lain.mods.wireless;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

public class ConfigOptions
{

    public static void loadConfig(File file)
    {
        Configuration config = new Configuration(file);

        Range = config.getInt("Range", Configuration.CATEGORY_GENERAL, 40, 1, 10000, "Players in this radius will get their charged.");
        TransferRate = config.getInt("TransferRate", Configuration.CATEGORY_GENERAL, 30000, 1, 1000000000, "Energy transfer rate per item.");
        Capacity = config.getInt("Capacity", Configuration.CATEGORY_GENERAL, 3000000, 1, 1000000000, "Max energy can be stored.");
        BlacklistedItems = Collections.unmodifiableSet(Arrays.stream(config.getStringList("BlacklistedItems", Configuration.CATEGORY_GENERAL, new String[] { "thermalexpansion:cell" }, "These items will not be charged.")).collect(toResourceLocations()));
        UpgradeItem = new ResourceLocation(config.getString("UpgradeItem", Configuration.CATEGORY_GENERAL, "minecraft:nether_star", "The item required to upgrade a WirelessCharger, set to minecraft:air if you want to disable it, existing ones will not change"));

        if (config.hasChanged())
            config.save();
    }

    private static Collector<String, ?, Set<ResourceLocation>> toResourceLocations()
    {
        return Collector.of(HashSet::new, (r, e) -> r.add(new ResourceLocation(e)), (r1, r2) -> {
            r1.addAll(r2);
            return r1;
        });
    }

    public static int Range = 40;
    public static int TransferRate = 30000;
    public static int Capacity = 3000000;
    public static Set<ResourceLocation> BlacklistedItems = Collections.unmodifiableSet(Arrays.stream(new String[] { "thermalexpansion:cell" }).collect(toResourceLocations()));
    public static ResourceLocation UpgradeItem;

}
