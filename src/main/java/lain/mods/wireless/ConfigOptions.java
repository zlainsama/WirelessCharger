package lain.mods.wireless;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

public class ConfigOptions
{

    public static void loadConfig(File file)
    {
        Configuration config = new Configuration(file);

        Range = config.getInt("Range", Configuration.CATEGORY_GENERAL, 40, 1, 4000, "radius");
        TransferRate = config.getInt("TransferRate", Configuration.CATEGORY_GENERAL, 30000, 1, 1000000000, "per item");

        if (config.hasChanged())
            config.save();
    }

    public static int Range = 40;
    public static int TransferRate = 30000;

}
