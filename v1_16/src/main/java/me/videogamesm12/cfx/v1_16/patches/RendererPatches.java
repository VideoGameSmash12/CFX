package me.videogamesm12.cfx.v1_16.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

public class RendererPatches
{
    public static class EntityPatches
    {
        @Mixin(EntityRenderer.class)
        @PatchMeta(minVersion = 735, maxVersion = 758) // 1.16 to 1.18.2
        public static class ExcessiveEntityNames
        {
            @ModifyVariable(method = "renderLabelIfPresent",
                    at = @At(value = "HEAD"),
                    argsOnly = true)
            private Text limitLabelSize(Text text)
            {
                if (CFX.getConfig().getRenderPatches().getEntity().isNameLengthLimitEnabled()
                        && text.asString().length() > CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit())
                {
                    return new LiteralText(text.asTruncatedString(CFX.getConfig().getRenderPatches().getEntity().getNameLengthLimit()))
                            .setStyle(text.getStyle());
                }

                return text;
            }
        }
    }

    public static class HudPatches
    {
        @Mixin(InGameHud.class)
        @PatchMeta(minVersion = 735, maxVersion = 762) // 1.16 to 1.19.4
        public static class ExcessiveHearts
        {
            @ModifyVariable(method = "renderStatusBars", at = @At("STORE"), ordinal = 6)
            public int capAbsorptionHeartCount(int absorption)
            {
                if (CFX.getConfig().getRenderPatches().getHud().isAbsorptionHeartCountLimitEnabled())
                {
                    return Math.min(absorption, CFX.getConfig().getRenderPatches().getHud()
                            .getMaxAbsorptionHeartsToRender() * 2);
                }

                return absorption;
            }

            @ModifyVariable(method = "renderStatusBars", at = @At("STORE"), ordinal = 0)
            public float capRegularHeartCount(float maxHearts)
            {
                if (CFX.getConfig().getRenderPatches().getHud().isHeartCountLimitEnabled())
                {
                    return Math.min(maxHearts,
                            CFX.getConfig().getRenderPatches().getHud().getMaxHeartsToRender() * 2);
                }

                return maxHearts;
            }
        }
    }
}
