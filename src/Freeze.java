import processing.core.PImage;
import java.util.List;

public class Freeze extends Animate
{
    public static final String FREEZE_ID = "freeze";
    public static final int FREEZE_ACTION_PERIOD = 5000;
    public static final int FREEZE_ANIMATION_PERIOD = 1;
    public static final int FREEZE_ANIMATION_REPEAT_COUNT = 999;

    public Freeze(String id, Point position,
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
        scheduler.unscheduleAllEvents( this);
        world.removeEntity(this);
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this,
                ImageStore.createActivityAction(this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent( this, Factory.createAnimation(this,
                FREEZE_ANIMATION_REPEAT_COUNT),
                this.getAnimationPeriod());
    }
}
