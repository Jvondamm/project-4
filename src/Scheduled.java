public interface Scheduled extends Entity
{
    void  scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
}
