package me.videogamesm12.cfx.v1_20_2.patches.individual;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.nbt.NbtTagSizeTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * <h1>NBTSize</h1>
 * <p>Patches an exploit caused by a cap in NBT size.</p>
 * <p>This patch is for 1.20.2+.</p>
 */
@Mixin(NbtTagSizeTracker.class)
@PatchMeta(minVersion = 764, maxVersion = 999) // 1.20.2 to Latest
public class NBTSize
{
    @Shadow private long allocatedBytes;

    @Inject(method = "add(J)V", at = @At(value = "HEAD"), cancellable = true)
    public void disableNbtSizeRestrictions(long bytes, CallbackInfo ci)
    {
        if (!CFX.getConfig().getNbtPatches().isSizeLimitEnabled())
        {
            // 1.20.2+ changed the order of operations so that if the size limit is exceeded, it'll throw an exception
            //  as you would expect but also just doesn't allocate the bytes like it used to. So here, we're allocating
            //  it ourselves and then preventing the check from going forward.
            this.allocatedBytes += bytes;
            ci.cancel();
        }
    }
}
