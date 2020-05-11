import processing.core.PImage;
import java.util.List;

public class Obstacle implements Entity
{
    private String id;
    public Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod;

    public Obstacle(String id, Point position,
                    List<PImage> images, int resourceLimit, int resourceCount,
                    int actionPeriod, int animationPeriod)
    {
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

    public static Obstacle createObstacle(String id, Point position,
                                          List<PImage> images)
    {
        return new Obstacle( id, position, images,
                0, 0, 0, 0);
    }

    public void nextImage()
    {
        imageIndex = (getImageIndex()+ 1) % getImages().size();
    }
}
