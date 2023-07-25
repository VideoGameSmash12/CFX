package me.videogamesm12.cfx.v1_19_1.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ComponentPatches
{
    @Mixin(TranslatableTextContent.class)
    @PatchMeta(minVersion = 760, maxVersion = 760) // 1.19.1 to 1.19.2
    public static class BoundlessTranslation
    {
        @Shadow @Final private static StringVisitable NULL_ARGUMENT;

        @Inject(method = "getArg", at = @At("HEAD"), cancellable = true)
        public void fixCrashExploit(int index, CallbackInfoReturnable<StringVisitable> cir)
        {
            if (CFX.getConfig().getTextPatches().getTranslation().isBoundaryPatchEnabled() && index < 0)
            {
                cir.setReturnValue(NULL_ARGUMENT);
            }
        }
    }
}