public class Activity extends Action
{
    public Activity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        super(entity, world, imageStore, 0);
    }

    public void executeAction(EventScheduler scheduler) {

        if(entity instanceof MinerFull)
        {
            ((MinerFull)entity).executeActivity(this.world,
                    this.imageStore, scheduler);
        }
        if(entity instanceof MinerNotFull)
        {

            ((MinerNotFull)entity).executeActivity(this.world,
                    this.imageStore, scheduler);
        }
        if(entity instanceof Ore)
        {
            ((Ore)entity).executeActivity(this.world,
                    this.imageStore, scheduler);
        }
        if(entity instanceof OreBlob)
        {
            ((OreBlob)entity).executeActivity(this.world,
                    this.imageStore, scheduler);
        }
        if(entity instanceof Quake)
        {
            ((Quake)entity).executeActivity(this.world,
                    this.imageStore, scheduler);
        }
        if(entity instanceof Vein)
        {
            ((Vein)entity).executeActivity(this.world,
                    this.imageStore, scheduler);
        }
    }
}