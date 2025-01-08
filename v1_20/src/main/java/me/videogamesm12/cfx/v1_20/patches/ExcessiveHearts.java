package me.videogamesm12.cfx.v1_20.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(InGameHud.class)
@PatchMeta(minVersion = 763, maxVersion = 999) // 1.20 to Latest
public class ExcessiveHearts
{
	@ModifyVariable(method = "renderHealthBar", at = @At("HEAD"), ordinal = 6, argsOnly = true)
	public int capAbsorptionHeartCount(int absorption)
	{
		if (CFX.getConfig().getRenderPatches().getHud().isAbsorptionHeartCountLimitEnabled())
		{
			// Half hearts are counted so we need to multiply it by 2 to get the full heart equivalents
			return Math.min(absorption, CFX.getConfig().getRenderPatches().getHud()
					.getMaxAbsorptionHeartsToRender() * 2);
		}

		return absorption;
	}

	@ModifyVariable(method = "renderHealthBar", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	public float capRegularHeartCount(float maxHearts)
	{
		if (CFX.getConfig().getRenderPatches().getHud().isHeartCountLimitEnabled())
		{
			// Half hearts are counted so we need to multiply it by 2 to get the full heart equivalents
			return Math.min(maxHearts,
					CFX.getConfig().getRenderPatches().getHud().getMaxHeartsToRender() * 2);
		}

		return maxHearts;
	}
}
