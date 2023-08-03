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

    private static final int latestVersion = 3;

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

    private NBT nbtPatches = new NBT();

    private Network networkPatches = new Network();

    private Render renderPatches = new Render();

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
        private boolean upsideDownPortalPatchEnabled = true;
    }

    @Getter
    @Setter
    public static class BlockEntities
    {
        private boolean potSherdValidationEnabled = true;

        private boolean customNameValidationEnabled = true;
    }

    @Getter
    @Setter
    public static class Entities
    {
        private boolean customNameValidationEnabled = true;
    }

    @Getter
    @Setter
    public static class NBT
    {
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
            private boolean particleLimitEnabled = true;

            private int particleLimit = 500;
        }
    }

    @Getter
    public static class Overrides
    {
        /**
         * A list of forcibly disabled patch mixin names. This is usually for in case a patch is causing a serious
         *  problem which can't be mitigated by disabling the patch traditionally (e.g. the client refuses to boot due
         *  to the patch mixin itself being applied in scenarios where it shouldn't be).
         */
        private List<String> forciblyDisabledPatches = new ArrayList<>();
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
            private boolean heartCountLimitEnabled = true;

            private int maxHeartsToRender = 32;
        }

        @Getter
        @Setter
        public static class Entities
        {
            private boolean nameLengthLimitEnabled = true;

            private int nameLengthLimit = 255;
        }
    }

    @Getter
    public static class Text
    {
        private ClickEventComponent clickEvent = new ClickEventComponent();
      
        private ExtraComponent extra = new ExtraComponent();

        private HoverEventComponent hoverEvent = new HoverEventComponent();

        private TranslatableComponent translation = new TranslatableComponent();

        public static class ClickEventComponent
        {
            private CommandClickMode commandClickMode = CommandClickMode.NOTIFY;

            public enum CommandClickMode
            {
                DO_NOTHING,     // Clicking the text will simply do nothing
                NOTIFY,         // Clicking the text will cause a prompt to appear on the screen asking for confirmation
                                //  before executing the command
                VANILLA         // Clicking the text will execute the command like normally
            }
        }
      
        @Getter
        @Setter
        public static class ExtraComponent
        {
            private boolean emptyArrayPatchEnabled = true;
        }

        @Getter
        @Setter
        public static class TranslatableComponent
        {
            private boolean boundaryPatchEnabled = true;

            private boolean placeholderLimitEnabled = true;

            private int placeholderLimit = 16;
        }

        @Getter
        @Setter
        public static class HoverEventComponent
        {
            private boolean identifierPatchEnabled = true;

            private boolean uuidPatchEnabled = true;
        }
    }
}
