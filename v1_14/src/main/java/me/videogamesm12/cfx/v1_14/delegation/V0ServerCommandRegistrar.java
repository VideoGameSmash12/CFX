package me.videogamesm12.cfx.v1_14.delegation;

import me.videogamesm12.cfx.command.ReloadCommand;
import me.videogamesm12.cfx.delegation.IServerCommandRegistrar;
import me.videogamesm12.cfx.delegation.Requirements;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.server.command.CommandManager;

/**
 * <h1>V0ServerCommandRegistrar</h1>
 * <p>Registers server commands for servers running version 0 of the the Fabric command API, which was used until ~1.14.4.</p>
 */
@Requirements(min = 477, max = 758, dependencies = {"fabric"})
public class V0ServerCommandRegistrar implements IServerCommandRegistrar
{
    @Override
    public void register()
    {
        CommandRegistry.INSTANCE.register(false, (dispatcher) -> dispatcher.register(
                CommandManager.literal("cfxserver")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("reload").executes(new ReloadCommand.Server()))
        ));
    }
}
