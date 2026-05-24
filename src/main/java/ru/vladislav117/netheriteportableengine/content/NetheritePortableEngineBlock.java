package ru.vladislav117.netheriteportableengine.content;

import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlock;
import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import ru.vladislav117.netheriteportableengine.CreateNetheritePortableEngine;

public class NetheritePortableEngineBlock extends PortableEngineBlock {
    public NetheritePortableEngineBlock(Properties properties) {
        super(properties, DyeColor.BLACK);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }

        PortableEngineBlockEntity be = (PortableEngineBlockEntity) level.getBlockEntity(pos);
        if (be != null && !be.inventory.isEmpty()) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), be.inventory.getItem(0));
        }

        level.removeBlockEntity(pos);
    }

    @Override
    public Class<PortableEngineBlockEntity> getBlockEntityClass() {
        return PortableEngineBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PortableEngineBlockEntity> getBlockEntityType() {
        return CreateNetheritePortableEngine.NETHERITE_PORTABLE_ENGINE_BE.get();
    }
}
