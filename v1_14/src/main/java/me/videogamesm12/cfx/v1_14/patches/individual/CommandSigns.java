package me.videogamesm12.cfx.v1_14.patches.individual;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * <h1>CommandSigns</h1>
 * <p>Patch for signs that execute commands as the server when clicked</p>
 */
@Mixin(SignBlockEntity.class)
@PatchMeta(minVersion = 477, maxVersion = 754)
public class CommandSigns
{
    @Inject(method = "onActivate",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/command/CommandManager;execute(Lnet/minecraft/server/command/ServerCommandSource;Ljava/lang/String;)I"),
            cancellable = true)
    public void handleClickedSignText(PlayerEntity playerEntity, CallbackInfoReturnable<Boolean> cir)
    {
        // Are we supposed to run the command?
        switch (CFX.getConfig().getTextPatches().getClickEvent().getCommandClickServerMode())
        {
            // Don't run the command
            case ONLY_NOTIFY:
            {
                CFX.getLogger().warn("Player " + playerEntity.getEntityName()
                        + " attempted to execute commands in a sign, but was unsuccessful.");
            }
            case DO_NOTHING:
            {
                cir.setReturnValue(true);
                return;
            }

            // Run the command
            case NOTIFY:
            {
                CFX.getLogger().warn("Player " + playerEntity.getEntityName()
                        + " clicked a sign with commands in it.");
            }
            case VANILLA:
            {
                // Do nothing
            }
        }
    }
}