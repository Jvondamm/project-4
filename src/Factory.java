import processing.core.PImage;
import java.util.List;

public class Factory {
    public static MinerFull createMinerFull(
            String id,
            int resourceLimit,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new MinerFull(id, position, images,
                resourceLimit, resourceLimit, actionPeriod,
                animationPeriod);
    }

    public static Vein createVein(
            String id, Point position, int actionPeriod, List<PImage> images)
    {
        return new Vein(id, position, images, 0, 0,
                actionPeriod, 0);
    }

    public static Blacksmith createBlacksmith(String id, Point position,
                                          List<PImage> images)
    {
        return new Blacksmith( id, position, images,
                0, 0, 0, 0);
    }

    public static MinerNotFull createMinerNotFull(
            String id,
            int resourceLimit,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new MinerNotFull(id, position, images,
                resourceLimit, 0, actionPeriod, animationPeriod);
    }

    public static Obstacle createObstacle(String id, Point position,
                                          List<PImage> images)
    {
        return new Obstacle( id, position, images,
                0, 0, 0, 0);
    }

    public static Ore createOre(
            String id, Point position, int actionPeriod, List<PImage> images)
    {
        return new Ore(id, position, images, 0, 0,
                actionPeriod, 0);
    }

    public static Quake createQuake(Point position, List<PImage> images)
    {
        return new Quake(Quake.QUAKE_ID, position, images, 0, 0,
                Quake.QUAKE_ACTION_PERIOD, Quake.QUAKE_ANIMATION_PERIOD);
    }

    public static OreBlob createOreBlob(String id, Point position,
                                        int actionPeriod, int animationPeriod, List<PImage> images)
    {
        return new OreBlob( id, position, images,
                0, 0, actionPeriod, animationPeriod);
    }

    public static Animation createAnimation(Animate entity, int repeatCount)
    {
        return new Animation(entity, repeatCount);
    }
}
