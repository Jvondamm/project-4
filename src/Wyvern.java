import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Wyvern extends Moving
{

    public Wyvern(String id, Point position,
                  List<PImage> images,
                  int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> blacksmithTarget =
                WorldModel.findNearest(world, this.position, Blacksmith.class);
        long nextPeriod = this.actionPeriod;

        if (blacksmithTarget.isPresent()) {
            Point tgtPos = blacksmithTarget.get().getPosition();

            if (moveTo(world, blacksmithTarget.get(), scheduler)) {

                Freeze freeze = Factory.createFreeze(tgtPos,
                        imageStore.getImageList(imageStore, Functions.FREEZE_KEY));

                world.addEntity(freeze);
                nextPeriod += this.actionPeriod;
                freeze.scheduleActions(scheduler, world, imageStore);

                world.removeEntity(this);
                scheduler.unscheduleAllEvents( this);
            }
        }

        scheduler.scheduleEvent( this,
                ImageStore.createActivityAction(this, world, imageStore),
                nextPeriod);
    }


    public boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (Point.adjacent(position, target.getPosition())) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!position.equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    public Point nextPosition(WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.getX() - position.getX());
        Point newPos = new Point(position.getX() + horiz, position.getY());

        Optional<Entity> occupant = world.getOccupant(newPos);

        if (horiz == 0 || (occupant.isPresent() && !(occupant.get().getClass()
                == Blacksmith.class)))
        {
            int vert = Integer.signum(destPos.getY() - position.getY());
            newPos = new Point(position.getX(), position.getY() + vert);
            occupant = world.getOccupant(newPos);

            if (vert == 0 || (occupant.isPresent() && !(occupant.get().getClass()
                    == Blacksmith.class)))
            {
                newPos = position;
            }
        }

        return newPos;
    }
}
