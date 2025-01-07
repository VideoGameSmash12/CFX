package me.videogamesm12.cfx.v1_21_2.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
@PatchMeta(minVersion = 768, maxVersion = 999) // 1.21.2 to Latest
public class ExcessiveEntityNames
{
	@ModifyVariable(method = "renderLabelIfPresent", at = @At(value = "HEAD"), argsOnly = true)
	private Text limitLabelSize(Text text)
	{
		if (CFX.getConfig().getRenderPatches().getEntity().isNameLengthLimitEnabled()
				&& text.getString().length() > CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit())
		{
			return Text.literal(text.asTruncatedString(CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit()))
					.setStyle(text.getStyle());
		}

		return text;
	}
}