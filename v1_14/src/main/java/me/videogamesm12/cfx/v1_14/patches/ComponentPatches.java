package me.videogamesm12.cfx.v1_14.patches;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

public class ComponentPatches
{
    /**
     * Fixes an exploit caused by an oversight in the translatable component's placeholder system
     */
    @Mixin(TranslatableText.class)
    @PatchMeta(minVersion = 477, maxVersion = 578) // 1.14.4 to 1.15.2
    public static class BoundlessTranslation
    {
        @Inject(method = "getArg", at = @At("HEAD"), cancellable = true)
        public void fixCrashExploit(int index, CallbackInfoReturnable<Text> cir)
        {
            if (CFX.getConfig().getTextPatches().getTranslation().isBoundaryPatchEnabled() && index < 0)
            {
                cir.setReturnValue(new LiteralText("null"));
            }
        }
    }

    @Mixin(Text.Serializer.class)
    @PatchMeta(minVersion = 477, maxVersion = 999) // 1.14 to Latest
    public static class ExtraEmptyArray
    {
        @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/text/Text;",
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
}
