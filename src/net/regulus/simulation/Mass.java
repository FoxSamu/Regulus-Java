package net.regulus.simulation;

public final class Mass {
    private final Body body;
    private double mass;
    private double invMass;
    private double density = 1;
    private double inertia;
    private double invInertia;
    private boolean isStatic;

    Mass( Body body ) {
        this.body = body;
    }

    public double getMass() {
        return mass;
    }

    public double getInvMass() {
        return invMass;
    }

    public double getDensity() {
        return density;
    }

    public double getInertia() {
        return inertia;
    }

    public double getInvInertia() {
        return invInertia;
    }

    public void setDensity( double density ) {
        this.density = density;
        mass = body.getCollider().computeMass( density );
        inertia = body.getCollider().computeInertia( density );
        invMass = 1 / mass;
        invInertia = 1 / inertia;
        isStatic = false;
    }

    public void setMass( double mass ) {
        this.mass = mass;
        density = body.getCollider().computeDensityFromMass( mass );
        inertia = body.getCollider().computeInertia( density );
        invMass = 1 / mass;
        invInertia = 1 / inertia;
        isStatic = false;
    }

    public void setInertia( double mass ) {
        this.mass = mass;
        density = body.getCollider().computeDensityFromMass( mass );
        inertia = body.getCollider().computeInertia( density );
        invMass = 1 / mass;
        invInertia = 1 / inertia;
        isStatic = false;
    }

    public void setStatic() {
        isStatic = true;
        invMass = 0;
        invInertia = 0;
        mass = Double.POSITIVE_INFINITY;
        inertia = Double.POSITIVE_INFINITY;
        density = Double.POSITIVE_INFINITY;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void recompute() {
        setDensity( density );
    }
}
