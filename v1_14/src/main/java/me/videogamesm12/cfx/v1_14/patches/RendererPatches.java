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

package me.videogamesm12.cfx.v1_14.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.gui.hud.InGameHud;
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
         * <p>This patch is for versions 1.14 to 1.14.4.</p>
         */
        @Mixin(EntityRenderer.class)
        @PatchMeta(minVersion = 477, maxVersion = 498) // 1.14.4 only
        public static class ExcessiveEntityNames
        {
            @ModifyVariable(method = "renderLabel(Lnet/minecraft/entity/Entity;Ljava/lang/String;DDDI)V",
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

    public static class HudPatches
    {
        /**
         * <h3>ExcessiveHearts</h3>
         * <p>Mitigates an exploit caused by a lack of a limit for how many hearts can be rendered on screen at once.</p>
         * <p>This patch is for versions 1.14 to 1.15.2.</p>
         */
        @Mixin(value = InGameHud.class)
        @PatchMeta(minVersion = 477, maxVersion = 578) // 1.14 to 1.15.2
        public static class ExcessiveHearts
        {
            @ModifyVariable(method = "renderStatusBars",
                    at = @At("STORE"),
                    ordinal = 6)
            public int capAbsorptionHeartCount(int hearts)
            {
                if (CFX.getConfig().getRenderPatches().getHud().isAbsorptionHeartCountLimitEnabled())
                {
                    return Math.min(hearts,
                            CFX.getConfig().getRenderPatches().getHud().getMaxAbsorptionHeartsToRender() * 2);
                }

                return hearts;
            }

            @ModifyVariable(method = "renderStatusBars", at = @At("STORE"), ordinal = 0)
            public float capRegularHeartCount(float maxHearts)
            {
                if (CFX.getConfig().getRenderPatches().getHud().isHeartCountLimitEnabled())
                {
                    return Math.min(maxHearts,
                            CFX.getConfig().getRenderPatches().getHud().getMaxHeartsToRender() * 2);
                }

                return maxHearts;
            }
        }
    }
}
