package me.videogamesm12.cfx.v1_15.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

public class RendererPatches
{
    public static class EntityPatches
    {
        @Mixin(EntityRenderer.class)
        @PatchMeta(minVersion = 573, maxVersion = 578) // 1.15 to 1.15.2
        public static class ExcessivelyLongText
        {
            @ModifyVariable(method = "renderLabelIfPresent", at = @At(value = "HEAD"), argsOnly = true)
            private String limitLabelSize(String text)
            {
                if (CFX.getConfig().getRenderPatches().getEntity().isNameLengthLimitEnabled()
                        && text.length() > CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit())
                {
                    return text.substring(0, CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit());
                }

                return text;
            }
        }
    }
}
