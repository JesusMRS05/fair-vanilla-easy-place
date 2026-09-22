package com.github.jesusmrs05.client.provider;

import com.github.jesusmrs05.client.target.SchematicTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class TargetProvider {

    private final Minecraft client;
    private final LitematicaProvider litematicaProvider;
    private final ReachProvider reachProvider;

    public TargetProvider(
            Minecraft client,
            LitematicaProvider litematicaProvider,
            ReachProvider reachProvider
    ) {
        this.client = client;
        this.litematicaProvider = litematicaProvider;
        this.reachProvider = reachProvider;
    }

    public SchematicTarget findTarget() {
        if (client == null || client.player == null || client.level == null) {
            return null;
        }

        LocalPlayer player = client.player;

        Vec3 start = player.getEyePosition(1.0F);
        double reach = reachProvider.getBlockInteractionRange();

        HitResult result = player.pick(reach, 1.0F, false);

        if (!(result instanceof BlockHitResult hitResult)
                || result.getType() != HitResult.Type.BLOCK) {
            return null;
        }

        BlockPos ghostPos = findFurthestGhost(
                start,
                hitResult.getLocation()
        );

        if (ghostPos == null) {
            return null;
        }

        return new SchematicTarget(
                ghostPos,
                hitResult
        );
    }

    private BlockPos findFurthestGhost(Vec3 start, Vec3 end) {
        Vec3 direction = end.subtract(start);

        double length = direction.length();

        if (length <= 0.0D) {
            return null;
        }

        direction = direction.scale(1.0D / length);

        double x = start.x;
        double y = start.y;
        double z = start.z;

        int blockX = floor(x);
        int blockY = floor(y);
        int blockZ = floor(z);

        int stepX = direction.x > 0.0D ? 1 : -1;
        int stepY = direction.y > 0.0D ? 1 : -1;
        int stepZ = direction.z > 0.0D ? 1 : -1;

        double tMaxX = firstBoundary(x, blockX, direction.x);
        double tMaxY = firstBoundary(y, blockY, direction.y);
        double tMaxZ = firstBoundary(z, blockZ, direction.z);

        double tDeltaX = direction.x == 0.0D
                ? Double.POSITIVE_INFINITY
                : Math.abs(1.0D / direction.x);

        double tDeltaY = direction.y == 0.0D
                ? Double.POSITIVE_INFINITY
                : Math.abs(1.0D / direction.y);

        double tDeltaZ = direction.z == 0.0D
                ? Double.POSITIVE_INFINITY
                : Math.abs(1.0D / direction.z);

        BlockPos furthestGhost = null;

        double currentT = 0.0D;
        double maxT = length;

        while (currentT <= maxT) {
            BlockPos currentPos = new BlockPos(
                    blockX,
                    blockY,
                    blockZ
            );

            if (currentT > 0.0D
                    && litematicaProvider.hasSchematicBlock(currentPos)) {
                furthestGhost = currentPos;
            }

            double nextT = Math.min(
                    tMaxX,
                    Math.min(tMaxY, tMaxZ)
            );

            if (nextT > maxT) {
                break;
            }

            if (tMaxX <= tMaxY && tMaxX <= tMaxZ) {
                blockX += stepX;
                tMaxX += tDeltaX;
            } else if (tMaxY <= tMaxZ) {
                blockY += stepY;
                tMaxY += tDeltaY;
            } else {
                blockZ += stepZ;
                tMaxZ += tDeltaZ;
            }

            currentT = nextT;
        }

        return furthestGhost;
    }

    private static double firstBoundary(
            double coordinate,
            int blockCoordinate,
            double direction
    ) {
        if (direction == 0.0D) {
            return Double.POSITIVE_INFINITY;
        }

        if (direction > 0.0D) {
            return (blockCoordinate + 1.0D - coordinate) / direction;
        }

        return (blockCoordinate - coordinate) / direction;
    }

    private static int floor(double value) {
        return (int) Math.floor(value);
    }
}