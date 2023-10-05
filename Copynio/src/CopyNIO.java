import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CopyNIO {

    public static void main(String[] args) {
        String sourceFile1 = "src/filee1";
        String copyFile1 = "src/filee1copy";
        String sourceFile2 = "src/filee2";
        String copyFile2 = "src/filee2copy";

        long startTime = System.currentTimeMillis();
        copySequentialNIO(sourceFile1, copyFile1);
        copySequentialNIO(sourceFile2, copyFile2);
        long endTime = System.currentTimeMillis();
        long sequentialTime = endTime - startTime;

        startTime = System.currentTimeMillis();
        copyParallelNIO(sourceFile1, copyFile1);
        copyParallelNIO(sourceFile2, copyFile2);
        endTime = System.currentTimeMillis();
        long parallelTime = endTime - startTime;

        System.out.println("\nВремя последовательной копии с использованием NIO: " + sequentialTime + " ms");
        System.out.println("\nВремя параллельной копии с использованием NIO: " + parallelTime + " ms");
    }

    public static void copySequentialNIO(String sourcePath, String destinationPath) {
        try {
            Path source = Path.of(sourcePath);
            Path destination = Path.of(destinationPath);

            FileChannel sourceChannel = FileChannel.open(source, StandardOpenOption.READ);
            FileChannel destinationChannel = FileChannel.open(destination, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (sourceChannel.read(buffer) != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    destinationChannel.write(buffer);
                }
                buffer.clear();
            }

            sourceChannel.close();
            destinationChannel.close();

            System.out.println("\nПоследовательная копия с использованием NIO завершена ");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void copyParallelNIO(String sourcePath, String destinationPath) {
        Thread copyThread = new Thread(() -> {
            copySequentialNIO(sourcePath, destinationPath);
        });

        copyThread.start();
        try {
            copyThread.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
