package me.videogamesm12.cfx.v1_20_3.patches;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.config.CFXConfig;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextArgumentType.class)
@PatchMeta(minVersion = 765, maxVersion = 999) // 1.20.3 to Latest
public class ComponentDepthTwo
{
	@Inject(method = "parse(Lcom/mojang/brigadier/StringReader;)Lnet/minecraft/text/Text;",
			at = @At("HEAD"),
			cancellable = true)
	public void patchComponentDepth(StringReader stringReader, CallbackInfoReturnable<Text> cir)
	{
		if (CFX.getConfig().getTextPatches().getGeneral().getArrayDepthMode() != CFXConfig.Text.General.ArrayDepthPatchMode.VANILLA)
		{
			// We need to parse this as JSON data before the game actually uses the codec system to do so because the
			// 	exploit itself happens during the serialization stage, which is really a pain to hook into.
			final JsonReader jsonReader = new JsonReader(new java.io.StringReader(stringReader.getRemaining()));
			jsonReader.setLenient(false);

			final JsonElement element = Streams.parse(jsonReader);
			if (!element.isJsonArray() || !element.isJsonObject())
			{
				return;
			}

			validateComponentDepth(element, 0, CFX.getConfig().getTextPatches().getGeneral().getArrayDepthMaximum(), cir);
		}
	}

	@Unique
	public void validateComponentDepth(JsonElement e, long depth, long max, CallbackInfoReturnable<Text> cir)
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
	public void validateArrayDepth(final JsonArray array, long depth, long max, CallbackInfoReturnable<Text> cir)
	{
		final long depth2 = depth + 1;

		for (JsonElement element : array)
		{
			validateComponentDepth(element, depth2, max, cir);
		}
	}
}
