package com.github.jesusmrs05.client.target;

import com.github.jesusmrs05.client.litematica.LitematicaBridge;
import com.github.jesusmrs05.client.placement.MaxReachProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class SchematicTargetFinder {

    private SchematicTargetFinder() {
    }

    public static SchematicTarget findTarget(
            Minecraft client,
            LitematicaBridge bridge
    ) {
        if (client == null
                || client.level == null
                || client.player == null
                || bridge == null) {
            return null;
        }

        LocalPlayer player = client.player;

        Vec3 eyePosition =
                player.getEyePosition(1.0F);

        double maxReach = MaxReachProvider.get(client);

        HitResult hitResult =
                player.pick(maxReach, 1.0F, false);

        if (!(hitResult instanceof BlockHitResult blockHitResult)
                || hitResult.getType() != HitResult.Type.BLOCK) {
            return null;
        }

        BlockPos ghostPos = findFurthestGhost(
                eyePosition,
                blockHitResult.getLocation(),
                bridge
        );

        if (ghostPos == null) {
            return null;
        }

        return new SchematicTarget(
                ghostPos,
                blockHitResult
        );
    }

    private static BlockPos findFurthestGhost(
            Vec3 start,
            Vec3 end,
            LitematicaBridge bridge
    ) {
        int x = floor(start.x);
        int y = floor(start.y);
        int z = floor(start.z);

        int endX = floor(end.x);
        int endY = floor(end.y);
        int endZ = floor(end.z);

        int stepX = Integer.compare(endX, x);
        int stepY = Integer.compare(endY, y);
        int stepZ = Integer.compare(endZ, z);

        double deltaX = end.x - start.x;
        double deltaY = end.y - start.y;
        double deltaZ = end.z - start.z;

        double tDeltaX = stepX == 0
                ? Double.POSITIVE_INFINITY
                : 1.0D / Math.abs(deltaX);

        double tDeltaY = stepY == 0
                ? Double.POSITIVE_INFINITY
                : 1.0D / Math.abs(deltaY);

        double tDeltaZ = stepZ == 0
                ? Double.POSITIVE_INFINITY
                : 1.0D / Math.abs(deltaZ);

        double tMaxX =
                firstBoundary(start.x, deltaX, x, stepX);

        double tMaxY =
                firstBoundary(start.y, deltaY, y, stepY);

        double tMaxZ =
                firstBoundary(start.z, deltaZ, z, stepZ);

        BlockPos furthestGhost = null;

        BlockPos current = new BlockPos(x, y, z);

        if (bridge.isSchematicGhost(current)) {
            furthestGhost = current;
        }

        while (x != endX || y != endY || z != endZ) {

            if (tMaxX < tMaxY && tMaxX < tMaxZ) {
                x += stepX;
                tMaxX += tDeltaX;
            } else if (tMaxY < tMaxZ) {
                y += stepY;
                tMaxY += tDeltaY;
            } else {
                z += stepZ;
                tMaxZ += tDeltaZ;
            }

            if (x == endX
                    && y == endY
                    && z == endZ) {
                break;
            }

            current = new BlockPos(x, y, z);

            if (bridge.isSchematicGhost(current)) {
                furthestGhost = current;
            }
        }

        return furthestGhost;
    }

    private static double firstBoundary(
            double coordinate,
            double delta,
            int blockCoordinate,
            int step
    ) {
        if (step == 0 || delta == 0.0D) {
            return Double.POSITIVE_INFINITY;
        }

        double boundary = step > 0
                ? blockCoordinate + 1.0D
                : blockCoordinate;

        return (boundary - coordinate) / delta;
    }

    private static int floor(double value) {
        return (int) Math.floor(value);
    }
}