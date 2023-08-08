package me.videogamesm12.cfx.v1_14.delegation;

import me.videogamesm12.cfx.delegation.IFeedbackSender;
import me.videogamesm12.cfx.delegation.Requirements;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

@Requirements(min = 477, max = 762)
public class FeedbackSender implements IFeedbackSender
{
    @Override
    public void sendFeedback(ServerCommandSource source, Text message, boolean broadcastToOps)
    {
        source.sendFeedback(message, broadcastToOps);
    }
}
