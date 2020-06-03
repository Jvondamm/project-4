import processing.core.PImage;

import java.util.List;

abstract public class Scheduled extends Entity
{
    public Scheduled(String id, Point position,
                List<PImage> images,
                int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this,
                ImageStore.createActivityAction(this, world, imageStore),
                this.actionPeriod);
    }
}
