/*
 * Copyright (c) 2023 Video
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

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

    public interface Server extends Command<ServerCommandSource>
    {
        @Override
        default int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
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

        static Server getInstance()
        {
            return new Server() {};
        }
    }
}
