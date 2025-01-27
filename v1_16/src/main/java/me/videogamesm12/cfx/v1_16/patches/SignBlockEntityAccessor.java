package me.videogamesm12.cfx.v1_16.patches;

import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SignBlockEntity.class)
@PatchMeta(minVersion = 735, maxVersion = 758)
public interface SignBlockEntityAccessor
{
	@Accessor
	Text[] getText();
}
