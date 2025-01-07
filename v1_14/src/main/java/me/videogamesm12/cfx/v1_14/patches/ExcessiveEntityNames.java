package me.videogamesm12.cfx.v1_14.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * <h1>ExcessiveEntityNames</h1>
 * <p>Mitigates an exploit caused by entities with custom names that are excessively long.</p>
 * <p>This patch is for versions 1.14 to 1.14.4.</p>
 */
@Mixin(EntityRenderer.class)
@PatchMeta(minVersion = 477, maxVersion = 498) // 1.14.4 only
public class ExcessiveEntityNames
{
	@ModifyVariable(method = "renderLabel(Lnet/minecraft/entity/Entity;Ljava/lang/String;DDDI)V",
			at = @At(value = "HEAD"),
			argsOnly = true)
	private String limitLabelSize(String text)
	{
		if (CFX.getConfig().getRenderPatches().getEntity().isNameLengthLimitEnabled()
				&& text.length() > CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit())
		{
			return text.substring(0, CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit());
		}

		return text;
	}
}
