package me.videogamesm12.cfx.v1_14.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.nbt.PositionTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class NbtPatches
{
    @Mixin(PositionTracker.class)
    @PatchMeta(minVersion = 498, maxVersion = 9999, conflictingMods = "deviousmod") // 1.14.4 to Latest
    public static class NBTSizeLimit
    {
        @Inject(method = "add", at = @At(value = "INVOKE", target = "Ljava/lang/RuntimeException;<init>(Ljava/lang/String;)V", shift = At.Shift.BEFORE), cancellable = true)
        public void disableNbtSizeRestrictions(long bits, CallbackInfo ci)
        {
            if (!CFX.getConfig().getNbtPatches().isSizeLimitEnabled())
            {
                ci.cancel();
            }
        }
    }
}
