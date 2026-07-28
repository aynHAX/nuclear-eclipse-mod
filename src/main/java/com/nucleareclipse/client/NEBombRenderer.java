package com.nucleareclipse.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nucleareclipse.NuclearEclipse;
import com.nucleareclipse.entity.BombEntity;
import com.nucleareclipse.registry.NEEntities;
import com.nucleareclipse.registry.NEItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Renders the thrown bomb as a spinning item sprite whose texture matches the
 * bomb's "kind" (registered in the entity's SynchedEntityData).
 *
 * <p>We extend {@link EntityRenderer} directly and delegate the actual drawing
 * to {@link ItemRenderer#renderStatic}, using the {@code GROUND} display
 * context so the bomb looks like a dropped item mid-flight.</p>
 */
@Mod.EventBusSubscriber(modid = NuclearEclipse.MOD_ID, value = Dist.CLIENT, bus = Bus.MOD)
public final class NEBombRenderer {

    private static final Map<String, RegistryObject<Item>> KIND_TO_ITEM = Map.of(
            "quantum_bomb",     NEItems.QUANTUM_BOMB,
            "chronos_bomb",     NEItems.CHRONOS_BOMB,
            "crystal_bomb",     NEItems.CRYSTAL_BOMB,
            "void_bomb",        NEItems.VOID_BOMB,
            "stellar_bomb",     NEItems.STELLAR_BOMB,
            "glow_spore_bomb",  NEItems.GLOW_SPORE_BOMB,
            "aurora_bomb",      NEItems.AURORA_BOMB
    );

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(NEEntities.BOMB.get(), BombRenderer::new);
    }

    /** Returns the ItemStack for a given bomb kind string. */
    public static ItemStack stackForKind(String kind) {
        RegistryObject<Item> reg = KIND_TO_ITEM.getOrDefault(kind, NEItems.STELLAR_BOMB);
        return new ItemStack(reg.get());
    }

    // ─────────────────────────────────────────────────────────────────────

    public static class BombRenderer extends EntityRenderer<BombEntity> {

        private final ItemRenderer itemRenderer;

        public BombRenderer(EntityRendererProvider.Context ctx) {
            super(ctx);
            this.itemRenderer = ctx.getItemRenderer();
        }

        @Nullable
        @Override
        public ResourceLocation getTextureLocation(BombEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(NuclearEclipse.MOD_ID,
                    "textures/item/" + entity.getKind() + ".png");
        }

        @Override
        public void render(BombEntity entity, float yaw, float partialTick,
                          PoseStack poseStack, MultiBufferSource buffer,
                          int packedLight) {
            ItemStack stack = stackForKind(entity.getKind());
            poseStack.pushPose();
            // Match the look of a thrown snowball-style item: a gentle spin.
            float spin = (entity.tickCount + partialTick) * 3.0F;
            poseStack.mulPose(new org.joml.Quaternionf().rotateY((float) Math.toRadians(spin)));
            poseStack.scale(1.3F, 1.3F, 1.3F);

            this.itemRenderer.renderStatic(
                    stack,
                    ItemDisplayContext.GROUND,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    buffer,
                    entity.level(),
                    entity.getId());

            poseStack.popPose();
            super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
        }
    }
}
