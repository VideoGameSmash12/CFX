package me.videogamesm12.cfx.v1_20.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * <h1>BadSherdIdentifiers</h1>
 * <p>Fixes an exploit caused by an oversight in how the game processes Decorated Pots.</p>
 * <p>This patch is for versions 1.20 to 1.20.1.</p>
 */
@Mixin(DecoratedPotBlockEntity.Sherds.class)
@PatchMeta(minVersion = 763, maxVersion = 763)
public class BadSherdIdentifiers
{
    @Inject(method = "getSherd", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/nbt/NbtList;get(I)Lnet/minecraft/nbt/NbtElement;",
            shift = At.Shift.AFTER),
            cancellable = true)
    private static void rejectInvalidIdentifiers(NbtList list, int index, CallbackInfoReturnable<Item> cir)
    {
        if (CFX.getConfig().getBlockEntityPatches().isPotSherdValidationEnabled() && !Identifier.isValid(list.get(index).asString()))
        {
            cir.setReturnValue(Items.BRICK);
        }
    }
}