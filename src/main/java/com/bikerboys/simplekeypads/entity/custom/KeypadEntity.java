package com.bikerboys.simplekeypads.entity.custom;

import com.bikerboys.simplekeypads.SimpleKeypads;
import com.bikerboys.simplekeypads.entity.ModEntities;
import com.bikerboys.simplekeypads.util.KeypadContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KeypadEntity extends HangingEntity {
    private static final EntityDataAccessor<Integer> DATA_ROTATION = SynchedEntityData.defineId(KeypadEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_ATTACHED_FACE = SynchedEntityData.defineId(KeypadEntity.class, EntityDataSerializers.INT);

    private boolean dropped = false;
    private Direction attachedFace = Direction.NORTH;
    private boolean firstplaced = true;

    private String keycode;

    public KeypadEntity(EntityType<?> entityType, Level level) {
        super(ModEntities.KEYPAD.get(), level);
    }

    public KeypadEntity(EntityType<?> entityType, Level level, BlockPos pos, Direction direction, Direction attachedFace) {
        super(ModEntities.KEYPAD.get(), level);
        direction = sanitizeDirection(direction);
        this.pos = pos;
        this.setDirection(direction);

        this.recalculateBoundingBox();
        this.attachedFace = attachedFace;
        this.setAttachedFace(attachedFace);

        System.out.println(pos);
        System.out.println(direction);
        System.out.println(attachedFace);


        keycode = "0000";
    }

    public void setFirstplaced(boolean firstplaced) {
        this.firstplaced = firstplaced;
    }

    public Boolean getFirstPlaced() {
        return this.firstplaced;
    }


    @Override
    public boolean skipAttackInteraction(Entity entity) {

        if (entity instanceof Player player) {
            for (KeypadContext keypadContext : SimpleKeypads.allowedplayercontext) {
                if (keypadContext.player == player) {
                    if (keypadContext.pos.equals(getOnPos())) {
                        if (keypadContext.face.equals(this.getAttachedFace())) {
                            return false;
                        }
                    }
                }

            }
        }

        return true;
    }


    @Override
    public boolean hurt(DamageSource damageSource, float amount) {


        if (damageSource.is(DamageTypes.PLAYER_ATTACK)) {
            this.kill();
            this.dropItem(this);
            return true;
        }

        if (!damageSource.isIndirect()) {
            this.kill();
            this.dropItem(this);
            return true;
        }


        return false;
    }

    @Override
    public void push(Entity p_20293_) {

    }

    @Override
    public boolean survives() {
        BlockPos onPos = this.getOnPos();
        BlockState blockState = this.level().getBlockState(onPos);

        if (blockState.isAir()) {
            return false;
        }

        // Instead of blocking on *any* hanging entity, maybe only block on keypads on the same face
        List<Entity> entities = this.level().getEntities(this, this.getBoundingBox(), e -> e instanceof KeypadEntity);
        return entities.isEmpty();
    }

    private static Direction sanitizeDirection(Direction direction) {
        if (direction.getAxis().isVertical()) {
            return Direction.NORTH;
        }
        return direction;
    }

    public void setAttachedFace(Direction face) {
        this.getEntityData().set(DATA_ATTACHED_FACE, face.get3DDataValue());
    }

    public Direction getAttachedFace() {
        return Direction.from3DDataValue(this.getEntityData().get(DATA_ATTACHED_FACE));
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ROTATION, 0);
        this.getEntityData().define(DATA_ATTACHED_FACE, Direction.NORTH.get3DDataValue());
    }

    @Override
    public int getWidth() {
        return 5;
    }

    @Override
    public int getHeight() {
        return 8;
    }

    @Override
    public void dropItem(@Nullable Entity p_31717_) {
        if (!this.level().isClientSide
                && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(new ItemStack(SimpleKeypads.KEYPAD_ITEM.get()));
        }
    }

    public boolean isAttachedToBlockFace(BlockPos blockPos, Direction face) {
        BlockPos attachedBlock = this.pos.relative(this.attachedFace.getOpposite());
        return attachedBlock.equals(blockPos) && this.attachedFace == face;
    }


    @Override
    public void playPlacementSound() {

    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(SimpleKeypads.KEYPAD_ITEM.get());
    }


    @Override
    protected void setDirection(Direction direction) {
        this.direction = sanitizeDirection(direction);
        if (direction.getAxis().isHorizontal()) {
            this.setXRot(0.0F);
            this.setYRot((float)(direction.get2DDataValue() * 90));
        } else {
            this.setXRot((float)(-90 * direction.getAxisDirection().getStep()));
            this.setYRot(0.0F);
        }
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();

        // compute proper position based on facing
        double d0 = 0.46875D;
        double x = this.pos.getX() + 0.5D - direction.getStepX() * d0;
        double y = this.pos.getY() + 0.5D - direction.getStepY() * d0;
        double z = this.pos.getZ() + 0.5D - direction.getStepZ() * d0;
        this.setPosRaw(x, y, z);

        // update bounding box
        double w = this.getWidth();
        double h = this.getHeight();
        double d = this.getWidth();
        switch (direction.getAxis()) {
            case X -> w = 1.0D;
            case Y -> h = 1.0D;
            case Z -> d = 1.0D;
        }
        w /= 32.0D;
        h /= 32.0D;
        d /= 32.0D;
        this.setBoundingBox(new AABB(x - w, y - h, z - d, x + w, y + h, z + d));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("Facing", (byte) sanitizeDirection(direction).get3DDataValue());
        tag.putString("code", this.keycode);
    }

    public int getRotation() {
        return this.getEntityData().get(DATA_ROTATION);
    }

    public void setRotation(int p_31771_) {
        this.setRotation(p_31771_, true);
    }

    private void setRotation(int p_31773_, boolean p_31774_) {
        this.getEntityData().set(DATA_ROTATION, p_31773_ % 8);
        if (p_31774_ && this.pos != null) {
            this.level().updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
        }

    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.sanitizeDirection(direction).get3DDataValue(), this.getPos());
    }




    public boolean isAttachedToBlock(BlockPos blockPos) {
        // Compute the block this keypad is facing/attached to
        BlockPos attached = this.pos.relative(this.getDirection().getOpposite());
        return attached.equals(blockPos);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.setDirection(sanitizeDirection(Direction.from3DDataValue(packet.getData())));
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        this.setKeycode(tag.getString("code"));
        this.setDirection(sanitizeDirection(Direction.from3DDataValue(tag.getByte("Facing"))));
    }
    public float getVisualRotationYInDegrees() {
        Direction direction = sanitizeDirection(this.getDirection());

        int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
        return (float) Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + this.getRotation() * 45 + i);
    }

    public String getKeycode() {
        return keycode;
    }

    public BlockPos getOnPos() {
        return this.pos.relative(this.getDirection().getOpposite());
    }

    public void setKeycode(String keycode) {
        this.keycode = keycode;
    }
}
