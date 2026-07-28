package com.nucleareclipse.entity;

import com.nucleareclipse.registry.NEEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Shared projectile entity for all seven bombs. Carries a {@code kind} string
 * that selects which {@link BombDetonation} runs on impact.
 *
 * <p>The throw uses the same rotation-based helper as snowballs so the arc
 * feels natural.</p>
 */
public class BombEntity extends Projectile {

    private static final EntityDataAccessor<String> DATA_KIND =
            SynchedEntityData.defineId(BombEntity.class, EntityDataSerializers.STRING);

    /** How long the bomb lives in ticks before self-detonating if it misses. */
    private static final int MAX_LIFE = 80;
    private int ticksInAir;

    public BombEntity(EntityType<? extends BombEntity> type, Level level) {
        super(type, level);
    }

    // ─────────────────────────── API ───────────────────────────

    public void setKind(String kind) {
        this.entityData.set(DATA_KIND, kind);
    }

    public String getKind() {
        return this.entityData.get(DATA_KIND);
    }

    public void shootFromRotation(Player thrower, float xRot, float yRot,
                                  float roll, float power, float inaccuracy) {
        // Spawn at the thrower's eye position.
        this.setPos(thrower.getEyePosition());
        // Compute a direction vector from the look angles (snowball-style arc).
        float rad = 0.017453292F;
        double dirX = -net.minecraft.util.Mth.sin(yRot * rad) * net.minecraft.util.Mth.cos(xRot * rad);
        double dirY = -net.minecraft.util.Mth.sin(xRot * rad);
        double dirZ =  net.minecraft.util.Mth.cos(yRot * rad) * net.minecraft.util.Mth.cos(xRot * rad);
        this.shoot(dirX, dirY, dirZ, power, inaccuracy);
    }

    /** Mirror of Projectile.shootFromRotation that doesn't need a thrower. */
    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
    }

    // ─────────────────────────── Tick / impact ───────────────────────────

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        this.ticksInAir++;
        if (this.ticksInAir > MAX_LIFE || this.getY() < level().getMinBuildHeight() - 16) {
            detonate();
            return;
        }

        // Standard projectile movement + collision query.
        Vec3 moveVec = this.getDeltaMovement();
        Vec3 nextPos = this.position().add(moveVec);
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS) {
            onHit(hit);
        }
        this.move(MoverType.SELF, moveVec);
        this.applyGravity();

        // Air drag — snowball-like gentle deceleration.
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.99, 0.99));
    }

    @Override
    protected void onHit(HitResult hit) {
        if (hit.getType() == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) hit);
        } else if (hit.getType() == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) hit);
        }
        detonate();
    }

    protected void onHitEntity(EntityHitResult hit) {
        // Nudge so the blast centers on the entity.
        this.setPos(hit.getEntity().position());
    }

    protected void onHitBlock(BlockHitResult hit) {
        this.setPos(hit.getLocation());
    }

    /** Run the detonation logic for the current bomb kind and remove this entity. */
    private void detonate() {
        if (level().isClientSide) return;
        BombDetonation detonation = BombDetonations.byKind(getKind());
        detonation.detonate(this, level(), this.blockPosition());
        this.discard();
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return !(entity instanceof Player) && entity.isPickable();
    }

    // ─────────────────────────── Boilerplate ───────────────────────────

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_KIND, "quantum_bomb");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Kind", getKind());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setKind(tag.contains("Kind") ? tag.getString("Kind") : "quantum_bomb");
    }

    @Override
    public boolean isPickable() { return false; }
}
