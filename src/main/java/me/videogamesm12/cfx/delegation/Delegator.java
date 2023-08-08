package me.videogamesm12.cfx.delegation;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.util.VersionChecker;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Comparator;
import java.util.Optional;

public class Delegator
{
    private static ITextProvider textProvider = null;

    private static IFeedbackSender feedbackSender = null;

    public static ITextProvider getTextProvider()
    {
        if (textProvider == null)
        {
            final Optional<ITextProvider> optional = FabricLoader.getInstance().getEntrypoints("cfx",
                    ITextProvider.class).stream().filter((provider) ->
            {
                if (!provider.getClass().isAnnotationPresent(Requirements.class))
                {
                    CFX.getLogger().error("Text provider " + provider.getClass().getName() + " is missing the required Requirements annotation");
                    return false;
                }

                return VersionChecker.isCompatibleWithCurrentVersion(provider.getClass().getAnnotation(Requirements.class));
            }).findFirst();

            if (!optional.isPresent())
            {
                throw new IllegalStateException("No text provider found!");
            }

            textProvider = optional.get();
        }

        return textProvider;
    }

    public static IFeedbackSender getFeedbackSender()
    {
        if (feedbackSender == null)
        {
            final Optional<IFeedbackSender> optional = FabricLoader.getInstance().getEntrypoints("cfx-feedback",
                    IFeedbackSender.class).stream().filter((sender) ->
            {
                if (!sender.getClass().isAnnotationPresent(Requirements.class))
                {
                    CFX.getLogger().error("Feedback sender " + sender.getClass().getName() + " is missing the required Requirements annotation");
                    return false;
                }

                return VersionChecker.isCompatibleWithCurrentVersion(sender.getClass().getAnnotation(Requirements.class));
            }).findFirst();

            if (!optional.isPresent())
            {
                CFX.getLogger().error("Feedback sender not found!!!!!!");
                throw new IllegalStateException("No feedback sender found!");
            }

            feedbackSender = optional.get();
        }

        return feedbackSender;
    }

    public static void registerClientCommands()
    {
        FabricLoader.getInstance().getEntrypoints("cfx-client", IClientCommandRegistrar.class).stream().filter((registrar) ->
        {
            if (!registrar.getClass().isAnnotationPresent(Requirements.class))
            {
                CFX.getLogger().error("Client command registrar " + registrar.getClass().getName() + " is missing the required Requirements annotation");
                return false;
            }

            final Requirements requirements = registrar.getClass().getAnnotation(Requirements.class);
            for (String id : requirements.dependencies())
            {
                if (!FabricLoader.getInstance().isModLoaded(id))
                {
                    return false;
                }
            }

            return VersionChecker.isCompatibleWithCurrentVersion(requirements);
        }).sorted(Comparator.comparingInt(one -> one.getClass().getAnnotation(Requirements.class).priority()))
                .findAny().ifPresent(IClientCommandRegistrar::register);
    }

    public static void registerServerCommands()
    {
        final Optional<IServerCommandRegistrar> optional = FabricLoader.getInstance().getEntrypoints("cfx-server",
                IServerCommandRegistrar.class).stream().filter((registrar) ->
        {
            if (!registrar.getClass().isAnnotationPresent(Requirements.class))
            {
                CFX.getLogger().error("Client command registrar " + registrar.getClass().getName() + " is missing the required Requirements annotation");
                return false;
            }

            final Requirements requirements = registrar.getClass().getAnnotation(Requirements.class);
            for (String id : requirements.dependencies())
            {
                if (!FabricLoader.getInstance().isModLoaded(id))
                {
                    return false;
                }
            }

            return VersionChecker.isCompatibleWithCurrentVersion(requirements);
        }).min(Comparator.comparingInt(one -> one.getClass().getAnnotation(Requirements.class).priority()));

        if (optional.isPresent())
        {
            optional.get().register();
        }
        else
        {
            CFX.getLogger().info("No compatible server command API detected! If you want to be able to reload CFX's"
                    + " configuration on the fly, consider installing a compatible API like the Fabric API.");
        }
    }
}
