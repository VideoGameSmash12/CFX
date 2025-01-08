package me.videogamesm12.cfx.v1_16.patches;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

/**
 * <h1>ExtraEmptyArray</h1>
 * <p>Fixes an exploit caused by an oversight in the translatable component's placeholder system.</p>
 * <p>This patch is for 1.16 to 1.20.2.</p>
 */
@Mixin(Text.Serializer.class)
@PatchMeta(minVersion = 735, maxVersion = 764) // 1.16 to 1.20.2
public class ExtraEmptyArray
{
	@Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/text/MutableText;",
			at = @At(value = "INVOKE",
					target = "Lcom/google/gson/JsonElement;getAsJsonArray()Lcom/google/gson/JsonArray;",
					shift = At.Shift.AFTER))
	public void patchEmptyArray(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<Text> cir)
	{
		final JsonArray array = jsonElement.getAsJsonArray();

		if (CFX.getConfig().getTextPatches().getExtra().isEmptyArrayPatchEnabled() && array.size() <= 0)
		{
			throw new JsonParseException("Unexpected empty array of components");
		}
	}
}