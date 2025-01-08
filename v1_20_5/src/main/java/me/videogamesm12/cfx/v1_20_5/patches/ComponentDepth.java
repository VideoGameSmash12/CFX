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

package me.videogamesm12.cfx.v1_20_5.patches;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.config.CFXConfig;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * <h1>ComponentDepth</h1>
 * <p>Fixes an exploit caused by a design flaw in the component system.</p>
 * <p>This patch is for versions 1.20.5+.</p>
 */
@Mixin(Text.Serialization.class)
@PatchMeta(minVersion = 766, maxVersion = 999) // 1.20.5 to Latest
public class ComponentDepth
{
    @Inject(method = "fromJson(Lcom/google/gson/JsonElement;Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Lnet/minecraft/text/MutableText;",
            at = @At("HEAD"),
            cancellable = true)
    private static void patchComponentDepth(JsonElement jsonElement, RegistryWrapper.WrapperLookup registries, CallbackInfoReturnable<MutableText> cir)
    {
        if (!jsonElement.isJsonArray() || !jsonElement.isJsonObject())
        {
            return;
        }

        if (CFX.getConfig().getTextPatches().getGeneral().getArrayDepthMode() != CFXConfig.Text.General.ArrayDepthPatchMode.VANILLA)
        {
            validateComponentDepth(jsonElement, 0, CFX.getConfig().getTextPatches().getGeneral().getArrayDepthMaximum(), cir);
        }
    }

    @Unique
    private static void validateComponentDepth(JsonElement e, long depth, long max, CallbackInfoReturnable<MutableText> cir)
    {
        if (depth > max)
        {
            switch (CFX.getConfig().getTextPatches().getGeneral().getArrayDepthMode())
            {
                case OBVIOUS ->
                {
                    cir.setReturnValue(Text.translatable("cfx.replacement.too_complex").formatted(Formatting.RED));
                    return;
                }
                case VANILLA_LIKE -> throw new JsonParseException("Component is too complex, depth >= " + max);
            }
        }

        if (e.isJsonObject())
        {
            final JsonObject object = e.getAsJsonObject();

            if (object.has("extra") && object.get("extra").isJsonArray())
            {
                validateArrayDepth(object.getAsJsonArray("extra"), depth, max, cir);
            }
            else if (object.has("translate") && object.has("with") && object.get("with").isJsonArray())
            {
                validateArrayDepth(object.getAsJsonArray("with"), depth, max, cir);
            }
        }
        else if (e.isJsonArray())
        {
            validateArrayDepth(e.getAsJsonArray(), depth, max, cir);
        }
    }

    @Unique
    private static void validateArrayDepth(final JsonArray array, long depth, long max, CallbackInfoReturnable<MutableText> cir)
    {
        final long depth2 = depth + 1;

        for (JsonElement element : array)
        {
            validateComponentDepth(element, depth2, max, cir);
        }
    }
}