package me.videogamesm12.cfx.v1_16.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.text.HoverEvent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * <h1>BadItemHoverIdentifier</h1>
 * <p>Fixes an exploit caused by invalid identifiers in the "show_item" hover event.</p>
 * <p>This patch is for 1.16 to 1.18.</p>
 */
@Mixin(HoverEvent.ItemStackContent.class)
@PatchMeta(minVersion = 735, maxVersion = 757) // 1.16 - 1.18
public class BadItemHoverIdentifier
{
	@ModifyArg(method = "parse(Lcom/google/gson/JsonElement;)Lnet/minecraft/text/HoverEvent$ItemStackContent;",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/util/Identifier;<init>(Ljava/lang/String;)V"))
	private static String validateIdentifier(String id)
	{
		if (!CFX.getConfig().getTextPatches().getHoverEvent().isIdentifierPatchEnabled())
		{
			return id;
		}

		return Identifier.isValid(id) ? id : "minecraft:air";
	}
}