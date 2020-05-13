public class Animation implements Action
{
    private Entity entity;
    private int repeatCount;

    public Animation(Animate entity, int repeatCount)
    {
        this.entity = entity;
        this.repeatCount = repeatCount;
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
