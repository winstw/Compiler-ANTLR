    package be.unamur.info.b314.compiler.main.nbc;

    import java.io.*;
    import java.util.*;

    public class NbcCompiler {
        enum ActionType {
            LEFT("left"),
            RIGHT("right"),
            UP("up"),
            DOWN("down"),
            JUMP("jump"),
            FIGHT("fight"),
            DIG("dig");

            private String name;

            ActionType(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }
        }

        private class SlipAction{
            ActionType type;
            int value;
            public SlipAction(ActionType type, int value) {
                this.type = type;
                this.value = value;
            }
            public SlipAction(ActionType type) {
                this.type = type;
                this.value = 0;
            }

            public boolean isParametrable() {
                return type == ActionType.LEFT || type == ActionType.RIGHT || type == ActionType.UP || type == ActionType.DOWN;
            }
        }

        private static final String L_MOTOR = "B";
        private static final String R_MOTOR = "C";
        private static final String FWD_MOTOR = "BC";

        Deque<SlipAction> actions;
        private Set<ActionType> actionsUsed = new HashSet<>();
        private PrintWriter writer;
        private File outputFile;

        public NbcCompiler(File outputFile) {
            this.actions = new LinkedList<>();
            this.outputFile = outputFile;
        }

        public void addAction(ActionType actionType){
            this.actions.addLast(new SlipAction(actionType));
        }

        public void addAction(ActionType actionType, int value){
            System.out.println("COMPILER ADD ACTION :" + actionType + value);
            if (actionType == ActionType.UP){
                SlipAction lastAction = this.actions.peekLast();
                if (lastAction != null && lastAction.type == actionType) {
                    lastAction.value = lastAction.value + value;

                } else actions.addLast(new SlipAction(actionType, value));

            } else if (actionType == ActionType.LEFT ||
                       actionType == ActionType.RIGHT ||
                    actionType == ActionType.DOWN) {

                actions.addLast(new SlipAction(actionType, value));

            } else this.actions.addLast(new SlipAction(actionType));

        }

        public void compile() {
            System.out.println("GENERATE COMPILE FILE TO : " + outputFile.getPath());
            File f = outputFile;
            if(f.exists()) {
                f.delete();
            }

            try {
                f.createNewFile();
                writer = new PrintWriter(f);
                printConstants();
                printSegment();
                printMain();
                printSubroutines();
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void printConstants() {
            writer.println("#define ROT_DEG 410");
            writer.println("#define FWD_DEG 1000");
            writer.println("#define SPEED 80");
            writer.println("#define WAIT 200");
            writer.printf("#define L_MOTOR OUT_%s", L_MOTOR).println();
            writer.printf("#define R_MOTOR OUT_%s", R_MOTOR).println();
            writer.printf("#define FWD_MOTOR OUT_%s", FWD_MOTOR).println();
            writer.println();
        }

        private void printSegment() {
            writer.println("var_def segment");
            writer.println("\tup_count byte");
            writer.println("\tnote_count byte");
            writer.println("ends");
            writer.println();
        }

        private void printMain() {
            writer.println("thread main");
            printActions();
            writer.println("endt");
            writer.println();
        }

        private void printSubroutines() {
            if (actionsUsed.contains(ActionType.UP)) printUpSubroutine();
            if (actionsUsed.contains(ActionType.DOWN)) printDownSubRoutine();
            if (actionsUsed.contains(ActionType.RIGHT)) printRightSubroutine();
            if (actionsUsed.contains(ActionType.LEFT)) printLeftSubroutine();
            if (actionsUsed.contains(ActionType.DIG)) printDigSubroutine();
            if (actionsUsed.contains(ActionType.JUMP)) printJumpSubroutine();
            if (actionsUsed.contains(ActionType.FIGHT)) printFightSubroutine();
        }

        private void printUpSubroutine() {
            writer.println("subroutine up");
            writer.println("up_loop:");
            writer.println("\tRotateMotor(FWD_MOTOR, SPEED, FWD_DEG)");
            writer.println("\twait WAIT");
            writer.println("\tsub up_count, up_count, 1");
            writer.println("\tbrtst GT, up_loop, up_count");
            writer.println("\treturn");
            writer.println("ends");
            writer.println();
        }

        private void printDownSubRoutine() {
            writer.println("subroutine down");
            writer.println("\tRotateMotor(R_MOTOR, SPEED, ROT_DEG)");
            writer.println("\twait WAIT");
            writer.println("\tRotateMotor(R_MOTOR, SPEED, ROT_DEG)");
            writer.println("\twait WAIT");
            writer.println("\tcall up");
            writer.println("\treturn");
            writer.println("ends");
            writer.println();
        }

        private void printRightSubroutine() {
            writer.println("subroutine right");
            writer.println("\tRotateMotor(L_MOTOR, SPEED, ROT_DEG)");
            writer.println("\twait WAIT");
            writer.println("\tcall up");
            writer.println("\treturn");
            writer.println("ends");
            writer.println();
        }

        private void printLeftSubroutine() {
            writer.println("subroutine left");
            writer.println("\tRotateMotor(R_MOTOR, SPEED, ROT_DEG)");
            writer.println("\twait WAIT");
            writer.println("\tcall up");
            writer.println("\treturn");
            writer.println("ends");
            writer.println();
        }

        private void printDigSubroutine() {
            writer.println("subroutine dig");
            writer.println("\tRotateMotor(R_MOTOR, SPEED, FWD_DEG)");
            writer.println("\tRotateMotor(L_MOTOR, SPEED, -FWD_DEG)");
            writer.println("\twait WAIT*2");
            writer.println("\tRotateMotor(R_MOTOR, SPEED, -FWD_DEG)");
            writer.println("\tRotateMotor(L_MOTOR, SPEED, FWD_DEG)");
            writer.println("\twait WAIT*2");
            writer.println("\treturn");
            writer.println("ends");
            writer.println();
        }

        private void printJumpSubroutine() {
            writer.println("subroutine jump");
            writer.println("\tset note_count, 8");
            writer.println("jump_loop:");
            writer.println("\tPlayTone(TONE_C5, 700)");
            writer.println("\twait 800");
            writer.println("\tsub note_count, note_count, 1");
            writer.println("\tbrtst GT, jump_loop, note_count");
            writer.println("\treturn");
            writer.println("ends");
            writer.println();
        }

        private void printFightSubroutine() {
            writer.println("subroutine fight");
            writer.println("\tset note_count, 8");
            writer.println("fight_loop:");
            writer.println("\tPlayTone(TONE_E5, 700)");
            writer.println("\twait 800");
            writer.println("\tsub note_count, note_count, 1");
            writer.println("\tbrtst GT, fight_loop, note_count");
            writer.println("\treturn");
            writer.println("ends");
            writer.println();
        }

        private void printActions() {
            SlipAction action;
            Iterator<SlipAction> iterator = actions.iterator();

            while (iterator.hasNext()) {
                action = iterator.next();
                actionsUsed.add(action.type);
                if (action.type == ActionType.LEFT || action.type == ActionType.RIGHT || action.type == ActionType.DOWN) {
                    actionsUsed.add(ActionType.UP);
                }

                if (action.isParametrable()) {
                    writer.printf("\tset up_count, %s", action.value).println();
                }
                writer.printf("\tcall %s", action.type.getName()).println();
            }

        }

        public String toString(){
            return this.actions.stream()
                    .map(action -> String.format("ACTION: %s  VALUE %s \n", action.type, action.value))
                    .reduce("", (a, b) -> a + b);
        }
    }
