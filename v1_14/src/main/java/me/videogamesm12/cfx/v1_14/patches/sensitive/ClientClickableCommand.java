package me.videogamesm12.cfx.v1_14.patches.sensitive;

import me.videogamesm12.cfx.CFX;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ClientClickableCommand
{
    @Inject(method = "handleComponentClicked",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;sendMessage(Ljava/lang/String;Z)V",
                    shift = At.Shift.BEFORE),
            cancellable = true)
    public void handleClickableCommand(Text text, CallbackInfoReturnable<Boolean> cir)
    {
        switch (CFX.getConfig().getTextPatches().getClickEvent().getCommandClickClientMode())
        {
            case NOTIFY:
            {
                final Style style = text.getStyle();

                if (style == null || style.getClickEvent() == null)
                {
                    return;
                }

                final ClickEvent clickEvent = style.getClickEvent();

                cir.setReturnValue(true);

                MinecraftClient.getInstance().openScreen(new ConfirmScreen(
                        (bool) ->
                        {
                            if (bool)
                            {
                                MinecraftClient.getInstance().player.sendChatMessage(clickEvent.getValue());
                            }

                            MinecraftClient.getInstance().openScreen(null);
                        },
                        new TranslatableText("cfx.prompt.run_command"),
                        new LiteralText(clickEvent.getValue())
                ));

                break;
            }
            case DO_NOTHING:
            {
                cir.setReturnValue(true);
                return;
            }
            default:
            case VANILLA:
            {
            }
        }
    }
}
