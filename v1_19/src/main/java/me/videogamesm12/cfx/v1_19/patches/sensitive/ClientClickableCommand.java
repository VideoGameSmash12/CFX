package me.videogamesm12.cfx.v1_19.patches.sensitive;

import me.videogamesm12.cfx.CFX;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ClientClickableCommand
{
    @Inject(method = "handleTextClick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendCommand(Ljava/lang/String;)V",
                    shift = At.Shift.BEFORE),
            cancellable = true)
    public void handleClickableCommand(Style style, CallbackInfoReturnable<Boolean> cir)
    {
        switch (CFX.getConfig().getTextPatches().getClickEvent().getCommandClickClientMode())
        {
            case NOTIFY ->
            {
                if (style == null || style.getClickEvent() == null)
                {
                    return;
                }

                final ClickEvent clickEvent = style.getClickEvent();

                cir.setReturnValue(true);

                MinecraftClient.getInstance().setScreen(new ConfirmScreen(
                        (bool) ->
                        {
                            if (bool)
                            {
                                MinecraftClient.getInstance().player.sendCommand(clickEvent.getValue().substring(1));
                            }

                            MinecraftClient.getInstance().setScreen(null);
                        },
                        Text.translatable("cfx.prompt.run_command"),
                        Text.literal(clickEvent.getValue())
                ));

            }
            case DO_NOTHING -> cir.setReturnValue(true);
            default ->
            {
                // Do nothing
            }
        }
    }

    @Inject(method = "handleTextClick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendChatMessage(Ljava/lang/String;)V",
                    shift = At.Shift.BEFORE),
            cancellable = true)
    public void handleClickableChat(Style style, CallbackInfoReturnable<Boolean> cir)
    {
        switch (CFX.getConfig().getTextPatches().getClickEvent().getCommandClickClientMode())
        {
            case NOTIFY ->
            {
                if (style == null || style.getClickEvent() == null)
                {
                    return;
                }

                final ClickEvent clickEvent = style.getClickEvent();

                cir.setReturnValue(true);

                MinecraftClient.getInstance().setScreen(new ConfirmScreen(
                        (bool) ->
                        {
                            if (bool)
                            {
                                MinecraftClient.getInstance().player.sendChatMessage(clickEvent.getValue());
                            }

                            MinecraftClient.getInstance().setScreen(null);
                        },
                        Text.translatable("cfx.prompt.run_command"),
                        Text.literal(clickEvent.getValue())
                ));

            }
            case DO_NOTHING -> cir.setReturnValue(true);
            default ->
            {
                // Do nothing
            }
        }
    }
}
