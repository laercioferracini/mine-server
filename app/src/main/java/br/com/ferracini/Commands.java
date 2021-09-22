package br.com.ferracini;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class Commands {

    Logger logger = Logger.getLogger(Commands.class.getName());
    String dir = System.getProperty("user.dir").concat("/app/mine_server");

    String javaCommand = "/home/ferracini/.sdkman/candidates/java/current/bin/java";
    List<String> command = List.of(javaCommand, "-Xms1G", "-Xmx2G", "-jar", "server.jar", "nogui");

    private static List<String> commandHistory = new ArrayList<>();

    public void setCommandHistory(String c) {
        commandHistory.add(c);
    }

    public List<String> getCommandHistory() {
        return commandHistory;
    }

    public void runCommandRuntime() throws IOException, InterruptedException {
        String homeDirectory = System.getProperty("user.home");

        Process process;
        if (isWindows()) {
            process = Runtime.getRuntime()
                    .exec(String.format("cmd.exe /c dir %s", homeDirectory));
        } else {
            process = Runtime.getRuntime()
                    .exec(String.format("sh -c ls -l %s", homeDirectory));
        }

        StreamReader streamGobbler = new StreamReader(process.getInputStream(), System.out::println);

        Future<?> submit = Executors.newSingleThreadExecutor().
                submit(streamGobbler);

        int exitCode = process.waitFor();
        assert exitCode == 0;
        submit.isDone();
    }

    public void runCommandProcessBuilder(List<String> command) throws InterruptedException, IOException {
        ProcessBuilder builder = new ProcessBuilder().inheritIO();
        if (isWindows()) {
            builder.command("cmd.exe", "/c", "dir");
        } else {

            var bash = new ArrayList<String>();
            bash.add("sh");
            bash.add("-c");
            bash.addAll(command);
            System.out.printf("command: %s%n", bash);
            builder.command(bash);
        }
        //builder.directory(new File(System.getProperty("user.dir").concat("/mine_server")));
        Process process = builder.start();
        StreamReader streamGobbler = new StreamReader(process.getInputStream(), System.out::println);
        StreamReader streamErrGobbler = new StreamReader(process.getErrorStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode = process.waitFor();
        assert exitCode == 0;

    }

    private boolean isWindows() {
        return System.getProperty("os.name").contains("Windows");
    }
}
