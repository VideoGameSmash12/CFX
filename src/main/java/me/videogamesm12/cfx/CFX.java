package me.videogamesm12.cfx;

import lombok.Getter;
import me.videogamesm12.cfx.config.CFXConfig;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CFX implements ModInitializer
{
    @Getter
    private static final Logger logger = LogManager.getLogger("CFX");
    //--
    @Getter
    private static final CFXConfig config = CFXConfig.load();

    @Override
    public void onInitialize()
    {
    }
}
