package me.videogamesm12.cfx.v1_19.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

public class RendererPatches
{
    public static class EntityPatches
    {
        @Mixin(EntityRenderer.class)
        @PatchMeta(minVersion = 759, maxVersion = 9999) // 1.19 to 1.20.1
        public static class ExcessiveEntityNames
        {
            @ModifyVariable(method = "renderLabelIfPresent", at = @At(value = "HEAD"), argsOnly = true)
            private Text limitLabelSize(Text text)
            {
                if (CFX.getConfig().getRenderPatches().getEntity().isNameLengthLimitEnabled()
                        && text.getString().length() > CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit())
                {
                    return Text.literal(text.asTruncatedString(CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit()))
                            .setStyle(text.getStyle());
                }

                return text;
            }
        }
    }
}
