package mods.castledefenders.common.blocks;

import mods.castledefenders.common.tileentities.TileEntityBlockKnight;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockKnight extends BlockCastleDefenders {
	
	/**
	 * Constructeur
	 * @param id
	 */
	public BlockKnight (String registerName) {
		super(registerName, Material.rock);
	}
	
	/**
	 * Creation de l'entite
	 */
	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityBlockKnight();
	}
	
}
