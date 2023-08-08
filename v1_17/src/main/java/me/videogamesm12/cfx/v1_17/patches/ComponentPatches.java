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

package me.videogamesm12.cfx.v1_17.patches;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComponentPatches
{
    /**
     * <h2>OutrageousTranslation</h2>
     * <p>Fixes an exploit caused by a design flaw in the translatable component's placeholder system.</p>
     * <p>This patch is for versions 1.17 to 1.18.2.</p>
     */
    @Mixin(Text.Serializer.class)
    @PatchMeta(minVersion = 755, maxVersion = 758) // 1.17 to 1.18.2
    public static class OutrageousTranslation
    {
        private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%[0-9]{1,}\\$s");

        @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/text/MutableText;",
                at = @At(value = "INVOKE",
                        target = "Lcom/google/gson/JsonElement;getAsJsonObject()Lcom/google/gson/JsonObject;",
                        shift = At.Shift.AFTER),
                cancellable = true)
        public void fixMajorExploit(JsonElement json, Type type, JsonDeserializationContext context, CallbackInfoReturnable<Object> cir)
        {
            if (CFX.getConfig().getTextPatches().getTranslation().isPlaceholderLimitEnabled()
                    && getNumberOfPlaceholders(json) > CFX.getConfig().getTextPatches().getTranslation().getPlaceholderLimit())
            {
                cir.setReturnValue(new TranslatableText("cfx.replacement.too_many_placeholders")
                        .formatted(Formatting.RED));
            }
        }

        private long getNumberOfPlaceholders(JsonElement element)
        {
            long amount = 0;

            // God dammit!
            if (!element.isJsonObject())
            {
                return amount;
            }

            JsonObject from = element.getAsJsonObject();

            // Figure out how many placeholders are in a single translatable component
            if (from.has("translate"))
            {
                String key = JsonHelper.getString(from, "translate");
                Matcher matcher = PLACEHOLDER_PATTERN.matcher(key);
                amount += matcher.results().count();
            }

            // Also applies to keybind components, but to a lesser extent
            if (from.has("keybind"))
            {
                String key = JsonHelper.getString(from, "keybind");
                Matcher matcher = PLACEHOLDER_PATTERN.matcher(key);
                amount += matcher.results().count();
            }

            // Recursively figure out how many placeholders the component has in the "with" shit
            if (from.has("with"))
            {
                JsonArray array = JsonHelper.getArray(from, "with");

                for (JsonElement within : array)
                {
                    long amountWithin = getNumberOfPlaceholders(within);

                    if (amountWithin == 1)
                    {
                        amount++;
                    }
                    else if (amountWithin > 1)
                    {
                        amount = amount * amountWithin;
                    }
                }
            }

            return amount;
        }
    }
}