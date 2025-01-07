package me.videogamesm12.cfx.v1_16.patches;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * <h1>BadEntityHoverIdentifier</h1>
 * <p>Fixes an exploit caused by invalid identifiers in the "show_entity" hover event.</p>
 * <p>This patch is for 1.16 to 1.18.</p>
 */
@Mixin(HoverEvent.EntityContent.class)
@PatchMeta(minVersion = 735, maxVersion = 757) // 1.16 - 1.18
public class BadEntityHoverIdentifier
{
	@Inject(method = "parse(Lcom/google/gson/JsonElement;)Lnet/minecraft/text/HoverEvent$EntityContent;",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/util/registry/DefaultedRegistry;get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;",
					shift = At.Shift.BEFORE),
			cancellable = true)
	private static void validateIdentifierJson(JsonElement json, CallbackInfoReturnable<HoverEvent.EntityContent> cir)
	{
		if (CFX.getConfig().getTextPatches().getHoverEvent().isIdentifierPatchEnabled())
		{
			final JsonObject object = json.getAsJsonObject();

			if (JsonHelper.hasString(object, "type")
					&& !Identifier.isValid(JsonHelper.getString(json.getAsJsonObject(), "type")))
			{
				cir.setReturnValue(null);
			}
		}
	}

	@Inject(method = "parse(Lnet/minecraft/text/Text;)Lnet/minecraft/text/HoverEvent$EntityContent;",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/util/registry/DefaultedRegistry;get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;",
					shift = At.Shift.BEFORE),
			cancellable = true)
	private static void validateIdentifierText(Text text, CallbackInfoReturnable<HoverEvent.EntityContent> cir)
	{
		if (CFX.getConfig().getTextPatches().getHoverEvent().isIdentifierPatchEnabled())
		{
			try
			{
				final CompoundTag nbt = StringNbtReader.parse(text.getString());
				if (!Identifier.isValid(nbt.getString("type")))
				{
					cir.setReturnValue(null);
				}
			}
			catch (CommandSyntaxException ignored)
			{
				cir.setReturnValue(null);
			}
		}
	}
}