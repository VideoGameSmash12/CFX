package me.videogamesm12.cfx.v1_14.delegation;


import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.videogamesm12.cfx.command.ReloadCommand;
import me.videogamesm12.cfx.delegation.IClientCommandRegistrar;
import me.videogamesm12.cfx.delegation.Requirements;

@Requirements(min = 477, max = 754, dependencies = {"cotton-client-commands"})
public class CottonCommandRegistrar implements IClientCommandRegistrar, ClientCommandPlugin
{
    @Override
    public void register()
    {
        // We don't actually do anything here, Cotton Client Commands does all the registering
    }

    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> dispatcher)
    {
        dispatcher.register(
                ArgumentBuilders.literal("cfxclient")
                        .then(ArgumentBuilders.literal("reload").executes(ReloadCommand.Client.getInstance()))
        );
    }
}
