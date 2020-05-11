import processing.core.PImage;
import java.util.List;
import java.util.Optional;

public class MinerFull implements Scheduled
{

    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod;
    private String id;

    public MinerFull(String id, Point position,
                     List<PImage> images, int resourceLimit, int resourceCount,
                     int actionPeriod, int animationPeriod)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public int getImageIndex() { return this.imageIndex; }
    public List<PImage> getImages() { return this.images; }
    public int getAnimationPeriod() { return this.animationPeriod; }
    public Point getPosition() { return this.position; }
    public void setPosition(Point p) { this.position = p; }

    public static MinerFull createMinerFull(
            String id,
            int resourceLimit,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new MinerFull(id, position, images,
                resourceLimit, resourceLimit, actionPeriod,
                animationPeriod);
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fullTarget =
                Point.findNearest(world, this.position, Blacksmith.class);

        if (fullTarget.isPresent() && this.moveTo(world,
                fullTarget.get(), scheduler))
        {
            this.transform(world, scheduler, imageStore);
        }
        else {
            scheduler.scheduleEvent(this,
                    ImageStore.createActivityAction(this, world, imageStore),
                    this.actionPeriod);
        }
    }

    public void transform(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        MinerNotFull miner = MinerNotFull.createMinerNotFull(id, resourceLimit,
                position, actionPeriod,
                animationPeriod,
                images);

        world.removeEntity(this);
        scheduler.unscheduleAllEvents( this);

        world.addEntity(miner);
        miner.scheduleActions(scheduler, world, imageStore);
    }

    public boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (Point.adjacent(position, target.getPosition())) {
            return true;
        }
        else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!position.equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    public Point nextPosition(WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - position.x);
        Point newPos = new Point(position.x + horiz, position.y);

        if (horiz == 0 || world.isOccupied(newPos)) {
            int vert = Integer.signum(destPos.y - position.y);
            newPos = new Point(position.x, position.y + vert);

            if (vert == 0 || world.isOccupied(newPos)) {
                newPos = position;
            }
        }

        return newPos;
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this,
                ImageStore.createActivityAction(this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent(this,
                Animation.createAnimation(this, 0),
                this.getAnimationPeriod());
    }

    public void nextImage()
    {
        imageIndex = (getImageIndex()+ 1) % getImages().size();
    }
}
