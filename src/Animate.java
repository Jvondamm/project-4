import processing.core.PImage;

import java.util.List;

abstract public class Animate extends Scheduled
{
    public Animate(String id, Point position,
                     List<PImage> images, int resourceLimit, int resourceCount,
                     int actionPeriod, int animationPeriod)
    {
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod);
    }

    public int getAnimationPeriod() { return this.animationPeriod; }

    public void nextImage()
    {
        imageIndex = (getImageIndex()+ 1) % getImages().size();
    }
}
