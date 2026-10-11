package com.juno.donutchesthighlight;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DonutChestHighlight.MOD_ID, value = Dist.CLIENT)
public final class ChestOutlineRenderer {
    private static final int RANGE = 24;

    private ChestOutlineRenderer() {}

    static {
        RenderHighlightEvent.Block.BUS.addListener(ChestOutlineRenderer::onBlockHighlight);
    }

    private static void onBlockHighlight(RenderHighlightEvent.Block event) {
        event.setCustomRenderer((source, pose, translucent, renderState) ->
                renderNearbyChests(source, pose));
    }

    private static void renderNearbyChests(MultiBufferSource.BufferSource source, PoseStack pose) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;

        BlockPos center = minecraft.player.blockPosition();
        VertexConsumer lines = source.getBuffer(RenderTypes.lines());
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = -RANGE; x <= RANGE; x++) {
            for (int y = -RANGE; y <= RANGE; y++) {
                for (int z = -RANGE; z <= RANGE; z++) {
                    pos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    BlockState state = minecraft.level.getBlockState(pos);
                    int color = colorFor(state);
                    if (color == 0) continue;

                    AABB box = new AABB(pos).inflate(0.002);
                    float red = ((color >> 16) & 255) / 255.0F;
                    float green = ((color >> 8) & 255) / 255.0F;
                    float blue = (color & 255) / 255.0F;
                    LevelRenderer.renderLineBox(pose, lines,
                            box.minX, box.minY, box.minZ,
                            box.maxX, box.maxY, box.maxZ,
                            red, green, blue, 0.95F);
                }
            }
        }
    }

    private static int colorFor(BlockState state) {
        if (state.is(Blocks.ENDER_CHEST)) return 0xB060FF;
        if (state.is(Blocks.BARREL)) return 0xFFB347;
        if (state.is(Blocks.CHEST) || state.is(Blocks.TRAPPED_CHEST)) return 0xFFD84D;
        if (state.is(Blocks.COPPER_CHEST)
                || state.is(Blocks.EXPOSED_COPPER_CHEST)
                || state.is(Blocks.WEATHERED_COPPER_CHEST)
                || state.is(Blocks.OXIDIZED_COPPER_CHEST)
                || state.is(Blocks.WAXED_COPPER_CHEST)
                || state.is(Blocks.WAXED_EXPOSED_COPPER_CHEST)
                || state.is(Blocks.WAXED_WEATHERED_COPPER_CHEST)
                || state.is(Blocks.WAXED_OXIDIZED_COPPER_CHEST)) return 0x55E6A5;
        return 0;
    }
}
