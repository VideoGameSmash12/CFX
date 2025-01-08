package me.videogamesm12.cfx.v1_16.patches;

import com.google.gson.JsonElement;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * <h1>BadEntityHoverUUID</h1>
 * <p>Fixes an exploit caused by invalid UUIDs in the "show_entity" hover event.</p>
 * <p>This patch is for 1.16 to 1.18.</p>
 */
@Mixin(HoverEvent.EntityContent.class)
@PatchMeta(minVersion = 735, maxVersion = 757) // 1.16 - 1.18
public class BadEntityHoverUUID
{
	@Inject(method = "parse(Lcom/google/gson/JsonElement;)Lnet/minecraft/text/HoverEvent$EntityContent;",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/util/registry/DefaultedRegistry;get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;",
					shift = At.Shift.AFTER),
			cancellable = true)
	private static void validateUuid(JsonElement json, CallbackInfoReturnable<HoverEvent.EntityContent> cir)
	{
		try
		{
			UUID.fromString(JsonHelper.getString(json.getAsJsonObject(), "id"));
		}
		catch (Throwable ex)
		{
			cir.setReturnValue(null);
		}
	}

	@Inject(method = "parse(Lnet/minecraft/text/Text;)Lnet/minecraft/text/HoverEvent$EntityContent;",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/util/registry/DefaultedRegistry;get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;",
					shift = At.Shift.AFTER),
			cancellable = true)
	private static void silentlyPatchInvalidUuid(Text text, CallbackInfoReturnable<HoverEvent.EntityContent> cir)
	{
		if (CFX.getConfig().getTextPatches().getHoverEvent().isUuidPatchEnabled())
		{
			try
			{
				UUID.fromString(StringNbtReader.parse(text.getString()).getString("id"));
			}
			catch (Exception ex)
			{
				cir.setReturnValue(null);
			}
		}
	}
}