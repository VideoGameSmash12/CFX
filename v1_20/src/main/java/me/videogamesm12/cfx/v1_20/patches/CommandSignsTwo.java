package me.videogamesm12.cfx.v1_20.patches;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Mixin(MinecraftClient.class)
@PatchMeta(minVersion = 763, maxVersion = 767)
public abstract class CommandSignsTwo
{
	@Shadow @Nullable public ClientWorld world;

	@Shadow public abstract void setScreen(Screen par1);

	@WrapOperation(method= "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactBlock(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"))
	public ActionResult verify(ClientPlayerInteractionManager instance, ClientPlayerEntity clientPlayerEntity, Hand hand,
							   BlockHitResult blockHitResult, Operation<ActionResult> original)
	{
		if (world != null && world.getBlockEntity(blockHitResult.getBlockPos()) != null
				&& world.getBlockEntity(blockHitResult.getBlockPos()) instanceof SignBlockEntity sign
				&& sign.getText(sign.isPlayerFacingFront(clientPlayerEntity)).hasRunCommandClickEvent(clientPlayerEntity))
		{
			switch (CFX.getConfig().getTextPatches().getClickEvent().getCommandClickClientMode())
			{
				case NOTIFY ->
				{
					final List<String> commands = Arrays.stream(sign.getText(sign.isPlayerFacingFront(clientPlayerEntity)).getMessages(clientPlayerEntity.shouldFilterText())).filter(text -> text.getStyle().getClickEvent() != null
							&& text.getStyle().getClickEvent().getAction() == ClickEvent.Action.RUN_COMMAND).map(text -> text.getStyle().getClickEvent().getValue()).toList();

					// This is the deal as we knew it when we clicked the sign.
					final BlockEntityType<?> type = sign.getType();
					final boolean playerIsFacingFront = sign.isPlayerFacingFront(clientPlayerEntity);

					setScreen(new ConfirmScreen((bool) -> {
						if (bool)
						{
							// -- Integrity checks to prevent switcheroos --

							// We will refuse to execute if:
							//	* The player is no longer facing the sign in question
							// 	* The sign is not loaded or no longer exists
							//	* The commands on the side of the sign have since changed
							if (playerIsFacingFront != sign.isPlayerFacingFront(clientPlayerEntity))
							{
								clientPlayerEntity.sendMessage(Text.translatable("cfx.error.not_executing_as_player_direction_changed").formatted(Formatting.RED), false);
								setScreen(null);
								return;
							}
							else if (world.getBlockEntity(blockHitResult.getBlockPos(), type).isEmpty())
							{
								clientPlayerEntity.sendMessage(Text.translatable("cfx.error.not_executing_as_sign_broke").formatted(Formatting.RED), false);
								setScreen(null);
								return;
							}

							// Zero tolerance for changes
							SignBlockEntity sign2 = (SignBlockEntity) world.getBlockEntity(blockHitResult.getBlockPos());
							List<String> commands2 = Arrays.stream(Objects.requireNonNull(sign2).getText(sign2.isPlayerFacingFront(clientPlayerEntity))
									.getMessages(clientPlayerEntity.shouldFilterText())).filter(text -> text.getStyle().getClickEvent() != null
									&& text.getStyle().getClickEvent().getAction() == ClickEvent.Action.RUN_COMMAND).map(text -> text.getStyle().getClickEvent().getValue()).toList();

							if (!commands.equals(commands2))
							{
								clientPlayerEntity.sendMessage(Text.translatable("cfx.error.not_executing_as_sign_changed").formatted(Formatting.RED), false);
								setScreen(null);
								return;
							}

							original.call(instance, clientPlayerEntity, hand, blockHitResult);
						}
						setScreen(null);
					}, Text.translatable("cfx.prompt.run_command" + (commands.size() > 1 ? "s" : "")),
							Text.literal(String.join("\n", commands))));

					return ActionResult.SUCCESS;
				}
				case DO_NOTHING ->
				{
					return ActionResult.FAIL;
				}
				default ->
				{
					return original.call(instance, clientPlayerEntity, hand, blockHitResult);
				}
			}
		}
		else
		{
			return original.call(instance, clientPlayerEntity, hand, blockHitResult);
		}
	}
}
