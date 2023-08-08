package me.videogamesm12.cfx.v1_19.delegation;

import com.mojang.brigadier.CommandDispatcher;
import me.videogamesm12.cfx.command.ReloadCommand;
import me.videogamesm12.cfx.delegation.IClientCommandRegistrar;
import me.videogamesm12.cfx.delegation.Requirements;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

/**
 * <h1>V2ClientCommandRegistrar</h1>
 * <p>Registers commands for clients running version 2 of the Fabric command API, which is the current version.</p>
 */
@Requirements(min = 759, max = 9999, dependencies = {"fabric-command-api-v2"})
public class V2ClientCommandRegistrar implements IClientCommandRegistrar
{
    private FabricAPIBridge fabricAPIBridge;

    @Override
    public void register()
    {
        fabricAPIBridge = new FabricAPIBridge();
    }

    public static class FabricAPIBridge implements ClientCommandRegistrationCallback
    {
        public FabricAPIBridge()
        {
            ClientCommandRegistrationCallback.EVENT.register(this);
        }

        @Override
        public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess)
        {
            dispatcher.register(
                    ClientCommandManager.literal("cfxclient")
                            .then(ClientCommandManager.literal("reload").executes(ReloadCommand.Client.getInstance()))
            );
        }
    }
}
