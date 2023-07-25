package me.videogamesm12.cfx.v1_16.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ComponentPatches
{
    @Mixin(TranslatableText.class)
    @PatchMeta(minVersion = 735, maxVersion = 736) // 1.16 to 1.16.1
    public static class BoundlessTranslation
    {
        @Shadow @Final private static StringRenderable NULL_ARGUMENT;

        @Inject(method = "method_29434", at = @At("HEAD"), cancellable = true)
        public void fixCrashExploit(int i, Language language, CallbackInfoReturnable<StringRenderable> cir)
        {
            if (CFX.getConfig().getTextPatches().getTranslation().isBoundaryPatchEnabled() && i < 0)
            {
                cir.setReturnValue(NULL_ARGUMENT);
            }
        }
    }
}