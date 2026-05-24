package ru.vladislav117.netheriteportableengine.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import ru.vladislav117.netheriteportableengine.CreateNetheritePortableEngine;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class NetheritePortableEngineDataGen {
    public static void gatherData(GatherDataEvent event) {
        if (!event.getMods().contains(CreateNetheritePortableEngine.ID)) {
            return;
        }

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        NetheritePortableEngineBlockTagsProvider blockTagsProvider = new NetheritePortableEngineBlockTagsProvider(output, lookupProvider, existingFileHelper);

        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new NetheritePortableEngineItemTagsProvider(output, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new NetheritePortableEngineRecipeProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(NetheritePortableEngineLootProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
    }
}
