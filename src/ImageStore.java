import java.util.*;

import processing.core.PApplet;
import processing.core.PImage;

public final class ImageStore
{
    public Map<String, List<PImage>> images;
    public List<PImage> defaultImages;

    public ImageStore(PImage defaultImage) {
        this.images = new HashMap<>();
        defaultImages = new LinkedList<>();
        defaultImages.add(defaultImage);
    }

    public static List<PImage> getImageList(ImageStore imageStore, String key) {
        return imageStore.images.getOrDefault(key, imageStore.defaultImages);
    }

    public static Action createActivityAction(
            Entity entity, WorldModel world, ImageStore imageStore)
    {
        return new Action(ActionKind.ACTIVITY, entity, world, imageStore, 0);
    }

    public static Optional<PImage> getBackgroundImage(
            WorldModel world, Point pos)
    {
        if (WorldModel.withinBounds(world, pos)) {
            return Optional.of(Background.getCurrentImage(WorldModel.getBackgroundCell(world, pos)));
        }
        else {
            return Optional.empty();
        }
    }
}
