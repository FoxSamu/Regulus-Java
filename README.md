# Regulus
<big>**Regulus is an experimental rigid body engine project, written in Java.**</big>

**A small note:**

I'd like to encourage you that this project is very experimental and may not work or perform very well... Use it on your own risk...

## What is Regulus
Regulus is a starting rigid body physics engine written in Java. Regulus is in early development stages and should not be used for professional development right now. In it's current state, Regulus is unstable, does not perform very well and may come up with several bugs or even crashes. Currently, it only supports basic rigid body simulation and a few simple constraints. 

### Features
- Collision detection with SAT
- Rigid body impulse resolution
- Compound bodies
- Constraints, including:
  - Axle constraints
  - Distance constraints
  - Spring constraints
- Basic shapes, including:
  - Circles
  - Boxes
  - Capsules
  - Regular polygons
  - Custom convex polygons
  - Infinite planes
  - Infinite bars
- Basic time scaling
- Basic particle systems (experimental)
- Simple broadphase with AABBs

## Regulus' future
In the future, Regulus will be a stable and optimized for games. It will at least include the following features:
- Stable, fast and sustainable collision correction (current implementation is very basic)
- Continuous collision detection
- Particle physics, including
  - Fluids/Gases of multiple materials
  - Elastic bodies
  - Rope and cloth simulation
- Sensors and events
- Force fields, including
  - Point attraction and repulsion
  - Buoyancy bodies
  - Wind field
  - Vortex field
  - Teleporters
  - Explosions
- More advanced colliders
  - Polygon decomposition and simplification
  - Boolean operations
  - Line and point colliders
  - Raycasting and point-inside functions
  - Intersection point functions
- Faster collision detection with SAT
- A better API with better documentation
- More and better constraints, including
  - Axial springs
  - Welds
  - Motors
  - Gears
  - Magnets
  - Sliders
  - Pulleys
- More advanced air friction and wind
- More advanced rigid bodies, including:
  - Kinematic bodies
  - Breakable bodies
  - Angle and axis locking
  - Sleeping & Disabling
  - Killer bodies
- Event handling
- Advanced collision filtering, including
  - Collision layers
  - Collision predicates
- Broadphase integrators, including
  - Naive broadphase
  - SAP-Broadphase
  - Grid-Broadphase
  - Bounds-Broadphase
- World bounds handling, including:
  - Colliding
  - Deleting
  - Teleporting to the other side
- Light physics including
  - Reflection and refraction according to Snell's law
  - Wavelength-dependent refraction
  - Refraction indices
- Solver integrators, including:
  - Gauss-Seidel solver
  - Correction solver with selectable collision correctors
- Island solver
- Solver iterations
- Parallel axis theorem for inertia of compound colliders
- Multithreading support
- An integrated looping engine
- A lot of performance improvements
