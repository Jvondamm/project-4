import processing.core.PImage;
import java.util.List;
import java.util.Optional;

public class Vein implements Scheduled
{
    public static final String ORE_KEY = "ore";
    public static final String ORE_ID_PREFIX = "ore -- ";
    public static final int ORE_CORRUPT_MIN = 20000;
    public static final int ORE_CORRUPT_MAX = 30000;

    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod;
    private String id;

    public Vein(String id, Point position,
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

    public static Vein createVein(
            String id, Point position, int actionPeriod, List<PImage> images)
    {
        return new Vein(id, position, images, 0, 0,
                actionPeriod, 0);
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Point> openPt = this.position.findOpenAround(world);

        if (openPt.isPresent()) {
            Ore ore = Ore.createOre(ORE_ID_PREFIX + this.id, openPt.get(),
                    ORE_CORRUPT_MIN + Functions.rand.nextInt(
                            ORE_CORRUPT_MAX - ORE_CORRUPT_MIN),
                    ImageStore.getImageList(imageStore, ORE_KEY));
            world.addEntity(ore);
            ore.scheduleActions(scheduler, world, imageStore);
        }

        scheduler.scheduleEvent( this,
                ImageStore.createActivityAction(this, world, imageStore),
                this.actionPeriod);
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
