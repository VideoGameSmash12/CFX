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

package me.videogamesm12.cfx.v1_20.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * <h1>ClickableCommandSign</h1>
 * <p>Fixes or mitigates signs that execute commands when clicked.</p>
 * <p>This patch is for versions 1.20 to 1.20.2.</p>
 */
@Mixin(SignBlockEntity.class)
@PatchMeta(minVersion = 763, maxVersion = 764) // 1.20 to 1.20.2
public class ClickableCommandSign
{
    @Inject(method = "runCommandClickEvent",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/command/CommandManager;executeWithPrefix(Lnet/minecraft/server/command/ServerCommandSource;Ljava/lang/String;)I"),
            cancellable = true)
    public void handleClickedSignText(PlayerEntity player, World world, BlockPos pos, boolean front, CallbackInfoReturnable<Boolean> cir)
    {
        // Are we supposed to run the command?
        switch (CFX.getConfig().getTextPatches().getClickEvent().getCommandClickServerMode())
        {
            // Don't run the command
            case ONLY_NOTIFY:
            {
                CFX.getLogger().warn("Player " + player.getEntityName() + " attempted to execute commands in a sign at ("
                        + pos.toShortString() + "), but was unsuccessful.");
            }
            case DO_NOTHING:
            {
                cir.setReturnValue(true);
                return;
            }

            // Run the command
            case NOTIFY:
            {
                CFX.getLogger().warn("Player " + player.getEntityName() + " clicked a sign with commands in it at ("
                        + pos.toShortString() + ").");
            }
            case VANILLA:
            {
                // Do nothing
            }
        }
    }
}