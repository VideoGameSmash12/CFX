package me.videogamesm12.cfx.delegation;

import me.videogamesm12.cfx.CFX;
import me.videogamesm12.cfx.util.VersionChecker;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Optional;

public class Delegator
{
    private static ITextProvider textProvider = null;

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
}
