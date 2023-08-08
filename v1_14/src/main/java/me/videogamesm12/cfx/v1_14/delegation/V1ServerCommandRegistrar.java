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
import me.videogamesm12.cfx.command.ReloadCommand;
import me.videogamesm12.cfx.delegation.ICommandRegistrar;
import me.videogamesm12.cfx.delegation.Requirements;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

/**
 * <h1>V1ServerCommandRegistrar</h1>
 * <p>Implementation of {@link ICommandRegistrar} that registers server-side commands with Fabric Command API v1.</p>
 */
@Requirements(min = 477, max = 758, dependencies = {"fabric-command-api-v1"}, priority = 1)
public class V1ServerCommandRegistrar implements ICommandRegistrar
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
                            .then(CommandManager.literal("reload").executes(ReloadCommand.Server.getInstance()))
            );
        }
    }
}
