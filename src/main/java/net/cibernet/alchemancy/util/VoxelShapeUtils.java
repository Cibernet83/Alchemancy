package net.cibernet.alchemancy.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.TreeMap;

public class VoxelShapeUtils {

	public static TreeMap<Direction, VoxelShape> createDirectionMap(VoxelShape shape) {

		TreeMap<Direction, VoxelShape> result = new TreeMap<>();
		for (Direction direction : Direction.values()) {
			result.put(direction, rotate(shape, direction));
		}
		return result;
	}

	@SuppressWarnings("all")
	public static VoxelShape rotate(VoxelShape shape, Direction direction) {

		VoxelShape result = Shapes.empty();

		for (AABB part : shape.toAabbs()) {
			VoxelShape rotatedPart = switch (direction) {
				case UP -> Shapes.create(part);
				case DOWN -> Shapes.box(part.minX, 1 - part.maxY, part.minZ, part.maxX, 1 - part.minY, part.maxZ);
				case SOUTH -> Shapes.box(part.minX, part.minZ, part.minY, part.maxX, part.maxZ, part.maxY);
				case NORTH -> Shapes.box(part.minX, part.minZ, 1 - part.maxY, part.maxX, part.maxZ, 1 - part.minY);
				case EAST -> Shapes.box(part.minY, part.minX, part.minZ, part.maxY, part.maxX, part.maxZ);
				case WEST -> Shapes.box(1 - part.maxY, part.minX, part.minZ, 1 - part.minY, part.maxX, part.maxZ);
			};

			result = Shapes.or(result, rotatedPart);
		}
		return result;
	}
}
