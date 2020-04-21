public final class Event
{
    public Action action;
    public long time;
    public Entity entity;

    public Event(Action action, long time, Entity entity) {
        this.action = action;
        this.time = time;
        this.entity = entity;
    }

    /*
       Assumes that there is no entity currently occupying the
       intended destination cell.
    */
    public static void addEntity(WorldModel world, Entity entity) {
        if (WorldModel.withinBounds(world, entity.position)) {
            WorldModel.setOccupancyCell(world, entity.position, entity);
            world.entities.add(entity);
        }
    }

    public static void moveEntity(WorldModel world, Entity entity, Point pos) {
        Point oldPos = entity.position;
        if (WorldModel.withinBounds(world, pos) && !pos.equals(oldPos)) {
            WorldModel.setOccupancyCell(world, oldPos, null);
            removeEntityAt(world, pos);
            WorldModel.setOccupancyCell(world, pos, entity);
            entity.position = pos;
        }
    }

    public static void removeEntity(WorldModel world, Entity entity) {
        removeEntityAt(world, entity.position);
    }

    public static void removeEntityAt(WorldModel world, Point pos) {
        if (WorldModel.withinBounds(world, pos) && WorldModel.getOccupancyCell(world, pos) != null) {
            Entity entity = WorldModel.getOccupancyCell(world, pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.position = new Point(-1, -1);
            world.entities.remove(entity);
            WorldModel.setOccupancyCell(world, pos, null);
        }
    }
}
