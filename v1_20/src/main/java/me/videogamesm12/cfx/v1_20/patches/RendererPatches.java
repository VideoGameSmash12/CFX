package me.videogamesm12.cfx.v1_20.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

public class RendererPatches
{
    public static class HudPatches
    {
        @Mixin(value = InGameHud.class)
        @PatchMeta(minVersion = 763, maxVersion = 763) // 1.20 to 1.20.1
        public static class ExcessiveHearts
        {
            @ModifyVariable(method = "renderHealthBar", at = @At("STORE"), ordinal = 0, argsOnly = true)
            public int injectRenderStatusBars(int hearts)
            {
                if (!CFX.getConfig().getRenderPatches().getHud().isHeartCountLimitEnabled())
                {
                    return hearts;
                }

                return Math.min(hearts, CFX.getConfig().getRenderPatches().getHud().getMaxHeartsToRender() * 2);
            }
        }
    }
}
