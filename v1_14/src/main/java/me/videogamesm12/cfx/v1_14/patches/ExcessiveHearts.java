package me.videogamesm12.cfx.v1_14.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * <h1>ExcessiveHearts</h1>
 * <p>Mitigates an exploit caused by a lack of a limit for how many hearts can be rendered on screen at once.</p>
 * <p>This patch is for versions 1.14 to 1.15.2.</p>
 */
@Mixin(value = InGameHud.class)
@PatchMeta(minVersion = 477, maxVersion = 578) // 1.14 to 1.15.2
public class ExcessiveHearts
{
	@ModifyVariable(method = "renderStatusBars",
			at = @At("STORE"),
			ordinal = 6)
	public int capAbsorptionHeartCount(int hearts)
	{
		if (CFX.getConfig().getRenderPatches().getHud().isAbsorptionHeartCountLimitEnabled())
		{
			return Math.min(hearts,
					CFX.getConfig().getRenderPatches().getHud().getMaxAbsorptionHeartsToRender() * 2);
		}

		return hearts;
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