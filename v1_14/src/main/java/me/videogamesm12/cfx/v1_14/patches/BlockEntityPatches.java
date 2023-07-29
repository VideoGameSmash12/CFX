package me.videogamesm12.cfx.v1_14.patches;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

public class BlockEntityPatches
{
    @Mixin(LockableContainerBlockEntity.class)
    @PatchMeta(minVersion = 477, maxVersion = 999)
    public static class BadBlockEntityName
    {
        @ModifyArg(method = "fromTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text$Serializer;fromJson(Ljava/lang/String;)Lnet/minecraft/text/Text;"))
        public String validateJson(String json)
        {
            if (CFX.getConfig().getBlockEntityPatches().isCustomNameValidationEnabled())
            {
                try
                {
                    Text.Serializer.fromJson(json);
                    return json;
                }
                catch (Throwable ignored)
                {
                    return "";
                }
            }
            else
            {
                return json;
            }
        }
    }
}
