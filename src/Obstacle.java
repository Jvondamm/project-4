import processing.core.PImage;
import java.util.List;

public class Obstacle extends Entity
{
    public Obstacle(String id, Point position,
                    List<PImage> images,
                    int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }
}
