package net.aeronica.mods.mcjammer.common.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class PlacardEntity extends Entity {

	public EntityLivingBase parent;
	public EntityLivingBase renderingParent;
	private Minecraft mc;
	private int index;

	public PlacardEntity(World par1World) {
		super(par1World);
		this.setSize(0.5F, 0.5F);
		ignoreFrustumCheck = true;
		renderDistanceWeight = 10D;
	}

	public PlacardEntity(World world, EntityLivingBase parent, float renderTick) {
		super(world);
		mc = Minecraft.getMinecraft();
		this.parent = this.renderingParent = parent;

		double diffX = (parent.prevPosX + (parent.posX - parent.prevPosX)
				* renderTick);
		double diffY = (parent.prevPosY + (parent.posY - parent.prevPosY)
				* renderTick);
		double diffZ = (parent.prevPosZ + (parent.posZ - parent.prevPosZ)
				* renderTick);

		double placardHeight = parent.isSneaking() ? 0.42d : 0.55d;
		if (mc.gameSettings.thirdPersonView > 0 && parent.isClientWorld())
			setLocationAndAngles(diffX, diffY + placardHeight, diffZ,
					parent.rotationYaw, parent.rotationPitch);
		else
			setLocationAndAngles(diffX, diffY + parent.height + placardHeight,
					diffZ, parent.rotationYaw, parent.rotationPitch);

		this.setSize(0.5F, 0.5F);
		ignoreFrustumCheck = true;
		renderDistanceWeight = 10D;
	}

	public void setPlacard(int index) {
		this.index = index;
	}
	
	public int getPlacardIndex() {
		return index;
	}
	
	@Override
	protected void entityInit() {
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (parent == null || !parent.isEntityAlive() || parent.isChild()) {
			setDead();
			return;
		}
	}

	@Override
	public boolean writeToNBTOptional(NBTTagCompound par1NBTTagCompound) {
		return false;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
	}
}
