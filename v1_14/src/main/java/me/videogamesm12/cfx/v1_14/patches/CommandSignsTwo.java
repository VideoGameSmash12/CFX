package me.videogamesm12.cfx.v1_14.patches;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.management.PatchMeta;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mixin(MinecraftClient.class)
@PatchMeta(minVersion = 477, maxVersion = 578)
public abstract class CommandSignsTwo
{
	@Shadow public abstract void openScreen(Screen par1);

	@WrapOperation(method= "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactBlock(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"))
	public ActionResult verify(ClientPlayerInteractionManager instance, ClientPlayerEntity player, ClientWorld world,
							   Hand hand, BlockHitResult hitResult, Operation<ActionResult> original)
	{
		if (world != null && world.getBlockEntity(hitResult.getBlockPos()) != null
				&& world.getBlockEntity(hitResult.getBlockPos()) instanceof SignBlockEntity)
		{
			SignBlockEntity sign = Objects.requireNonNull((SignBlockEntity) world.getBlockEntity(hitResult.getBlockPos()));

			switch (CFX.getConfig().getTextPatches().getClickEvent().getCommandClickClientMode())
			{
				case NOTIFY:
				{
					final List<String> commands = cfx$getComponentsFromSign(sign).stream().filter(text -> text.getStyle().getClickEvent() != null
							&& text.getStyle().getClickEvent().getAction() == ClickEvent.Action.RUN_COMMAND).map(text -> text.getStyle().getClickEvent().getValue()).collect(Collectors.toList());

					// No commands to execute, just move on
					if (commands.isEmpty())
					{
						return original.call(instance, player, world, hand, hitResult);
					}

					openScreen(new ConfirmScreen((bool) -> {
						if (bool)
						{
							// -- Integrity checks to prevent switcheroos --

							// We will refuse to execute if:
							// 	* The sign is not loaded or no longer exists
							//	* The commands on the side of the sign have since changed
							if (world.getBlockEntity(hitResult.getBlockPos()) == null)
							{
								player.sendMessage(new TranslatableText("cfx.error.not_executing_as_sign_broke").formatted(Formatting.RED));
								openScreen(null);
								return;
							}

							// Zero tolerance for changes
							SignBlockEntity sign2 = (SignBlockEntity) world.getBlockEntity(hitResult.getBlockPos());
							List<String> commands2 = cfx$getComponentsFromSign(sign2).stream().filter(text -> text.getStyle().getClickEvent() != null
									&& text.getStyle().getClickEvent().getAction() == ClickEvent.Action.RUN_COMMAND).map(text -> text.getStyle().getClickEvent().getValue()).collect(Collectors.toList());

							if (!commands.equals(commands2))
							{
								player.sendMessage(new TranslatableText("cfx.error.not_executing_as_sign_changed").formatted(Formatting.RED));
								openScreen(null);
								return;
							}

							original.call(instance, player, world, hand, hitResult);
						}
						openScreen(null);
					}, new TranslatableText("cfx.prompt.run_command" + (commands.size() > 1 ? "s" : "")),
							new LiteralText(String.join("\n", commands))));
				}
				case DO_NOTHING:
				{
					return ActionResult.FAIL;
				}
				default:
				{
					return original.call(instance, player, world, hand, hitResult);
				}
			}
		}
		else
		{
			return original.call(instance, player, world, hand, hitResult);
		}
	}

	@Unique
	private List<Text> cfx$getComponentsFromSign(SignBlockEntity sign)
	{
		final List<Text> lines = new ArrayList<>();
		for (int i = 0; i < 4; i++)
		{
			lines.add(sign.getTextOnRow(i));
		}

		return lines;
	}
}
