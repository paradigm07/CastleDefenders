package mods.castledefenders.common.worldgenerator;

import java.util.ArrayList;
import java.util.Random;

import mods.castledefenders.common.ModCastleDefenders;
import mods.castledefenders.common.building.Building;
import mods.castledefenders.common.building.Building.Unity;
import mods.castledefenders.common.building.Building.Unity.Content;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockWall;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGeneratorByBuilding implements IWorldGenerator {

	public static final int DIMENSION_ID_NETHER = -1;
	public static final int DIMENSION_ID_SURFACE = 0;
	
	private class BuildingAndInfos {
		Building building;
		int spawnRate;
	}
	
	/**
	 * Spawn global de tous les batiment de cette instance de worldGenerator
	 */
	int globalSpawnRate = 0;

	private ArrayList<BuildingAndInfos> buildingsNether  = new ArrayList<BuildingAndInfos> ();
	private ArrayList<BuildingAndInfos> buildingsSurface = new ArrayList<BuildingAndInfos> ();
	
	
	
	public WorldGeneratorByBuilding(int globalSpawnRate) {
		this.globalSpawnRate = globalSpawnRate;
	}
	

	public void addbuilding(Building building, int buildingSpawnRate) {
		this.addbuilding(building, buildingSpawnRate, this.DIMENSION_ID_SURFACE);
	}
	
	/**
	 * Ajoute un batiment
	 * @param buildingMercenary1
	 * @param mercenaryBuilding1SpawnRate
	 * @param dimensionIdSurface
	 */
	public void addbuilding(Building building, int buildingSpawnRate, int dimensionId) {
		
		BuildingAndInfos buildingAndInfos = new BuildingAndInfos ();
		buildingAndInfos.building         = building;
		buildingAndInfos.spawnRate        = buildingSpawnRate;
		
		switch (dimensionId) {
			case WorldGeneratorByBuilding.DIMENSION_ID_NETHER:
				this.buildingsNether.add(buildingAndInfos);
				break;
				
			case WorldGeneratorByBuilding.DIMENSION_ID_SURFACE:
				this.buildingsSurface.add(buildingAndInfos);
				break;
			default:
		}
	}
	
	/**
	 * Methode de genera
	 */
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		
		// Generation diffenrente entre le nether et la surface
		switch (world.provider.dimensionId) {
			case WorldGeneratorByBuilding.DIMENSION_ID_NETHER:
				this.generateBuilding(world, random, chunkX, chunkZ, buildingsNether);
				break;
				
			case WorldGeneratorByBuilding.DIMENSION_ID_SURFACE:
				this.generateBuilding(world, random, chunkX, chunkZ, buildingsSurface);
				break;
			default:
		}
	}
	
	/**
	 * Renvoie le spawn total de tous une liste de batiment
	 * @param buildins
	 * @return
	 */
	private int totalRateSpawnByBuildingList (ArrayList<BuildingAndInfos> buildings) {
		int total = 0;
		for (BuildingAndInfos building : buildings) {
			total += building.spawnRate;
		}
		return total;
	}
	
	/**
	 * Charge le batiment de maniere aléatoire en fonction du ratio
	 * @param buildings
	 * @param totalRate
	 * @return
	 */
	private Building getBuildingInRate(ArrayList<BuildingAndInfos> buildings, Random random) {
		
		ArrayList<Building>buildingsForRate = new ArrayList<Building>();
		
		for (BuildingAndInfos buildingAndInfos : buildings) {
			for (int i = 0; i < buildingAndInfos.spawnRate; i++) {
				buildingsForRate.add(buildingAndInfos.building);
			}
		}
		
		return buildingsForRate.get(random.nextInt(this.totalRateSpawnByBuildingList (buildings)));
	}
	
	/**
	 * Genere le batiment dans le terrain correspondant
	 * @param world
	 * @param random
	 * @param wolrdX
	 * @param wolrdZ
	 * @param buildings
	 * @param random
	 */
	private void generateBuilding(World world, Random random, int chunkX, int chunkZ, ArrayList<BuildingAndInfos> buildings) {
		
		if (buildings.size() == 0) {
			return;
		}
		
		// test du Spawn global
		if (random.nextInt(50) < Math.min (this.globalSpawnRate, 10)  && true) {
			

			// Position initial de la génération en hauteur
			int worldY = 64;
			Building building = this.getBuildingInRate (buildings, random);
			
			// Position initiale du batiment
			int initX = chunkX * 16 + random.nextInt(8) - random.nextInt(8);
			int initY = worldY      + random.nextInt(8) - random.nextInt(8);
			int initZ = chunkZ * 16 + random.nextInt(8) - random.nextInt(8);
			
			// Pour test sur un superflat
			initY = 3;
			
			//Test si on est sur de la terre (faudrais aps que le batiment vol)
			if (world.getBlockId(initX + 3, initY, initZ + 3) == Block.grass.blockID) {
				
				// Parcours la matrice et ajoute les blocks
				for (int x= 0; x < building.maxX; x++) {
					for (int y= 0; y < building.maxY; y++) {
						for (int z= 0; z < building.maxZ; z++) {
							
							Unity unity = building.get(x, y, z);
							
							// Position réél dans le monde du block
							int finalX = initX + x;
							int finalY = initY + y;
							int finalZ = initZ + z;
							
							if (unity.block != null) {
								world.setBlock(finalX, finalY, finalZ, unity.block.blockID, unity.metadata, 2);
							} else {
								world.setBlock(finalX, finalY, finalZ, 0, 0, 2);
							}

							this.setOrientation (world, finalX, finalY, finalZ, unity.orientation);
							this.setContents    (world, finalX, finalY, finalZ, unity.contents);
						}
					}
				}
				
//				this.buildOld(world, random, initX, initY, initZ + 20);
//				ModCastleDefenders.log.warning("End create old building in : "+initX +" "+ initY +" "+ initZ);
			}
		}
	}
	
	/**
	 * Insert le contenu du block
	 * @param world
	 * @param i
	 * @param j
	 * @param k
	 * @param contents
	 */
	private void setContents(World world, int x, int y, int z, ArrayList<ArrayList<Content>> contents) {
		// TODO
	}


	/**
	 * Affecte l'orientation
	 * @param i
	 * @param j
	 * @param k
	 * @param orientation
	 */
	private void setOrientation(World world, int x, int y, int z, int orientation) {

		Block block  = Block.blocksList [world.getBlockId (x, y, z)];
		int metadata = world.getBlockMetadata (x, y, z);
		
		if (block instanceof BlockTorch) {

			if (orientation == Unity.ORIENTATION_NONE)  { metadata = (metadata & 0x8) + 0; } else 
			if (orientation == Unity.ORIENTATION_UP)    { metadata = (metadata & 0x8) + 4; } else 
			if (orientation == Unity.ORIENTATION_DOWN)  { metadata = (metadata & 0x8) + 3; } else 
			if (orientation == Unity.ORIENTATION_LEFT)  { metadata = (metadata & 0x8) + 2; } else 
			if (orientation == Unity.ORIENTATION_RIGTH) { metadata = (metadata & 0x8) + 1; } else 
			{
				ModCastleDefenders.log.severe("Bad orientation : "+x+","+y+","+z);
			}
			
			world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
			return;
		}
		
		if (block instanceof BlockDirectional) {

			if (orientation == Unity.ORIENTATION_NONE)  { metadata = (metadata & 0x8) + 0; } else 
			if (orientation == Unity.ORIENTATION_UP)    { metadata = (metadata & 0x8) + 0; } else 
			if (orientation == Unity.ORIENTATION_DOWN)  { metadata = (metadata & 0x8) + 2; } else 
			if (orientation == Unity.ORIENTATION_LEFT)  { metadata = (metadata & 0x8) + 3; } else 
			if (orientation == Unity.ORIENTATION_RIGTH) { metadata = (metadata & 0x8) + 1; } else 
			{
				ModCastleDefenders.log.severe("Bad orientation : "+x+","+y+","+z);
			}
			
			world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
			return;
		}
		
		if (
			block instanceof BlockLadder ||
			block instanceof BlockWall ||
			block instanceof BlockFurnace ||
			block instanceof BlockChest
		) {
			
			if (orientation == Unity.ORIENTATION_NONE)  { metadata = (metadata & 0x8) + 2; } else 
			if (orientation == Unity.ORIENTATION_UP)    { metadata = (metadata & 0x8) + 2; } else 
			if (orientation == Unity.ORIENTATION_DOWN)  { metadata = (metadata & 0x8) + 3; } else 
			if (orientation == Unity.ORIENTATION_LEFT)  { metadata = (metadata & 0x8) + 4; } else 
			if (orientation == Unity.ORIENTATION_RIGTH) { metadata = (metadata & 0x8) + 5; } else 
			{
				ModCastleDefenders.log.severe("Bad orientation : "+x+","+y+","+z);
			}
			

			ModCastleDefenders.log.info("blockID : "+block.blockID);
			ModCastleDefenders.log.info("metadata : "+metadata);
			
			world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
			return;
		}
		
	}


	private void buildOld (World world, Random random, int ramdom8M8_X, int ramdom8M8_Y, int ramdom8M8_Z) {
		
		ModCastleDefenders.log.warning("Create old building in : "+ramdom8M8_X+" "+ramdom8M8_Y+" "+ramdom8M8_Z);
		
            int var8 = ramdom8M8_X;
            int var9 = ramdom8M8_Y;
            int var10 = ramdom8M8_Z;
            int x, y, z;
            World var1 = world;
            Random var2 = random;

            if (var1.getBlockId(var8 + 3, var9 - 1, var10 + 3) == Block.grass.blockID)
            {
                for (y = var9; y < var9 + 8; ++y)
                {
                    for (x = 0; x < 11; ++x)
                    {
                        for (z = 0; z < 11; ++z)
                        {
                            var1.setBlock(var8 + x, y, var10 + z, 0, 0, 2);
                        }
                    }
                }

                for (y = var9; y < var9 + 4; ++y)
                {
                    for (x = 0; x < 6; ++x)
                    {
                        for (z = 0; z < 11; ++z)
                        {
                            var1.setBlock(var8 + x, y, var10 + z, 5);
                        }
                    }
                }

                for (y = var9; y < var9 + 3; ++y)
                {
                    for (x = 1; x < 5; ++x)
                    {
                        for (z = 1; z < 10; ++z)
                        {
                            var1.setBlock(var8 + x, y, var10 + z, 0);
                        }
                    }
                }

                var1.setBlock(var8 + 5, var9, var10 + 5, 0);
                var1.setBlock(var8 + 5, var9 + 1, var10 + 5, 0);
                var1.setBlock(var8 + 4, var9 + 1, var10 + 4, 50);
                var1.setBlock(var8 + 4, var9 + 1, var10 + 6, 50);

                if (var1.getBlockId(var8 + 3, var9 + 1, var10 - 1) == 0)
                {
                    var1.setBlock(var8 + 3, var9 + 1, var10, 0);
                }
                else
                {
                    var1.setBlock(var8 + 3, var9 + 1, var10, 50);
                }

                var1.setBlock(var8 + 1, var9, var10 + 1, 61);
                var1.setBlock(var8 + 1, var9, var10 + 2, 54);
                var1.setBlock(var8 + 1, var9, var10 + 3, 54);
                var1.setBlock(var8 + 4, var9, var10 + 8, 26);
                var1.setBlock(var8 + 4, var9, var10 + 9, 26);
                var1.setBlock(var8 + 2, var9, var10 + 8, 26);
                var1.setBlock(var8 + 2, var9, var10 + 9, 26);

                for (y = 6; y < 11; ++y)
                {
                    for (x = 0; x < 11; ++x)
                    {
                        if (var1.getBlockId(var8 + y, var9 - 1, var10 + x) != 0)
                        {
                            var1.setBlock(var8 + y, var9, var10 + x, 85);
                        }
                    }
                }

                for (y = 6; y < 10; ++y)
                {
                    for (x = 1; x < 10; ++x)
                    {
                        var1.setBlock(var8 + y, var9, var10 + x, 0);
                    }
                }

                var1.setBlock(var8 + 10, var9, var10 + 5, 0);
                var1.setBlock(var8 + 4, var9 - 1, var10 + 2, ModCastleDefenders.blockMerc.blockID);

                if (var1.getBlockId(var8 + 8, var9 - 1, var10 + 2) != 0)
                {
                    var1.setBlock(var8 + 8, var9 - 1, var10 + 2, ModCastleDefenders.blockMerc.blockID);
                }

//                for (y = 0; y < 2; ++y)
//                {
//                    TileEntityChest var15 = (TileEntityChest)var1.getBlockTileEntity(var8 + 1, var9, var10 + 3);
//                    ItemStack var14 = this.pickCheckLootItem(var2);
//
//                    if (var14 != null)
//                    {
//                        var15.setInventorySlotContents(var2.nextInt(var15.getSizeInventory()), var14);
//                    }
//                }

                var1.setBlock(var8 + 1, var9 + 4, var10 + 1, 126);
                var1.setBlock(var8 + 1, var9 + 4, var10 + 2, 126);
                var1.setBlock(var8 + 1, var9 + 4, var10 + 3, 126);
                var1.setBlock(var8 + 1, var9 + 4, var10 + 4, 5);
                var1.setBlock(var8 + 1, var9 + 4, var10 + 5, 5);
                var1.setBlock(var8 + 1, var9 + 4, var10 + 6, 5);
                var1.setBlock(var8 + 1, var9 + 4, var10 + 7, 126);
                var1.setBlock(var8 + 1, var9 + 4, var10 + 8, 126);
                var1.setBlock(var8 + 1, var9 + 4, var10 + 9, 126);
                var1.setBlock(var8 + 2, var9 + 4, var10 + 1, 126);
                var1.setBlock(var8 + 3, var9 + 4, var10 + 1, 126);
                var1.setBlock(var8 + 4, var9 + 4, var10 + 1, 126);
                var1.setBlock(var8 + 4, var9 + 4, var10 + 2, 126);
                var1.setBlock(var8 + 4, var9 + 4, var10 + 3, 126);
                var1.setBlock(var8 + 4, var9 + 4, var10 + 4, 5);
                var1.setBlock(var8 + 4, var9 + 4, var10 + 5, 5);
                var1.setBlock(var8 + 4, var9 + 4, var10 + 6, 5);
                var1.setBlock(var8 + 4, var9 + 4, var10 + 7, 126);
                var1.setBlock(var8 + 4, var9 + 4, var10 + 8, 126);
                var1.setBlock(var8 + 4, var9 + 4, var10 + 9, 126);
                var1.setBlock(var8 + 2, var9 + 4, var10 + 9, 126);
                var1.setBlock(var8 + 3, var9 + 4, var10 + 9, 126);
                var1.setBlock(var8 + 4, var9 + 5, var10 + 4, 5);
                var1.setBlock(var8 + 4, var9 + 5, var10 + 5, 5);
                var1.setBlock(var8 + 4, var9 + 5, var10 + 6, 5);
                var1.setBlock(var8 + 1, var9 + 5, var10 + 4, 5);
                var1.setBlock(var8 + 1, var9 + 5, var10 + 5, 5);
                var1.setBlock(var8 + 1, var9 + 5, var10 + 6, 5);
                var1.setBlock(var8 + 2, var9 + 4, var10 + 6, 5);
                var1.setBlock(var8 + 2, var9 + 4, var10 + 4, 5);
                var1.setBlock(var8 + 2, var9 + 5, var10 + 6, 5);
                var1.setBlock(var8 + 2, var9 + 5, var10 + 4, 5);
                var1.setBlock(var8 + 2, var9 + 5, var10 + 4, 0);
                var1.setBlock(var8 + 2, var9 + 4, var10 + 4, 0);
                var1.setBlock(var8 + 2, var9 + 5, var10 + 6, 0);
                var1.setBlock(var8 + 2, var9 + 4, var10 + 6, 0);
                var1.setBlock(var8 + 2, var9 + 2, var10 + 5, 5);
                var1.setBlock(var8 + 2, var9 + 1, var10 + 5, 5);
                var1.setBlock(var8 + 2, var9 + 0, var10 + 5, 5);
                var1.setBlock(var8, var9 + 3, var10, 126);
                var1.setBlock(var8, var9 + 3, var10 + 10, 126);
                var1.setBlock(var8 + 5, var9 + 3, var10, 126);
                var1.setBlock(var8 + 5, var9 + 3, var10 + 10, 126);

                for (y = var9 + 6; y < var9 + 7; ++y)
                {
                    for (x = 1; x < 5; ++x)
                    {
                        for (z = 3; z < 8; ++z)
                        {
                            var1.setBlock(var8 + x, y, var10 + z, 5);
                        }
                    }
                }

                for (y = var9 + 7; y < var9 + 8; ++y)
                {
                    for (x = 1; x < 5; ++x)
                    {
                        for (z = 3; z < 8; ++z)
                        {
                            var1.setBlock(var8 + x, y, var10 + z, 126);
                        }
                    }
                }

                for (y = var9 + 7; y < var9 + 8; ++y)
                {
                    for (x = 2; x < 4; ++x)
                    {
                        for (z = 4; z < 7; ++z)
                        {
                            var1.setBlock(var8 + x, y, var10 + z, 0);
                        }
                    }
                }

                var1.setBlock(var8 + 1, var9 + 7, var10 + 3, 5);
                var1.setBlock(var8 + 1, var9 + 7, var10 + 7, 5);
                var1.setBlock(var8 + 4, var9 + 7, var10 + 3, 5);
                var1.setBlock(var8 + 4, var9 + 7, var10 + 7, 5);
                var1.setBlock(var8 + 2, var9 + 6, var10 + 6, ModCastleDefenders.blockArcherM.blockID);
                var1.setBlock(var8 + 3, var9 + 6, var10 + 5, Block.ladder.blockID, 1 << Direction.facingToDirection[Facing.oppositeSide[5]], 2);
                var1.setBlock(var8 + 3, var9 + 5, var10 + 5, Block.ladder.blockID, 1 << Direction.facingToDirection[Facing.oppositeSide[5]], 2);
                var1.setBlock(var8 + 3, var9 + 4, var10 + 5, Block.ladder.blockID, 1 << Direction.facingToDirection[Facing.oppositeSide[5]], 2);
                var1.setBlock(var8 + 3, var9 + 3, var10 + 5, Block.ladder.blockID, 1 << Direction.facingToDirection[Facing.oppositeSide[5]], 2);
                var1.setBlock(var8 + 3, var9 + 2, var10 + 5, Block.ladder.blockID, 1 << Direction.facingToDirection[Facing.oppositeSide[5]], 2);
                var1.setBlock(var8 + 3, var9 + 1, var10 + 5, Block.ladder.blockID, 1 << Direction.facingToDirection[Facing.oppositeSide[5]], 2);
                var1.setBlock(var8 + 3, var9 + 0, var10 + 5, Block.ladder.blockID, 1 << Direction.facingToDirection[Facing.oppositeSide[5]], 2);
            }
        }
	
}