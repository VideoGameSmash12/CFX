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

import com.google.gson.*;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

public class ComponentPatches
{
    /**
     * <h2>BoundlessTranslation</h2>
     * <p>Fixes an exploit caused by an oversight in the translatable component's placeholder system.</p>
     * <p>This patch is for versions 1.14 to 1.15.2.</p>
     */
    @Mixin(TranslatableText.class)
    @PatchMeta(minVersion = 477, maxVersion = 578) // 1.14.4 to 1.15.2
    public static class BoundlessTranslation
    {
        @Inject(method = "getArg", at = @At("HEAD"), cancellable = true)
        public void fixCrashExploit(int index, CallbackInfoReturnable<Text> cir)
        {
            if (CFX.getConfig().getTextPatches().getTranslation().isBoundaryPatchEnabled() && index < 0)
            {
                cir.setReturnValue(new LiteralText("null"));
            }
        }
    }

    /**
     * <h2>ExtraEmptyArray</h2>
     * <p>Fixes an exploit caused by an oversight in the translatable component's placeholder system.</p>
     * <p>This patch is for 1.14 to 1.15.2.</p>
     */
    @Mixin(Text.Serializer.class)
    @PatchMeta(minVersion = 477, maxVersion = 578) // 1.14 to 1.15.2
    public static class ExtraEmptyArray
    {
        @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/text/Text;",
                at = @At(value = "INVOKE",
                        target = "Lcom/google/gson/JsonElement;getAsJsonArray()Lcom/google/gson/JsonArray;",
                        shift = At.Shift.AFTER))
        public void patchEmptyArray(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<Text> cir)
        {
            final JsonArray array = jsonElement.getAsJsonArray();

            if (CFX.getConfig().getTextPatches().getExtra().isEmptyArrayPatchEnabled() && array.size() <= 0)
            {
                throw new JsonParseException("Unexpected empty array of components");
            }
        }
    }
}
