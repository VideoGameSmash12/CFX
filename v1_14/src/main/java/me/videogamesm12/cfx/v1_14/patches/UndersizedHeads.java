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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.SkinRemappingImageFilter;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.resource.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * <h1>UndersizedHeads</h1>
 * <p>Patches an exploit caused by an oversight in how the game handles player skins.</p>
 * <p>This patch is for versions 1.14 to 1.14.4.</p>
 */
@Mixin(SkinRemappingImageFilter.class)
@PatchMeta(minVersion = 477, maxVersion = 498) // 1.14 to 1.14.4
public class UndersizedHeads
{
    @ModifyVariable(method = "filterImage", at = @At("HEAD"), argsOnly = true)
    private NativeImage enforceMinimumImageSize(NativeImage image)
    {
        if (CFX.getConfig().getResourcePatches().getPlayerSkins().isMinimumSkinResolutionEnforcementEnabled()
                && image.getHeight() < 32 || image.getWidth() < 64)
        {
            final ReloadableResourceManager rm = (ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager();
            final ResourceTexture.TextureData data = ResourceTexture.TextureData.load(rm, DefaultSkinHelper.getTexture());

            try
            {
                return data.getImage();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return image;
    }
}
