import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public final class Point
{
    public final int x;
    public final int y;
    public static final int ORE_REACH = 1;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Optional<Entity> nearestEntity(
            List<Entity> entities, Point pos)
    {
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        else {
            Entity nearest = entities.get(0);
            int nearestDistance = distanceSquared(nearest.position, pos);

            for (Entity other : entities) {
                int otherDistance = distanceSquared(other.position, pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    public static int distanceSquared(Point p1, Point p2) {
        int deltaX = p1.x - p2.x;
        int deltaY = p1.y - p2.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    public static Optional<Entity> findNearest(
            WorldModel world, Point pos, EntityKind kind)
    {
        List<Entity> ofType = new LinkedList<>();
        for (Entity entity : world.entities) {
            if (entity.kind == kind) {
                ofType.add(entity);
            }
        }

        return nearestEntity(ofType, pos);
    }

    public static Optional<PImage> getBackgroundImage(
            WorldModel world, Point pos)
    {
        if (WorldModel.withinBounds(world, pos)) {
            return Optional.of(Functions.getCurrentImage(WorldModel.getBackgroundCell(world, pos)));
        }
        else {
            return Optional.empty();
        }
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean equals(Object other) {
        return other instanceof Point && ((Point)other).x == this.x
                && ((Point)other).y == this.y;
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + x;
        result = result * 31 + y;
        return result;
    }

    public static boolean adjacent(Point p1, Point p2) {
        return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) || (p1.y == p2.y
                && Math.abs(p1.x - p2.x) == 1);
    }

    public Optional<Point> findOpenAround(WorldModel world) {
        for (int dy = -ORE_REACH; dy <= ORE_REACH; dy++) {
            for (int dx = -ORE_REACH; dx <= ORE_REACH; dx++) {
                Point newPt = new Point(x + dx, y + dy);
                if (WorldModel.withinBounds(world, newPt) && !WorldModel.isOccupied(world, newPt)) {
                    return Optional.of(newPt);
                }
            }
        }

        return Optional.empty();
    }
}
