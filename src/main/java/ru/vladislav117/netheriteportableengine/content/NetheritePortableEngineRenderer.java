package ru.vladislav117.netheriteportableengine.content;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlockEntity;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NetheritePortableEngineRenderer extends KineticBlockEntityRenderer<PortableEngineBlockEntity> {
    private static final Field VISUAL_STRENGTH_FIELD = visualStrengthField();

    public NetheritePortableEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected static float getHatchOpenProgress(PortableEngineBlockEntity engine, float partialTicks) {
        return Mth.sin(engine.getHatchOpenTime(partialTicks) / 10 * Mth.HALF_PI);
    }

    private static float getVisualStrength(PortableEngineBlockEntity be, float partialTicks) {
        if (VISUAL_STRENGTH_FIELD == null) {
            return be.getBlockState().getValue(BlockStateProperties.LIT) ? 1.0f : 0.0f;
        }

        try {
            Object visualStrength = VISUAL_STRENGTH_FIELD.get(be);
            Method getValue = visualStrength.getClass().getMethod("getValue", float.class);
            return ((Number) getValue.invoke(visualStrength, partialTicks)).floatValue();
        } catch (ReflectiveOperationException e) {
            return be.getBlockState().getValue(BlockStateProperties.LIT) ? 1.0f : 0.0f;
        }
    }

    private static Field visualStrengthField() {
        try {
            Field field = dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlockEntity.class.getDeclaredField("visualStrength");
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    @Override
    protected void renderSafe(PortableEngineBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        BlockState state = this.getRenderedBlockState(be);
        RenderType type = this.getRenderType(be, state);
        renderRotatingBuffer(be, this.getRotatedModel(be, state), ms, buffer.getBuffer(type), light);

        FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
        VertexConsumer cutout = buffer.getBuffer(RenderType.cutout());

        Direction direction = be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockState blockState = be.getBlockState();
        NetheritePortableEnginePartialModels.EngineParts engineParts = NetheritePortableEnginePartialModels.ENGINE_PARTS;

        float visualStrength = getVisualStrength(be, partialTicks);
        boolean lit = blockState.getValue(BlockStateProperties.LIT);
        this.renderHatch(be, partialTicks, ms, light, blockState, direction, cutout, 255, engineParts, !lit, false);
        this.renderPipes(be, partialTicks, ms, light, blockState, direction, cutout, 255, engineParts, false);

        float hatchOpenProgress = 1.0f - getHatchOpenProgress(be, partialTicks);
        if (visualStrength > 0) {
            VertexConsumer translucent = buffer.getBuffer(RenderType.translucent());
            engineParts = be.isSuperHeated()
                    ? NetheritePortableEnginePartialModels.ENGINE_PARTS_SUPERHEATED
                    : NetheritePortableEnginePartialModels.ENGINE_PARTS_HEATED;

            this.renderPipes(be, partialTicks, ms, LightTexture.FULL_BRIGHT, blockState, direction, translucent, (int) (visualStrength * 255), engineParts, true);
        }

        if (lit) {
            VertexConsumer translucent = buffer.getBuffer(RenderType.translucent());
            this.renderHatch(be, partialTicks, ms, LightTexture.FULL_BRIGHT, blockState, direction, translucent, (int) (hatchOpenProgress * 255), engineParts, true, true);
        }
    }

    private void renderHatch(PortableEngineBlockEntity be, float partialTicks, PoseStack ms, int light, BlockState blockState, Direction direction, VertexConsumer consumer, int alpha, NetheritePortableEnginePartialModels.EngineParts parts, boolean renderInner, boolean lit) {
        if (be.isVirtual()) {
            lit = false;
        }

        double hatchPivotY = 4.9f / 16.0f;
        double hatchPivotZ = 3.7f / 16.0f;
        float hatchOpenAmount = getHatchOpenProgress(be, partialTicks) * 0.65f;

        SuperByteBuffer hatchBottom = this.rotateToFacing(CachedBuffers.partial(parts.hatchBottom, blockState), direction);
        if (lit) {
            hatchBottom.disableDiffuse();
        }
        hatchBottom
                .translate(0.0f, hatchPivotY, hatchPivotZ)
                .rotate(-hatchOpenAmount, Direction.EAST)
                .translate(-0.0f, -hatchPivotY, -hatchPivotZ)
                .light(light)
                .color(255, 255, 255, alpha)
                .renderInto(ms, consumer);

        SuperByteBuffer hatchTop = this.rotateToFacing(CachedBuffers.partial(parts.hatchTop, blockState), direction);
        if (lit) {
            hatchTop.disableDiffuse();
        }
        hatchTop
                .light(light)
                .color(255, 255, 255, alpha)
                .renderInto(ms, consumer);

        if (renderInner) {
            SuperByteBuffer mouth = this.rotateToFacing(CachedBuffers.partial(parts.mouth, blockState), direction.getOpposite());
            if (lit) {
                mouth.disableDiffuse();
            }
            mouth
                    .light(light)
                    .renderInto(ms, consumer);
        }
    }

    private void renderPipes(PortableEngineBlockEntity be, float partialTicks, PoseStack ms, int light, BlockState blockState, Direction direction, VertexConsumer consumer, int alpha, NetheritePortableEnginePartialModels.EngineParts parts, boolean lit) {
        float renderTime = AnimationTickHolder.getRenderTime(be.getLevel()) / 20;
        double pulseTime = renderTime * 7.0;
        double clipHeight = 0.65;
        float pulseStrength = 0.03f * getVisualStrength(be, partialTicks);
        float pipePulseStrength = pulseStrength * 1.1f;

        float pipeScale = (float) (Math.max(Math.sin(pulseTime) + clipHeight, 0.0) - clipHeight) * pipePulseStrength + 1.0f;
        float outletScale = (float) (Math.max(Math.sin(pulseTime - 1.15) + clipHeight, 0.0) - clipHeight) * pulseStrength + 1.0f;

        Vector3f outletRotationPointLeft = new Vector3f(2.2f, 10.2f, 11.0f).div(16.0f);
        Vector3f outletRotationPointRight = new Vector3f(13.6f, 10.2f, 11.0f).div(16.0f);
        float outletRotation = (float) Math.toRadians(7.5);
        Vector3f pipeCenterRight = new Vector3f(14.0f, 10.0f, 8.0f).div(16.0f);
        Vector3f pipeCenterLeft = new Vector3f(16.0f - 14.0f, 10.0f, 8.0f).div(16.0f);

        if (be.isVirtual()) {
            lit = false;
        }

        SuperByteBuffer pipeRight = this.rotateToFacing(CachedBuffers.partial(parts.pipeRight, blockState), direction);
        if (lit) {
            pipeRight.disableDiffuse();
        }
        pipeRight
                .translate(pipeCenterRight)
                .scale(pipeScale)
                .translateBack(pipeCenterRight)
                .light(light)
                .color(255, 255, 255, alpha)
                .renderInto(ms, consumer);

        SuperByteBuffer outletRight = this.rotateToFacing(CachedBuffers.partial(parts.outletRight, blockState), direction);
        if (lit) {
            outletRight.disableDiffuse();
        }
        outletRight
                .translate(pipeCenterRight)
                .scale(outletScale)
                .translateBack(pipeCenterRight)
                .translate(outletRotationPointRight)
                .rotateY(-outletRotation)
                .translateBack(outletRotationPointRight)
                .light(light)
                .color(255, 255, 255, alpha)
                .renderInto(ms, consumer);

        SuperByteBuffer pipeLeft = this.rotateToFacing(CachedBuffers.partial(parts.pipeLeft, blockState), direction);
        if (lit) {
            pipeLeft.disableDiffuse();
        }
        pipeLeft
                .translate(pipeCenterLeft)
                .scale(pipeScale)
                .translateBack(pipeCenterLeft)
                .light(light)
                .color(255, 255, 255, alpha)
                .renderInto(ms, consumer);

        SuperByteBuffer outletLeft = this.rotateToFacing(CachedBuffers.partial(parts.outletLeft, blockState), direction);
        if (lit) {
            outletLeft.disableDiffuse();
        }
        outletLeft
                .translate(pipeCenterLeft)
                .scale(outletScale)
                .translateBack(pipeCenterLeft)
                .translate(outletRotationPointLeft)
                .rotateY(outletRotation)
                .translateBack(outletRotationPointLeft)
                .light(light)
                .color(255, 255, 255, alpha)
                .renderInto(ms, consumer);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(PortableEngineBlockEntity te, BlockState state) {
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, te.getBlockState(), te.getBlockState()
                .getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    protected SuperByteBuffer rotateToFacing(SuperByteBuffer buffer, Direction facing) {
        buffer.rotateCentered(AngleHelper.rad(AngleHelper.horizontalAngle(facing)), Direction.UP);
        return buffer;
    }
}
