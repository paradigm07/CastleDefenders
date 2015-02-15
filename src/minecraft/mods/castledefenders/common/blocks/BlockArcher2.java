package mods.castledefenders.common.blocks;

import mods.castledefenders.common.tileentities.TileEntityBlockArcher2;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockArcher2 extends BlockCastleDefenders {
	
	/**
	 * Constructeur
	 * @param id
	 */
	public BlockArcher2 (String registerName) {
		super(registerName, Material.rock);
	}
	
	/**
	 * Creation de l'entite
	 */
	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityBlockArcher2();
	}
}
