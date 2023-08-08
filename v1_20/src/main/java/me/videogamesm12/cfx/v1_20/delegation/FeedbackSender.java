package me.videogamesm12.cfx.v1_20.delegation;

import me.videogamesm12.cfx.delegation.IFeedbackSender;
import me.videogamesm12.cfx.delegation.Requirements;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

@Requirements(min = 763, max = 999)
public class FeedbackSender implements IFeedbackSender
{
    @Override
    public void sendFeedback(ServerCommandSource source, Text message, boolean broadcastToOps)
    {
        source.sendFeedback(() -> message, broadcastToOps);
    }
}
