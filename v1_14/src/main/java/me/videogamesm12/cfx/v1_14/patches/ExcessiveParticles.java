package me.videogamesm12.cfx.v1_14.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * <h1>ExcessiveParticles</h1>
 * <p>Fixes an exploit caused by a lack of a limit in how many particles can be in a single packet.</p>
 * <p>This patch is for 1.14+.</p>
 */
@Mixin(ClientPlayNetworkHandler.class)
@PatchMeta(minVersion = 477, maxVersion = 9999) // 1.14 to Latest
public class ExcessiveParticles
{
	@Inject(method = "onParticle",
			at = @At("HEAD"),
			cancellable = true)
	public void rejectTooManyParticles(ParticleS2CPacket packet, CallbackInfo ci)
	{
		if (CFX.getConfig().getNetworkPatches().getClientBound().isParticleLimitEnabled()
				&& packet.getCount() > CFX.getConfig().getNetworkPatches().getClientBound().getParticleLimit())
		{
			ci.cancel();
		}
	}
}