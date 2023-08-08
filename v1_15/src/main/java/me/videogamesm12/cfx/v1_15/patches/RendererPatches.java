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

package me.videogamesm12.cfx.v1_15.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

public class RendererPatches
{
    public static class EntityPatches
    {
        /**
         * <h3>ExcessiveEntityNames</h3>
         * <p>Mitigates an exploit caused by entities with custom names that are excessively long.</p>
         * <p>This patch is for versions 1.15 to 1.15.2.</p>
         */
        @Mixin(EntityRenderer.class)
        @PatchMeta(minVersion = 573, maxVersion = 578) // 1.15 to 1.15.2
        public static class ExcessiveEntityNames
        {
            @ModifyVariable(method = "renderLabelIfPresent",
                    at = @At(value = "HEAD"),
                    argsOnly = true)
            private String limitLabelSize(String text)
            {
                if (CFX.getConfig().getRenderPatches().getEntity().isNameLengthLimitEnabled()
                        && text.length() > CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit())
                {
                    return text.substring(0, CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit());
                }

                return text;
            }
        }
    }
}
