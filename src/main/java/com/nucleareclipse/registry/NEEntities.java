package com.nucleareclipse.registry;

import com.nucleareclipse.NuclearEclipse;
import com.nucleareclipse.entity.BombEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * All bomb projectile entities. One shared {@link BombEntity} carries the
 * bomb "kind" tag so a single entity class powers every bomb type.
 */
public final class NEEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NuclearEclipse.MOD_ID);

    public static final RegistryObject<EntityType<BombEntity>> BOMB = ENTITIES.register("bomb",
            () -> EntityType.Builder.<BombEntity>of(BombEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(20)
                    .fireImmune()
                    .build("bomb"));

    private NEEntities() {}
}
