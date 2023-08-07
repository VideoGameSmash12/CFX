package me.videogamesm12.cfx.v1_14.patches.individual;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.SkinRemappingImageFilter;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.resource.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SkinRemappingImageFilter.class)
@PatchMeta(minVersion = 477, maxVersion = 498) // 1.14 to 1.14.4
public class UndersizedHeads
{
    @ModifyVariable(method = "filterImage", at = @At("HEAD"), argsOnly = true)
    private NativeImage enforceMinimumImageSize(NativeImage image)
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
