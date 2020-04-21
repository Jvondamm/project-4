import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.LinkedList;

public final class EventScheduler
{
    public PriorityQueue<Event> eventQueue;
    public Map<Entity, List<Event>> pendingEvents;
    public double timeScale;

    public EventScheduler(double timeScale) {
        this.eventQueue = new PriorityQueue<>(new EventComparator());
        this.pendingEvents = new HashMap<>();
        this.timeScale = timeScale;
    }
    public static void scheduleActions(
            Entity entity,
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
        switch (entity.kind) {
            case MINER_FULL:
                scheduleEvent(scheduler, entity,
                        ImageStore.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                scheduleEvent(scheduler, entity,
                        Entity.createAnimationAction(entity, 0),
                        entity.getAnimationPeriod());
                break;

            case MINER_NOT_FULL:
                scheduleEvent(scheduler, entity,
                        ImageStore.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                scheduleEvent(scheduler, entity,
                        Entity.createAnimationAction(entity, 0),
                        entity.getAnimationPeriod());
                break;

            case ORE:
                scheduleEvent(scheduler, entity,
                        ImageStore.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                break;

            case ORE_BLOB:
                scheduleEvent(scheduler, entity,
                        ImageStore.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                scheduleEvent(scheduler, entity,
                        Entity.createAnimationAction(entity, 0),
                        entity.getAnimationPeriod());
                break;

            case QUAKE:
                scheduleEvent(scheduler, entity,
                        ImageStore.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                scheduleEvent(scheduler, entity, Entity.createAnimationAction(entity,
                        Functions.QUAKE_ANIMATION_REPEAT_COUNT),
                        entity.getAnimationPeriod());
                break;

            case VEIN:
                scheduleEvent(scheduler, entity,
                        ImageStore.createActivityAction(entity, world, imageStore),
                        entity.actionPeriod);
                break;

            default:
        }
    }

    public static void scheduleEvent(
            EventScheduler scheduler,
            Entity entity,
            Action action,
            long afterPeriod)
    {
        long time = System.currentTimeMillis() + (long)(afterPeriod
                * scheduler.timeScale);
        Event event = new Event(action, time, entity);

        scheduler.eventQueue.add(event);

        // update list of pending events for the given entity
        List<Event> pending = scheduler.pendingEvents.getOrDefault(entity,
                new LinkedList<>());
        pending.add(event);
        scheduler.pendingEvents.put(entity, pending);
    }

    public static void unscheduleAllEvents(
            EventScheduler scheduler, Entity entity)
    {
        List<Event> pending = scheduler.pendingEvents.remove(entity);

        if (pending != null) {
            for (Event event : pending) {
                scheduler.eventQueue.remove(event);
            }
        }
    }

    public static void removePendingEvent(
            EventScheduler scheduler, Event event)
    {
        List<Event> pending = scheduler.pendingEvents.get(event.entity);

        if (pending != null) {
            pending.remove(event);
        }
    }


    public static void updateOnTime(EventScheduler scheduler, long time) {
        while (!scheduler.eventQueue.isEmpty()
                && scheduler.eventQueue.peek().time < time) {
            Event next = scheduler.eventQueue.poll();

            removePendingEvent(scheduler, next);

            next.action.executeAction(next.action, scheduler);
        }
    }
}
