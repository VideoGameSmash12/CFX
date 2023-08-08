package me.videogamesm12.cfx.v1_19.delegation;

import com.mojang.brigadier.CommandDispatcher;
import me.videogamesm12.cfx.command.ReloadCommand;
import me.videogamesm12.cfx.delegation.IServerCommandRegistrar;
import me.videogamesm12.cfx.delegation.Requirements;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

/**
 * <h1>V2ClientCommandRegistrar</h1>
 * <p>Registers commands for clients running version 2 of the Fabric command API, which is the current version.</p>
 */
@Requirements(min = 759, max = 9999, dependencies = {"fabric-command-api-v2"})
public class V2ServerCommandRegistrar implements IServerCommandRegistrar
{
    private FabricAPIBridge fabricAPIBridge;

    @Override
    public void register()
    {
        fabricAPIBridge = new FabricAPIBridge();
    }

    public static class FabricAPIBridge implements CommandRegistrationCallback
    {
        public FabricAPIBridge()
        {
            CommandRegistrationCallback.EVENT.register(this);
        }

        @Override
        public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment)
        {
            dispatcher.register(
                    CommandManager.literal("cfxserver")
                            .requires(source -> source.hasPermissionLevel(2))
                            .then(CommandManager.literal("reload").executes(new ReloadCommand.Server()))
            );
        }
    }
}
