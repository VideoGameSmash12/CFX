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
    public interface Client<T> extends Command<T>
    {
        @Override
        default int run(CommandContext<T> context) throws CommandSyntaxException
        {
            CFX.reloadConfig();

            // To ensure compatibility with both Fabric API and Cotton Client Commands, we do not rely on a specific API
            //  for sending chat messages to the sender with client-side commands. Instead, we just simply use
            //  InGameHud's addMessage method.
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
                    Delegator.getTextProvider().translatable("cfx.command.reload.success", Formatting.GREEN));
            return 1;
        }

        static <Z> Client<Z> getInstance()
        {
            return new Client<Z>() {};
        }
    }

    public static class Server implements Command<ServerCommandSource>
    {
        @Override
        public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
        {
            CFX.reloadConfig();
            try
            {
                Delegator.getFeedbackSender().sendFeedback(
                        context.getSource(),
                        Delegator.getTextProvider().translatable("cfx.command.reload.success", Formatting.GREEN),
                        true
                );
            }
            catch (Throwable ex)
            {
                CFX.getLogger().error("WTF?");
                ex.printStackTrace();
            }
            return 1;
        }
    }
}
