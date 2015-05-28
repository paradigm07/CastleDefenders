package mods.castledefenders.common.blocks;

import mods.castledefenders.ModCastleDefenders;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import com.gollum.core.tools.helper.blocks.HBlockContainer;

public abstract class BlockCastleDefenders extends HBlockContainer {
	
	/**
	 * Constructeur
	 * @param id
	 */
	public BlockCastleDefenders (int id, String registerName, Material material) {
		super(id, registerName, material);
		this.setCreativeTab(ModCastleDefenders.tabCastleDefenders);
	}
	
	/**
	 * only called by clickMiddleMouseButton , and passed to
	 * inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	public int idPicked(World var1, int var2, int var3, int var4) {
		return 0;
	}
	
	/**
	 * Is this block (a) opaque and (b) a full 1m cube? This determines whether
	 * or not to render the shared face of two adjacent blocks and also whether
	 * the player can attach torches, redstone wire, etc to this block.
	 */
	public boolean isOpaqueCube() {
		return false;
	}
	
}
