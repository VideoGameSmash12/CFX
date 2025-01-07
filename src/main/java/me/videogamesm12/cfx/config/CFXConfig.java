/*
 * Copyright (c) 2023 Video
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.videogamesm12.cfx.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import lombok.Getter;
import lombok.Setter;
import me.videogamesm12.cfx.CFX;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CFXConfig
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "cfx.json");

    private static final int latestVersion = 10;

    public static CFXConfig load()
    {
        CFXConfig instance;

        if (file.exists())
        {
            try
            {
                instance = gson.fromJson(Files.newBufferedReader(file.toPath()), CFXConfig.class);

                // Updates the configuration if the current version is newer than the configuration's version
                if (instance.getVersion() < latestVersion)
                {
                    CFX.getLogger().info("Updating configuration to the newest version");
                    instance.setVersion(latestVersion);
                    instance.save();
                }

                return instance;
            }
            catch (IOException ex)
            {
                CFX.getLogger().error("Failed to load configuration", ex);
            }
            catch (JsonParseException ex)
            {
                CFX.getLogger().error("Failed to parse configuration", ex);
            }
        }

        instance = new CFXConfig();
        instance.save();

        return instance;
    }

    @Setter
    private int version = latestVersion;

    private BlockEntities blockEntityPatches = new BlockEntities();

    private Blocks blockPatches = new Blocks();

    private Entities entityPatches = new Entities();

    private Libraries libraryPatches = new Libraries();

    private NBT nbtPatches = new NBT();

    private Network networkPatches = new Network();

    private Render renderPatches = new Render();

    private Resources resourcePatches = new Resources();

    private Text textPatches = new Text();

    private Overrides overrides = new Overrides();

    public void save()
    {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath()))
        {
            writer.write(gson.toJson(this));
        }
        catch (Throwable ex)
        {
            CFX.getLogger().error("Failed to save configuration");
        }
    }

    @Getter
    @Setter
    public static class Blocks
    {
        /**
         * Whether to prevent portals from being ignited by a dispenser that is facing upwards or downwards. This does
         *  not have any effect in versions older than 1.16.2 or newer than 1.16.4 as Mojang patched the exploit in
         *  1.16.5.
         */
        private boolean upsideDownPortalPatchEnabled = true;
    }

    @Getter
    @Setter
    public static class BlockEntities
    {
        /**
         * Whether to validate a Decoated Pot's sherds. This does not have any effect on 1.20.2+ as those versions
         *  patched the exploit.
         */
        private boolean potSherdValidationEnabled = true;

        /**
         * Whether to validate a block entity's custom name JSON. This does not have any effect on 1.17+ as those
         *  versions patched the exploit.
         */
        private boolean customNameValidationEnabled = true;
    }

    @Getter
    @Setter
    public static class Entities
    {
        /**
         * Whether to validate an entity's custom name JSON. This does not have any effect on versions 1.16+ as those
         *  versions patched the exploit.
         */
        private boolean customNameValidationEnabled = true;
    }

    @Getter
    public static class Libraries
    {
        private AuthLib authLib = new AuthLib();

        @Getter
        @Setter
        public static class AuthLib
        {
            /**
             * Validate URLs of player skins before attempting to check whether they are allowed to be used. This does
             *  not have any effect on versions <= 1.20.2 as those versions are immune to the exploit.
             */
            private boolean textureUrlValidationEnabled = true;
        }
    }

    @Getter
    @Setter
    public static class NBT
    {
        /**
         * Whether to enforce the built-in NBT size cap that often kicks players who see someone holding an item that is
         *  too large in NBT data.
         */
        private boolean sizeLimitEnabled = false;
    }

    @Getter
    public static class Network
    {
        private ClientBound clientBound = new ClientBound();

        @Getter
        @Setter
        public static class ClientBound
        {
            /**
             * Whether to stop particles with a higher count than the limit defined below from being processed by the
             *  client. This is often used to counteract a known client freeze exploit.
             */
            private boolean particleLimitEnabled = true;

            /**
             * The highest a particle packet's count can go before the mod cuts it off and refuses to process it.
             */
            private int particleLimit = 500;
        }
    }

    public static class Overrides
    {
        /**
         * Allow patches that affect very sensitive parts of the game. Sensitive patches are initialized in a different
         *  way from traditional patches in order to reduce the amount of mod conflicts that may occur due to how the
         *  mod actually pulls metadata for the patches.
         */
        private boolean sensitivePatchesAllowed = true;

        /**
         * Class names for patches that shouldn't be applied even if they are compatible with the current version of
         *  Minecraft that is running. This is helpful for cases where it's causing conflicts with other mods but the
         *  maintainer of the mod isn't aware of the issue yet.
         */
        @Getter
        private List<String> disabledPatches = new ArrayList<>();

        /**
         * Allow the mod to set up the server-side command "/cfxserver" if the appropriate dependencies are in place. If
         *  no dependencies are detected, then the mod will act like this setting was set to false.
         */
        @Getter
        private boolean serverCommandEnabled = true;

        /**
         * Allow the mod to set up the client-side command "/cfxclient" if the appropriate dependencies are in place. If
         *  no dependencies are detected, then the mod will act like this setting was set to false.
         */
        @Getter
        private boolean clientCommandEnabled = true;

        /**
         * Returns whether to allow sensitive patches to be applied
         * @return The value of sensitivePatchesAllowed
         */
        public boolean areSensitivePatchesAllowed()
        {
            return sensitivePatchesAllowed;
        }
    }

    @Getter
    public static class Resources
    {
        private PlayerSkins playerSkins = new PlayerSkins();

        @Getter
        @Setter
        public static class PlayerSkins
        {
            /**
             * Prevent player skins smaller than the minimum resolution of 64x32 from being processed. This does not
             *  have any effect on versions 1.17+ as those versions patched the exploit.
             */
            private boolean minimumSkinResolutionEnforcementEnabled = true;
        }
    }

    @Getter
    public static class Render
    {
        private Entities entity = new Entities();

        private Hud hud = new Hud();

        @Getter
        @Setter
        public static class Hud
        {
            /**
             * Whether to cap how many regular hearts can be rendered.
             */
            private boolean heartCountLimitEnabled = true;

            /**
             * Whether to cap how many absorption hearts can be rendered.
             */
            private boolean absorptionHeartCountLimitEnabled = true;

            /**
             * The limit for how many absorption hearts can be rendered.
             */
            private int maxAbsorptionHeartsToRender = 48;

            /**
             * The limit for how many regular hearts can be rendered.
             */
            private int maxHeartsToRender = 48;
        }

        @Getter
        @Setter
        public static class Entities
        {
            /**
             * Whether to enforce a limit defined in "nameLengthLimit" on how large entity text can be before the mod
             *  steps in and caps it.
             */
            private boolean nameLengthLimitEnabled = true;

            /**
             * The maximum character length an entity's name can be before being cut off.
             */
            private int nameLengthLimit = 255;
        }
    }

    @Getter
    public static class Text
    {
        private General general = new General();

        //--

        private ClickEventComponent clickEvent = new ClickEventComponent();

        private ExtraComponent extra = new ExtraComponent();

        private HoverEventComponent hoverEvent = new HoverEventComponent();

        private TranslatableComponent translation = new TranslatableComponent();

        @Getter
        @Setter
        public static class ClickEventComponent
        {
            /**
             * How the mod handles clicks of text components with "run_command" click events. It has multiple modes:
             * - DO_NOTHING: Clicking the text does absolutely nothing.
             * - NOTIFY:     Clicking the text shows a prompt asking the user if they're sure they want to execute the
             *                  command.
             * - VANILLA:    Clicking the text executes the command.
             */
            private CommandClickClientMode commandClickClientMode = CommandClickClientMode.NOTIFY;

            /**
             * How the mod handles clicks of text components with "run_command" click events in signs. It has multiple
             *  modes:
             * - DO_NOTHING:    Clicking the sign will simply do nothing
             * - NOTIFY:        Clicking the text will cause a warning message to be sent to the logs, but the command(s)
             *                      will still execute as per normally.
             * - ONLY_NOTIFY:   Clicking the text will cause a warning message to be sent to the logs, but the command(s)
             *                      will not execute.
             * - VANILLA:       Clicking the text will execute the command(s).
             */
            private CommandClickServerMode commandClickServerMode = CommandClickServerMode.NOTIFY;

            public enum CommandClickClientMode
            {
                DO_NOTHING,     // Clicking the text will simply do nothing
                NOTIFY,         // Clicking the text will cause a prompt to appear on the screen asking for confirmation
                                //  before executing the command
                VANILLA         // Clicking the text will execute the command like normally
            }

            public enum CommandClickServerMode
            {
                DO_NOTHING,     // Clicking the text will simply do nothing
                NOTIFY,         // Clicking the text will cause a warning message to be sent to the logs and execute the
                //  command
                ONLY_NOTIFY,    // Clicking the text will cause a warning message to be sent to the logs but nothing
                                //  will be executed
                VANILLA         // Clicking the text will execute the command like normally
            }
        }

        @Getter
        @Setter
        public static class ExtraComponent
        {
            /**
             * Whether to do a simple null check in the "extra" component's array before moving forward.
             */
            private boolean emptyArrayPatchEnabled = true;
        }

        @Getter
        @Setter
        public static class General
        {
            /**
             * How the mod handles component arrays with too much depth in them. There are multiple modes:
             *  - OBVIOUS:      The mod will return a text component that simply says "*** Component is too complex ***".
             *  - VANILLA_LIKE: The mod will throw a JSON parse exception in a similar manner to how vanilla Minecraft
             *                      does it in certain circumstances.
             *  - VANILLA:      The mod will take no action and allow the component to be processed.
             */
            private ArrayDepthPatchMode arrayDepthMode = ArrayDepthPatchMode.OBVIOUS;

            /**
             * The maximum depth a component can have before it takes action to stop it.
             */
            private long arrayDepthMaximum = 96;

            public enum ArrayDepthPatchMode
            {
                OBVIOUS,        // The mod will return a component that says "*** Component is too complex ***"
                VANILLA_LIKE,   // The mod will throw a JSON parse exception
                VANILLA         // Nothing will happen
            }
        }

        @Getter
        @Setter
        public static class TranslatableComponent
        {
            /**
             * Whether to handle translatable components with out-of-bounds placeholders. This does not have any effect
             *  on versions 1.19.3+ as those versions patched the exploit.
             */
            private boolean boundaryPatchEnabled = true;

            /**
             * Whether to limit how many selective placeholders a translatable component can have before it takes
             *  action and returns a component saying "*** Component is too complex ***".
             */
            private boolean placeholderLimitEnabled = true;

            /**
             * The limit for how many selective placeholders a translatable component can have. This accounts
             *  recursively as well.
             */
            private int placeholderLimit = 16;
        }

        @Getter
        @Setter
        public static class HoverEventComponent
        {
            /**
             * Whether to validate Identifiers passed to components with "show_entity" hover events. This does not have
             *  any effect on versions 1.18.1+ as those versions patched the exploit.
             */
            private boolean identifierPatchEnabled = true;

            /**
             * Whether to validate UUIDs passed to components with "show_entity" hover events. This does not have any
             *  effect on versions 1.18.1+ as those versions patched the exploit.
             */
            private boolean uuidPatchEnabled = true;
        }
    }
}
