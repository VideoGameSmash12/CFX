package me.videogamesm12.cfx.v1_16_5.delegation;

import me.videogamesm12.cfx.command.ReloadCommand;
import me.videogamesm12.cfx.delegation.IClientCommandRegistrar;
import me.videogamesm12.cfx.delegation.Requirements;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

/**
 * <h1>V1ClientCommandRegistrar</h1>
 * <p>Registers commands for clients running version 1 of the Fabric command API, which was used until 1.19.</p>
 */
@Requirements(min = 754, max = 758, dependencies = {"fabric-command-api-v1"}, priority = 1)
public class V1ClientCommandRegistrar implements IClientCommandRegistrar
{
    @Override
    public void register()
    {
        ClientCommandManager.DISPATCHER.register(
                ClientCommandManager.literal("cfxclient")
                        .then(ClientCommandManager.literal("reload").executes(ReloadCommand.Client.getInstance())));
    }
}
