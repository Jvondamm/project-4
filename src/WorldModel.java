import java.util.*;

public final class WorldModel
{
    public int numRows;
    public int numCols;
    public Background background[][];
    public Entity occupancy[][];
    public Set<Entity> entities;

    public WorldModel(int numRows, int numCols, Background defaultBackground) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.background = new Background[numRows][numCols];
        this.occupancy = new Entity[numRows][numCols];
        this.entities = new HashSet<>();

        for (int row = 0; row < numRows; row++) {
            Arrays.fill(this.background[row], defaultBackground);
        }
    }

    public static void load(
            Scanner in, WorldModel world, ImageStore imageStore)
    {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                if (!processLine(in.nextLine(), world, imageStore)) {
                    System.err.println(String.format("invalid entry on line %d",
                                                     lineNumber));
                }
            }
            catch (NumberFormatException e) {
                System.err.println(
                        String.format("invalid entry on line %d", lineNumber));
            }
            catch (IllegalArgumentException e) {
                System.err.println(
                        String.format("issue on line %d: %s", lineNumber,
                                      e.getMessage()));
            }
            lineNumber++;
        }
    }

    public static boolean processLine(
            String line, WorldModel world, ImageStore imageStore)
    {
        String[] properties = line.split("\\s");
        if (properties.length > 0) {
            switch (properties[Functions.PROPERTY_KEY]) {
                case Functions.BGND_KEY:
                    return parseBackground(properties, world, imageStore);
                case Functions.MINER_KEY:
                    return parseMiner(properties, world, imageStore);
                case Functions.OBSTACLE_KEY:
                    return parseObstacle(properties, world, imageStore);
                case Functions.ORE_KEY:
                    return parseOre(properties, world, imageStore);
                case Functions.SMITH_KEY:
                    return parseSmith(properties, world, imageStore);
                case Functions.VEIN_KEY:
                    return parseVein(properties, world, imageStore);
            }
        }

        return false;
    }

    public static boolean parseBackground(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == Functions.BGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.BGND_COL]),
                                 Integer.parseInt(properties[Functions.BGND_ROW]));
            String id = properties[Functions.BGND_ID];
            setBackground(world, pt,
                          new Background(id, ImageStore.getImageList(imageStore, id)));
        }

        return properties.length == Functions.BGND_NUM_PROPERTIES;
    }

    public static boolean parseMiner(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == Functions.MINER_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.MINER_COL]),
                                 Integer.parseInt(properties[Functions.MINER_ROW]));
            Entity entity = Entity.createMinerNotFull(properties[Functions.MINER_ID],
                                               Integer.parseInt(
                                                       properties[Functions.MINER_LIMIT]),
                                               pt, Integer.parseInt(
                            properties[Functions.MINER_ACTION_PERIOD]), Integer.parseInt(
                            properties[Functions.MINER_ANIMATION_PERIOD]),
                                               ImageStore.getImageList(imageStore,
                                                            Functions.MINER_KEY));
            tryAddEntity(world, entity);
        }

        return properties.length == Functions.MINER_NUM_PROPERTIES;
    }

    public static boolean parseObstacle(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == Functions.OBSTACLE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.OBSTACLE_COL]),
                                 Integer.parseInt(properties[Functions.OBSTACLE_ROW]));
            Entity entity = Entity.createObstacle(properties[Functions.OBSTACLE_ID], pt,
                                           ImageStore.getImageList(imageStore,
                                                        Functions.OBSTACLE_KEY));
            tryAddEntity(world, entity);
        }

        return properties.length == Functions.OBSTACLE_NUM_PROPERTIES;
    }

    public static boolean parseOre(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == Functions.ORE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.ORE_COL]),
                                 Integer.parseInt(properties[Functions.ORE_ROW]));
            Entity entity = Entity.createOre(properties[Functions.ORE_ID], pt, Integer.parseInt(
                    properties[Functions.ORE_ACTION_PERIOD]),
                                      ImageStore.getImageList(imageStore, Functions.ORE_KEY));
            tryAddEntity(world, entity);
        }

        return properties.length == Functions.ORE_NUM_PROPERTIES;
    }

    public static boolean parseSmith(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == Functions.SMITH_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.SMITH_COL]),
                                 Integer.parseInt(properties[Functions.SMITH_ROW]));
            Entity entity = Entity.createBlacksmith(properties[Functions.SMITH_ID], pt,
                                             ImageStore.getImageList(imageStore,
                                                          Functions.SMITH_KEY));
            tryAddEntity(world, entity);
        }

        return properties.length == Functions.SMITH_NUM_PROPERTIES;
    }

    public static boolean parseVein(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == Functions.VEIN_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.VEIN_COL]),
                                 Integer.parseInt(properties[Functions.VEIN_ROW]));
            Entity entity = Entity.createVein(properties[Functions.VEIN_ID], pt,
                                       Integer.parseInt(
                                               properties[Functions.VEIN_ACTION_PERIOD]),
                                       ImageStore.getImageList(imageStore, Functions.VEIN_KEY));
            tryAddEntity(world, entity);
        }

        return properties.length == Functions.VEIN_NUM_PROPERTIES;
    }

    public static void tryAddEntity(WorldModel world, Entity entity) {
        if (isOccupied(world, entity.position)) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        Event.addEntity(world, entity);
    }

    public static boolean withinBounds(WorldModel world, Point pos) {
        return pos.y >= 0 && pos.y < world.numRows && pos.x >= 0
                && pos.x < world.numCols;
    }

    public static boolean isOccupied(WorldModel world, Point pos) {
        return withinBounds(world, pos) && getOccupancyCell(world, pos) != null;
    }

    public static void setBackground(
            WorldModel world, Point pos, Background background)
    {
        if (withinBounds(world, pos)) {
            setBackgroundCell(world, pos, background);
        }
    }

    public static Optional<Entity> getOccupant(WorldModel world, Point pos) {
        if (isOccupied(world, pos)) {
            return Optional.of(getOccupancyCell(world, pos));
        }
        else {
            return Optional.empty();
        }
    }

    public static Entity getOccupancyCell(WorldModel world, Point pos) {
        return world.occupancy[pos.y][pos.x];
    }

    public static void setOccupancyCell(
            WorldModel world, Point pos, Entity entity)
    {
        world.occupancy[pos.y][pos.x] = entity;
    }

    public static Background getBackgroundCell(WorldModel world, Point pos) {
        return world.background[pos.y][pos.x];
    }

    public static void setBackgroundCell(
            WorldModel world, Point pos, Background background)
    {
        world.background[pos.y][pos.x] = background;
    }
}
