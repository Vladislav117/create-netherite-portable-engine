package ru.vladislav117.netheriteportableengine.content;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.Simulated;
import net.minecraft.resources.ResourceLocation;
import ru.vladislav117.netheriteportableengine.CreateNetheritePortableEngine;

public class NetheritePortableEnginePartialModels {
    public static final EngineParts ENGINE_PARTS = new EngineParts("");
    public static final EngineParts ENGINE_PARTS_HEATED = new EngineParts("heated/");
    public static final EngineParts ENGINE_PARTS_SUPERHEATED = new EngineParts("superheated/");

    private static PartialModel block(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateNetheritePortableEngine.ID, "block/" + path));
    }

    private static PartialModel simulatedBlock(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(Simulated.MOD_ID, "block/" + path));
    }

    public static void init() {
    }

    public static class EngineParts {
        public final PartialModel pipeLeft;
        public final PartialModel pipeRight;
        public final PartialModel outletLeft;
        public final PartialModel outletRight;
        public final PartialModel hatchBottom;
        public final PartialModel hatchTop;
        public final PartialModel mouth;

        public EngineParts(String prefix) {
            this.pipeLeft = simulatedBlock("portable_engine/" + prefix + "exhaust_pipe_left");
            this.pipeRight = simulatedBlock("portable_engine/" + prefix + "exhaust_pipe_right");
            this.outletLeft = simulatedBlock("portable_engine/" + prefix + "exhaust_outlet_left");
            this.outletRight = simulatedBlock("portable_engine/" + prefix + "exhaust_outlet_right");
            this.hatchBottom = block("portable_engine/" + prefix + "hatch_bottom");
            this.hatchTop = block("portable_engine/" + prefix + "hatch_top");
            this.mouth = simulatedBlock("portable_engine/" + prefix + "mouth");
        }
    }
}
