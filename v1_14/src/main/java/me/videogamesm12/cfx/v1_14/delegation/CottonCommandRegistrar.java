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

package me.videogamesm12.cfx.v1_14.delegation;

import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.videogamesm12.cfx.command.ReloadCommand;
import me.videogamesm12.cfx.delegation.ICommandRegistrar;
import me.videogamesm12.cfx.delegation.Requirements;

/**
 * <h1>CottonCommandRegistrar</h1>
 * <p>Implementation of {@link ICommandRegistrar} that registers client-side commands with Cotton Client Commands.</p>
 */
@Requirements(min = 477, max = 754, dependencies = {"cotton-client-commands"})
public class CottonCommandRegistrar implements ICommandRegistrar, ClientCommandPlugin
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
