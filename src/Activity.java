public class Activity implements Action
{

    private Entity entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    public Activity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    public static Activity createActivity(Entity entity, WorldModel world, ImageStore imageStore)
    {
        return new Activity(entity, world, imageStore, 0);
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