import java.util.List;
import processing.core.PImage;

abstract public class Entity
{
    protected String id;
    protected Point position;
    protected List<PImage> images;
    protected int imageIndex;
    protected int resourceLimit;
    protected int resourceCount;
    protected int actionPeriod;
    protected int animationPeriod;

    public Entity(String id, Point position,
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
    public Point getPosition() { return this.position; }
    public void setPosition(Point p) { this.position = p; }

    public PImage getCurrentImage()
    {
        return (this.getImages()).get(this.getImageIndex());
    }
}