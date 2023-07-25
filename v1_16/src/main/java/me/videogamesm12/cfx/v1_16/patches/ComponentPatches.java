package me.videogamesm12.cfx.v1_16.patches;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

public class ComponentPatches
{
    /**
     * Fixes an exploit caused by an oversight in the translatable component's placeholder system
     */
    @Mixin(TranslatableText.class)
    @PatchMeta(minVersion = 735, maxVersion = 736) // 1.16 to 1.16.1
    public static class BoundlessTranslation
    {
        @Shadow @Final private static StringRenderable NULL_ARGUMENT;

        @Inject(method = "method_29434",
                at = @At("HEAD"),
                cancellable = true)
        public void fixCrashExploit(int i, Language language, CallbackInfoReturnable<StringRenderable> cir)
        {
            if (CFX.getConfig().getTextPatches().getTranslation().isBoundaryPatchEnabled() && i < 0)
            {
                cir.setReturnValue(NULL_ARGUMENT);
            }
        }
    }

    /**
     * Fixes an exploit caused by invalid identifiers in the "show_entity" hover event.
     */
    @Mixin(HoverEvent.EntityContent.class)
    @PatchMeta(minVersion = 735, maxVersion = 757) // 1.16 - 1.18
    public static class BadEntityHoverIdentifier
    {
        @Inject(method = "parse(Lcom/google/gson/JsonElement;)Lnet/minecraft/text/HoverEvent$EntityContent;",
                at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/util/registry/DefaultedRegistry;get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;",
                        shift = At.Shift.BEFORE),
                cancellable = true)
        private static void validateIdentifierJson(JsonElement json, CallbackInfoReturnable<HoverEvent.EntityContent> cir)
        {
            if (CFX.getConfig().getTextPatches().getHoverEvent().isIdentifierPatchEnabled())
            {
                final JsonObject object = json.getAsJsonObject();

                if (JsonHelper.hasString(object, "type")
                        && !Identifier.isValid(JsonHelper.getString(json.getAsJsonObject(), "type")))
                {
                    cir.setReturnValue(null);
                }
            }
        }

        @Inject(method = "parse(Lnet/minecraft/text/Text;)Lnet/minecraft/text/HoverEvent$EntityContent;",
                at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/util/registry/DefaultedRegistry;get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;",
                        shift = At.Shift.BEFORE),
                cancellable = true)
        private static void validateIdentifierText(Text text, CallbackInfoReturnable<HoverEvent.EntityContent> cir)
        {
            if (CFX.getConfig().getTextPatches().getHoverEvent().isIdentifierPatchEnabled())
            {
                try
                {
                    final CompoundTag nbt = StringNbtReader.parse(text.getString());
                    if (!Identifier.isValid(nbt.getString("type")))
                    {
                        cir.setReturnValue(null);
                    }
                }
                catch (CommandSyntaxException ignored)
                {
                    cir.setReturnValue(null);
                }
            }
        }
    }

    /**
     * Fixes an exploit caused by invalid UUIDs in the "show_entity" hover event.
     */
    @Mixin(HoverEvent.EntityContent.class)
    @PatchMeta(minVersion = 735, maxVersion = 757) // 1.16 - 1.18
    public static class BadEntityHoverUUID
    {
        @Inject(method = "parse(Lcom/google/gson/JsonElement;)Lnet/minecraft/text/HoverEvent$EntityContent;",
                at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/util/registry/DefaultedRegistry;get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;",
                        shift = At.Shift.AFTER),
                cancellable = true)
        private static void validateUuid(JsonElement json, CallbackInfoReturnable<HoverEvent.EntityContent> cir)
        {
            try
            {
                UUID.fromString(JsonHelper.getString(json.getAsJsonObject(), "id"));
            }
            catch (Throwable ex)
            {
                cir.setReturnValue(null);
            }
        }

        @Inject(method = "parse(Lnet/minecraft/text/Text;)Lnet/minecraft/text/HoverEvent$EntityContent;",
                at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/util/registry/DefaultedRegistry;get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;",
                        shift = At.Shift.AFTER),
                cancellable = true)
        private static void silentlyPatchInvalidUuid(Text text, CallbackInfoReturnable<HoverEvent.EntityContent> cir)
        {
            if (CFX.getConfig().getTextPatches().getHoverEvent().isUuidPatchEnabled())
            {
                try
                {
                    UUID.fromString(StringNbtReader.parse(text.getString()).getString("id"));
                }
                catch (Exception ex)
                {
                    cir.setReturnValue(null);
                }
            }
        }
    }

    /**
     * Fixes an exploit caused by invalid identifiers in the "show_item" hover event.
     */
    @Mixin(HoverEvent.ItemStackContent.class)
    @PatchMeta(minVersion = 735, maxVersion = 757) // 1.16 - 1.18
    public static class BadItemHoverIdentifier
    {
        @ModifyArg(method = "parse(Lcom/google/gson/JsonElement;)Lnet/minecraft/text/HoverEvent$ItemStackContent;",
                at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/util/Identifier;<init>(Ljava/lang/String;)V"))
        private static String validateIdentifier(String id)
        {
            if (!CFX.getConfig().getTextPatches().getHoverEvent().isIdentifierPatchEnabled())
            {
                return id;
            }

            return Identifier.isValid(id) ? id : "minecraft:air";
        }
    }
}