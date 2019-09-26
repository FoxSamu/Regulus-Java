package net.regulus.simulation.particle;

import java.util.List;

import net.regulus.geom.Vec2;

public abstract class Particle {
    public final Vec2 position = new Vec2();
    public final Vec2 velocity = new Vec2();
    public final ParticleSystem system;
    public double radius = 0.08;
    public double mass = 0.006;

    protected final Vec2 force = new Vec2();
    protected double density;
    protected double pressure;
    public double interactionRadius = 0.5;
    public double gasConst = 200;
    public double restDens = 100;
    public double viscosity = 0.1;

    public double invMass() {
        return 1 / mass;
    }

    protected Particle( ParticleSystem system ) {
        this.system = system;
    }

    public abstract void update( double dt );
    public abstract void getInteractingParticles( List<ParticleSystem.ParticleCollision> collisions );
}
