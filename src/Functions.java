import java.util.Optional;
import java.util.Random;

import processing.core.PImage;

public final class Functions
{
    public static final Random rand = new Random();

    public static final String BLOB_KEY = "blob";
    public static final String BLOB_ID_SUFFIX = " -- blob";
    public static final int BLOB_PERIOD_SCALE = 4;
    public static final int BLOB_ANIMATION_MIN = 50;
    public static final int BLOB_ANIMATION_MAX = 150;

    public static final String ORE_ID_PREFIX = "ore -- ";
    public static final int ORE_CORRUPT_MIN = 20000;
    public static final int ORE_CORRUPT_MAX = 30000;

    public static final String QUAKE_KEY = "quake";
    public static final String QUAKE_ID = "quake";
    public static final int QUAKE_ACTION_PERIOD = 1100;
    public static final int QUAKE_ANIMATION_PERIOD = 100;
    public static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

    public static final int COLOR_MASK = 0xffffff;
    public static final int KEYED_IMAGE_MIN = 5;

    public static final int PROPERTY_KEY = 0;

    public static final String BGND_KEY = "background";
    public static final int BGND_NUM_PROPERTIES = 4;
    public static final int BGND_ID = 1;
    public static final int BGND_COL = 2;
    public static final int BGND_ROW = 3;

    public static final String MINER_KEY = "miner";
    public static final int MINER_NUM_PROPERTIES = 7;
    public static final int MINER_ID = 1;
    public static final int MINER_COL = 2;
    public static final int MINER_ROW = 3;
    public static final int MINER_LIMIT = 4;
    public static final int MINER_ACTION_PERIOD = 5;
    public static final int MINER_ANIMATION_PERIOD = 6;

    public static final String OBSTACLE_KEY = "obstacle";
    public static final int OBSTACLE_NUM_PROPERTIES = 4;
    public static final int OBSTACLE_ID = 1;
    public static final int OBSTACLE_COL = 2;
    public static final int OBSTACLE_ROW = 3;

    public static final String ORE_KEY = "ore";
    public static final int ORE_NUM_PROPERTIES = 5;
    public static final int ORE_ID = 1;
    public static final int ORE_COL = 2;
    public static final int ORE_ROW = 3;
    public static final int ORE_ACTION_PERIOD = 4;

    public static final String SMITH_KEY = "blacksmith";
    public static final int SMITH_NUM_PROPERTIES = 4;
    public static final int SMITH_ID = 1;
    public static final int SMITH_COL = 2;
    public static final int SMITH_ROW = 3;

    public static final String VEIN_KEY = "vein";
    public static final int VEIN_NUM_PROPERTIES = 5;
    public static final int VEIN_ID = 1;
    public static final int VEIN_COL = 2;
    public static final int VEIN_ROW = 3;
    public static final int VEIN_ACTION_PERIOD = 4;


    public static PImage getCurrentImage(Object entity) {
        if (entity instanceof Background) {
            return ((Background)entity).images.get(
                    ((Background)entity).imageIndex);
        }
        else if (entity instanceof Entity) {
            return ((Entity)entity).images.get(((Entity)entity).imageIndex);
        }
        else {
            throw new UnsupportedOperationException(
                    String.format("getCurrentImage not supported for %s",
                                  entity));
        }
    }

    public static void executeMinerFullActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fullTarget =
                Point.findNearest(world, entity.position, EntityKind.BLACKSMITH);

        if (fullTarget.isPresent() && entity.moveToFull(world,
                                                 fullTarget.get(), scheduler))
        {
            entity.transformFull(world, scheduler, imageStore);
        }
        else {
            EventScheduler.scheduleEvent(scheduler, entity,
                          ImageStore.createActivityAction(entity, world, imageStore),
                          entity.actionPeriod);
        }
    }

    public static void executeMinerNotFullActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> notFullTarget =
                Point.findNearest(world, entity.position, EntityKind.ORE);

        if (!notFullTarget.isPresent() || !entity.moveToNotFull(world,
                                                         notFullTarget.get(),
                                                         scheduler)
                || !entity.transformNotFull(world, scheduler, imageStore))
        {
            EventScheduler.scheduleEvent(scheduler, entity,
                          ImageStore.createActivityAction(entity, world, imageStore),
                          entity.actionPeriod);
        }
    }

    public static void executeOreActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Point pos = entity.position;

        Event.removeEntity(world, entity);
        EventScheduler.unscheduleAllEvents(scheduler, entity);

        Entity blob = Entity.createOreBlob(entity.id + BLOB_ID_SUFFIX, pos,
                                    entity.actionPeriod / BLOB_PERIOD_SCALE,
                                    BLOB_ANIMATION_MIN + rand.nextInt(
                                            BLOB_ANIMATION_MAX
                                                    - BLOB_ANIMATION_MIN),
                                    ImageStore.getImageList(imageStore, BLOB_KEY));

        Event.addEntity(world, blob);
        EventScheduler.scheduleActions(blob, scheduler, world, imageStore);
    }

    public static void executeOreBlobActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> blobTarget =
                Point.findNearest(world, entity.position, EntityKind.VEIN);
        long nextPeriod = entity.actionPeriod;

        if (blobTarget.isPresent()) {
            Point tgtPos = blobTarget.get().position;

            if (moveToOreBlob(entity, world, blobTarget.get(), scheduler)) {
                Entity quake = Entity.createQuake(tgtPos,
                                           ImageStore.getImageList(imageStore, QUAKE_KEY));

                Event.addEntity(world, quake);
                nextPeriod += entity.actionPeriod;
                EventScheduler.scheduleActions(quake, scheduler, world, imageStore);
            }
        }

        EventScheduler.scheduleEvent(scheduler, entity,
                      ImageStore.createActivityAction(entity, world, imageStore),
                      nextPeriod);
    }

    public static void executeQuakeActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        EventScheduler.unscheduleAllEvents(scheduler, entity);
        Event.removeEntity(world, entity);
    }

    public static void executeVeinActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Point> openPt = entity.position.findOpenAround(world);

        if (openPt.isPresent()) {
            Entity ore = Entity.createOre(ORE_ID_PREFIX + entity.id, openPt.get(),
                                   ORE_CORRUPT_MIN + rand.nextInt(
                                           ORE_CORRUPT_MAX - ORE_CORRUPT_MIN),
                                   ImageStore.getImageList(imageStore, ORE_KEY));
            Event.addEntity(world, ore);
            EventScheduler.scheduleActions(ore, scheduler, world, imageStore);
        }

        EventScheduler.scheduleEvent(scheduler, entity,
                      ImageStore.createActivityAction(entity, world, imageStore),
                      entity.actionPeriod);
    }

    public static boolean moveToOreBlob(
            Entity blob,
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (Point.adjacent(blob.position, target.position)) {
            Event.removeEntity(world, target);
            EventScheduler.unscheduleAllEvents(scheduler, target);
            return true;
        }
        else {
            Point nextPos = blob.nextPositionOreBlob(world, target.position);

            if (!blob.position.equals(nextPos)) {
                Optional<Entity> occupant = WorldModel.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    EventScheduler.unscheduleAllEvents(scheduler, occupant.get());
                }

                Event.moveEntity(world, blob, nextPos);
            }
            return false;
        }
    }
}
