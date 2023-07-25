package me.videogamesm12.cfx.v1_16_2.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class BlockPatches
{
    /**
     * Fixes an exploit caused by an oversight in how the game handles nether portals and dispensers
     */
    @Mixin(AbstractFireBlock.class)
    @PatchMeta(minVersion = 751, maxVersion = 754)
    public static class UpsideDownPortals
    {
        @Inject(method = "method_30033",
                at = @At("HEAD"),
                cancellable = true)
        private static void injectMethod30033(World world, BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir)
        {
            if (CFX.getConfig().getBlockPatches().isUpsideDownPortalPatchEnabled()
                    && direction.getAxis().getType() == Direction.Type.VERTICAL)
            {
                cir.setReturnValue(false);
            }
        }
    }
}
