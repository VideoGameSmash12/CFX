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

package me.videogamesm12.cfx.v1_16_2.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * <h1>UpsideDownPortals</h1>
 * <p>Fixes an exploit caused by an oversight in how the game handles nether portals and dispensers.</p>
 * <p>This patch is for versions 1.16.2 to 1.16.4.</p>
 */
@Mixin(AbstractFireBlock.class)
@PatchMeta(minVersion = 751, maxVersion = 754)
public class UpsideDownPortals
{
    @Inject(method = "method_30033",
            at = @At("HEAD"),
            cancellable = true)
    private static void properlyHandlePortalDirection(World world, BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir)
    {
        if (CFX.getConfig().getBlockPatches().isUpsideDownPortalPatchEnabled()
                && direction.getAxis().getType() == Direction.Type.VERTICAL)
        {
            cir.setReturnValue(false);
        }
    }
}