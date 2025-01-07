package me.videogamesm12.cfx.v1_16.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * <h1>ExcessiveEntityNames</h1>
 * <p>Mitigates an exploit caused by entities with custom names that are excessively long.</p>
 * <p>This patch is for versions 1.16 to 1.18.2.</p>
 */
@Mixin(EntityRenderer.class)
@PatchMeta(minVersion = 735, maxVersion = 758) // 1.16 to 1.18.2
public class ExcessiveEntityNames
{
	@ModifyVariable(method = "renderLabelIfPresent",
			at = @At(value = "HEAD"),
			argsOnly = true)
	private Text limitLabelSize(Text text)
	{
		if (CFX.getConfig().getRenderPatches().getEntity().isNameLengthLimitEnabled()
				&& text.asString().length() > CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit())
		{
			return new LiteralText(text.asTruncatedString(CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit()))
					.setStyle(text.getStyle());
		}

		return text;
	}
}
