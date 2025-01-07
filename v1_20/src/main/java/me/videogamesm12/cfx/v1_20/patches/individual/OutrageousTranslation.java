package me.videogamesm12.cfx.v1_20.patches.individual;

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
 * <p>Fixes an exploit caused by a design flaw in the translatable component's placeholder system.</p>
 * <p>This patch is for 1.19 to 1.20.2.</p>
 */
@Mixin(Text.Serializer.class)
@PatchMeta(minVersion = 763, maxVersion = 764) // 1.20 to 1.20.2
public class OutrageousTranslation
{
	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([0-9]{1,}\\$)?s");

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
}