import java.util.List;

import processing.core.PImage;

public final class Background
{
    public String id;
    public List<PImage> images;
    public int imageIndex;

    public Background(String id, List<PImage> images) {
        this.id = id;
        this.images = images;
    }

    public int getImageIndex() { return this.imageIndex; }
    public List<PImage> getImages() { return this.images; }

    public PImage getCurrentImage()
    {
        return (this.getImages()).get(this.getImageIndex());
    }
}
