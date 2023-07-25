/*
 * Copyright (c) 2023 Video
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.videogamesm12.cfx.v1_14.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class NetworkPatches
{
    public static class ClientBoundPatches
    {
        @Mixin(ClientPlayNetworkHandler.class)
        @PatchMeta(minVersion = 477, maxVersion = 9999) // 1.14.4 to Latest
        public static class ExcessiveParticles
        {
            /**
             * Fixes an exploit caused by particle packets with extreme counts.
             * @param packet    ParticleS2CPacket
             * @param ci        CallbackInfo
             */
            @Inject(method = "onParticle", at = @At("HEAD"), cancellable = true)
            public void rejectTooManyParticles(ParticleS2CPacket packet, CallbackInfo ci)
            {
                if (CFX.getConfig().getNetworkPatches().getClientBound().isParticleLimitEnabled()
                        && packet.getCount() > CFX.getConfig().getNetworkPatches().getClientBound().getParticleLimit())
                {
                    ci.cancel();
                }
            }
        }
    }
}
