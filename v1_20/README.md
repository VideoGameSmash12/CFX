# v1_20
This module handles patches for 1.20+.

## What needs to be done
[ ] Port ExcessiveHearts to 1.20+ - The original attempt to patch it didn't work as it would just crash on startup with this error - `Caused by: org.spongepowered.asm.mixin.injection.throwables.InjectionError: Critical injection failure: Variable modifier method injectRenderStatusBars(I)I in cfx.v1_20.mixins.json:RendererPatches$HudPatches$ExcessiveHearts from mod cfx-v1_20 failed injection check, (0/1) succeeded. Scanned 1 target(s). Using refmap v1_20-refmap.json`