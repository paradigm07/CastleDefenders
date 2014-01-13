package mods.CastleDef;

import java.util.List;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMage extends EntityDefender
{
    protected int attackStrength;

    public EntityMage(World var1)
    {
        super(var1);
        this.texture = "/mods/Mage.png";
        this.health = 10;
        this.moveSpeed = 0.0F;
        this.setSize(0.9F, 1.8F);
        this.isImmuneToFire = true;
        this.attackStrength = 0;
        this.tasks.addTask(1, new EntityMageAttack(this, IMob.class, this.moveSpeed, true));
        this.tasks.addTask(2, new EntityAIWander(this, this.moveSpeed));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, IMob.class, 120.0F, 0, true));
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return 0;
    }

    public int getMaxHealth()
    {
        return 25;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        if (this.worldObj.countEntities(this.getClass()) >= 500)
        {
            return false;
        }
        else
        {
            int var1 = MathHelper.floor_double(this.posX);
            int var2 = MathHelper.floor_double(this.boundingBox.minY);
            int var3 = MathHelper.floor_double(this.posZ);
            this.worldObj.getBlockId(var1, var2 - 1, var3);
            List var5 = this.worldObj.getEntitiesWithinAABB(EntityMage.class, AxisAlignedBB.getBoundingBox(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D).expand(2.0D, 2.0D, 2.0D));
            return this.worldObj.getBlockId(var1, var2 - 1, var3) == mod_castledef.BlockMageID && this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() == 0 && var5.isEmpty();
        }
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk()
    {
        return 0;
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn()
    {
        return false;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound var1)
    {
        super.writeEntityToNBT(var1);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound var1)
    {
        super.readEntityFromNBT(var1);
    }
}