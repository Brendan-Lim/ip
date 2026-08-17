public class HabpyDuck {
    private static final String CHATBOT_NAME = "HabpyDuck";
    private static final String SEPARATOR = "____________________________________________________________";

    public static void main(String[] args) {
        String banner = " _   _       _                 ____             _    \n"
                + "| | | | __ _| |__  _ __  _   _|  _ \\ _   _  ___| | __\n"
                + "| |_| |/ _` | '_ \\| '_ \\| | | | | | | | | |/ __| |/ /\n"
                + "|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < \n"
                + "|_| |_|\\__,_|_.__/| .__/ \\__, |____/ \\__,_|\\___|_|\\_\\\n"
                + "                  |_|    |___/                       \n";
        System.out.println(SEPARATOR);
        System.out.print(banner);
        System.out.println("Hi friend! I'm " + CHATBOT_NAME + ".");
        System.out.println("What can I do for you today?");
        System.out.println(SEPARATOR);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(SEPARATOR);
    }
}
