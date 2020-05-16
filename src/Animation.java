public class Animation extends Action
{

    public Animation(Animate entity, int repeatCount)
    {
        super(entity, null, null, repeatCount);
    }

    public static Animation createAnimation(Animate entity, int repeatCount)
    {
        return new Animation(entity, repeatCount);
    }

    public void executeAction(EventScheduler scheduler)
    {
        if (entity instanceof Animate)
        {
            ((Animate)entity).nextImage();
        }
        if (this.repeatCount != 1)
        {
            scheduler.scheduleEvent(this.entity, createAnimation((Animate)entity, Math.max(this.repeatCount - 1, 0) ), ((Animate)entity).getAnimationPeriod());
        }
    }
}
