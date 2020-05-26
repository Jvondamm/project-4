public class Animation extends Action
{

    public Animation(Animate entity, int repeatCount)
    {
        super(entity, null, null, repeatCount);
    }

    public void executeAction(EventScheduler scheduler)
    {
        ((Animate)entity).nextImage();
        if (this.repeatCount != 1)
        {
            scheduler.scheduleEvent(entity, Factory.createAnimation((Animate)entity, Math.max(this.repeatCount - 1, 0) ), ((Animate)entity).getAnimationPeriod());
        }
    }
}