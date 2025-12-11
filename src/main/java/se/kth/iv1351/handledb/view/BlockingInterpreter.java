package se.kth.iv1351.handledb.view;

import se.kth.iv1351.handledb.controller.Controller;
import se.kth.iv1351.handledb.model.*;

import java.util.List;
import java.util.Scanner;

public class BlockingInterpreter {
    private static final String PROMPT = "> ";
    private final Scanner scanner = new Scanner(System.in);
    private Controller controller;
    private boolean keepReceivingCmds = false;

    public BlockingInterpreter(Controller controller) {
        this.controller = controller;
    }

    public void stop() {
        keepReceivingCmds = false;
    }

    public void handleCommands() {
        keepReceivingCmds = true;
        while (keepReceivingCmds) {

            while (keepReceivingCmds) {
                try {
                    CmdLine cmdLine = new CmdLine(readNextLine());
                    switch (cmdLine.getCmd()) {
                        case HELP:
                            for (Command command : Command.values()) {
                                if (command == Command.ILLEGAL_COMMAND) {
                                    continue;
                                }
                                System.out.println(command.toString().toLowerCase());
                            }
                            break;
                        case QUIT:
                            keepReceivingCmds = false;
                            break;
                        case COST:
                            InstanceCostDTO cost = controller.readCourseInstanceCost(cmdLine.getParameter(0), cmdLine.getParameter(1));
                            if (cost != null) {
                                System.out.println(
                                        "Course Code: " + cost.getCourseCode() + ", " +
                                                "Instance Id: " + cost.getInstanceId() + ", " +
                                                "Study Period: " + cost.getStudyPeriod() + ", " +
                                                "Planned Cost (in KSEK): " + String.format("%.2f", cost.getPlannedCost()) + ", " +
                                                "Actual Cost (in KSEK): " + String.format("%.2f", cost.getActualCost())
                                );
                            }
                            break;
                        case ADD_STUDENTS:
                            controller.updateNumberOfStudents(cmdLine.getParameter(0), Integer.parseInt(cmdLine.getParameter(1)));
                            break;

                        case ALLOCATE:
                            controller.updateTeachingLoad(new TeacherAllocationDTO(cmdLine.getParameter(0), cmdLine.getParameter(1),
                                    cmdLine.getParameter(2), Integer.parseInt(cmdLine.getParameter(3))));
                            break;
                        case DEALLOCATE:
                            controller.deleteTeachingLoad(new TeacherAllocationDTO(cmdLine.getParameter(0), cmdLine.getParameter(1),
                                    cmdLine.getParameter(2)));
                            break;
                        case ADD_ACTIVITY:
                            controller.updateTeachingActivity(new ActivityDTO(cmdLine.getParameter(0), cmdLine.getParameter(1), cmdLine.getParameter(2),
                                    Integer.parseInt(cmdLine.getParameter(3)), Float.parseFloat(cmdLine.getParameter(4))));
                            break;
                        case LIST_ACTIVITY:
                            List<AllocatedActivityDTO> activities = controller.getAllocatedActivities(cmdLine.getParameter(0));
                            for (AllocatedActivityDTO activity : activities) {
                                System.out.println(
                                        "Course Code: " + activity.getCourseCode() + ", " +
                                                "Course Name: " + activity.getCourseName() + ", " +
                                                "Instance ID: " + activity.getInstanceId() + ", " +
                                                "Teacher: " + activity.getFirstName() + " " + activity.getLastName() + ", " +
                                                "Activity: " + activity.getActivityName() + ", " +
                                                "Planned Hours: " + activity.getPlannedHours() + ", " +
                                                "Factor: " + activity.getFactor()
                                );
                            }
                            break;

                        case PERSON:
                            List<Person> persons = controller.readPerson(cmdLine.getParameter(0), cmdLine.getParameter(1));
                            for (Person person : persons) {
                                System.out.println(
                                        "Employment Id: " + person.getEmploymentId() + ", " +
                                                "First Name: " + person.getFirstName() + ", " +
                                                "Last Name: " + person.getLastName() + ", " +
                                                "Personal Number: " + person.getPersonalNumber()
                                );
                            }
                            break;

                        case INSTANCE_ID:
                            String instanceID = controller.readInstanceID(cmdLine.getParameter(0), cmdLine.getParameter(1), Integer.parseInt(cmdLine.getParameter(2)));
                            if (instanceID != null) {
                                System.out.println(instanceID);
                            }
                            break;


                        default:
                            System.out.println("illegal command");
                    }
                } catch (Exception e) {
                    System.out.println("Operation failed");
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }

    }

    private String readNextLine() {
        System.out.print(PROMPT);
        return scanner.nextLine();
    }

}
