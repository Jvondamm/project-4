import java.util.List;
import processing.core.PImage;

public interface Entity
{
    void nextImage();
    int getAnimationPeriod();
    int getImageIndex();
    List <PImage> getImages();
    Point getPosition();
    void setPosition(Point p);
}