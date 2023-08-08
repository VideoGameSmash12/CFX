package me.videogamesm12.cfx.v1_14.delegation;

import com.mojang.brigadier.CommandDispatcher;
import me.videogamesm12.cfx.command.ReloadCommand;
import me.videogamesm12.cfx.delegation.IServerCommandRegistrar;
import me.videogamesm12.cfx.delegation.Requirements;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

/**
 * <h1>V1ServerCommandRegistrar</h1>
 * <p>Registers server commands for servers running version 1 of the the Fabric command API, which was used until 1.19.</p>
 */
@Requirements(min = 477, max = 758, dependencies = {"fabric-command-api-v1"}, priority = 1)
public class V1ServerCommandRegistrar implements IServerCommandRegistrar
{
    private FabricAPIBridge fabricAPIBridge;

    @Override
    public void register()
    {
        fabricAPIBridge = new FabricAPIBridge();
    }

    // This is its own separate class because if it wasn't, the client would refuse to start because the Fabric classes
    //  wouldn't be found, even though that's entirely the point of what I'm trying to pull off...
    public static class FabricAPIBridge implements CommandRegistrationCallback
    {
        public FabricAPIBridge()
        {
            CommandRegistrationCallback.EVENT.register(this);
        }

        @Override
        public void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated)
        {
            dispatcher.register(
                    CommandManager.literal("cfxserver")
                            .requires(source -> source.hasPermissionLevel(2))
                            .then(CommandManager.literal("reload").executes(new ReloadCommand.Server()))
            );
        }
    }
}
