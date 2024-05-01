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

package me.videogamesm12.cfx.v1_19.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

public class RendererPatches
{
    public static class EntityPatches
    {
        @Mixin(EntityRenderer.class)
        @PatchMeta(minVersion = 759, maxVersion = 765) // 1.19 to 1.20.4
        public static class ExcessiveEntityNames
        {
            @ModifyVariable(method = "renderLabelIfPresent", at = @At(value = "HEAD"), argsOnly = true)
            private Text limitLabelSize(Text text)
            {
                if (CFX.getConfig().getRenderPatches().getEntity().isNameLengthLimitEnabled()
                        && text.getString().length() > CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit())
                {
                    return Text.literal(text.asTruncatedString(CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit()))
                            .setStyle(text.getStyle());
                }

                return text;
            }
        }
    }
}
