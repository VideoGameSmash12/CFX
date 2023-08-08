package me.videogamesm12.cfx.entrypoint;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.delegation.Delegator;
import net.fabricmc.api.ClientModInitializer;

public class Client implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        if (CFX.getConfig().getOverrides().isClientCommandEnabled())
        {
            Delegator.registerClientCommands();
        }
    }
}
