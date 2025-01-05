package me.videogamesm12.cfx.v1_20_3.patches.individual;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(TranslatableTextContent.class)
@PatchMeta(minVersion = 765, maxVersion = 765) // 1.20.3 to 1.20.4
public class OutrageousTranslation
{
	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([0-9]{1,}\\$)?s");

	@Inject(method = "of", at = @At("HEAD"), cancellable = true)
	private static void injectOf(String key, Optional<String> fallback, Optional<List<Object>> args, CallbackInfoReturnable<TranslatableTextContent> cir)
	{
		if (CFX.getConfig().getTextPatches().getTranslation().isPlaceholderLimitEnabled()
				&& getNumberOfPlaceholders(key, fallback.orElse(null),
				args.orElse(Collections.emptyList()).toArray(), null) > CFX.getConfig().getTextPatches().getTranslation().getPlaceholderLimit())
		{
			cir.setReturnValue(new TranslatableTextContent("", null, new Object[]{}));
		}
	}

	private static long getNumberOfPlaceholders(String key, String fallback, Object[] args, Language lang)
	{
		long amount = 0;

		// Current language
		if (lang == null)
		{
			lang = Language.getInstance();
		}

		// The key that would get processed
		key = lang.get(key, fallback != null ? lang.get(fallback) : key);
		Matcher matcher = PLACEHOLDER_PATTERN.matcher(key);
		amount += matcher.results().count();

		// Arguments
		if (args != null)
		{
			for (Object obj : args)
			{
				long amountWithIn = getNumberOfPlaceholders(obj, lang);

				if (amountWithIn == 1)
				{
					amount++;
				}
				else if (amountWithIn > 1)
				{
					if (amount == 0)
					{
						amount = amountWithIn;
					}
					else
					{
						amount = amount * amountWithIn;
					}
				}
			}
		}

		return amount;
	}

	private static long getNumberOfPlaceholders(Object element, Language lang)
	{
		long amount = 0;

		if (element instanceof Text text)
		{
			if (text.getContent() instanceof TranslatableTextContent translatable)
			{
				amount = getNumberOfPlaceholders(translatable.getKey(), translatable.getFallback(), translatable.getArgs(), lang);
			}

			for (Text sibling : text.getSiblings())
			{
				long amountWithIn = getNumberOfPlaceholders(sibling, lang);

				if (amountWithIn == 1)
				{
					amount++;
				}
				else if (amountWithIn > 1)
				{
					if (amount == 0)
					{
						amount = amountWithIn;
					}
					else
					{
						amount = amount * amountWithIn;
					}
				}
			}
		}

		return amount;
	}
}
