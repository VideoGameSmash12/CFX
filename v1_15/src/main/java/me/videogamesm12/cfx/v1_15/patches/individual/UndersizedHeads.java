package me.videogamesm12.cfx.v1_15.patches.individual;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.resource.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerSkinTexture.class)
@PatchMeta(minVersion = 573, maxVersion = 754) // 1.15 to 1.16.5
public class UndersizedHeads
{
    @ModifyVariable(method = "remapTexture", at = @At("HEAD"), argsOnly = true)
    private static NativeImage enforceMinimumImageSize(NativeImage image)
    {
        if (CFX.getConfig().getResourcePatches().getPlayerSkins().isMinimumSkinResolutionEnforcementEnabled()
                && image.getHeight() < 32 || image.getWidth() < 64)
        {
            final ReloadableResourceManager rm = (ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager();
            final ResourceTexture.TextureData data = ResourceTexture.TextureData.load(rm, DefaultSkinHelper.getTexture());

            try
            {
                return data.getImage();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return image;
    }
}
