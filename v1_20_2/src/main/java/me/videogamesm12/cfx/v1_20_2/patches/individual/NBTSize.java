package me.videogamesm12.cfx.v1_20_2.patches.individual;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.nbt.NbtTagSizeTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NbtTagSizeTracker.class)
@PatchMeta(minVersion = 764, maxVersion = 999)
public class NBTSize
{
    @Inject(method = "add(J)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/nbt/NbtSizeValidationException;<init>(Ljava/lang/String;)V",
                    shift = At.Shift.BEFORE),
            cancellable = true)
    public void disableNbtSizeRestrictions(long bits, CallbackInfo ci)
    {
        if (!CFX.getConfig().getNbtPatches().isSizeLimitEnabled())
        {
            ci.cancel();
        }
    }
}
