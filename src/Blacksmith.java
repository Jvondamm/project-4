import processing.core.PImage;
import java.util.List;

public class Blacksmith extends Entity
{
    public Blacksmith(String id, Point position,
                      List<PImage> images, int resourceLimit, int resourceCount,
                      int actionPeriod, int animationPeriod)
    {
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod);
    }
}
