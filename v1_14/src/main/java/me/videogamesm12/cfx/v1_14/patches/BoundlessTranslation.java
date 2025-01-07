package me.videogamesm12.cfx.v1_14.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * <h1>BoundlessTranslation</h1>
 * <p>Fixes an exploit caused by an oversight in the translatable component's placeholder system.</p>
 * <p>This patch is for versions 1.14 to 1.15.2.</p>
 */
@Mixin(TranslatableText.class)
@PatchMeta(minVersion = 477, maxVersion = 578) // 1.14.4 to 1.15.2
public class BoundlessTranslation
{
	@Inject(method = "getArg", at = @At("HEAD"), cancellable = true)
	public void fixCrashExploit(int index, CallbackInfoReturnable<Text> cir)
	{
		if (CFX.getConfig().getTextPatches().getTranslation().isBoundaryPatchEnabled() && index < 0)
		{
			cir.setReturnValue(new LiteralText("null"));
		}
	}
}
