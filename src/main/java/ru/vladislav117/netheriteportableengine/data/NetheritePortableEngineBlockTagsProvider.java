package ru.vladislav117.netheriteportableengine.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import ru.vladislav117.netheriteportableengine.CreateNetheritePortableEngine;

import java.util.concurrent.CompletableFuture;

public class NetheritePortableEngineBlockTagsProvider extends TagsProvider<Block> {
    public NetheritePortableEngineBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, Registries.BLOCK, lookupProvider, CreateNetheritePortableEngine.ID, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ResourceKey.create(Registries.BLOCK, CreateNetheritePortableEngine.NETHERITE_PORTABLE_ENGINE_ITEM.getId()));
    }
}
