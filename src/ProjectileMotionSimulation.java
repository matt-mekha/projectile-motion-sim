public class ProjectileMotionSimulation {
  private final static double g = 9.81; // acceleration due to gravity (m/s^2)
  private final static double rho = 1.225; // density of air (kg/m^3)

  public static class CommonProjectiles {
    public static class Sphere {
      public static final double dragCoefficient = 0.47;

      public static double frontalArea(double radius) {
        return radius * radius * Math.PI;
      }
    }
  }

  private final double projectileMass;
  private final Vector2 projectileWeight;
  private final double projectileDragFactor;
  private final double projectileTerminalVelocity;

  private double simulationStep = 0.01;
  private double simulationIterations = 10;

  private double launchAngle;

  public ProjectileMotionSimulation(double projectileMass, double projectileFrontalArea,
      double projectileDragCoefficient) {
    this.projectileMass = projectileMass;
    this.projectileWeight = new Vector2(0, projectileMass * -g);
    this.projectileDragFactor = 0.5 * rho * projectileDragCoefficient * projectileFrontalArea;
    this.projectileTerminalVelocity = Math.sqrt(projectileMass * g / projectileDragFactor);
  }

  public ProjectileMotionSimulation(double projectileMass, double projectileFrontalArea,
      double projectileDragCoefficient, double launchAngle) {
    this(projectileFrontalArea, projectileMass, projectileDragCoefficient);
    setLaunchAngle(launchAngle);
  }

  /**
   * @param launchAngle The angle from the horizontal, in degrees, that the
   *                    projectile begins motion in. For reference, 90 is
   *                    completely vertical and 0 is completely horizontal.
   */
  public void setLaunchAngle(double launchAngle) {
    this.launchAngle = launchAngle;
  }

  /**
   * @param simulationStep Number of seconds between each "tick" in the
   *                       simulation, 0.01 by default.
   */
  public void setSimulationStep(double simulationStep) {
    this.simulationStep = simulationStep;
  }

  /**
   * @param simulationIterations Number of iterations of the simulation completed
   *                             to find the optimal launch velocity, 10 by
   *                             default. Each iteration doubles the precision
   *                             using a binary search.
   */
  public void setSimulationIterations(double simulationIterations) {
    this.simulationIterations = simulationIterations;
  }

  private Vector2 getDragForce(Vector2 velocity) {
    double v = velocity.getMagnitude();
    double dragForceMagnitude = projectileDragFactor * v * v;
    double dragForceAngle = velocity.getAngle() + 180.0;
    return Vector2.fromPolar(dragForceMagnitude, dragForceAngle);
  }

  public double[] runSingleSimulation(double goalY, double launchVelocity) {
    Vector2 launchVelocityVector = Vector2.fromPolar(launchVelocity, launchAngle);
    return runSingleSimulation(goalY, launchVelocityVector);
  }

  private double[] runSingleSimulation(double goalY, Vector2 launchVelocity) {
    Vector2 position = new Vector2();
    Vector2 lastPosition = new Vector2();
    Vector2 velocity = launchVelocity.clone();

    double time = 0.0;

    // System.out.println("p: " + position);
    // System.out.println("v: " + velocity);

    while ((position.y > goalY || velocity.y > 0) && velocity.x > 0 && time < 5.0) {
      lastPosition = position.clone();
      position = position.plus(velocity.scaled(simulationStep));

      Vector2 netForce = projectileWeight.plus(getDragForce(velocity));
      Vector2 acceleration = netForce.scaled(1.0 / projectileMass);

      velocity = velocity.plus(acceleration.scaled(simulationStep));
      time += simulationStep;

      // System.out.println();
      // System.out.println("t: " + time);
      // System.out.println("d: " + getDragForce(velocity));
      // System.out.println("p: " + position);
      // System.out.println("v: " + velocity);
      // System.out.println("a: " + acceleration);
    }

    double range = Double.NaN;
    if (position.y <= goalY && lastPosition.y >= goalY) {
      double alpha = (goalY - lastPosition.y) / (position.y - lastPosition.y);
      range = (position.x - lastPosition.x) * alpha + lastPosition.x;
      time = time - simulationStep * (1 - alpha);
    } else {
      time = Double.NaN;
    }

    return new double[] { range, time };
  }
}
