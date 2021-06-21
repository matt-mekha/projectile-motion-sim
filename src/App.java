import java.io.File;
import java.io.PrintWriter;

public class App {
  public static void main(String[] args) throws Exception {
    PrintWriter writer = new PrintWriter(new File("output.csv"));

    ProjectileMotionSimulation sim = new ProjectileMotionSimulation(0.142,
        ProjectileMotionSimulation.CommonProjectiles.Sphere.frontalArea(0.178),
        ProjectileMotionSimulation.CommonProjectiles.Sphere.dragCoefficient);

    writer.println("angle,rpm,range,airtime");

    for (int hoodAngle = 57; hoodAngle <= 76; hoodAngle++) {
      sim.setLaunchAngle(hoodAngle);
      for (int flywheelRpm = 3500; flywheelRpm <= 6000; flywheelRpm += 50) {
        double[] results = sim.runSingleSimulation(2.496, flywheelRpmToVelocity(flywheelRpm));
        double range = results[0];
        double airtime = results[1];

        StringBuilder sb = new StringBuilder();
        sb.append(hoodAngle);
        sb.append(",");
        sb.append(flywheelRpm);
        sb.append(",");
        sb.append(range);
        sb.append(",");
        sb.append(airtime);
        writer.println(sb.toString());
      }
    }

    // sim.setLaunchAngle(76);
    // sim.runSingleSimulation(2.496, flywheelRpmToVelocity(4000));

    writer.close();
  }

  private static double flywheelRpmToVelocity(double rpm) {
    return rpm / 60.0 * (2.0 * Math.PI * 0.051) / 2.0;
  }
}
