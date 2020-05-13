import java.util.List;
import processing.core.PImage;

public interface Entity
{
    int getImageIndex();
    List <PImage> getImages();
    Point getPosition();
    void setPosition(Point p);
    PImage getCurrentImage();
}