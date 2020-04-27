    package be.unamur.info.b314.compiler.main.nbc;

    import java.util.Deque;
    import java.util.LinkedList;

    public class NbcCompiler {
        enum ActionType {
            LEFT,
            RIGHT,
            UP,
            DOWN,
            JUMP,
            FIGHT,
            DIG
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
        }

        Deque<SlipAction> actions;

        public NbcCompiler(){
            this.actions = new LinkedList<>();
        }

        public void addAction(ActionType actionType){
            this.actions.addLast(new SlipAction(actionType));
        }

        public void addAction(ActionType actionType, int value){
            System.out.println("COMPILER ADD ACTION :" + actionType + value);
            if (actionType == ActionType.LEFT
                    || actionType ==  ActionType.RIGHT
                    || actionType ==  ActionType.UP
                    || actionType == ActionType.DOWN) {
                SlipAction lastAction = this.actions.peekLast();
                if (lastAction != null && lastAction.type == actionType) {
                    lastAction.value = lastAction.value + value;
                } else {
                    actions.addLast(new SlipAction(actionType, value));
                }
            } else {
                this.actions.addLast(new SlipAction(actionType));
            }
        }

        public String toString(){
            return this.actions.stream()
                    .map(action -> String.format("ACTION: %s  VALUE %s \n", action.type, action.value))
                    .reduce("", (a, b) -> a + b);
        }
    }
