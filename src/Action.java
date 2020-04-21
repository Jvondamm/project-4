public final class Action
{
    public ActionKind kind;
    public Entity entity;
    public WorldModel world;
    public ImageStore imageStore;
    public int repeatCount;

    public Action(
            ActionKind kind,
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        this.kind = kind;
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    public void executeAction(Action action, EventScheduler scheduler) {
        switch (kind) {
            case ACTIVITY:
                executeActivityAction(action, scheduler);
                break;

            case ANIMATION:
                executeAnimationAction(action, scheduler);
                break;
        }
    }

    public void executeAnimationAction(
            Action action, EventScheduler scheduler)
    {
        action.entity.nextImage();

        if (action.repeatCount != 1) {
            EventScheduler.scheduleEvent(scheduler, action.entity,
                    Entity.createAnimationAction(action.entity,
                            Math.max(action.repeatCount - 1,
                                    0)),
                    action.entity.getAnimationPeriod());
        }
    }

    public void executeActivityAction(
            Action action, EventScheduler scheduler)
    {
        switch (action.entity.kind) {
            case MINER_FULL:
                Functions.executeMinerFullActivity(action.entity, action.world,
                        action.imageStore, scheduler);
                break;

            case MINER_NOT_FULL:
                Functions.executeMinerNotFullActivity(action.entity, action.world,
                        action.imageStore, scheduler);
                break;

            case ORE:
                Functions.executeOreActivity(action.entity, action.world,
                        action.imageStore, scheduler);
                break;

            case ORE_BLOB:
                Functions.executeOreBlobActivity(action.entity, action.world,
                        action.imageStore, scheduler);
                break;

            case QUAKE:
                Functions.executeQuakeActivity(action.entity, action.world,
                        action.imageStore, scheduler);
                break;

            case VEIN:
                Functions.executeVeinActivity(action.entity, action.world,
                        action.imageStore, scheduler);
                break;

            default:
                throw new UnsupportedOperationException(String.format(
                        "executeActivityAction not supported for %s",
                        action.entity.kind));
        }
    }
}
