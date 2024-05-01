package me.videogamesm12.cfx.v1_20_3.patches.individual;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import me.videogamesm12.cfx.v1_20_3.mixin.JsonReaderUtilsAccessor;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>OutrageousTranslationThree</h1>
 * <p>Patches an exploit in the other way of doing Text serialization by code.</p>
 * <p>This patch is for 1.20.3+.</p>
 */
@Mixin(TextArgumentType.class)
@PatchMeta(minVersion = 765, maxVersion = 999) // 1.20.3 to Latest
public class OutrageousTranslationThree
{
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%[0-9]{1,}\\$s");
    private static final Gson GSON = new Gson();

    @Inject(method = "parse(Lcom/mojang/brigadier/StringReader;)Lnet/minecraft/text/Text;",
            at = @At("HEAD"),
            cancellable = true)
    public void fixMajorExploit(StringReader stringReader, CallbackInfoReturnable<Text> cir)
    {
        // We need to parse this as JSON data before the game actually uses the codec system to do so because once the
        //  codec system is called into play, it becomes impossible to actually prevent the exploit from happening.
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(stringReader.getRemaining()));
        jsonReader.setLenient(false);

        if (CFX.getConfig().getTextPatches().getTranslation().isPlaceholderLimitEnabled()
                && getNumberOfPlaceholders(Streams.parse(jsonReader)) > CFX.getConfig().getTextPatches().getTranslation().getPlaceholderLimit())
        {
            cir.setReturnValue(Text.translatable("cfx.replacement.too_many_placeholders")
                    .formatted(Formatting.RED));

            stringReader.setCursor(stringReader.getCursor() + JsonReaderUtilsAccessor.invokeGetPos(jsonReader));
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
        else
        {
            return amount;
        }
    }
}
