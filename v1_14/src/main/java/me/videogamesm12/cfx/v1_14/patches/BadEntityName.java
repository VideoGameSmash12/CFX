package me.videogamesm12.cfx.v1_14.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * <h2>BadEntityName</h2>
 * <p>Fixes an exploit caused by a lack of JSON validation for entity custom names.</p>
 * <p>This patch is for versions 1.14 to 1.15.2.</p>
 */
@Mixin(Entity.class)
@PatchMeta(minVersion = 477, maxVersion = 578) // 1.14 to 1.15.2
public class BadEntityName
{
	@ModifyArg(method = "fromTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text$Serializer;fromJson(Ljava/lang/String;)Lnet/minecraft/text/Text;"))
	public String validateJson(String json)
	{
		if (CFX.getConfig().getEntityPatches().isCustomNameValidationEnabled())
		{
			try
			{
				Text.Serializer.fromJson(json);
				return json;
			}
			catch (Throwable ignored)
			{
				return "";
			}
		}
		else
		{
			return json;
		}
	}
}
