package ch.ethz.systems.netbench.core.run;

public class MainSimulation {

	public static void main(String args[]) {
		/*
		 * Comment the original settings if (args.length == 0) {
		 * System.out.println("Please specify which experiments to run!"); }
		 * 
		 * for (String arg : args) { switch(arg) { } }
		 */
		MainFromProperties.main(new String[] {
				"projects/sppifo/runs/sppifo_analysis/uniform_rank_distribution/uniform_8_queues/SPPIFO.properties" });
	}

}
