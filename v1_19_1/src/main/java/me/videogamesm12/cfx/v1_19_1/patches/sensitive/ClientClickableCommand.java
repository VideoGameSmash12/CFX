package me.videogamesm12.cfx.v1_19_1.patches.sensitive;

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
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendCommand(Ljava/lang/String;)Z",
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
                                boolean success = MinecraftClient.getInstance().player.sendCommand(
                                        clickEvent.getValue().substring(1));

                                // Recreate vanilla mechanics by throwing a similar error into the logs if the command fails
                                if (!success)
                                {
                                    CFX.getLogger().error("Not allowed to run command with signed argument from click event: '" + clickEvent.getValue() + "'");
                                }
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
