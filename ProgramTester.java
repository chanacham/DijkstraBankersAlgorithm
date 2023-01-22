import java.util.ArrayList;
import java.util.Random;

public class ProgramTester {

    final static int NUM_PROCS = 6; // How many concurrent processes
    final static int TOTAL_RESOURCES = 30; // Total resources in the system
    final static int MAX_PROC_RESOURCES = 13; // Highest amount of resources any process could need
    final static int ITERATIONS = 30; // How long to run the program
    static int totalHeldResources = 0; // How many resources are currently being held
    static Random rand = new Random();

    public static void main(String[] args) {
        // The list of processes:
        ArrayList<Process> processes = new ArrayList<>();
        for (int i = 0; i < NUM_PROCS; i++) {
            // Initialize to a new Proc, with some small range for its max
            processes.add(new Process(MAX_PROC_RESOURCES - rand.nextInt(3)));
        }

        // Run the simulation:
        for (int i = 0; i < ITERATIONS; i++) {
            // loop through the processes and for each one get its request
            for (int j = 0; j < processes.size(); j++) {
                // Get the request
                int currRequest = processes.get(j).resourceRequest(TOTAL_RESOURCES - totalHeldResources);

                // just ignore processes that don't ask for resources
                if (currRequest == 0) {
                    System.out.println("Continue to next request, process did not request resources");
                    continue;
                } else if (currRequest < 0) {
                    totalHeldResources += currRequest;
                    System.out.println("Process " + j + " finished, releasing resources");
                } else {
                    // Here you have to enter code to determine whether the request can be granted,
                    // and then grant the request if possible. Remember to give output to the console
                    // this indicates what the request is, and whether its granted.
                    boolean canGrant = checkCanGrant(processes, j, currRequest);
                    if (canGrant) {
                        System.out.println("Request " + j + " of " + currRequest + " resources is granted");

                        processes.get(j).addResources(currRequest);
                        totalHeldResources += currRequest;
                    } else {
                        System.out.println("Request " + j + " of " + currRequest + " resources is denied");
                    }
                }

                // At the end of each iteration, give a summary of the current status:
                System.out.println("\n***** STATUS *****");
                System.out.println("Total Available: " + (TOTAL_RESOURCES - totalHeldResources));
                for (int k = 0; k < processes.size(); k++)
                    System.out.println("Process " + k + " holds: " + processes.get(k).getHeldResources() + ", max: " +
                            processes.get(k).getMaxResources() + ", claim: " +
                            (processes.get(k).getMaxResources() - processes.get(k).getHeldResources()));
                System.out.println("***** END STATUS *****\n");
            }
        }
    }

    private static boolean checkCanGrant(ArrayList<Process> processes, int currProc, int currRequest) {

        ArrayList<Process> copyProcesses = new ArrayList<>(processes);
        ArrayList<Integer> indexHolder = new ArrayList<>();

        int available = TOTAL_RESOURCES - totalHeldResources - currRequest;
        copyProcesses.get(currProc).addResources(currRequest);

        boolean found = true;
        while (!copyProcesses.isEmpty() && found) {
            found = false;
            for (int ix = 0; ix < copyProcesses.size(); ix++) {
                Process curr = copyProcesses.get(ix);
                if ((curr.getMaxResources() - curr.getHeldResources()) <= available) {
                    available += curr.getHeldResources();
                    indexHolder.add(ix);
                    found = true;
                }
            }
            for (int jx = indexHolder.size() - 1; jx >= 0; jx--) {
                copyProcesses.remove((int) indexHolder.get(jx));
            }
            indexHolder.clear();
        }
        processes.get(currProc).addResources(-currRequest);
        return found;
    }

}