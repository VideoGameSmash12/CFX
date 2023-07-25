package me.videogamesm12.cfx.v1_19.patches;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComponentPatches
{
    @Mixin(TranslatableTextContent.class)
    @PatchMeta(minVersion = 759, maxVersion = 760) // 1.19.1 to 1.19.2
    public static class BoundlessTranslation
    {
        @Shadow
        @Final
        private static StringVisitable NULL_ARGUMENT;

        @Inject(method = "getArg", at = @At("HEAD"), cancellable = true)
        public void fixCrashExploit(int index, CallbackInfoReturnable<StringVisitable> cir)
        {
            if (CFX.getConfig().getTextPatches().getTranslation().isBoundaryPatchEnabled() && index < 0)
            {
                cir.setReturnValue(NULL_ARGUMENT);
            }
        }
    }

    @Mixin(Text.Serializer.class)
    @PatchMeta(minVersion = 759, maxVersion = 999) // 1.19 to Latest
    public static class OutrageousTranslation
    {
        private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%[0-9]{1,}\\$s");

        @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/text/MutableText;", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonElement;getAsJsonObject()Lcom/google/gson/JsonObject;", shift = At.Shift.AFTER), cancellable = true)
        public void fixMajorExploit(JsonElement json, Type type, JsonDeserializationContext context, CallbackInfoReturnable<Object> cir)
        {
            if (CFX.getConfig().getTextPatches().getTranslation().isPlaceholderLimitEnabled()
                    && getNumberOfPlaceholders(json) > CFX.getConfig().getTextPatches().getTranslation().getPlaceholderLimit())
            {
                cir.setReturnValue(Text.translatable("cfx.replacement.too_many_placeholders")
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

            // Undocumented way for 1.19.4+
            if (from.has("fallback"))
            {
                String key = JsonHelper.getString(from, "fallback");
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