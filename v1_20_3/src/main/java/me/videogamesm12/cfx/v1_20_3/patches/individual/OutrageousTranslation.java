package me.videogamesm12.cfx.v1_20_3.patches.individual;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>OutrageousTranslation</h1>
 * <p>Patches an exploit in the old way of doing Text serialization by code.</p>
 * <p>This patch is for 1.20.3+ and even though the method isn't used anywhere, it's still here for compatibility reasons.</p>
 */
@Mixin(Text.Serializer.class)
@PatchMeta(minVersion = 765, maxVersion = 999) // 1.20.3 to Latest
public class OutrageousTranslation
{
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([0-9]{1,}\\$)?s");

    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/text/MutableText;",
            at = @At("HEAD"),
            cancellable = true)
    public void fixMajorExploit(JsonElement json, Type type, JsonDeserializationContext context, CallbackInfoReturnable<Object> cir)
    {
        if (CFX.getConfig().getTextPatches().getTranslation().isPlaceholderLimitEnabled()
                && getNumberOfPlaceholders(json) > CFX.getConfig().getTextPatches().getTranslation().getPlaceholderLimit())
        {
            cir.setReturnValue(Text.translatable("cfx.replacement.too_many_placeholders")
                    .formatted(Formatting.RED));
        }
    }

    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Ljava/lang/Object;",
            at = @At("HEAD"),
            cancellable = true)
    public void fixMajorExploitAgain(JsonElement json, Type type, JsonDeserializationContext context, CallbackInfoReturnable<Object> cir)
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

        if (element.isJsonArray())
        {
            for (JsonElement within : element.getAsJsonArray())
            {
                long amountWithin = getNumberOfPlaceholders(within);

                if (amountWithin == 1)
                {
                    amount++;
                }
                else if (amountWithin > 1)
                {
                    if (amount == 0)
                    {
                        amount = amountWithin;
                    }
                    else
                    {
                        amount = amount * amountWithin;
                    }
                }
            }

            return amount;
        }
        else if (element.isJsonObject())
        {
            JsonObject from = element.getAsJsonObject();

            // Figure out how many placeholders are in a single translatable component
            if (from.has("translate"))
            {
                String key = JsonHelper.getString(from, "translate");

                // Account for valid localization entries as well
                if (Language.getInstance().hasTranslation(key))
                {
                    key = Language.getInstance().get(key);
                }

                Matcher matcher = PLACEHOLDER_PATTERN.matcher(key);
                amount += matcher.results().count();
            }

            // Undocumented way for 1.19.4+
            if (from.has("fallback"))
            {
                String key = JsonHelper.getString(from, "fallback");

                // Account for valid localization entries as well
                if (Language.getInstance().hasTranslation(key))
                {
                    key = Language.getInstance().get(key);
                }

                Matcher matcher = PLACEHOLDER_PATTERN.matcher(key);
                amount += matcher.results().count();
            }

            // Also applies to keybind components, but to a lesser extent
            if (from.has("keybind"))
            {
                String key = JsonHelper.getString(from, "keybind");

                // Account for valid localization entries as well
                if (Language.getInstance().hasTranslation(key))
                {
                    key = Language.getInstance().get(key);
                }

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
        else
        {
            return amount;
        }
    }
}
