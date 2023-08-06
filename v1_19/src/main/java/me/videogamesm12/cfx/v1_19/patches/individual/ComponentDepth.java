package me.videogamesm12.cfx.v1_19.patches.individual;

import com.google.gson.*;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.config.CFXConfig;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

@Mixin(Text.Serializer.class)
@PatchMeta(minVersion = 759, maxVersion = 9999) // 1.19 to Latest
public class ComponentDepth
{
    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/text/MutableText;",
            at = @At(value = "INVOKE",
                    target = "Lcom/google/gson/JsonElement;getAsJsonArray()Lcom/google/gson/JsonArray;",
                    shift = At.Shift.AFTER),
            cancellable = true)
    public void patchComponentDepth(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<Text> cir)
    {
        final JsonArray array = jsonElement.getAsJsonArray();

        if (CFX.getConfig().getTextPatches().getGeneral().getArrayDepthMode() != CFXConfig.Text.General.ArrayDepthPatchMode.VANILLA)
        {
            validateComponentDepth(array, 0, CFX.getConfig().getTextPatches().getGeneral().getArrayDepthMaximum(), cir);
        }
    }


    public void validateComponentDepth(JsonElement e, long depth, long max, CallbackInfoReturnable<Text> cir)
    {
        if (depth > max)
        {
            switch (CFX.getConfig().getTextPatches().getGeneral().getArrayDepthMode())
            {
                case OBVIOUS ->
                {
                    cir.setReturnValue(Text.translatable("cfx.replacement.too_complex").formatted(Formatting.RED));
                    return;
                }
                case VANILLA_LIKE -> throw new JsonParseException("Component is too complex, depth >= " + max);
            }
        }

        if (e.isJsonObject())
        {
            final JsonObject object = e.getAsJsonObject();

            if (object.has("extra") && object.get("extra").isJsonArray())
            {
                validateArrayDepth(object.getAsJsonArray("extra"), depth, max, cir);
            }
            else if (object.has("translate") && object.has("with") && object.get("with").isJsonArray())
            {
                validateArrayDepth(object.getAsJsonArray("with"), depth, max, cir);
            }
        }
        else if (e.isJsonArray())
        {
            validateArrayDepth(e.getAsJsonArray(), depth, max, cir);
        }
    }

    public void validateArrayDepth(final JsonArray array, long depth, long max, CallbackInfoReturnable<Text> cir)
    {
        final long depth2 = depth + 1;

        for (JsonElement element : array)
        {
            validateComponentDepth(element, depth2, max, cir);
        }
    }
}