package br.com.ferracini;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Commands {

    public void runCommandRuntime() throws IOException, InterruptedException {
        String homeDirectory = System.getProperty("user.home");
        Process process;
        if(isWindows()){
            process = Runtime.getRuntime()
                    .exec(String.format("cmd.exe /c dir %s", homeDirectory));
        } else {
            process = Runtime.getRuntime()
                    .exec(String.format("sh -c ls %s", homeDirectory));
        }

        StreamReader streamGobbler = new StreamReader(process.getInputStream(), System.out::println);
        Future<?> submit = Executors.newSingleThreadExecutor().
                submit(streamGobbler);

        int exitCode = process.waitFor();
        assert exitCode ==0;
        submit.isDone();
    }
    public void runCommandProcessBuilder() throws InterruptedException, IOException {
        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows()) {
            builder.command("cmd.exe", "/c", "dir");
        } else {
            builder.command("sh", "-c", "ls");
        }
        builder.directory(new File(System.getProperty("user.home")));
        Process process = builder.start();
        StreamReader streamGobbler =
                new StreamReader(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode = process.waitFor();
        assert exitCode == 0;
    }

    private boolean isWindows() {
       return System.getProperty("os.name").contains("Windows");
    }
}
