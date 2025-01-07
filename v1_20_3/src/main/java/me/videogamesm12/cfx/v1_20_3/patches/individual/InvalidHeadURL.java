package me.videogamesm12.cfx.v1_20_3.patches.individual;

import com.mojang.authlib.yggdrasil.TextureUrlChecker;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.URI;
import java.net.URISyntaxException;

@Mixin(TextureUrlChecker.class)
@PatchMeta(minVersion = 765, maxVersion = 765) // 1.20.3 to 1.20.4
public class InvalidHeadURL
{
	@Inject(method = "isAllowedTextureDomain", at = @At("HEAD"), cancellable = true, remap = false)
	private static void validateUrl(String url, CallbackInfoReturnable<Boolean> cir)
	{
		if (CFX.getConfig().getLibraryPatches().getAuthLib().isTextureUrlValidationEnabled())
		{
			try
			{
				new URI(url).normalize();
			}
			catch (URISyntaxException ex)
			{
				cir.setReturnValue(false);
			}
		}
	}
}
