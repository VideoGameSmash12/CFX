package me.videogamesm12.cfx.delegation;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public interface IFeedbackSender
{
    void sendFeedback(ServerCommandSource source, Text message, boolean broadcastToOps);
}