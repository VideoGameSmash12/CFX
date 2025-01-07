package me.videogamesm12.cfx.v1_16.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * <h1>ExcessiveHearts</h1>
 * <p>Mitigates an exploit caused by a lack of a limit for how many hearts can be rendered on screen at once.</p>
 * <p>This patch is for versions 1.16 to 1.19.4.</p>
 */
@Mixin(InGameHud.class)
@PatchMeta(minVersion = 735, maxVersion = 762) // 1.16 to 1.19.4
public class ExcessiveHearts
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
