package ru.vladislav117.netheriteportableengine;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import ru.vladislav117.netheriteportableengine.content.NetheritePortableEnginePartialModels;
import ru.vladislav117.netheriteportableengine.content.NetheritePortableEngineRenderer;

@Mod(value = CreateNetheritePortableEngine.ID, dist = Dist.CLIENT)
public class CreateNetheritePortableEngineClient {
    public CreateNetheritePortableEngineClient(IEventBus modEventBus) {
        NetheritePortableEnginePartialModels.init();
        modEventBus.addListener(this::registerRenderers);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CreateNetheritePortableEngine.NETHERITE_PORTABLE_ENGINE_BE.get(), NetheritePortableEngineRenderer::new);
    }
}
