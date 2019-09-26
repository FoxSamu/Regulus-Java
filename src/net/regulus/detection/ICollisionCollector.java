package net.regulus.detection;

@FunctionalInterface
public interface ICollisionCollector {
    void addCollision( CollisionPrimer primer );
}
