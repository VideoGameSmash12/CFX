package me.videogamesm12.cfx.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.delegation.Delegator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;

public class ReloadCommand
{
    public static class Client<T> implements Command<T>
    {
        @Override
        public int run(CommandContext<T> context) throws CommandSyntaxException
        {
            CFX.reloadConfig();

            // To ensure compatibility with both Fabric API and Cotton Client Commands, we do not rely on a specific API
            //  for sending chat messages to the sender with client-side commands. Instead, we just simply use
            //  ClientPlayerEntity's sendMessage method to achieve the same goal.
            MinecraftClient.getInstance().player.sendMessage(
                    Delegator.getTextProvider().translatable("cfx.command.reload.success", Formatting.GREEN));

            return 1;
        }

        public static Client<?> getInstance()
        {
            return new Client<>();
        }
    }

    public static class Server implements Command<ServerCommandSource>
    {
        @Override
        public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
        {
            CFX.reloadConfig();
            context.getSource().sendFeedback(() ->
                    Delegator.getTextProvider().translatable("cfx.command.reload.success", Formatting.GREEN),
                    true);

            return 1;
        }
    }
}
