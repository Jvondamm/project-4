import processing.core.PImage;
import java.util.List;

public class Blacksmith implements Entity
{
    private String id;
    public Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod;

    public PImage getCurrentImage()
    {
        return this.getImages().get((this).getImageIndex());

    }

    public Blacksmith(String id, Point position,
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

}
