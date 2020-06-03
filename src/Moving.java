import processing.core.PImage;

import java.util.List;

abstract public class Moving extends Animate
{
    public Moving(String id, Point position,
                   List<PImage> images,
                   int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public Point nextPosition(WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.getX() - position.getX());
        Point newPos = new Point(position.getX() + horiz, position.getY());

        if (horiz == 0 || world.isOccupied(newPos)) {
            int vert = Integer.signum(destPos.getY() - position.getY());
            newPos = new Point(position.getX(), position.getY() + vert);

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
                Factory.createAnimation(this, 0),
                this.getAnimationPeriod());
    }

    public abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
}
