package me.videogamesm12.cfx;

import lombok.Getter;
import me.videogamesm12.cfx.config.CFXConfig;
import me.videogamesm12.cfx.delegation.Delegator;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CFX implements ModInitializer
{
    @Getter
    private static final Logger logger = LogManager.getLogger("CFX");
    //--
    @Getter
    private static CFXConfig config = CFXConfig.load();

    @Override
    public void onInitialize()
    {
        if (config.getOverrides().isServerCommandEnabled()) Delegator.registerServerCommands();
    }

    public static void reloadConfig()
    {
        logger.info("Reloading configuration!");
        config = CFXConfig.load();
    }
}
