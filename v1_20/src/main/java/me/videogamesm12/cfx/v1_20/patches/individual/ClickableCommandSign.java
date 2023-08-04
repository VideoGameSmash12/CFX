package me.videogamesm12.cfx.v1_20.patches.individual;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * <h1>ClickableCommandSign</h1>
 * <p>Patch for signs that execute commands as the server when clicked</p>
 */
@Mixin(SignBlockEntity.class)
@PatchMeta(minVersion = 763, maxVersion = 999)
public class ClickableCommandSign
{
    @Inject(method = "runCommandClickEvent",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/command/CommandManager;executeWithPrefix(Lnet/minecraft/server/command/ServerCommandSource;Ljava/lang/String;)I"),
            cancellable = true)
    public void handleClickedSignText(PlayerEntity player, World world, BlockPos pos, boolean front, CallbackInfoReturnable<Boolean> cir)
    {
        // Are we supposed to run the command?
        switch (CFX.getConfig().getTextPatches().getClickEvent().getCommandClickServerMode())
        {
            // Don't run the command
            case ONLY_NOTIFY:
            {
                CFX.getLogger().warn("Player " + player.getEntityName() + " attempted to execute commands in a sign at ("
                        + pos.toShortString() + "), but was unsuccessful.");
            }
            case DO_NOTHING:
            {
                cir.setReturnValue(true);
                return;
            }

            // Run the command
            case NOTIFY:
            {
                CFX.getLogger().warn("Player " + player.getEntityName() + " clicked a sign with commands in it at ("
                        + pos.toShortString() + ").");
            }
            case VANILLA:
            {
                // Do nothing
            }
        }
    }
}