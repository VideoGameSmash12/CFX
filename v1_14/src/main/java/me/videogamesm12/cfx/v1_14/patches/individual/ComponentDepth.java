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

package me.videogamesm12.cfx.v1_14.patches.individual;

import com.google.gson.*;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.config.CFXConfig;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

/**
 * <h1>ComponentDepth</h1>
 * <p>Patches an exploit caused by a design flaw in the component system.</p>
 * <p>This patch is for versions 1.14 to 1.15.2.</p>
 */
@Mixin(Text.Serializer.class)
@PatchMeta(minVersion = 477, maxVersion = 578) // 1.14 to 1.15.2
public class ComponentDepth
{
    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/text/Text;",
            at = @At(value = "INVOKE",
                    target = "Lcom/google/gson/JsonElement;getAsJsonArray()Lcom/google/gson/JsonArray;",
                    shift = At.Shift.AFTER),
            cancellable = true)
    public void patchComponentDepth(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<Text> cir)
    {
        final JsonArray array = jsonElement.getAsJsonArray();

        if (CFX.getConfig().getTextPatches().getGeneral().getArrayDepthMode() != CFXConfig.Text.General.ArrayDepthPatchMode.VANILLA)
        {
            validateComponentDepth(array, 0, CFX.getConfig().getTextPatches().getGeneral().getArrayDepthMaximum(), cir);
        }
    }


    public void validateComponentDepth(JsonElement e, long depth, long max, CallbackInfoReturnable<Text> cir)
    {
        if (depth > max)
        {
            switch (CFX.getConfig().getTextPatches().getGeneral().getArrayDepthMode())
            {
                case OBVIOUS:
                {
                    cir.setReturnValue(new TranslatableText("cfx.replacement.too_complex").formatted(Formatting.RED));
                    return;
                }
                case VANILLA_LIKE:
                {
                    throw new JsonParseException("Component is too complex, depth >= " + max);
                }
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

    public void validateArrayDepth(final JsonArray array, long depth, long max, CallbackInfoReturnable<Text> cir)
    {
        final long depth2 = depth + 1;

        for (JsonElement element : array)
        {
            validateComponentDepth(element, depth2, max, cir);
        }
    }
}