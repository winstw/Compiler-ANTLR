    package be.unamur.info.b314.compiler.main.nbc;

    import java.io.*;
    import java.util.Deque;
    import java.util.LinkedList;

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

        Deque<SlipAction> actions;
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
            writer.println("#define ROT_DEG 410\n#define FWD_DEG 1000\n#define SPEED 80\n#define WAIT 200\n");
        }

        private void printSegment() {
            writer.println("var_def segment\n\tup_count byte\n\tnote_count byte\nends\n");
        }

        private void printMain() {
            writer.println("thread main");
            printActions();
            writer.println("endt\n");
        }

        private void printSubroutines() {
            writer.println("subroutine up\nup_loop:\n\tRotateMotor(OUT_BC, SPEED, FWD_DEG)\n\twait WAIT\n\tsub up_count, up_count, 1\n\tbrtst GT, up_loop, up_count\n\treturn\nends\n");
            writer.println("subroutine down\n\tRotateMotor(OUT_C, SPEED, ROT_DEG)\n\twait WAIT\n\tRotateMotor(OUT_C, SPEED, ROT_DEG)\n\twait WAIT\n\tcall up\n\treturn\nends\n");
            writer.println("subroutine right\n\tRotateMotor(OUT_B, SPEED, ROT_DEG)\n\twait WAIT\n\tcall up\n\treturn\nends\n");
            writer.println("subroutine left\n\tRotateMotor(OUT_C, SPEED, ROT_DEG)\n\twait WAIT\n\tcall up\n\treturn\nends\n");
            writer.println("subroutine dig\n\tRotateMotor(OUT_C, SPEED, FWD_DEG)\n\tRotateMotor(OUT_B, SPEED, -FWD_DEG)\n\twait WAIT*2\n\tRotateMotor(OUT_C, SPEED, -FWD_DEG)\n\tRotateMotor(OUT_B, SPEED, FWD_DEG)\n\twait WAIT*2\n\treturn\nends\n");
            writer.println("subroutine jump\n\tset note_count, 8\njump_loop:\n\tPlayTone(TONE_C5, 700)\n\twait 800\n\tsub note_count, note_count, 1\n\tbrtst GT, jump_loop, note_count\n\treturn\nends\n");
            writer.println("subroutine fight\n\tset note_count, 8\nfight_loop:\n\tPlayTone(TONE_E5, 700)\n\twait 800\n\tsub note_count, note_count, 1\n\tbrtst GT, fight_loop, note_count\n\treturn\nends\n");
        }

        private void printActions() {
            SlipAction action;
            while (actions.size() > 0) {
                action = actions.removeFirst();

                if (action.isParametrable()) {
                    writer.printf("\tset up_count, %s\n", action.value);
                }
                writer.printf("\tcall %s\n", action.type.getName());

            }
        }

        public String toString(){
            return this.actions.stream()
                    .map(action -> String.format("ACTION: %s  VALUE %s \n", action.type, action.value))
                    .reduce("", (a, b) -> a + b);
        }
    }
