package asteroids.model;

import be.kuleuven.cs.som.annotate.Basic;

/**
 * @author Jaro Deklerck
 */
public class BlackHole extends Entity {

    /**
     * Create a new black hole with the given position and radius.
     * @param x
     * 		X coordinate for the new black hole.
     * @param y
     * 		Y coordinate for the new black hole.
     * @param radius
     * 		Radius for the new black hole.
     * @post ...
     * 		| (x != IsNaN) && (y != IsNaN) && (radius != IsNaN)
     * @effect setPosition
     * @effect setRadius
     * @throws EntityException
     */

    public BlackHole(double x, double y, double radius) throws EntityException {
        if (!Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(radius)){
            setPosition(x, y);
            setVelocity(0, 0);
            setRadius(radius);
        }
        else {
            throw new EntityException("Values are NaN!");
        }
    }

    @Override
    protected void setRadius(double radius) throws EntityException {
        if (radius >= 100) {
            this.radius = radius;
        }
        else {
            throw new EntityException("Wrong radius!");
        }
    }

    @SuppressWarnings("unused")
    protected void changeRadius(double radius) throws WorldException, EntityException {
        if (this.getWorld() != null) {
            if (this.getPosition()[0] >= radius && this.getPosition()[1] >= radius && world.getSize()[0] - this.getPosition()[0] >= radius && world.getSize()[1] - this.getPosition()[1] >= radius) {
                for (Entity entity : world.entities) {
                    if (Math.hypot(entity.x-this.x,entity.y-this.y)-(entity.radius+radius) < 0 && this != entity) {
                        if (entity instanceof BlackHole) {
                            BlackHole bh = new BlackHole((this.x+entity.x)/2, (this.y+entity.y)/2, radius+entity.radius);
                            World w = this.getWorld();
                            this.terminate();
                            entity.terminate();
                            bh.addEntityToWorld(w);
                        }
                        else if (entity instanceof Bullet) {
                            this.setRadius(radius);
                        }
                        else {
                            entity.terminate();
                            this.setRadius(radius);
                        }
                    }
                }
            }
            else {
                throw new WorldException("Black hole outside boundaries.");
            }
        }
        else {
            throw new WorldException("Black hole not located in world.");
        }
    }

    /**
     * Adds a black hole to the world.
     * @param world
     *      The world the black hole has to be added to.
     * @see implementation
     * @throws WorldException
     */

    @Override
    public void addEntityToWorld(World world) throws WorldException, IllegalArgumentException, EntityException {
        if (world == null) {
            throw new IllegalArgumentException();
        }
        double check = this.getRadius();
        if (this.getWorld() == null) {
            if (this.getPosition()[0] >= check && this.getPosition()[1] >= check && world.getSize()[0] - this.getPosition()[0] >= check && world.getSize()[1] - this.getPosition()[1] >= check) {
                boolean c = true;
                for (Entity entity : world.entities) {
                    if (this.overlapAddToWorld(entity)) {
                        if (entity instanceof BlackHole) {
                            BlackHole bh = new BlackHole((this.x+entity.x)/2, (this.y+entity.y)/2, this.radius+entity.radius);
                            this.terminate();
                            entity.terminate();
                            bh.addEntityToWorld(world);
                            c = false;
                        }
                        else if (entity instanceof Bullet) {
                        }
                        else {
                            entity.terminate();
                        }
                    }
                }
                if (c) {
                    world.entities.add(this);
                    this.setWorld(world);
                }
            } else {
                throw new WorldException("Black hole outside boundary.");
            }
        }
        else {
            throw new WorldException("Black hole is already located in a world!");
        }
    }

    /**
     * Remove a black hole from the world.
     * @param world
     *      The world the black hole has to be removed from.
     * @see implementation
     * @throws WorldException
     */

    @Override
    public void removeEntityFromWorld(World world) throws WorldException {
        if (world.entities.contains(this)) {
            world.entities.remove(this);
            this.world = null;
        }
        else {
            throw new WorldException("Black hole is not in the world");
        }
    }


    /**
     * Terminates the black hole.
     * @post ...
     * 		|new blackhole == null
     */

    @Override
    public void terminate() throws WorldException, EntityException {
        if (this.world != null){
            this.world.entities.remove(this);
        }
        this.world = null;
        this.x = Double.NaN;
        this.y = Double.NaN;
        this.radius = Double.NaN;
    }

    /**
     * Returns the state of the black hole.
     * @return
     */

    @Basic
    public boolean isTerminated() {
        if ((this.x <= 0 || this.x > 0) && (this.y <= 0 || this.y > 0)) {
            return false;
        }
        else {
            return true;
        }
    }
}
