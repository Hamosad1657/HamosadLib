
package com.hamosad1657.lib.motors;

/** Add your docs here. */
abstract public class HaBaseTalon extends HaMotorController {
	abstract public double getP();

	abstract public double getI();

	abstract public double getD();

	abstract public double getFF();

	abstract public double getIZone();

	abstract public void setP(double value);

	abstract public void setI(double value);

	abstract public void setD(double value);

	abstract public void setFF(double value);

	abstract public void setIZone(double value);
}
