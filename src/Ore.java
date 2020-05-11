import processing.core.PImage;
import java.util.List;

public class Ore implements Scheduled
{
    public static final String BLOB_KEY = "blob";
    public static final String BLOB_ID_SUFFIX = " -- blob";
    public static final int BLOB_PERIOD_SCALE = 4;
    public static final int BLOB_ANIMATION_MIN = 50;
    public static final int BLOB_ANIMATION_MAX = 150;

    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod;
    private String id;

    public Ore(String id, Point position,
               List<PImage> images, int resourceLimit, int resourceCount,
               int actionPeriod, int animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
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

    public static Ore createOre(
            String id, Point position, int actionPeriod, List<PImage> images)
    {
        return new Ore(id, position, images, 0, 0,
                actionPeriod, 0);
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Point pos = this.position;

        world.removeEntity(this);
        scheduler.unscheduleAllEvents( this);

        OreBlob blob = OreBlob.createOreBlob(this.id + BLOB_ID_SUFFIX, pos,
                this.actionPeriod / BLOB_PERIOD_SCALE,
                BLOB_ANIMATION_MIN + Functions.rand.nextInt(
                        BLOB_ANIMATION_MAX
                                - BLOB_ANIMATION_MIN),
                ImageStore.getImageList(imageStore, BLOB_KEY));

        world.addEntity(blob);
        blob.scheduleActions(scheduler, world, imageStore);
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this,
                ImageStore.createActivityAction(this, world, imageStore),
                this.actionPeriod);
    }

    public void nextImage()
    {
        imageIndex = (getImageIndex()+ 1) % getImages().size();
    }
}