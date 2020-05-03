import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public final class Entity
{
    public EntityKind kind;
    public String id;
    public Point position;
    public List<PImage> images;
    public int imageIndex;
    public int resourceLimit;
    public int resourceCount;
    public int actionPeriod;
    public int animationPeriod;

    public Entity(
            EntityKind kind,
            String id,
            Point position,
            List<PImage> images,
            int resourceLimit,
            int resourceCount,
            int actionPeriod,
            int animationPeriod)
    {
        this.kind = kind;
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public static PImage getCurrentImage(Object entity) {
        if (entity instanceof Entity) {
            return ((Entity)entity).images.get(((Entity)entity).imageIndex);
        }
        else {
            throw new UnsupportedOperationException(
                    String.format("getCurrentImage not supported for %s",
                            entity));
        }
    }

    public static Action createAnimationAction(Entity entity, int repeatCount) {
        return new Action(ActionKind.ANIMATION, entity, null, null,
                          repeatCount);
    }

    public static void scheduleActions(
            Entity entity,
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
        switch (entity.kind) {
            case MINER_FULL:
                EventScheduler.scheduleEvent(scheduler, entity,
                        ImageStore.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                EventScheduler.scheduleEvent(scheduler, entity,
                        createAnimationAction(entity, 0),
                        entity.getAnimationPeriod());
                break;

            case MINER_NOT_FULL:
                EventScheduler.scheduleEvent(scheduler, entity,
                        ImageStore.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                EventScheduler.scheduleEvent(scheduler, entity,
                        createAnimationAction(entity, 0),
                        entity.getAnimationPeriod());
                break;

            case ORE:
                EventScheduler.scheduleEvent(scheduler, entity,
                        ImageStore.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                break;

            case ORE_BLOB:
                EventScheduler.scheduleEvent(scheduler, entity,
                        ImageStore.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                EventScheduler.scheduleEvent(scheduler, entity,
                        createAnimationAction(entity, 0),
                        entity.getAnimationPeriod());
                break;

            case QUAKE:
                EventScheduler.scheduleEvent(scheduler, entity,
                        ImageStore.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                EventScheduler.scheduleEvent(scheduler, entity, createAnimationAction(entity,
                        Functions.QUAKE_ANIMATION_REPEAT_COUNT),
                        entity.getAnimationPeriod());
                break;

            case VEIN:
                EventScheduler.scheduleEvent(scheduler, entity,
                        ImageStore.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                break;

            default:
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

        Entity blob = Functions.createOreBlob(entity.id + Functions.BLOB_ID_SUFFIX, pos,
                                    entity.actionPeriod / Functions.BLOB_PERIOD_SCALE,
                                    Functions.BLOB_ANIMATION_MIN + Functions.rand.nextInt(
                                            Functions.BLOB_ANIMATION_MAX
                                                    - Functions.BLOB_ANIMATION_MIN),
                                    ImageStore.getImageList(imageStore, Functions.BLOB_KEY));

        WorldModel.addEntity(world, blob);
        scheduleActions(blob, scheduler, world, imageStore);
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

            if (Functions.moveToOreBlob(entity, world, blobTarget.get(), scheduler)) {
                Entity quake = Functions.createQuake(tgtPos,
                                           ImageStore.getImageList(imageStore, Functions.QUAKE_KEY));

                WorldModel.addEntity(world, quake);
                nextPeriod += entity.actionPeriod;
                scheduleActions(quake, scheduler, world, imageStore);
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
            Entity ore = Functions.createOre(Functions.ORE_ID_PREFIX + entity.id, openPt.get(),
                                   Functions.ORE_CORRUPT_MIN + Functions.rand.nextInt(
                                           Functions.ORE_CORRUPT_MAX - Functions.ORE_CORRUPT_MIN),
                                   ImageStore.getImageList(imageStore, Functions.ORE_KEY));
            WorldModel.addEntity(world, ore);
            scheduleActions(ore, scheduler, world, imageStore);
        }

        EventScheduler.scheduleEvent(scheduler, entity,
                      ImageStore.createActivityAction(entity, world, imageStore),
                      entity.actionPeriod);
    }

    public int getAnimationPeriod() {
        switch (kind) {
            case MINER_FULL:
            case MINER_NOT_FULL:
            case ORE_BLOB:
            case QUAKE:
                return animationPeriod;
            default:
                throw new UnsupportedOperationException(
                        String.format("getAnimationPeriod not supported for %s",
                                kind));
        }
    }

    public void nextImage() {
        imageIndex = (imageIndex + 1) % images.size();
    }

    public boolean transformNotFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (resourceCount >= resourceLimit) {
            Entity miner = Functions.createMinerFull(id, resourceLimit,
                    position, actionPeriod,
                    animationPeriod,
                    images);

            Event.removeEntity(world, this);
            EventScheduler.unscheduleAllEvents(scheduler, this);

            WorldModel.addEntity(world, miner);
            scheduleActions(miner, scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public void transformFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        Entity miner = Functions.createMinerNotFull(id, resourceLimit,
                position, actionPeriod,
                animationPeriod,
                images);

        Event.removeEntity(world, this);
        EventScheduler.unscheduleAllEvents(scheduler, this);

        WorldModel.addEntity(world, miner);
        scheduleActions(miner, scheduler, world, imageStore);
    }

    public boolean moveToNotFull(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (Point.adjacent(position, target.position)) {
            resourceCount += 1;
            Event.removeEntity(world, target);
            EventScheduler.unscheduleAllEvents(scheduler, target);

            return true;
        }
        else {
            Point nextPos = nextPositionMiner(world, target.position);

            if (!position.equals(nextPos)) {
                Optional<Entity> occupant = WorldModel.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    EventScheduler.unscheduleAllEvents(scheduler, occupant.get());
                }

                Event.moveEntity(world, this, nextPos);
            }
            return false;
        }
    }

    public boolean moveToFull(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (Point.adjacent(position, target.position)) {
            return true;
        }
        else {
            Point nextPos = nextPositionMiner(world, target.position);

            if (!position.equals(nextPos)) {
                Optional<Entity> occupant = WorldModel.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    EventScheduler.unscheduleAllEvents(scheduler, occupant.get());
                }

                Event.moveEntity(world, this, nextPos);
            }
            return false;
        }
    }

    public Point nextPositionMiner(WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - position.x);
        Point newPos = new Point(position.x + horiz, position.y);

        if (horiz == 0 || WorldModel.isOccupied(world, newPos)) {
            int vert = Integer.signum(destPos.y - position.y);
            newPos = new Point(position.x, position.y + vert);

            if (vert == 0 || WorldModel.isOccupied(world, newPos)) {
                newPos = position;
            }
        }

        return newPos;
    }

    public Point nextPositionOreBlob(WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - position.x);
        Point newPos = new Point(position.x + horiz, position.y);

        Optional<Entity> occupant = WorldModel.getOccupant(world, newPos);

        if (horiz == 0 || (occupant.isPresent() && !(occupant.get().kind
                == EntityKind.ORE)))
        {
            int vert = Integer.signum(destPos.y - position.y);
            newPos = new Point(position.x, position.y + vert);
            occupant = WorldModel.getOccupant(world, newPos);

            if (vert == 0 || (occupant.isPresent() && !(occupant.get().kind
                    == EntityKind.ORE)))
            {
                newPos = position;
            }
        }

        return newPos;
    }
}
